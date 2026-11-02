package com.beforeithappens.kidsbook.ui.comic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.beforeithappens.kidsbook.data.BookContent
import com.beforeithappens.kidsbook.data.CharacterSpec
import com.beforeithappens.kidsbook.data.Panel
import com.beforeithappens.kidsbook.data.PanelPerson
import com.beforeithappens.kidsbook.data.Speech
import com.beforeithappens.kidsbook.ui.theme.BookColors
import com.beforeithappens.kidsbook.ui.theme.forWhiteText
import com.beforeithappens.kidsbook.ui.theme.parseHex
import androidx.compose.material3.MaterialTheme

private val Dark = Color(0xFF2B2B2B)
private val Cheek = Color(0xFFF48FB1)
private val Tear = Color(0xFF4FA3E0)

/** Base character height (kids) inside a full comic panel. */
private val KidHeight = 112.dp
private val CharacterArea = 176.dp
private val GroundInset = 14.dp

/**
 * One comic panel: numbered caption bar in the part colour, the scene and characters drawn
 * on a Canvas, and the speech bubbles as real text (so they scale with the system font size).
 */
@Composable
fun ComicPanel(
    content: BookContent,
    panel: Panel,
    number: Int,
    partColor: Color,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = content.describePanel(panel, number)
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Dark, RoundedCornerShape(16.dp))
            .clickable(onClickLabel = "hear this panel", onClick = onTap)
            .clearAndSetSemantics {
                contentDescription = description
                role = Role.Button
            },
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(partColor.forWhiteText())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(28.dp).background(Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("$number", color = partColor.forWhiteText(), fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.size(10.dp))
            Text(panel.caption, color = Color.White, style = MaterialTheme.typography.titleSmall)
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val panelWidth = maxWidth
            Canvas(Modifier.matchParentSize()) {
                val groundY = size.height - GroundInset.toPx()
                drawScene(panel.scene, groundY)
                drawPeople(content, panel.people, groundY, KidHeight.toPx())
            }
            Column(Modifier.fillMaxWidth().padding(top = 10.dp)) {
                panel.speech.forEach { s ->
                    SpeechBubble(panel, s, panelWidth)
                    Spacer(Modifier.height(6.dp))
                }
                Spacer(Modifier.height(CharacterArea))
            }
        }
    }
}

/** Small picture-only version used on story cards. */
@Composable
fun ComicThumbnail(content: BookContent, panel: Panel, modifier: Modifier = Modifier) {
    Canvas(modifier.clip(RoundedCornerShape(12.dp)).border(1.5.dp, Dark, RoundedCornerShape(12.dp))) {
        val groundY = size.height * 0.92f
        drawScene(panel.scene, groundY)
        drawPeople(content, panel.people, groundY, size.height * 0.62f)
    }
}

/** A single character on its own, for avatars and Meet the crew. */
@Composable
fun CharacterPortrait(
    content: BookContent,
    who: String,
    modifier: Modifier = Modifier,
    face: String = "happy",
    arm: String = "wave",
    background: Color = Color(0xFFE6F2EF),
) {
    Canvas(modifier.clip(CircleShape).background(background)) {
        val spec = content.character(who) ?: return@Canvas
        val h = size.height * 0.92f / spec.height.coerceAtLeast(1f)
        drawCharacter(spec, PanelPerson(who = who, face = face, arm = arm), size.width / 2f, size.height * 1.02f, h)
    }
}

@Composable
private fun SpeechBubble(panel: Panel, speech: Speech, panelWidth: Dp) {
    val speaker = speech.speaker?.let { panel.people.getOrNull(it) }
    val density = LocalDensity.current
    val x = speaker?.x ?: 0.5f
    val bias = ((x * 2f) - 1f).coerceIn(-0.9f, 0.9f)
    var bubbleWidthPx by remember { mutableIntStateOf(0) }
    val maxBubble = panelWidth * 0.78f

    Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
        Column(
            Modifier
                .align(BiasAlignment(bias, 0f))
                .widthIn(max = maxBubble)
                .width(IntrinsicSize.Max)
                .onSizeChanged { bubbleWidthPx = it.width },
        ) {
            if (speaker == null) {
                // On-screen text, like a message popping up in a game.
                Text(
                    speech.text,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(Color(0xFF263238), RoundedCornerShape(6.dp))
                        .border(2.dp, Color(0xFF80CBC4), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            } else {
                val think = speech.kind == "think"
                Text(
                    speech.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(if (think) 28.dp else 16.dp))
                        .border(2.dp, Dark, RoundedCornerShape(if (think) 28.dp else 16.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
                // Tail pointing down towards the speaker.
                Canvas(Modifier.fillMaxWidth().height(14.dp)) {
                    val panelPx = with(density) { (panelWidth - 20.dp).toPx() }
                    val bubbleLeft = (panelPx - bubbleWidthPx) * (bias + 1f) / 2f
                    val speakerPx = x * panelPx
                    val tipX = (speakerPx - bubbleLeft).coerceIn(18.dp.toPx(), (size.width - 18.dp.toPx()).coerceAtLeast(18.dp.toPx()))
                    if (think) {
                        drawCircle(Color.White, 5.dp.toPx(), Offset(tipX, 4.dp.toPx()))
                        drawCircle(Dark, 5.dp.toPx(), Offset(tipX, 4.dp.toPx()), style = Stroke(2.dp.toPx()))
                        drawCircle(Color.White, 3.dp.toPx(), Offset(tipX + 4.dp.toPx(), 11.dp.toPx()))
                        drawCircle(Dark, 3.dp.toPx(), Offset(tipX + 4.dp.toPx(), 11.dp.toPx()), style = Stroke(1.5.dp.toPx()))
                    } else {
                        val p = Path().apply {
                            moveTo(tipX - 8.dp.toPx(), -1.dp.toPx())
                            lineTo(tipX + 8.dp.toPx(), -1.dp.toPx())
                            lineTo(tipX + 2.dp.toPx(), size.height)
                            close()
                        }
                        drawPath(p, Color.White)
                        drawLine(Dark, Offset(tipX - 8.dp.toPx(), 0f), Offset(tipX + 2.dp.toPx(), size.height), 2.dp.toPx())
                        drawLine(Dark, Offset(tipX + 8.dp.toPx(), 0f), Offset(tipX + 2.dp.toPx(), size.height), 2.dp.toPx())
                    }
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* Drawing                                                              */
/* ------------------------------------------------------------------ */

private fun DrawScope.drawPeople(content: BookContent, people: List<PanelPerson>, groundY: Float, kidHeight: Float) {
    people.forEach { p ->
        val spec = content.character(p.who) ?: return@forEach
        drawCharacter(spec, p, p.x.coerceIn(0.08f, 0.92f) * size.width, groundY, kidHeight)
    }
}

fun DrawScope.drawCharacter(spec: CharacterSpec, p: PanelPerson, cx: Float, groundY: Float, kidHeight: Float) {
    val h = kidHeight * spec.height
    val d = if (p.facingLeft) -1f else 1f
    val skin = parseHex(spec.skin)
    val hair = parseHex(spec.hair)
    val top = parseHex(spec.top)
    val legs = parseHex(spec.legs)

    val bodyTop = groundY - 0.66f * h
    val r = 0.17f * h
    val head = Offset(cx, bodyTop - 0.14f * h)

    // Hair that sits behind the head.
    when (spec.hairStyle) {
        "long" -> drawRoundRect(
            hair,
            topLeft = Offset(head.x - 1.12f * r, head.y - 0.95f * r),
            size = Size(2.24f * r, 2.25f * r),
            cornerRadius = CornerRadius(0.6f * r),
        )
        "ponytail" -> drawOval(
            hair,
            topLeft = Offset(head.x - d * 1.05f * r - 0.38f * r, head.y - 0.55f * r),
            size = Size(0.76f * r, 1.35f * r),
        )
        "puffs" -> {
            drawCircle(hair, 0.46f * r, Offset(head.x - 0.82f * r, head.y - 0.82f * r))
            drawCircle(hair, 0.46f * r, Offset(head.x + 0.82f * r, head.y - 0.82f * r))
        }
    }

    // Legs and shoes.
    val legTop = groundY - 0.30f * h
    for (side in listOf(-1f, 1f)) {
        val lx = cx + side * 0.08f * h
        drawLine(legs, Offset(lx, legTop), Offset(lx, groundY - 0.04f * h), 0.10f * h, StrokeCap.Round)
        drawOval(
            Dark,
            topLeft = Offset(lx - 0.075f * h + d * 0.02f * h, groundY - 0.07f * h),
            size = Size(0.15f * h, 0.07f * h),
        )
    }

    // Body.
    drawRoundRect(
        top,
        topLeft = Offset(cx - 0.18f * h, bodyTop),
        size = Size(0.36f * h, 0.38f * h),
        cornerRadius = CornerRadius(0.08f * h),
    )

    // Head and hair cap.
    drawCircle(skin, r, head)
    drawArc(
        hair,
        startAngle = 198f,
        sweepAngle = 144f,
        useCenter = false,
        topLeft = Offset(head.x - 1.06f * r, head.y - 1.06f * r),
        size = Size(2.12f * r, 2.12f * r),
    )
    if (spec.hairStyle == "spiky") {
        listOf(-0.6f, -0.2f, 0.2f, 0.6f).forEach { o ->
            val bx = head.x + o * r
            val by = head.y - 0.82f * r
            drawPath(Path().apply {
                moveTo(bx - 0.24f * r, by + 0.1f * r)
                lineTo(bx + 0.24f * r, by + 0.1f * r)
                lineTo(bx + o * 0.15f * r, by - 0.42f * r)
                close()
            }, hair)
        }
    }

    drawFace(p, head, r, d, spec.glasses, featureColor(skin))
    drawArms(p, cx, bodyTop, h, d, skin, top)
}

/** Very dark skin gets lighter mouth and brows so the expression stays readable. */
private fun featureColor(skin: Color): Color =
    if (0.2126f * skin.red + 0.7152f * skin.green + 0.0722f * skin.blue < 0.3f) Color(0xFFF3D9C6) else Dark

private fun DrawScope.drawFace(p: PanelPerson, c: Offset, r: Float, d: Float, glasses: Boolean, ink: Color) {
    val shift = d * 0.12f * r
    val eyeY = c.y + 0.02f * r
    val eyes = listOf(c.x + shift - 0.36f * r, c.x + shift + 0.36f * r)
    val eyeR = if (p.face == "surprised") 0.12f * r else 0.08f * r
    eyes.forEach {
        if (ink != Dark) drawCircle(Color.White, eyeR * 1.7f, Offset(it, eyeY))
        drawCircle(Dark, eyeR, Offset(it, eyeY))
    }

    val m = Offset(c.x + shift, c.y + 0.45f * r)
    val w = 0.5f * r
    val sw = 0.07f * r
    val line = Stroke(sw, cap = StrokeCap.Round)

    fun brows(innerUp: Boolean) {
        eyes.forEachIndexed { i, ex ->
            val innerSide = if (i == 0) 1f else -1f // towards the middle of the face
            val inner = Offset(ex + innerSide * 0.15f * r, eyeY - (if (innerUp) 0.36f else 0.2f) * r)
            val outer = Offset(ex - innerSide * 0.15f * r, eyeY - (if (innerUp) 0.24f else 0.34f) * r)
            drawLine(ink, inner, outer, sw, StrokeCap.Round)
        }
    }

    when (p.face) {
        "happy", "proud" -> {
            drawArc(ink, 20f, 140f, false, Offset(m.x - w / 2, m.y - 0.32f * r), Size(w, 0.4f * r), style = line)
            eyes.forEach { drawCircle(Cheek.copy(alpha = 0.6f), 0.13f * r, Offset(it + (if (it < c.x + shift) -0.18f else 0.18f) * r, c.y + 0.3f * r)) }
        }
        "sad" -> drawArc(ink, 200f, 140f, false, Offset(m.x - w / 2, m.y - 0.02f * r), Size(w, 0.34f * r), style = line)
        "worried" -> {
            drawArc(ink, 205f, 130f, false, Offset(m.x - w * 0.3f, m.y), Size(w * 0.6f, 0.26f * r), style = line)
            brows(innerUp = true)
        }
        "cross" -> {
            drawLine(ink, Offset(m.x - w * 0.35f, m.y + 0.05f * r), Offset(m.x + w * 0.35f, m.y + 0.05f * r), sw, StrokeCap.Round)
            brows(innerUp = false)
        }
        "surprised" -> drawCircle(Color(0xFF5A1E1E), 0.11f * r, Offset(m.x, m.y + 0.05f * r))
        "smirk" -> drawArc(ink, 10f, 80f, false, Offset(m.x - w / 2, m.y - 0.3f * r), Size(w, 0.4f * r), style = line)
        "laugh" -> drawArc(Color(0xFF5A1E1E), 0f, 180f, true, Offset(m.x - w / 2, m.y - 0.22f * r), Size(w, 0.46f * r))
        else -> drawLine(ink, Offset(m.x - w * 0.25f, m.y + 0.05f * r), Offset(m.x + w * 0.25f, m.y + 0.05f * r), sw, StrokeCap.Round)
    }

    if (p.tears) {
        val tx = eyes[0]
        val ty = eyeY + 0.2f * r
        drawPath(Path().apply {
            moveTo(tx, ty)
            lineTo(tx - 0.09f * r, ty + 0.2f * r)
            lineTo(tx + 0.09f * r, ty + 0.2f * r)
            close()
        }, Tear)
        drawCircle(Tear, 0.1f * r, Offset(tx, ty + 0.24f * r))
    }

    if (glasses) {
        val gs = Stroke(0.06f * r)
        eyes.forEach { drawCircle(Dark, 0.22f * r, Offset(it, eyeY), style = gs) }
        drawLine(Dark, Offset(eyes[0] + 0.22f * r, eyeY), Offset(eyes[1] - 0.22f * r, eyeY), 0.06f * r)
    }
}

private fun DrawScope.drawArms(p: PanelPerson, cx: Float, bodyTop: Float, h: Float, d: Float, skin: Color, top: Color) {
    val armW = 0.075f * h
    val down = { s: Float -> Pair<Offset?, Offset>(null, Offset(cx + s * 0.23f * h, bodyTop + 0.32f * h)) }
    var itemAt: Offset? = null

    for (s in listOf(-1f, 1f)) {
        val front = s == d
        val shoulder = Offset(cx + s * 0.15f * h, bodyTop + 0.05f * h)
        val (elbow, hand) = when (p.arm) {
            "wave" -> if (front) Pair(Offset(cx + d * 0.32f * h, bodyTop + 0.02f * h), Offset(cx + d * 0.33f * h, bodyTop - 0.22f * h)) else down(s)
            "stop" -> if (front) Pair(null, Offset(cx + d * 0.46f * h, bodyTop + 0.07f * h)) else down(s)
            "point" -> if (front) Pair(null, Offset(cx + d * 0.44f * h, bodyTop - 0.06f * h)) else down(s)
            "hold" -> Pair(Offset(cx + s * 0.24f * h, bodyTop + 0.24f * h), Offset(cx + d * 0.06f * h + s * 0.035f * h, bodyTop + 0.2f * h))
            "up" -> Pair(null, Offset(cx + s * 0.30f * h, bodyTop - 0.30f * h))
            "hips" -> Pair(Offset(cx + s * 0.32f * h, bodyTop + 0.16f * h), Offset(cx + s * 0.17f * h, bodyTop + 0.30f * h))
            "give" -> if (front) Pair(null, Offset(cx + d * 0.45f * h, bodyTop + 0.16f * h)) else down(s)
            else -> down(s)
        }
        // Sleeve in top colour, then skin.
        if (elbow != null) {
            drawLine(top, shoulder, elbow, armW, StrokeCap.Round)
            drawLine(skin, elbow, hand, armW * 0.85f, StrokeCap.Round)
        } else {
            val mid = Offset(shoulder.x + (hand.x - shoulder.x) * 0.4f, shoulder.y + (hand.y - shoulder.y) * 0.4f)
            drawLine(top, shoulder, mid, armW, StrokeCap.Round)
            drawLine(skin, mid, hand, armW * 0.85f, StrokeCap.Round)
        }
        drawCircle(skin, 0.042f * h, hand)

        if (front) {
            when (p.arm) {
                "stop" -> drawRoundRect(
                    skin,
                    topLeft = Offset(hand.x - 0.03f * h + d * 0.02f * h, hand.y - 0.08f * h),
                    size = Size(0.06f * h, 0.12f * h),
                    cornerRadius = CornerRadius(0.03f * h),
                )
                "point" -> drawLine(skin, hand, Offset(hand.x + d * 0.07f * h, hand.y - 0.02f * h), 0.025f * h, StrokeCap.Round)
            }
            itemAt = when (p.arm) {
                "hold" -> Offset(cx + d * 0.07f * h, bodyTop + 0.15f * h)
                else -> Offset(hand.x + d * 0.02f * h, hand.y - 0.04f * h)
            }
        }
    }
    p.item?.let { item -> itemAt?.let { drawItem(item, it, 0.13f * h) } }
}

private fun DrawScope.drawItem(item: String, c: Offset, u: Float) {
    when (item) {
        "phone" -> {
            drawRoundRect(Dark, Offset(c.x - 0.32f * u, c.y - 0.55f * u), Size(0.64f * u, 1.1f * u), CornerRadius(0.12f * u))
            drawRect(Color(0xFF7FC8F8), Offset(c.x - 0.24f * u, c.y - 0.44f * u), Size(0.48f * u, 0.82f * u))
        }
        "lolly" -> {
            drawLine(Color.White, Offset(c.x, c.y), Offset(c.x, c.y + 0.9f * u), 0.08f * u)
            drawCircle(Color(0xFFFF6FA3), 0.42f * u, Offset(c.x, c.y - 0.2f * u))
            drawCircle(Color.White, 0.22f * u, Offset(c.x, c.y - 0.2f * u), style = Stroke(0.08f * u))
        }
        "ball" -> {
            drawCircle(Color(0xFFF08A34), 0.55f * u, c)
            drawLine(Dark, Offset(c.x - 0.55f * u, c.y), Offset(c.x + 0.55f * u, c.y), 0.05f * u)
            drawLine(Dark, Offset(c.x, c.y - 0.55f * u), Offset(c.x, c.y + 0.55f * u), 0.05f * u)
        }
        "controller" -> {
            drawRoundRect(Color(0xFF555B66), Offset(c.x - 0.6f * u, c.y - 0.3f * u), Size(1.2f * u, 0.6f * u), CornerRadius(0.25f * u))
            drawCircle(Color(0xFFE5533D), 0.08f * u, Offset(c.x + 0.3f * u, c.y - 0.05f * u))
            drawCircle(Color(0xFF3E8ED0), 0.08f * u, Offset(c.x + 0.42f * u, c.y + 0.08f * u))
            drawLine(Color.White, Offset(c.x - 0.45f * u, c.y), Offset(c.x - 0.2f * u, c.y), 0.07f * u)
        }
        "invite" -> {
            drawRect(Color.White, Offset(c.x - 0.5f * u, c.y - 0.35f * u), Size(1.0f * u, 0.7f * u))
            drawRect(Dark, Offset(c.x - 0.5f * u, c.y - 0.35f * u), Size(1.0f * u, 0.7f * u), style = Stroke(0.04f * u))
            drawRect(Color(0xFFFF8FAB), Offset(c.x - 0.5f * u, c.y - 0.12f * u), Size(1.0f * u, 0.18f * u))
        }
        "book" -> {
            drawRect(Color(0xFFC0392B), Offset(c.x - 0.45f * u, c.y - 0.55f * u), Size(0.9f * u, 1.1f * u))
            drawRect(Color.White, Offset(c.x + 0.3f * u, c.y - 0.5f * u), Size(0.1f * u, 1.0f * u))
        }
    }
}

private fun DrawScope.drawScene(scene: String, groundY: Float) {
    val w = size.width
    val hgt = size.height
    fun sky(c: Color) = drawRect(c, Offset.Zero, Size(w, groundY))
    fun ground(c: Color) = drawRect(c, Offset(0f, groundY), Size(w, hgt - groundY))
    fun tree(x: Float, scale: Float, leaf: Color) {
        drawRect(Color(0xFF8D6E63), Offset(x - 0.03f * w * scale, groundY - 0.32f * hgt * scale), Size(0.06f * w * scale, 0.32f * hgt * scale))
        drawCircle(leaf, 0.13f * hgt * scale + 0.05f * w * scale, Offset(x, groundY - 0.4f * hgt * scale))
    }

    when (scene) {
        "outside" -> {
            sky(Color(0xFFDFF2FB)); ground(Color(0xFF8BC34A))
            drawRect(Color(0xFF8BC34A), Offset(0f, groundY - 0.03f * hgt), Size(w, 0.03f * hgt))
            tree(0.9f * w, 1f, Color(0xFF66BB6A))
        }
        "school" -> {
            sky(Color(0xFFFFF4DC)); ground(Color(0xFFC8A27A))
            drawRect(Color(0xFF8D6E63), Offset(0.06f * w - 6f, groundY * 0.42f - 6f), Size(0.5f * w + 12f, groundY * 0.3f + 12f))
            drawRect(Color(0xFF2F6B4F), Offset(0.06f * w, groundY * 0.42f), Size(0.5f * w, groundY * 0.3f))
            drawLine(Color.White.copy(alpha = 0.7f), Offset(0.1f * w, groundY * 0.5f), Offset(0.3f * w, groundY * 0.5f), 3f)
            drawLine(Color.White.copy(alpha = 0.7f), Offset(0.1f * w, groundY * 0.58f), Offset(0.4f * w, groundY * 0.58f), 3f)
        }
        "shop" -> {
            sky(Color(0xFFFDEBD3)); ground(Color(0xFFBDBDBD))
            val colors = listOf(Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFFFFD54F), Color(0xFF81C784), Color(0xFFBA68C8))
            listOf(0.45f, 0.68f).forEachIndexed { row, f ->
                val y = groundY * f
                drawRect(Color(0xFF8D6E63), Offset(0f, y), Size(w, 6f))
                var x = 0.04f * w
                var i = row
                while (x < w - 0.06f * w) {
                    drawRect(colors[i % colors.size], Offset(x, y - 0.1f * groundY), Size(0.05f * w, 0.1f * groundY))
                    x += 0.075f * w; i++
                }
            }
        }
        "court" -> {
            sky(Color(0xFFE3EEF9)); ground(Color(0xFFD9A066))
            drawLine(Color(0xFFB5794A), Offset(0f, groundY + (hgt - groundY) / 2), Offset(w, groundY + (hgt - groundY) / 2), 2f)
            val bx = 0.86f * w
            drawLine(Color(0xFF616161), Offset(bx + 0.06f * w, groundY), Offset(bx + 0.06f * w, groundY * 0.4f), 6f)
            drawRect(Color.White, Offset(bx - 0.08f * w, groundY * 0.3f), Size(0.16f * w, groundY * 0.18f))
            drawRect(Dark, Offset(bx - 0.08f * w, groundY * 0.3f), Size(0.16f * w, groundY * 0.18f), style = Stroke(3f))
            drawLine(Color(0xFFF08A34), Offset(bx - 0.1f * w, groundY * 0.5f), Offset(bx + 0.02f * w, groundY * 0.5f), 5f)
        }
        "party" -> {
            sky(Color(0xFFFFF0F5)); ground(Color(0xFFD7CCC8))
            listOf(0.1f to Color(0xFFFF8FAB), 0.2f to Color(0xFFFFD54F), 0.82f to Color(0xFF64B5F6), 0.92f to Color(0xFF81C784)).forEach { (fx, col) ->
                val c = Offset(fx * w, groundY * 0.45f)
                drawLine(Dark.copy(alpha = 0.5f), c, Offset(c.x, groundY * 0.8f), 2f)
                drawOval(col, Offset(c.x - 0.035f * w, c.y - 0.06f * groundY), Size(0.07f * w, 0.12f * groundY))
            }
        }
        "home" -> {
            sky(Color(0xFFF6EEDF)); ground(Color(0xFFB98B5E))
            drawRect(Color(0xFFB3E0F7), Offset(0.07f * w, groundY * 0.38f), Size(0.24f * w, groundY * 0.3f))
            drawRect(Color.White, Offset(0.07f * w, groundY * 0.38f), Size(0.24f * w, groundY * 0.3f), style = Stroke(6f))
            drawLine(Color.White, Offset(0.19f * w, groundY * 0.38f), Offset(0.19f * w, groundY * 0.68f), 4f)
            drawRoundRect(Color(0xFF7986CB), Offset(0.7f * w, groundY * 0.72f), Size(0.28f * w, groundY * 0.28f), CornerRadius(14f))
            drawRoundRect(Color(0xFF5C6BC0), Offset(0.7f * w, groundY * 0.62f), Size(0.28f * w, groundY * 0.14f), CornerRadius(14f))
        }
        "beach" -> {
            sky(Color(0xFFCDEBFA))
            drawRect(Color(0xFF4FA3D9), Offset(0f, groundY * 0.7f), Size(w, groundY * 0.3f))
            ground(Color(0xFFF3DC9B))
            val px = 0.9f * w
            drawLine(Dark, Offset(px, groundY), Offset(px, groundY * 0.45f), 4f)
            drawRect(Color(0xFFE53935), Offset(px, groundY * 0.45f), Size(0.06f * w, groundY * 0.06f))
            drawRect(Color(0xFFFFD600), Offset(px, groundY * 0.51f), Size(0.06f * w, groundY * 0.06f))
        }
        "bush" -> {
            sky(Color(0xFFE2F1E0)); ground(Color(0xFF9CBF6B))
            tree(0.1f * w, 0.9f, Color(0xFF558B2F))
            tree(0.82f * w, 1.05f, Color(0xFF689F38))
            tree(0.95f * w, 0.8f, Color(0xFF33691E))
        }
        "screen" -> {
            sky(Color(0xFFE9ECF3)); ground(Color(0xFFB0BEC5))
            drawRoundRect(Color(0xFF37474F), Offset(0.58f * w, groundY * 0.45f), Size(0.36f * w, groundY * 0.36f), CornerRadius(10f))
            drawRect(Color(0xFF4DB6AC), Offset(0.6f * w, groundY * 0.48f), Size(0.32f * w, groundY * 0.3f))
            drawRect(Color(0xFF37474F), Offset(0.74f * w, groundY * 0.81f), Size(0.04f * w, groundY * 0.19f))
        }
        else -> {
            sky(BookColors.Cream); ground(Color(0xFFEDE3CF))
        }
    }
}
