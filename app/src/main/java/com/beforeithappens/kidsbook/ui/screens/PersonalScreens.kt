package com.beforeithappens.kidsbook.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beforeithappens.kidsbook.BookViewModel
import com.beforeithappens.kidsbook.data.Bests
import com.beforeithappens.kidsbook.data.BookContent
import com.beforeithappens.kidsbook.data.HandPerson
import com.beforeithappens.kidsbook.data.ScoreEntry
import com.beforeithappens.kidsbook.data.Skill
import com.beforeithappens.kidsbook.data.UserData
import com.beforeithappens.kidsbook.ui.Nav
import com.beforeithappens.kidsbook.ui.components.BigButton
import com.beforeithappens.kidsbook.ui.components.KidScaffold
import com.beforeithappens.kidsbook.ui.components.PinGate
import com.beforeithappens.kidsbook.ui.components.ReadToMeButton
import com.beforeithappens.kidsbook.ui.components.ScreenColumn
import com.beforeithappens.kidsbook.ui.components.SectionCard
import com.beforeithappens.kidsbook.ui.theme.BookColors
import com.beforeithappens.kidsbook.ui.theme.FeelingsDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dayFormat = DateTimeFormatter.ofPattern("d MMM yyyy")

/** A text field that keeps its own state and saves every change. */
@Composable
private fun SavedField(
    key: String,
    initial: String,
    label: String,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLength: Int = 200,
) {
    var text by remember(key) { mutableStateOf(initial) }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it.take(maxLength); onSave(text) },
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 2,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
    )
}

/* ---------------- My Safety Hand ---------------- */

@Composable
fun SafetyHandScreen(content: BookContent, user: UserData, update: ((UserData) -> UserData) -> Unit, nav: Nav) {
    val intro = content.book.activityIntros.safetyHand
    val hand = user.safetyHand.let { if (it.size >= 5) it else it + List(5 - it.size) { HandPerson() } }

    fun save(i: Int, person: HandPerson) = update { u ->
        val list = u.safetyHand.toMutableList()
        while (list.size < 5) list.add(HandPerson())
        list[i] = person
        u.copy(safetyHand = list)
    }

    KidScaffold(title = intro.title, color = BookColors.Safe, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text(intro.text, style = MaterialTheme.typography.bodyLarge)
            HandDrawing(hand.map { it.name })
            hand.forEachIndexed { i, person ->
                SectionCard("Finger ${i + 1}") {
                    SavedField("hand-name-$i", person.name, "Name", { save(i, hand[i].copy(name = it)) })
                    SavedField("hand-phone-$i", person.phone, "Phone (optional)", { save(i, hand[i].copy(phone = it)) },
                        keyboardType = KeyboardType.Phone)
                }
            }
            Text("These stay on this device only.", style = MaterialTheme.typography.bodySmall, color = BookColors.Muted)
        }
    }
}

@Composable
private fun HandDrawing(names: List<String>) {
    val measurer = rememberTextMeasurer()
    val skin = Color(0xFFF6D3BC)
    val outline = BookColors.Safe
    val described = names.mapIndexed { i, n -> "Finger ${i + 1}: ${n.ifBlank { "empty" }}" }.joinToString(". ")
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .clearAndSetSemantics { contentDescription = "My Safety Hand. $described" },
    ) {
        val cx = size.width / 2
        val palmTop = size.height * 0.52f
        val palmW = size.height * 0.42f
        val palmH = size.height * 0.4f
        val fingerW = palmW / 4.6f
        // Thumb (finger 1) then four fingers (2 to 5).
        val fingers = listOf(
            Triple(cx - palmW / 2 - fingerW * 0.2f, palmTop + palmH * 0.1f, -40f),
            Triple(cx - palmW * 0.36f, palmTop, -8f),
            Triple(cx - palmW * 0.12f, palmTop, -2f),
            Triple(cx + palmW * 0.12f, palmTop, 2f),
            Triple(cx + palmW * 0.36f, palmTop, 8f),
        )
        val lengths = listOf(0.55f, 0.9f, 1.0f, 0.95f, 0.75f)
        fingers.forEachIndexed { i, (fx, fy, angle) ->
            val len = palmH * lengths[i]
            rotate(angle, Offset(fx, fy + fingerW / 2)) {
                drawRoundRect(skin, Offset(fx - fingerW / 2, fy - len), Size(fingerW, len + fingerW), CornerRadius(fingerW / 2))
                drawRoundRect(outline, Offset(fx - fingerW / 2, fy - len), Size(fingerW, len + fingerW), CornerRadius(fingerW / 2), style = Stroke(3f))
                val label = measurer.measure("${i + 1}", TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = outline))
                drawText(label, topLeft = Offset(fx - label.size.width / 2f, fy - len + fingerW * 0.25f))
            }
        }
        drawRoundRect(skin, Offset(cx - palmW / 2, palmTop), Size(palmW, palmH), CornerRadius(palmW * 0.3f))
        drawRoundRect(outline, Offset(cx - palmW / 2, palmTop), Size(palmW, palmH), CornerRadius(palmW * 0.3f), style = Stroke(3f))
        val count = names.count { it.isNotBlank() }
        val label = measurer.measure("$count of 5", TextStyle(fontSize = 14.sp, color = BookColors.Ink, fontWeight = FontWeight.Medium))
        drawText(label, topLeft = Offset(cx - label.size.width / 2f, palmTop + palmH / 2 - label.size.height / 2f))
        // Names beside the hand.
        names.forEachIndexed { i, n ->
            if (n.isBlank()) return@forEachIndexed
            val t = measurer.measure("${i + 1}. $n", TextStyle(fontSize = 14.sp, color = BookColors.Ink), maxLines = 1,
                constraints = androidx.compose.ui.unit.Constraints(maxWidth = (size.width * 0.28f).toInt().coerceAtLeast(1)))
            val left = i < 2
            val x = if (left) 8f else size.width - t.size.width - 8f
            val y = if (left) 16f + i * 28.dp.toPx() else 16f + (i - 2) * 28.dp.toPx()
            drawText(t, topLeft = Offset(x, y))
        }
    }
}

/* ---------------- Things I Know By Heart (PIN) ---------------- */

@Composable
fun KnowByHeartScreen(content: BookContent, user: UserData, vm: BookViewModel, nav: Nav) {
    val intro = content.book.activityIntros.knowByHeart
    val prompts = content.stories.activities.knowByHeart
    var practise by rememberSaveable { mutableStateOf(false) }
    val revealed = remember { mutableStateListOf<Int>() }

    KidScaffold(title = intro.title, color = BookColors.Safe, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        Box(Modifier.padding(pad)) {
            PinGate(
                hasPin = user.hasPin,
                unlocked = vm.unlocked,
                checkPin = vm::checkPin,
                createPin = vm::createPin,
                onUnlocked = vm::unlock,
            ) {
                ScreenColumn(androidx.compose.foundation.layout.PaddingValues()) {
                    Text(intro.text, style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = !practise, onClick = { practise = false }, label = { Text("Fill in") },
                            modifier = Modifier.heightIn(min = 48.dp))
                        FilterChip(selected = practise, onClick = { practise = true; revealed.clear() }, label = { Text("Practise") },
                            modifier = Modifier.heightIn(min = 48.dp))
                    }
                    prompts.forEachIndexed { i, prompt ->
                        val isField = prompt.trim().endsWith(":")
                        val answer = user.knowByHeart[i].orEmpty()
                        when {
                            !isField -> SectionCard(background = BookColors.TryIt) {
                                Text(prompt, style = MaterialTheme.typography.titleSmall)
                            }
                            !practise -> SavedField("kbh-$i", answer, prompt.removeSuffix(":"), { v ->
                                vm.update { it.copy(knowByHeart = it.knowByHeart + (i to v)) }
                            })
                            else -> {
                                val show = i in revealed
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 72.dp)
                                        .background(if (show) BookColors.Magic else Color.White, RoundedCornerShape(16.dp))
                                        .border(2.dp, BookColors.Safe, RoundedCornerShape(16.dp))
                                        .clickable(role = Role.Button, onClickLabel = if (show) "hide answer" else "show answer") {
                                            if (show) revealed.remove(i) else revealed.add(i)
                                        }
                                        .padding(14.dp),
                                ) {
                                    Text(prompt, style = MaterialTheme.typography.titleSmall)
                                    if (show) {
                                        Text(answer.ifBlank { "(Not filled in yet)" }, style = MaterialTheme.typography.titleLarge)
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Visibility, contentDescription = null, tint = BookColors.Muted)
                                            Spacer(Modifier.width(6.dp))
                                            Text("Say it, then tap to check", style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    intro.password?.let {
                        SectionCard(background = BookColors.Magic) { Text(it, style = MaterialTheme.typography.bodyLarge) }
                    }
                    Text("These stay on this device only, behind the grown-up PIN.",
                        style = MaterialTheme.typography.bodySmall, color = BookColors.Muted)
                }
            }
        }
    }
}

/* ---------------- My Personal Bests ---------------- */

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalBestsScreen(content: BookContent, user: UserData, update: ((UserData) -> UserData) -> Unit, nav: Nav) {
    val intro = content.book.activityIntros.personalBests
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var newSkill by rememberSaveable { mutableStateOf("") }
    var score by rememberSaveable { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }
    var celebrate by remember { mutableStateOf<String?>(null) }
    val skills = user.bests
    val skillIndex = selected.coerceIn(0, (skills.size - 1).coerceAtLeast(0))
    val ideas = listOf("Free throws out of 10", "Skips in a minute", "Times tables in a minute", "Spelling words", "Pages read", "Laps of the oval")

    fun addSkill(name: String) {
        val n = name.trim()
        if (n.isEmpty()) return
        update { it.copy(bests = it.bests + Skill(n)) }
        selected = skills.size
        newSkill = ""
    }

    KidScaffold(title = intro.title, color = FeelingsDark, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text(intro.text, style = MaterialTheme.typography.bodyLarge)

            if (skills.isNotEmpty()) {
                Text("My skills", style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    skills.forEachIndexed { i, s ->
                        FilterChip(selected = i == skillIndex, onClick = { selected = i }, label = { Text(s.name) },
                            modifier = Modifier.heightIn(min = 48.dp))
                    }
                }
            }

            SectionCard("Add a skill") {
                OutlinedTextField(value = newSkill, onValueChange = { newSkill = it.take(40) }, label = { Text("My skill") },
                    singleLine = true, modifier = Modifier.fillMaxWidth())
                BigButton("Add skill", { addSkill(newSkill) }, color = FeelingsDark, enabled = newSkill.isNotBlank(),
                    modifier = Modifier.fillMaxWidth())
                intro.ideas?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted) }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ideas.filter { idea -> skills.none { it.name.equals(idea, true) } }.forEach { idea ->
                        AssistChip(onClick = { addSkill(idea) }, label = { Text(idea) }, modifier = Modifier.heightIn(min = 48.dp))
                    }
                }
            }

            skills.getOrNull(skillIndex)?.let { skill ->
                val bests = Bests.newBestIndices(skill.entries)
                SectionCard(skill.name) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = score,
                            onValueChange = { v -> if (v.length <= 6 && v.all { it.isDigit() || it == '.' }) score = v },
                            label = { Text("Today's score") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(8.dp))
                        BigButton("Add", {
                            val v = score.toDoubleOrNull() ?: return@BigButton
                            val oldBest = skill.entries.maxOfOrNull { it.score }
                            val entry = ScoreEntry(LocalDate.now().toString(), v)
                            update { u ->
                                u.copy(bests = u.bests.mapIndexed { i, s -> if (i == skillIndex) s.copy(entries = s.entries + entry) else s })
                            }
                            celebrate = if (oldBest == null || v > oldBest) "★ New best! ${fmt(v)}" else "Saved! Keep going."
                            score = ""
                        }, color = FeelingsDark, enabled = score.toDoubleOrNull() != null)
                    }
                    celebrate?.let {
                        Text(it, style = MaterialTheme.typography.titleMedium, color = FeelingsDark,
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                    }
                    if (skill.entries.size >= 2) BestsChart(skill.entries, bests)
                    if (skill.entries.isEmpty()) {
                        Text("No scores yet. Add your first one!", style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted)
                    }
                    skill.entries.withIndex().reversed().forEach { (i, e) ->
                        val isBest = i in bests
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(if (isBest) BookColors.Magic else Color.Transparent, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .semantics(mergeDescendants = true) {},
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(prettyDate(e.date), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text(fmt(e.score), style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.width(8.dp))
                            if (isBest) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = FeelingsDark)
                                Text("New best", style = MaterialTheme.typography.labelSmall)
                            }
                            IconButton(onClick = {
                                update { u ->
                                    u.copy(bests = u.bests.mapIndexed { si, s ->
                                        if (si == skillIndex) s.copy(entries = s.entries.filterIndexed { ei, _ -> ei != i }) else s
                                    })
                                }
                            }) { Icon(Icons.Filled.Delete, contentDescription = "Delete score ${fmt(e.score)} from ${prettyDate(e.date)}") }
                        }
                    }
                    TextButton(onClick = { confirmDelete = true }, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text("Remove this skill", color = BookColors.HelpRedText)
                    }
                }
                if (confirmDelete) {
                    AlertDialog(
                        onDismissRequest = { confirmDelete = false },
                        title = { Text("Remove ${skill.name}?") },
                        text = { Text("All its scores will be deleted.") },
                        confirmButton = {
                            TextButton(onClick = {
                                update { u -> u.copy(bests = u.bests.filterIndexed { i, _ -> i != skillIndex }) }
                                selected = 0; confirmDelete = false
                            }) { Text("Remove") }
                        },
                        dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Keep it") } },
                    )
                }
            }
        }
    }
}

private fun fmt(v: Double): String = if (v == Math.floor(v)) v.toLong().toString() else v.toString()

private fun prettyDate(iso: String): String = runCatching { LocalDate.parse(iso).format(dayFormat) }.getOrDefault(iso)

@Composable
private fun BestsChart(entries: List<ScoreEntry>, bests: Set<Int>) {
    val min = entries.minOf { it.score }
    val max = entries.maxOf { it.score }
    val line = FeelingsDark
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x22000000), RoundedCornerShape(12.dp))
            .clearAndSetSemantics {
                contentDescription = "Line chart of ${entries.size} scores, from ${fmt(entries.first().score)} to ${fmt(entries.last().score)}. Best is ${fmt(max)}."
            },
    ) {
        val pad = 20.dp.toPx()
        val w = size.width - pad * 2
        val h = size.height - pad * 2
        val range = (max - min).takeIf { it > 0 } ?: 1.0
        fun pt(i: Int) = Offset(
            pad + w * (i.toFloat() / (entries.size - 1).coerceAtLeast(1)),
            pad + h * (1f - ((entries[i].score - min) / range).toFloat()),
        )
        drawLine(Color(0x33000000), Offset(pad, pad + h), Offset(pad + w, pad + h), 2f)
        for (i in 1 until entries.size) drawLine(line, pt(i - 1), pt(i), 4.dp.toPx(), StrokeCap.Round)
        entries.indices.forEach { i ->
            if (i in bests) {
                // Star-ish marker so 'best' isn't shown by colour alone.
                val c = pt(i)
                val r = 8.dp.toPx()
                val path = Path()
                for (k in 0 until 10) {
                    val rad = if (k % 2 == 0) r else r * 0.45f
                    val a = Math.toRadians((k * 36 - 90).toDouble())
                    val p = Offset(c.x + (rad * Math.cos(a)).toFloat(), c.y + (rad * Math.sin(a)).toFloat())
                    if (k == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                }
                path.close()
                drawPath(path, BookColors.Magic)
                drawPath(path, line, style = Stroke(2f))
            } else {
                drawCircle(line, 4.dp.toPx(), pt(i))
            }
        }
    }
}

/* ---------------- My Calm-Down Plan ---------------- */

private val levelColors = mapOf(
    5 to Color(0xFFE5533D), 4 to Color(0xFFF08A34), 3 to Color(0xFFF2C94C), 2 to Color(0xFF8BC34A), 1 to Color(0xFF4CAF50),
)

@Composable
fun CalmPlanScreen(content: BookContent, user: UserData, update: ((UserData) -> UserData) -> Unit, nav: Nav) {
    val intro = content.book.activityIntros.calmPlan
    val activities = content.stories.activities

    KidScaffold(title = intro.title, color = FeelingsDark, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text(intro.text, style = MaterialTheme.typography.bodyLarge)
            Text("How big is my feeling? Tap your number.", style = MaterialTheme.typography.titleMedium)
            activities.calmPlan.sortedByDescending { it.level }.forEach { lvl ->
                val chosen = user.calmLevel == lvl.level
                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp)
                        .background(if (chosen) levelColors[lvl.level]!!.copy(alpha = 0.25f) else Color.White, RoundedCornerShape(16.dp))
                        .border(if (chosen) 4.dp else 1.dp, if (chosen) BookColors.Ink else Color(0x22000000), RoundedCornerShape(16.dp))
                        .clickable(role = Role.RadioButton) { update { it.copy(calmLevel = if (chosen) null else lvl.level) } }
                        .padding(12.dp)
                        .semantics(mergeDescendants = true) { selected = chosen },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(48.dp).background(levelColors[lvl.level]!!, CircleShape), contentAlignment = Alignment.Center) {
                        Text("${lvl.level}", style = MaterialTheme.typography.titleLarge, color = BookColors.Ink)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(lvl.label, style = MaterialTheme.typography.titleSmall)
                        Text(lvl.action, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (chosen) Text("← me", style = MaterialTheme.typography.labelLarge)
                }
            }

            SectionCard("My plan") {
                activities.calmPrompts.forEachIndexed { i, prompt ->
                    Text(prompt, style = MaterialTheme.typography.titleSmall)
                    SavedField("calm-$i", user.calmAnswers[i].orEmpty(), "My answer", { v ->
                        update { it.copy(calmAnswers = it.calmAnswers + (i to v)) }
                    }, singleLine = false)
                }
            }

            Breathing(intro.breathing.orEmpty())
        }
    }
}

@Composable
private fun Breathing(instructions: String) {
    var running by remember { mutableStateOf(false) }
    var breath by remember { mutableIntStateOf(0) }
    var inhale by remember { mutableStateOf(true) }
    val scale = remember { Animatable(0.5f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        for (b in 1..5) {
            breath = b
            inhale = true
            scale.animateTo(1f, tween(4000, easing = LinearEasing))
            inhale = false
            scale.animateTo(0.5f, tween(5000, easing = LinearEasing))
        }
        delay(300)
        breath = 6
        running = false
    }

    SectionCard("Flower and candle breathing", background = BookColors.TryIt) {
        Text(instructions, style = MaterialTheme.typography.bodyLarge)
        val status = when {
            breath == 6 && !running -> "All done. Well done!"
            !running -> "Press start when you're ready."
            inhale -> "Breath $breath of 5: Smell the flower... breathe in slowly through your nose."
            else -> "Breath $breath of 5: Blow out the candle... long breath out through your mouth."
        }
        Text(status, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite })
        Canvas(Modifier.fillMaxWidth().height(200.dp).clearAndSetSemantics { }) {
            val s = scale.value
            val cy = size.height / 2
            val flowerX = size.width * 0.3f
            val candleX = size.width * 0.7f
            // Flower grows as you breathe in.
            val pr = 26.dp.toPx() * (0.7f + s * 0.6f)
            for (k in 0 until 6) {
                val a = Math.toRadians(k * 60.0)
                drawCircle(Color(0xFFFF8FAB), pr * 0.55f, Offset(flowerX + (pr * Math.cos(a)).toFloat(), cy + (pr * Math.sin(a)).toFloat()))
            }
            drawCircle(Color(0xFFFFD54F), pr * 0.5f, Offset(flowerX, cy))
            drawLine(Color(0xFF66BB6A), Offset(flowerX, cy + pr * 1.4f), Offset(flowerX, size.height), 6.dp.toPx())
            // Candle flame shrinks and bends as you breathe out.
            val ch = 70.dp.toPx()
            val cw = 26.dp.toPx()
            drawRoundRect(Color(0xFFE3F2FD), Offset(candleX - cw / 2, size.height - ch), Size(cw, ch), CornerRadius(6f))
            drawRoundRect(Color(0xFF90CAF9), Offset(candleX - cw / 2, size.height - ch), Size(cw, ch), CornerRadius(6f), style = Stroke(3f))
            val flame = (if (running && !inhale) s else 1f).coerceIn(0.35f, 1f)
            val fh = 34.dp.toPx() * flame
            val lean = if (running && !inhale) (1f - s) * 18.dp.toPx() else 0f
            val top = size.height - ch - 6.dp.toPx()
            val path = Path().apply {
                moveTo(candleX - 9.dp.toPx() * flame, top)
                quadraticTo(candleX + lean, top - fh * 1.3f, candleX + lean, top - fh)
                quadraticTo(candleX + lean * 0.5f, top - fh * 0.3f, candleX + 9.dp.toPx() * flame, top)
                close()
            }
            drawPath(path, Color(0xFFFFA726))
            drawLine(Color(0xFF424242), Offset(candleX, top), Offset(candleX, top + 6.dp.toPx()), 2.dp.toPx())
        }
        BigButton(
            if (running) "Stop" else "Start (5 breaths)",
            {
                if (running) {
                    running = false; breath = 0
                    scope.launch { scale.snapTo(0.5f) }
                } else {
                    breath = 0; running = true
                }
            },
            color = BookColors.Friends,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/* ---------------- Our Tech Rules ---------------- */

@Composable
fun TechRulesScreen(content: BookContent, user: UserData, update: ((UserData) -> UserData) -> Unit, nav: Nav) {
    val intro = content.book.activityIntros.techRules
    val rules = content.stories.activities.techRules

    KidScaffold(title = intro.title, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text(intro.text, style = MaterialTheme.typography.bodyLarge)
            ReadToMeButton("tech", { rules.map { it.replace(Regex("_+"), "blank") } })
            rules.forEachIndexed { i, rule ->
                val ticked = i in user.techTicks
                val toggle = { update { it.copy(techTicks = if (ticked) it.techTicks - i else it.techTicks + i) } }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(if (ticked) BookColors.TryIt else Color.White, RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0x22000000), RoundedCornerShape(14.dp))
                        .padding(6.dp),
                ) {
                    val hasBlanks = rule.contains("___")
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .clickable(role = Role.Checkbox, onClick = toggle)
                            .semantics(mergeDescendants = true) {},
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(checked = ticked, onCheckedChange = null,
                            colors = CheckboxDefaults.colors(checkedColor = BookColors.Friends))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (hasBlanks) "Screen-time limits" else rule,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (hasBlanks) {
                        Row(Modifier.padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SavedField("tech-school", user.schoolMinutes, "School days (minutes)", { v ->
                                update { it.copy(schoolMinutes = v.filter(Char::isDigit)) }
                            }, modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number, maxLength = 4)
                            SavedField("tech-weekend", user.weekendMinutes, "Weekends (minutes)", { v ->
                                update { it.copy(weekendMinutes = v.filter(Char::isDigit)) }
                            }, modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number, maxLength = 4)
                        }
                    }
                }
            }
            SavedField("tech-extra", user.extraRules, intro.extra ?: "Our extra rules:", { v ->
                update { it.copy(extraRules = v) }
            }, singleLine = false, maxLength = 1000)
            SavedField("tech-gu-sign", user.grownUpSignature, intro.grownUpSignature ?: "Grown-up signature:", { v ->
                update { it.copy(grownUpSignature = v) }
            })
            SavedField("tech-my-sign", user.mySignature, intro.mySignature ?: "My signature:", { v ->
                update { it.copy(mySignature = v) }
            })
        }
    }
}
