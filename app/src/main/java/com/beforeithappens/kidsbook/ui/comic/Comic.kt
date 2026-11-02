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
import com.beforeithappens.kidsbook.ui.theme.HandStyle
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.sp
import com.beforeithappens.kidsbook.ui.theme.parseHex
import androidx.compose.material3.MaterialTheme

private val Dark = Color(0xFF262626)
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
            .clip(RoundedCornerShape(6.dp))
            .border(2.5.dp, Dark, RoundedCornerShape(6.dp))
            .clickable(onClickLabel = "hear this panel", onClick = onTap)
            .clearAndSetSemantics {
                contentDescription = description
                role = Role.Button
            },
    ) {
        // Caption bar: a pastel strip in the section colour, like the printed journal.
        Row(
            Modifier
                .fillMaxWidth()
                .background(lerp(Color.White, partColor, 0.32f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(28.dp).border(1.5.dp, Dark, CircleShape).background(Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("$number", color = Dark, style = HandStyle.copy(fontSize = 18.sp, lineHeight = 20.sp))
            }
            Spacer(Modifier.size(10.dp))
            Text(panel.caption, color = Dark, style = HandStyle)
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val panelWidth = maxWidth
            Canvas(Modifier.matchParentSize()) {
                val groundY = size.height - GroundInset.toPx()
                drawScene(panel.scene, groundY, density)
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
        drawScene(panel.scene, groundY, density * 0.5f)
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
    background: Color = Color(0xFFEAF4F8),
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
                    style = HandStyle,
                    modifier = Modifier
                        .background(Color(0xFF263238), RoundedCornerShape(6.dp))
                        .border(2.dp, Color(0xFF80CBC4), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            } else {
                val think = speech.kind == "think"
                Text(
                    speech.text,
                    style = HandStyle,
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
/* Drawing: line-drawn journal style (ink outlines, pastel colour)      */
/* ------------------------------------------------------------------ */

private fun DrawScope.drawPeople(content: BookContent, people: List<PanelPerson>, groundY: Float, kidHeight: Float) {
    people.forEach { p ->
        val spec = content.character(p.who) ?: return@forEach
        drawCharacter(spec, p, p.x.coerceIn(0.08f, 0.92f) * size.width, groundY, kidHeight)
    }
}

/** Fills a shape, then outlines it in ink, like a pen drawing coloured in. */
private inline fun DrawScope.inked(stroke: Float, fill: () -> Unit, outline: (Stroke) -> Unit) {
    fill()
    outline(Stroke(stroke, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
}

fun DrawScope.drawCharacter(spec: CharacterSpec, p: PanelPerson, cx: Float, groundY: Float, kidHeight: Float) {
    val h = kidHeight * spec.height
    val d = if (p.facingLeft) -1f else 1f
    val ink = (h * 0.018f).coerceAtLeast(1.4f)
    val skin = parseHex(spec.skin)
    val hair = parseHex(spec.hair)
    val top = parseHex(spec.top)
    val legs = parseHex(spec.legs)

    val bodyTop = groundY - 0.66f * h
    val r = 0.17f * h
    val head = Offset(cx, bodyTop - 0.14f * h)

    // Hair that sits behind the head.
    when (spec.hairStyle) {
        "long" -> inked(ink, {
            drawRoundRect(hair, Offset(head.x - 1.12f * r, head.y - 0.95f * r), Size(2.24f * r, 2.5f * r), CornerRadius(0.6f * r))
        }) { drawRoundRect(Dark, Offset(head.x - 1.12f * r, head.y - 0.95f * r), Size(2.24f * r, 2.5f * r), CornerRadius(0.6f * r), style = it) }
        "ponytail" -> inked(ink, {
            drawOval(hair, Offset(head.x - d * 1.05f * r - 0.38f * r, head.y - 0.55f * r), Size(0.76f * r, 1.35f * r))
        }) { drawOval(Dark, Offset(head.x - d * 1.05f * r - 0.38f * r, head.y - 0.55f * r), Size(0.76f * r, 1.35f * r), style = it) }
        "puffs" -> listOf(-1f, 1f).forEach { side ->
            val c = Offset(head.x + side * 0.82f * r, head.y - 0.82f * r)
            drawCircle(hair, 0.46f * r, c)
            drawCircle(Dark, 0.46f * r, c, style = Stroke(ink))
        }
    }

    // Legs: outlined rounded strips, with shoes.
    val legTop = groundY - 0.32f * h
    val legW = 0.1f * h
    for (side in listOf(-1f, 1f)) {
        val lx = cx + side * 0.08f * h - legW / 2
        drawRoundRect(legs, Offset(lx, legTop), Size(legW, 0.28f * h), CornerRadius(legW * 0.3f))
        drawRoundRect(Dark, Offset(lx, legTop), Size(legW, 0.28f * h), CornerRadius(legW * 0.3f), style = Stroke(ink))
        val shoe = Offset(lx - 0.02f * h + d * 0.015f * h, groundY - 0.055f * h)
        drawRoundRect(Color.White, shoe, Size(legW + 0.04f * h, 0.055f * h), CornerRadius(0.02f * h))
        drawRoundRect(Dark, shoe, Size(legW + 0.04f * h, 0.055f * h), CornerRadius(0.02f * h), style = Stroke(ink))
    }

    // Body (top), with the journal's details: pocket, stripes or a sports number.
    val bodyLeft = cx - 0.18f * h
    val bodySize = Size(0.36f * h, 0.36f * h)
    drawRoundRect(top, Offset(bodyLeft, bodyTop), bodySize, CornerRadius(0.06f * h))
    spec.stripes?.let { st ->
        val c = parseHex(st)
        for (k in 1..4) {
            val y = bodyTop + bodySize.height * k / 5f
            drawLine(c, Offset(bodyLeft + ink, y), Offset(bodyLeft + bodySize.width - ink, y), ink * 1.2f)
        }
    }
    spec.pocket?.let { pk ->
        val pw = 0.13f * h
        val py = bodyTop + 0.2f * h
        val path = Path().apply {
            moveTo(cx - pw / 2, py); lineTo(cx + pw / 2, py)
            lineTo(cx + pw * 0.4f, py + 0.08f * h); lineTo(cx - pw * 0.4f, py + 0.08f * h); close()
        }
        drawPath(path, parseHex(pk)); drawPath(path, Dark, style = Stroke(ink * 0.8f))
    }
    drawRoundRect(Dark, Offset(bodyLeft, bodyTop), bodySize, CornerRadius(0.06f * h), style = Stroke(ink))
    // Collar line.
    drawArc(Dark, 20f, 140f, false, Offset(cx - 0.06f * h, bodyTop - 0.03f * h), Size(0.12f * h, 0.06f * h), style = Stroke(ink * 0.8f))

    // Head.
    drawCircle(skin, r, head)
    drawCircle(Dark, r, head, style = Stroke(ink))

    // Hair on top.
    when (spec.hairStyle) {
        "cap" -> {
            val cap = Path().apply {
                addArc(androidx.compose.ui.geometry.Rect(head.x - 1.04f * r, head.y - 1.04f * r, head.x + 1.04f * r, head.y + 1.04f * r), 190f, 160f)
                close()
            }
            drawPath(cap, hair)
            // Brim pointing the way they face.
            drawRoundRect(hair, Offset(head.x + (if (d > 0) 0.2f * r else -1.25f * r), head.y - 0.42f * r), Size(1.05f * r, 0.18f * r), CornerRadius(0.08f * r))
        }
        else -> {
            drawArc(hair, 198f, 144f, false, Offset(head.x - 1.06f * r, head.y - 1.06f * r), Size(2.12f * r, 2.12f * r))
            drawArc(Dark, 198f, 144f, false, Offset(head.x - 1.06f * r, head.y - 1.06f * r), Size(2.12f * r, 2.12f * r), style = Stroke(ink))
            if (spec.hairStyle == "messy" || spec.hairStyle == "spiky") {
                // Scribbly tufts and hatching, like the journal drawings.
                listOf(-0.65f, -0.3f, 0.05f, 0.4f, 0.7f).forEachIndexed { i, o ->
                    val bx = head.x + o * r
                    val by = head.y - 0.86f * r + kotlin.math.abs(o) * 0.25f * r
                    val tip = Offset(bx + (if (i % 2 == 0) 0.12f else -0.08f) * r, by - 0.32f * r)
                    val tuft = Path().apply { moveTo(bx - 0.18f * r, by + 0.05f * r); lineTo(tip.x, tip.y); lineTo(bx + 0.18f * r, by + 0.05f * r); close() }
                    drawPath(tuft, hair); drawPath(tuft, Dark, style = Stroke(ink * 0.8f))
                }
                for (k in -3..3) {
                    val x = head.x + k * 0.22f * r
                    drawLine(Dark.copy(alpha = 0.55f), Offset(x - 0.1f * r, head.y - 0.45f * r), Offset(x + 0.12f * r, head.y - 0.8f * r), ink * 0.6f)
                }
            }
        }
    }

    drawFace(p, head, r, d, spec.glasses, spec.freckles, ink)
    drawArms(p, cx, bodyTop, h, d, skin, top, ink)

    // Sports number on the front of the top (drawn after the arms so it stays visible).
    spec.number?.let { n ->
        val nh = 0.12f * h
        val x0 = cx - nh * 0.3f
        val y0 = bodyTop + 0.11f * h
        if (n == "7") {
            drawLine(Dark, Offset(x0, y0), Offset(x0 + nh * 0.6f, y0), ink * 1.4f, StrokeCap.Round)
            drawLine(Dark, Offset(x0 + nh * 0.6f, y0), Offset(x0 + nh * 0.15f, y0 + nh), ink * 1.4f, StrokeCap.Round)
        }
    }
}

private fun DrawScope.drawFace(p: PanelPerson, c: Offset, r: Float, d: Float, glasses: Boolean, freckles: Boolean, ink: Float) {
    val shift = d * 0.1f * r
    val eyeY = c.y + 0.02f * r
    val eyes = listOf(c.x + shift - 0.34f * r, c.x + shift + 0.34f * r)
    val eyeR = if (p.face == "surprised") 0.12f * r else 0.075f * r
    if (p.face == "surprised") eyes.forEach { drawCircle(Dark, eyeR * 1.6f, Offset(it, eyeY), style = Stroke(ink * 0.7f)) }
    eyes.forEach { drawCircle(Dark, eyeR, Offset(it, eyeY)) }

    val m = Offset(c.x + shift, c.y + 0.45f * r)
    val w = 0.5f * r
    val line = Stroke(ink * 0.9f, cap = StrokeCap.Round)

    fun brows(innerUp: Boolean) {
        eyes.forEachIndexed { i, ex ->
            val innerSide = if (i == 0) 1f else -1f
            val inner = Offset(ex + innerSide * 0.15f * r, eyeY - (if (innerUp) 0.36f else 0.2f) * r)
            val outer = Offset(ex - innerSide * 0.15f * r, eyeY - (if (innerUp) 0.24f else 0.34f) * r)
            drawLine(Dark, inner, outer, ink * 0.9f, StrokeCap.Round)
        }
    }

    when (p.face) {
        "happy", "proud" -> {
            drawArc(Dark, 20f, 140f, false, Offset(m.x - w / 2, m.y - 0.32f * r), Size(w, 0.4f * r), style = line)
            eyes.forEach { drawCircle(Cheek.copy(alpha = 0.5f), 0.12f * r, Offset(it + (if (it < c.x + shift) -0.16f else 0.16f) * r, c.y + 0.3f * r)) }
        }
        "sad" -> drawArc(Dark, 200f, 140f, false, Offset(m.x - w / 2, m.y - 0.02f * r), Size(w, 0.34f * r), style = line)
        "worried" -> {
            drawArc(Dark, 205f, 130f, false, Offset(m.x - w * 0.3f, m.y), Size(w * 0.6f, 0.26f * r), style = line)
            brows(innerUp = true)
        }
        "cross" -> {
            drawLine(Dark, Offset(m.x - w * 0.35f, m.y + 0.05f * r), Offset(m.x + w * 0.35f, m.y + 0.05f * r), ink, StrokeCap.Round)
            brows(innerUp = false)
        }
        "surprised" -> drawCircle(Dark, 0.1f * r, Offset(m.x, m.y + 0.05f * r), style = line)
        "smirk" -> drawArc(Dark, 10f, 80f, false, Offset(m.x - w / 2, m.y - 0.3f * r), Size(w, 0.4f * r), style = line)
        "laugh" -> {
            drawArc(Color(0xFF7A2E2E), 0f, 180f, true, Offset(m.x - w / 2, m.y - 0.22f * r), Size(w, 0.46f * r))
            drawArc(Dark, 0f, 180f, true, Offset(m.x - w / 2, m.y - 0.22f * r), Size(w, 0.46f * r), style = line)
        }
        else -> drawLine(Dark, Offset(m.x - w * 0.25f, m.y + 0.05f * r), Offset(m.x + w * 0.25f, m.y + 0.05f * r), ink * 0.9f, StrokeCap.Round)
    }

    if (freckles) {
        listOf(-0.5f, -0.38f, 0.38f, 0.5f).forEach { fx ->
            drawCircle(Color(0xFFC07A4A), 0.035f * r, Offset(c.x + shift + fx * r, c.y + 0.24f * r))
        }
    }

    if (p.tears) {
        val tx = eyes[0]
        val ty = eyeY + 0.2f * r
        drawPath(Path().apply {
            moveTo(tx, ty); lineTo(tx - 0.09f * r, ty + 0.2f * r); lineTo(tx + 0.09f * r, ty + 0.2f * r); close()
        }, Tear)
        drawCircle(Tear, 0.1f * r, Offset(tx, ty + 0.24f * r))
    }

    if (glasses) {
        eyes.forEach { drawRoundRect(Dark, Offset(it - 0.21f * r, eyeY - 0.16f * r), Size(0.42f * r, 0.32f * r), CornerRadius(0.08f * r), style = Stroke(ink * 0.9f)) }
        drawLine(Dark, Offset(eyes[0] + 0.21f * r, eyeY), Offset(eyes[1] - 0.21f * r, eyeY), ink * 0.9f)
    }
}

private fun DrawScope.drawArms(p: PanelPerson, cx: Float, bodyTop: Float, h: Float, d: Float, skin: Color, top: Color, ink: Float) {
    val armW = 0.06f * h
    val down = { s: Float -> Pair<Offset?, Offset>(null, Offset(cx + s * 0.23f * h, bodyTop + 0.32f * h)) }
    var itemAt: Offset? = null

    fun inkLine(color: Color, a: Offset, b: Offset, width: Float) {
        drawLine(Dark, a, b, width + ink * 2f, StrokeCap.Round)
        drawLine(color, a, b, width, StrokeCap.Round)
    }

    for (s in listOf(-1f, 1f)) {
        val front = s == d
        val shoulder = Offset(cx + s * 0.15f * h, bodyTop + 0.05f * h)
        val (elbow, hand) = when (p.arm) {
            "wave" -> if (front) Pair(Offset(cx + d * 0.32f * h, bodyTop + 0.02f * h), Offset(cx + d * 0.33f * h, bodyTop - 0.22f * h)) else down(s)
            "stop" -> if (front) Pair(null, Offset(cx + d * 0.46f * h, bodyTop + 0.07f * h)) else down(s)
            "point" -> if (front) Pair(null, Offset(cx + d * 0.44f * h, bodyTop - 0.02f * h)) else down(s)
            "hold" -> Pair(Offset(cx + s * 0.24f * h, bodyTop + 0.24f * h), Offset(cx + d * 0.06f * h + s * 0.035f * h, bodyTop + 0.2f * h))
            "up" -> Pair(null, Offset(cx + s * 0.30f * h, bodyTop - 0.30f * h))
            "hips" -> Pair(Offset(cx + s * 0.3f * h, bodyTop + 0.1f * h), Offset(cx + s * 0.38f * h, bodyTop + 0.02f * h))
            "give" -> if (front) Pair(null, Offset(cx + d * 0.45f * h, bodyTop + 0.16f * h)) else down(s)
            else -> down(s)
        }
        if (elbow != null) {
            inkLine(top, shoulder, elbow, armW)
            inkLine(skin, elbow, hand, armW * 0.8f)
        } else {
            val mid = Offset(shoulder.x + (hand.x - shoulder.x) * 0.35f, shoulder.y + (hand.y - shoulder.y) * 0.35f)
            inkLine(top, shoulder, mid, armW)
            inkLine(skin, mid, hand, armW * 0.8f)
        }
        drawCircle(skin, 0.04f * h, hand)
        drawCircle(Dark, 0.04f * h, hand, style = Stroke(ink))

        if (front) {
            when (p.arm) {
                "stop" -> {
                    drawRoundRect(skin, Offset(hand.x - 0.03f * h + d * 0.02f * h, hand.y - 0.08f * h), Size(0.06f * h, 0.12f * h), CornerRadius(0.03f * h))
                    drawRoundRect(Dark, Offset(hand.x - 0.03f * h + d * 0.02f * h, hand.y - 0.08f * h), Size(0.06f * h, 0.12f * h), CornerRadius(0.03f * h), style = Stroke(ink))
                }
                "point" -> drawLine(Dark, hand, Offset(hand.x + d * 0.07f * h, hand.y - 0.02f * h), ink * 1.2f, StrokeCap.Round)
            }
            itemAt = when (p.arm) {
                "hold" -> Offset(cx + d * 0.07f * h, bodyTop + 0.15f * h)
                else -> Offset(hand.x + d * 0.02f * h, hand.y - 0.04f * h)
            }
        }
    }
    p.item?.let { item -> itemAt?.let { drawItem(item, it, 0.13f * h, ink) } }
}

private fun DrawScope.drawItem(item: String, c: Offset, u: Float, ink: Float) {
    val o = Stroke(ink)
    when (item) {
        "phone" -> {
            drawRoundRect(Color.White, Offset(c.x - 0.32f * u, c.y - 0.55f * u), Size(0.64f * u, 1.1f * u), CornerRadius(0.12f * u))
            drawRoundRect(Dark, Offset(c.x - 0.32f * u, c.y - 0.55f * u), Size(0.64f * u, 1.1f * u), CornerRadius(0.12f * u), style = o)
            drawRect(Color(0xFFBFE3F5), Offset(c.x - 0.22f * u, c.y - 0.42f * u), Size(0.44f * u, 0.8f * u))
        }
        "lolly" -> {
            drawLine(Dark, Offset(c.x, c.y), Offset(c.x, c.y + 0.9f * u), ink)
            drawCircle(Color(0xFFFF9EC0), 0.4f * u, Offset(c.x, c.y - 0.2f * u))
            drawCircle(Dark, 0.4f * u, Offset(c.x, c.y - 0.2f * u), style = o)
        }
        "ball" -> {
            drawCircle(Color(0xFFF2A65A), 0.55f * u, c)
            drawCircle(Dark, 0.55f * u, c, style = o)
            drawLine(Dark, Offset(c.x - 0.55f * u, c.y), Offset(c.x + 0.55f * u, c.y), ink * 0.7f)
            drawLine(Dark, Offset(c.x, c.y - 0.55f * u), Offset(c.x, c.y + 0.55f * u), ink * 0.7f)
        }
        "controller" -> {
            drawRoundRect(Color.White, Offset(c.x - 0.6f * u, c.y - 0.3f * u), Size(1.2f * u, 0.6f * u), CornerRadius(0.25f * u))
            drawRoundRect(Dark, Offset(c.x - 0.6f * u, c.y - 0.3f * u), Size(1.2f * u, 0.6f * u), CornerRadius(0.25f * u), style = o)
            drawCircle(Dark, 0.07f * u, Offset(c.x + 0.3f * u, c.y))
            drawLine(Dark, Offset(c.x - 0.45f * u, c.y), Offset(c.x - 0.2f * u, c.y), ink)
        }
        "invite" -> {
            drawRect(Color.White, Offset(c.x - 0.5f * u, c.y - 0.35f * u), Size(1.0f * u, 0.7f * u))
            drawRect(Color(0xFFFFC2D4), Offset(c.x - 0.5f * u, c.y - 0.1f * u), Size(1.0f * u, 0.18f * u))
            drawRect(Dark, Offset(c.x - 0.5f * u, c.y - 0.35f * u), Size(1.0f * u, 0.7f * u), style = o)
        }
        "book" -> {
            drawRect(Color.White, Offset(c.x - 0.45f * u, c.y - 0.55f * u), Size(0.9f * u, 1.1f * u))
            drawRect(Dark, Offset(c.x - 0.45f * u, c.y - 0.55f * u), Size(0.9f * u, 1.1f * u), style = o)
            for (k in 1..3) drawLine(Dark.copy(alpha = 0.5f), Offset(c.x - 0.3f * u, c.y - 0.55f * u + k * 0.27f * u), Offset(c.x + 0.3f * u, c.y - 0.55f * u + k * 0.27f * u), ink * 0.6f)
        }
        "money" -> {
            drawRect(Color(0xFFB8E0B0), Offset(c.x - 0.55f * u, c.y - 0.28f * u), Size(1.1f * u, 0.56f * u))
            drawRect(Dark, Offset(c.x - 0.55f * u, c.y - 0.28f * u), Size(1.1f * u, 0.56f * u), style = o)
            drawCircle(Dark, 0.14f * u, c, style = Stroke(ink * 0.7f))
        }
    }
}

/** Pale, outlined props on white paper, like the journal comics. */
private fun DrawScope.drawScene(scene: String, groundY: Float, px: Float) {
    val w = size.width
    val hgt = size.height
    val ink = 1.4f * px
    val o = Stroke(ink)
    val green = Color(0xFFE2F2DF)
    val blue = Color(0xFFE3F1FB)
    drawRect(Color.White, Offset.Zero, size)

    fun bush(x: Float, scale: Float) {
        val r = 0.07f * w * scale + 10f * px
        val c = Offset(x, groundY - r * 2.2f)
        drawLine(Dark, Offset(x, groundY), Offset(x, c.y), ink)
        val path = Path()
        for (k in 0 until 14) {
            val a = Math.toRadians(k * 360.0 / 14)
            val rr = if (k % 2 == 0) r else r * 0.8f
            val pt = Offset(c.x + (rr * Math.cos(a)).toFloat(), c.y + (rr * Math.sin(a)).toFloat())
            if (k == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
        }
        path.close()
        drawPath(path, green); drawPath(path, Dark, style = o)
    }
    fun box(x: Float, y: Float, bw: Float, bh: Float, fill: Color) {
        drawRect(fill, Offset(x, y), Size(bw, bh)); drawRect(Dark, Offset(x, y), Size(bw, bh), style = o)
    }
    fun window(x: Float, y: Float, s: Float) {
        box(x, y, s, s, Color.White)
        drawLine(Dark, Offset(x + s / 2, y), Offset(x + s / 2, y + s), ink)
        drawLine(Dark, Offset(x, y + s / 2), Offset(x + s, y + s / 2), ink)
    }

    when (scene) {
        "outside" -> bush(0.88f * w, 1f)
        "school" -> {
            box(0.04f * w, groundY - 0.36f * groundY, 0.34f * w, 0.18f * groundY, green)
            for (k in 1..2) drawLine(Dark.copy(alpha = 0.5f), Offset(0.07f * w, groundY - 0.36f * groundY + k * 0.06f * groundY), Offset(0.3f * w, groundY - 0.36f * groundY + k * 0.06f * groundY), ink * 0.7f)
        }
        "shop" -> {
            listOf(0.5f, 0.68f, 0.86f).forEach { f ->
                val y = groundY * f
                drawLine(Dark, Offset(0.03f * w, y), Offset(0.38f * w, y), ink)
                var x = 0.05f * w
                while (x < 0.34f * w) { box(x, y - 0.08f * groundY, 0.045f * w, 0.08f * groundY, Color(0xFFFFF0C2)); x += 0.07f * w }
            }
        }
        "canteen" -> {
            box(0.02f * w, groundY - 0.2f * groundY, 0.42f * w, 0.2f * groundY, Color.White)
            drawLine(Dark, Offset(0.02f * w, groundY - 0.33f * groundY), Offset(0.44f * w, groundY - 0.33f * groundY), ink)
            drawContext.canvas.nativeCanvas.drawText("CANTEEN", 0.05f * w, groundY - 0.35f * groundY,
                android.graphics.Paint().apply { color = android.graphics.Color.rgb(43, 43, 43); textSize = 10f * px; isAntiAlias = true })
        }
        "court" -> {
            val bx = 0.86f * w
            drawLine(Dark, Offset(bx + 0.04f * w, groundY), Offset(bx + 0.04f * w, groundY * 0.45f), ink * 1.5f)
            box(bx - 0.05f * w, groundY * 0.4f, 0.12f * w, groundY * 0.12f, Color.White)
            drawOval(Dark, Offset(bx - 0.07f * w, groundY * 0.54f), Size(0.07f * w, 0.025f * groundY), style = o)
        }
        "home" -> {
            box(0.03f * w, groundY - 0.1f * groundY, 0.14f * w, 0.1f * groundY, Color.White)
            window(0.62f * w, groundY * 0.45f, 0.16f * w)
        }
        "screen" -> {
            box(0.6f * w, groundY * 0.5f, 0.3f * w, 0.26f * groundY, blue)
            drawLine(Dark, Offset(0.75f * w, groundY * 0.76f), Offset(0.75f * w, groundY), ink)
        }
        "car" -> {
            box(0.05f * w, groundY - 0.12f * groundY, 0.9f * w, 0.12f * groundY, blue)
            window(0.8f * w, groundY * 0.4f, 0.13f * w)
        }
        "bush" -> {
            bush(0.1f * w, 0.9f); bush(0.88f * w, 1f); bush(0.32f * w, 0.6f)
        }
        "beach" -> {
            drawLine(Color(0xFF7FB8E0), Offset(0f, groundY * 0.75f), Offset(w, groundY * 0.75f), ink * 2)
        }
        "party" -> listOf(0.1f, 0.88f).forEach { fx ->
            drawLine(Dark, Offset(fx * w, groundY * 0.45f), Offset(fx * w, groundY * 0.8f), ink)
            drawOval(Color(0xFFFFC2D4), Offset(fx * w - 0.03f * w, groundY * 0.33f), Size(0.06f * w, 0.12f * groundY))
            drawOval(Dark, Offset(fx * w - 0.03f * w, groundY * 0.33f), Size(0.06f * w, 0.12f * groundY), style = o)
        }
    }
    // Ground line with a few grass ticks.
    drawLine(Dark, Offset(0f, groundY), Offset(w, groundY), ink)
    if (scene == "outside" || scene == "bush") {
        var x = 0.05f * w
        while (x < w) { drawLine(Dark, Offset(x, groundY), Offset(x + 2f * px, groundY - 5f * px), ink * 0.7f); x += 0.09f * w }
    }
}
