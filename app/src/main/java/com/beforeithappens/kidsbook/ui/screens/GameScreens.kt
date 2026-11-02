package com.beforeithappens.kidsbook.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beforeithappens.kidsbook.data.BookContent
import com.beforeithappens.kidsbook.data.UserData
import com.beforeithappens.kidsbook.tts.LocalSpeaker
import com.beforeithappens.kidsbook.ui.Nav
import com.beforeithappens.kidsbook.ui.components.BigButton
import com.beforeithappens.kidsbook.ui.components.KidScaffold
import com.beforeithappens.kidsbook.ui.components.ReadToMeButton
import com.beforeithappens.kidsbook.ui.components.ScreenColumn
import com.beforeithappens.kidsbook.ui.components.SectionCard
import com.beforeithappens.kidsbook.ui.theme.BookColors
import com.beforeithappens.kidsbook.ui.theme.FeelingsDark
import com.beforeithappens.kidsbook.ui.theme.HandStyle
import com.beforeithappens.kidsbook.data.spokenLine
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.OutlinedTextField

/* ---------------- Magic Words ---------------- */

@Composable
fun MagicWordsScreen(content: BookContent, user: UserData, nav: Nav) {
    val intro = content.book.activityIntros.magicWords
    var quiz by rememberSaveable { mutableStateOf(false) }
    // In list mode cards start showing the words; in quiz mode they start face down.
    val flipped = remember { mutableStateListOf<Int>() }

    KidScaffold(title = intro.title, color = FeelingsDark, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text(intro.text, style = MaterialTheme.typography.bodyLarge)
            Text(
                "You've collected ${user.magicCollected.size} of ${content.allStories.size}. Tap 'Got it!' in an entry to add one.",
                style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !quiz, onClick = { quiz = false; flipped.clear() }, label = { Text("All cards") },
                    modifier = Modifier.heightIn(min = 48.dp))
                FilterChip(selected = quiz, onClick = { quiz = true; flipped.clear() }, label = { Text("Quiz me") },
                    modifier = Modifier.heightIn(min = 48.dp))
            }
            if (quiz) {
                Text("Read the entry title. Can you remember the Magic Words? Then tap to flip and check.",
                    style = MaterialTheme.typography.bodyLarge)
            } else {
                Text("Tap a card to flip it.", style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted)
            }
            content.allStories.forEach { story ->
                val showWords = story.id in flipped
                FlipCard(
                    front = "${story.id}. ${story.title}",
                    back = story.magicWords,
                    showBack = showWords,
                    collected = story.id in user.magicCollected,
                    onClick = { if (showWords) flipped.remove(story.id) else flipped.add(story.id) },
                )
            }
        }
    }
}

@Composable
private fun FlipCard(front: String, back: String, showBack: Boolean, collected: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(if (showBack) 180f else 0f, tween(400), label = "flip")
    val density = LocalDensity.current.density
    val speaker = LocalSpeaker.current
    val backVisible = rotation > 90f
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .background(if (backVisible) BookColors.Magic else Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, if (backVisible) Color(0xFFE0B840) else BookColors.Ink.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClickLabel = if (showBack) "show the story" else "show the Magic Words") {
                onClick()
                if (!showBack) speaker.speak(back)
            }
            .padding(16.dp)
            .semantics(mergeDescendants = true) {
                stateDescription = if (showBack) "Showing Magic Words" else "Showing story title"
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.graphicsLayer { rotationY = if (backVisible) 180f else 0f },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (backVisible) {
                Text(back, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            } else {
                Text(front, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
                if (collected) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = FeelingsDark)
                        Spacer(Modifier.width(4.dp))
                        Text("Collected", style = MaterialTheme.typography.labelSmall, color = FeelingsDark)
                    }
                }
            }
        }
    }
}

/* ---------------- What If? ---------------- */

@Composable
fun WhatIfScreen(content: BookContent, nav: Nav) {
    val intro = content.book.activityIntros.whatIf
    val cards = content.stories.games.whatIfCards
    var order by rememberSaveable { mutableStateOf(cards.indices.shuffled()) }
    var pos by rememberSaveable { mutableIntStateOf(0) }
    val index = order[pos]
    val speaker = LocalSpeaker.current

    KidScaffold(title = intro.title, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text(intro.text, style = MaterialTheme.typography.bodyLarge)
            Text("Card ${pos + 1} of ${cards.size}", style = MaterialTheme.typography.labelLarge, color = BookColors.Muted)
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp)
                    .background(BookColors.Friends, RoundedCornerShape(20.dp))
                    .clickable(role = Role.Button, onClickLabel = "hear it") { speaker.speak("What if... ${cards[index]}") }
                    .padding(24.dp)
                    .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("What if...", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.heightIn(min = 8.dp))
                Text(cards[index], color = Color.White, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            }
            Text("Say what you'd do now, and who you'd tell later. Then test your grown-up!", style = MaterialTheme.typography.bodyLarge)
            content.storyForWhatIf(index)?.let { story ->
                BigButton("Show me (Entry ${story.id})", { nav.story(story.id) }, color = BookColors.Safe, modifier = Modifier.fillMaxWidth())
            }
            BigButton("Next card", {
                if (pos + 1 < order.size) pos += 1 else { order = cards.indices.shuffled(); pos = 0 }
            }, modifier = Modifier.fillMaxWidth())
            BigButton("Shuffle", { order = cards.indices.shuffled(); pos = 0 }, color = BookColors.Ink,
                icon = Icons.Filled.Shuffle, modifier = Modifier.fillMaxWidth())
        }
    }
}

/* ---------------- Keep It or Tell It? ---------------- */

@Composable
fun KeepOrTellScreen(content: BookContent, nav: Nav) {
    val intro = content.book.activityIntros.keepOrTell
    val items = content.stories.games.keepOrTell
    val test = content.stories.games.keepOrTellTest
    var pos by rememberSaveable { mutableIntStateOf(0) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var answered by rememberSaveable { mutableStateOf<Boolean?>(null) } // the child's choice: true = keep it
    val speaker = LocalSpeaker.current

    KidScaffold(title = intro.title, color = BookColors.Safe, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text(intro.text, style = MaterialTheme.typography.bodyLarge)
            if (pos >= items.size) {
                SectionCard(background = BookColors.Magic) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Celebration, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("You got $score out of ${items.size}!", style = MaterialTheme.typography.headlineSmall)
                    }
                    Text(test, style = MaterialTheme.typography.bodyLarge)
                }
                BigButton("Play again", { pos = 0; score = 0; answered = null }, color = BookColors.Safe, modifier = Modifier.fillMaxWidth())
                return@ScreenColumn
            }
            val item = items[pos]
            Text("${pos + 1} of ${items.size}", style = MaterialTheme.typography.labelLarge, color = BookColors.Muted)
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp)
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .border(2.dp, BookColors.Safe, RoundedCornerShape(20.dp))
                    .clickable(role = Role.Button, onClickLabel = "hear it") { speaker.speak(item.statement) }
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(item.statement, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }
            val choice = answered
            if (choice == null) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BigButton("Keep it", {
                        answered = true
                        if (item.isKeep) score++
                    }, color = BookColors.Friends, modifier = Modifier.weight(1f).heightIn(min = 72.dp))
                    BigButton("Tell it!", {
                        answered = false
                        if (!item.isKeep) score++
                    }, color = BookColors.Safe, modifier = Modifier.weight(1f).heightIn(min = 72.dp))
                }
            } else {
                val right = choice == item.isKeep
                val msg = when {
                    item.isKeep -> "This one is fine to keep. Nobody gets hurt, and no grown-up asked you to hide it from your parents."
                    else -> "This one is a TELL, even if you promised. Tell a grown-up you trust. That's not dobbing. That's how people get help."
                }
                SectionCard(background = if (right) Color(0xFFE8F5E9) else BookColors.Magic) {
                    Text(if (right) "✓ Right!" else "Good try!", style = MaterialTheme.typography.titleLarge)
                    Text(msg, style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                }
                BigButton(if (pos + 1 < items.size) "Next" else "See my score", {
                    pos++; answered = null
                }, color = BookColors.Safe, modifier = Modifier.fillMaxWidth())
            }
            Text(test, style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted)
            Text("Score: $score", style = MaterialTheme.typography.labelLarge)
        }
    }
}

/* ---------------- My Exit Lines ---------------- */

@Composable
fun ExitLinesScreen(
    content: BookContent,
    user: UserData,
    update: ((UserData) -> UserData) -> Unit,
    nav: Nav,
) {
    val intro = content.book.activityIntros.exitLines
    val lines = content.stories.activities.exitLines
    val speaker = LocalSpeaker.current
    var own by remember { mutableStateOf(user.ownExitLines) }
    var quiet by remember { mutableStateOf(user.quietTellGrownUps) }

    KidScaffold(title = intro.title, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text(intro.text, style = MaterialTheme.typography.bodyLarge)
            Text("Tap a line to hear it. Tap the star to make it one of yours.",
                style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted)
            lines.forEachIndexed { i, line ->
                val fav = i in user.exitFavourites
                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .background(if (fav) BookColors.Magic else Color.White, RoundedCornerShape(20.dp))
                        .border(2.dp, BookColors.Ink, RoundedCornerShape(20.dp))
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "\u201C$line\u201D",
                        style = HandStyle,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(role = Role.Button, onClickLabel = "hear it") { speaker.speak(spokenLine("\"$line\"")) }
                            .padding(vertical = 10.dp),
                    )
                    IconToggleButton(checked = fav, onCheckedChange = {
                        update { u -> u.copy(exitFavourites = if (fav) u.exitFavourites - i else u.exitFavourites + i) }
                    }) {
                        Icon(
                            if (fav) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = if (fav) "One of my lines" else "Make this one of my lines",
                            tint = FeelingsDark,
                        )
                    }
                }
            }
            OutlinedTextField(
                value = own,
                onValueChange = { own = it.take(1000); update { u -> u.copy(ownExitLines = own) } },
                label = { Text(intro.extra ?: "My own exit lines:") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = quiet,
                onValueChange = { quiet = it.take(1000); update { u -> u.copy(quietTellGrownUps = quiet) } },
                label = { Text(intro.grownUpSignature ?: "Grown-ups I can tell quietly later:") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            Text("These stay on this device only.", style = MaterialTheme.typography.bodySmall, color = BookColors.Muted)
        }
    }
}

/* ---------------- Good Friend or Bossy Friend? ---------------- */

@Composable
fun GoodOrBossyScreen(content: BookContent, nav: Nav) {
    val intro = content.book.activityIntros.goodOrBossy
    val data = content.stories.games.goodOrBossyFriend
    val all = remember { (data.good.map { it to true } + data.bossy.map { it to false }) }
    var order by rememberSaveable { mutableStateOf(all.indices.shuffled()) }
    var pos by rememberSaveable { mutableIntStateOf(0) }
    var goodPile by rememberSaveable { mutableStateOf(listOf<Int>()) }
    var bossyPile by rememberSaveable { mutableStateOf(listOf<Int>()) }
    var feedback by remember { mutableStateOf<String?>(null) }
    val speaker = LocalSpeaker.current

    KidScaffold(title = intro.title, color = FeelingsDark, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text(intro.text, style = MaterialTheme.typography.bodyLarge)
            if (pos < order.size) {
                val (statement, isGood) = all[order[pos]]
                Text("${pos + 1} of ${all.size}", style = MaterialTheme.typography.labelLarge, color = BookColors.Muted)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(2.dp, BookColors.Ink, RoundedCornerShape(20.dp))
                        .clickable(role = Role.Button, onClickLabel = "hear it") { speaker.speak(statement) }
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("A friend who...", style = MaterialTheme.typography.labelLarge, color = BookColors.Muted)
                    Text(statement, style = MaterialTheme.typography.headlineSmall)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BigButton("Good friend 😊", {
                        feedback = if (isGood) "✓ Yes, that's a good friend!" else "Hmm, that one is bossy. It's in the bossy pile."
                        if (isGood) goodPile = goodPile + order[pos] else bossyPile = bossyPile + order[pos]
                        pos++
                    }, color = BookColors.Friends, modifier = Modifier.weight(1f).heightIn(min = 72.dp))
                    BigButton("Bossy friend 😠", {
                        feedback = if (!isGood) "✓ Yes, that's bossy!" else "Actually, that's what a good friend does. It's in the good pile."
                        if (isGood) goodPile = goodPile + order[pos] else bossyPile = bossyPile + order[pos]
                        pos++
                    }, color = FeelingsDark, modifier = Modifier.weight(1f).heightIn(min = 72.dp))
                }
            } else {
                SectionCard(background = BookColors.Magic) {
                    Text("All sorted!", style = MaterialTheme.typography.headlineSmall)
                    intro.talk?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
                }
                BigButton("Play again", {
                    order = all.indices.shuffled(); pos = 0; goodPile = emptyList(); bossyPile = emptyList(); feedback = null
                }, color = FeelingsDark, modifier = Modifier.fillMaxWidth())
            }
            feedback?.let {
                Text(it, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            }
            Pile("Good friend", goodPile.map { all[it].first }, BookColors.Friends)
            Pile("Bossy friend", bossyPile.map { all[it].first }, FeelingsDark)
            ReadToMeButton("goodbossy-all", {
                listOf("Good friend.") + data.good + listOf("Bossy friend.") + data.bossy
            }, label = "Read both lists")
        }
    }
}

@Composable
private fun Pile(title: String, items: List<String>, color: Color) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .border(2.dp, color, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text("$title (${items.size})", style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        if (items.isEmpty()) Text("Nothing here yet.", style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted)
        items.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
    }
}
