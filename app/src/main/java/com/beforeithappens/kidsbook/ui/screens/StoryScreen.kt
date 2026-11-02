package com.beforeithappens.kidsbook.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beforeithappens.kidsbook.data.BookContent
import com.beforeithappens.kidsbook.data.Story
import com.beforeithappens.kidsbook.data.spokenLine
import com.beforeithappens.kidsbook.ui.theme.HandStyle
import com.beforeithappens.kidsbook.ui.theme.notebookPaper
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.material.icons.filled.Schedule
import com.beforeithappens.kidsbook.data.UserData
import com.beforeithappens.kidsbook.tts.LocalSpeaker
import com.beforeithappens.kidsbook.ui.Nav
import com.beforeithappens.kidsbook.ui.Routes
import com.beforeithappens.kidsbook.ui.comic.ComicPanel
import com.beforeithappens.kidsbook.ui.components.BigButton
import com.beforeithappens.kidsbook.ui.components.Bullet
import com.beforeithappens.kidsbook.ui.components.KidScaffold
import com.beforeithappens.kidsbook.ui.components.ReadToMeButton
import com.beforeithappens.kidsbook.ui.components.ScreenColumn
import com.beforeithappens.kidsbook.ui.components.SectionCard
import com.beforeithappens.kidsbook.ui.theme.BookColors
import com.beforeithappens.kidsbook.ui.theme.forWhiteText
import com.beforeithappens.kidsbook.ui.theme.parseHex
import kotlinx.coroutines.delay

/** Activities a 'Try it!' text can point to, matched by name. */
private val tryItLinks = listOf(
    "Good Friend or Bossy Friend" to Routes.GOOD_BOSSY,
    "Personal Bests" to Routes.BESTS,
    "Keep It or Tell It" to Routes.KEEP_TELL,
    "Exit Lines" to Routes.EXIT_LINES,
    "Safety Hand" to Routes.HAND,
    "Calm-Down Plan" to Routes.CALM,
    "Tech Rules" to Routes.TECH,
    "Know By Heart" to Routes.HEART,
    "What If" to Routes.WHAT_IF,
    "Magic Words" to Routes.MAGIC,
)

@Composable
fun StoryScreen(
    content: BookContent,
    user: UserData,
    storyId: Int,
    update: ((UserData) -> UserData) -> Unit,
    nav: Nav,
) {
    val story = content.story(storyId) ?: return
    val part = content.part(story.part)
    val color = part?.let { parseHex(it.color) } ?: BookColors.Friends
    val speaker = LocalSpeaker.current
    val prompt = content.book.myTurnPrompt

    // Panels appear one at a time.
    var shownPanels by remember(storyId) { mutableIntStateOf(0) }
    LaunchedEffect(storyId) {
        for (i in 1..story.panels.size) {
            delay(if (i == 1) 150 else 600)
            shownPanels = i
        }
    }

    KidScaffold(
        title = "Entry ${story.id}",
        color = color,
        onBack = nav::back,
        onHome = nav::home,
        onHelp = nav::help,
    ) { pad ->
        ScreenColumn(pad) {
            // Entry header, like the top of a journal page.
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(lerp(Color.White, color, 0.28f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    "Entry ${story.id} • ${story.day} • ${part?.name ?: ""}",
                    color = BookColors.Ink,
                    style = MaterialTheme.typography.labelMedium,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        story.title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f).semantics { heading() },
                    )
                    val fav = story.id in user.favourites
                    IconButton(onClick = {
                        update { it.copy(favourites = if (fav) it.favourites - story.id else it.favourites + story.id) }
                    }) {
                        Icon(
                            if (fav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (fav) "Remove from favourites" else "Add to favourites",
                            tint = BookColors.HelpRedText,
                        )
                    }
                }
            }

            // Alex's journal bit, handwritten on ruled paper.
            Text(
                story.journal,
                style = HandStyle,
                color = BookColors.Ink,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0x22000000), RoundedCornerShape(8.dp))
                    .notebookPaper()
                    .padding(start = 26.dp, end = 14.dp, top = 6.dp, bottom = 12.dp),
            )

            ReadToMeButton("story-${story.id}", { storyReadAloud(content, story) }, modifier = Modifier.fillMaxWidth())

            // Comic.
            story.panels.forEachIndexed { i, panel ->
                AnimatedVisibility(
                    visible = i < shownPanels,
                    enter = fadeIn() + slideInVertically { it / 6 } + expandVertically(),
                ) {
                    ComicPanel(
                        content = content,
                        panel = panel,
                        number = i + 1,
                        partColor = color,
                        onTap = {
                            speaker.speakAll(panel.speech.map { content.describeSpeech(panel, it) }, "panel-${story.id}-$i")
                        },
                    )
                }
            }
            Text(
                "Tap a picture to hear it.",
                style = MaterialTheme.typography.bodyMedium,
                color = BookColors.Muted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            SectionCard("What I figured out", Icons.Filled.Lightbulb, background = Color(0xFFFFF6D6)) {
                story.figured.forEach { Bullet(it, mark = "–") }
            }

            SayNowSection(story)

            story.later?.let { later ->
                SectionCard("Later", Icons.Filled.Schedule) {
                    Text(later, style = MaterialTheme.typography.bodyLarge)
                }
            }

            MagicWordsCard(story, collected = story.id in user.magicCollected) {
                update { it.copy(magicCollected = it.magicCollected + story.id) }
            }

            SectionCard("Try it!", Icons.Filled.SportsEsports, background = BookColors.TryIt) {
                Text(story.tryIt, style = MaterialTheme.typography.bodyLarge)
                tryItLinks.firstOrNull { story.tryIt.contains(it.first, ignoreCase = true) }?.let { (name, route) ->
                    BigButton("Open $name", { nav.go(route) }, color = BookColors.Friends, modifier = Modifier.fillMaxWidth())
                }
            }

            MyTurnCard(story, prompt, user.myTurn[story.id] ?: "") { text ->
                update { it.copy(myTurn = it.myTurn + (story.id to text)) }
            }

            GrownUpCorner(story)

            val read = story.id in user.readStories
            if (!read) {
                BigButton("I've read this entry", {
                    update { it.copy(readStories = it.readStories + story.id) }
                }, color = BookColors.Good, icon = Icons.Filled.CheckCircle, modifier = Modifier.fillMaxWidth())
            } else {
                Row(
                    Modifier.fillMaxWidth().background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp)).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = BookColors.Good)
                    Spacer(Modifier.width(8.dp))
                    Text("You've read this entry. Well done!", style = MaterialTheme.typography.bodyLarge)
                }
            }
            content.story(story.id + 1)?.let { next ->
                BigButton("Next entry: ${next.title}", { nav.story(next.id) }, color = content.part(next.part)?.let { parseHex(it.color) } ?: color, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

private fun storyReadAloud(content: BookContent, story: Story): List<String> = buildList {
    add("Entry ${story.id}. ${story.day}. ${story.title}.")
    add(story.journal)
    story.panels.forEachIndexed { i, p -> add(content.describePanel(p, i + 1)) }
    add("What I figured out.")
    addAll(story.figured)
    add("What I can say right now.")
    addAll(story.sayNow)
    story.later?.let { add("Later: $it") }
    add("Magic Words: ${story.magicWords}")
    add("Try it! ${story.tryIt}")
}

@Composable
private fun SayNowSection(story: Story) {
    val speaker = LocalSpeaker.current
    var practising by remember { mutableStateOf<String?>(null) }

    SectionCard("What I can say right now", Icons.AutoMirrored.Filled.Chat) {
        Text("Low-key and calm. Tap a bubble to hear it.", style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted)
        story.sayNow.forEachIndexed { i, line ->
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(2.dp, BookColors.Ink, RoundedCornerShape(20.dp))
                        .clickable(role = Role.Button, onClickLabel = "hear it") { speaker.speak(spokenLine(line), "say-${story.id}-$i") }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(line, style = HandStyle, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = BookColors.Friends)
                }
                TextButton(
                    onClick = { practising = line },
                    modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = "Say it out loud: $line" },
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Say it out loud")
                }
            }
        }
    }

    practising?.let { line ->
        var saidIt by remember(line) { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { practising = null },
            title = { Text(if (saidIt) "Great job!" else "Your turn!") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        if (saidIt) "Saying it out loud makes it easier to say for real. Try it again, nice and calm!"
                        else "Say it calmly, like it's no big deal. Then walk away:",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        line,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BookColors.Magic, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                    Text("Nothing is recorded.", style = MaterialTheme.typography.bodySmall, color = BookColors.Muted)
                }
            },
            confirmButton = {
                TextButton(onClick = { if (saidIt) practising = null else saidIt = true }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(if (saidIt) "Done" else "I said it!")
                }
            },
            dismissButton = {
                TextButton(onClick = { speaker.speak(spokenLine(line)) }, modifier = Modifier.heightIn(min = 48.dp)) { Text("Hear it first") }
            },
        )
    }
}

@Composable
private fun MagicWordsCard(story: Story, collected: Boolean, onGotIt: () -> Unit) {
    val speaker = LocalSpeaker.current
    Column(
        Modifier
            .fillMaxWidth()
            .background(BookColors.Magic, RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFFE0B840), RoundedCornerShape(16.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Magic Words", style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        }
        Text(
            story.magicWords,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable(role = Role.Button, onClickLabel = "hear it") { speaker.speak(story.magicWords) },
        )
        if (collected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.semantics(mergeDescendants = true) { stateDescription = "Collected" },
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = BookColors.Good)
                Spacer(Modifier.width(6.dp))
                Text("In your Magic Words collection", style = MaterialTheme.typography.labelLarge)
            }
        } else {
            BigButton("Got it!", onGotIt, color = BookColors.Ink, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun MyTurnCard(story: Story, prompt: String, saved: String, onSave: (String) -> Unit) {
    var text by remember(story.id) { mutableStateOf(saved) }
    SectionCard("My Turn", Icons.Filled.EditNote) {
        Text(prompt, style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = text,
            onValueChange = { text = it.take(2000); onSave(text) },
            label = { Text("My answer") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )
        Text("Only you can see this. It stays on this device.", style = MaterialTheme.typography.bodySmall, color = BookColors.Muted)
    }
}

@Composable
private fun GrownUpCorner(story: Story) {
    var open by remember(story.id) { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .background(BookColors.GrownUp, RoundedCornerShape(12.dp))
            .padding(4.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clickable(role = Role.Button) { open = !open }
                .padding(horizontal = 12.dp)
                .semantics { stateDescription = if (open) "Open" else "Closed" },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("GROWN-UP CORNER (for grown-ups)", style = MaterialTheme.typography.labelMedium, color = BookColors.Muted,
                modifier = Modifier.weight(1f))
            Icon(if (open) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null, tint = BookColors.Muted)
        }
        AnimatedVisibility(open) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Ask: \"${story.grownUp.ask}\"", style = MaterialTheme.typography.bodySmall)
                Text("Tip: ${story.grownUp.tip}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.size(4.dp))
            }
        }
    }
}
