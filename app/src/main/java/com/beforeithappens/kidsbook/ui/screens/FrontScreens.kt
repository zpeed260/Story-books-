package com.beforeithappens.kidsbook.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beforeithappens.kidsbook.data.BookContent
import com.beforeithappens.kidsbook.data.UserData
import com.beforeithappens.kidsbook.ui.Nav
import com.beforeithappens.kidsbook.ui.Routes
import com.beforeithappens.kidsbook.ui.comic.CharacterPortrait
import com.beforeithappens.kidsbook.ui.comic.ComicThumbnail
import com.beforeithappens.kidsbook.ui.components.BigButton
import com.beforeithappens.kidsbook.ui.components.GrownUpBox
import com.beforeithappens.kidsbook.ui.components.KidScaffold
import com.beforeithappens.kidsbook.ui.components.ReadToMeButton
import com.beforeithappens.kidsbook.ui.components.ScreenColumn
import com.beforeithappens.kidsbook.ui.components.SectionCard
import com.beforeithappens.kidsbook.ui.theme.BookColors
import com.beforeithappens.kidsbook.ui.theme.forWhiteText
import com.beforeithappens.kidsbook.ui.theme.parseHex

private val crewKeys = listOf("zara", "sam", "kai", "ruby", "theo", "mia")

/* ---------------- Onboarding ---------------- */

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(content: BookContent, user: UserData, update: ((UserData) -> UserData) -> Unit, nav: Nav) {
    val book = content.book
    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf(user.name) }
    var buddy by remember { mutableStateOf(user.buddy) }
    var avatar by remember { mutableStateOf(user.avatar) }

    Column(
        Modifier
            .fillMaxSize()
            .background(BookColors.Cream)
            .padding(top = 32.dp)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (step) {
            0 -> {
                Text(book.cover.title, style = MaterialTheme.typography.displaySmall, color = BookColors.Friends,
                    textAlign = TextAlign.Center)
                Text(book.cover.subtitle, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                Text(book.cover.tagline, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                CrewRow(content)
                Text(book.cover.features, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                BigButton("Let's start", { step = 1 }, modifier = Modifier.fillMaxWidth())
            }
            1 -> {
                Text("This book belongs to", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                OutlinedTextField(
                    value = name, onValueChange = { name = it.take(40) },
                    label = { Text(book.belongsTo.nameLabel) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = buddy, onValueChange = { buddy = it.take(40) },
                    label = { Text(book.belongsTo.buddyLabel) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(book.belongsTo.text, style = MaterialTheme.typography.bodyLarge)
                BigButton("Next", { step = 2 }, modifier = Modifier.fillMaxWidth())
            }
            2 -> {
                Text("Pick your buddy from the crew", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                Text(book.crewOutro, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    crewKeys.forEach { key ->
                        val spec = content.character(key) ?: return@forEach
                        val chosen = avatar == key
                        Column(
                            Modifier
                                .width(100.dp)
                                .border(if (chosen) 4.dp else 1.dp, if (chosen) BookColors.Friends else Color(0x22000000), RoundedCornerShape(16.dp))
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .clickable { avatar = key }
                                .padding(8.dp)
                                .semantics(mergeDescendants = true) {
                                    selected = chosen
                                    role = Role.RadioButton
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            CharacterPortrait(content, key, Modifier.size(76.dp))
                            Text(spec.name, style = MaterialTheme.typography.labelLarge)
                            if (chosen) Icon(Icons.Filled.CheckCircle, contentDescription = "chosen", tint = BookColors.Friends)
                        }
                    }
                }
                BigButton("Next", { step = 3 }, modifier = Modifier.fillMaxWidth())
            }
            else -> {
                CharacterPortrait(content, avatar, Modifier.size(140.dp))
                Text(book.belongsTo.tellExplainer, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                SectionCard(background = BookColors.Magic) {
                    Text(book.alwaysTell.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(book.alwaysTell.text, style = MaterialTheme.typography.bodyLarge)
                }
                BigButton("Open my book", {
                    update { it.copy(onboarded = true, name = name.trim(), buddy = buddy.trim(), avatar = avatar) }
                    nav.finishOnboarding()
                }, modifier = Modifier.fillMaxWidth())
            }
        }
        if (step > 0) {
            Text(
                "Back",
                color = BookColors.Friends,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clickable(role = Role.Button) { step -= 1 }
                    .heightIn(min = 48.dp)
                    .padding(12.dp),
            )
        }
    }
}

@Composable
private fun CrewRow(content: BookContent) {
    Row(
        Modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = "The six crew members waving and smiling" },
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        crewKeys.forEach { CharacterPortrait(content, it, Modifier.size(50.dp)) }
    }
}

/* ---------------- Home ---------------- */

@Composable
fun HomeScreen(content: BookContent, user: UserData, nav: Nav) {
    KidScaffold(title = content.book.cover.title, onBack = null, onHome = null, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CharacterPortrait(content, user.avatar, Modifier.size(72.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(if (user.name.isBlank()) "Hi there!" else "Hi, ${user.name}!",
                        style = MaterialTheme.typography.headlineSmall)
                    Text(content.book.cover.tagline, style = MaterialTheme.typography.bodyMedium)
                }
            }
            content.parts.forEach { part ->
                val stories = content.storiesInPart(part.id)
                val read = stories.count { it.id in user.readStories }
                PartTile(
                    title = part.name,
                    subtitle = "Stories ${stories.first().id} to ${stories.last().id} • $read of ${stories.size} read",
                    color = parseHex(part.color),
                    onClick = { nav.part(part.id) },
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallTile("Magic Words", Icons.Filled.AutoAwesome, BookColors.Magic, BookColors.Ink,
                    "${user.magicCollected.size} of ${content.allStories.size} collected", Modifier.weight(1f)) { nav.go(Routes.MAGIC) }
                SmallTile("Activities", Icons.Filled.Extension, BookColors.TryIt, BookColors.Ink,
                    "Games and my pages", Modifier.weight(1f)) { nav.go(Routes.ACTIVITIES) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallTile("Meet the crew", Icons.Filled.Groups, Color.White, BookColors.Ink, null, Modifier.weight(1f)) { nav.go(Routes.CREW) }
                SmallTile("How to use this book", Icons.Filled.MenuBook, Color.White, BookColors.Ink, null, Modifier.weight(1f)) { nav.go(Routes.HOW_TO) }
            }
            SmallTile("For grown-ups", Icons.Filled.Lock, BookColors.GrownUp, BookColors.Muted, "PIN needed", Modifier.fillMaxWidth()) {
                nav.go(Routes.GROWN_UPS)
            }
        }
    }
}

@Composable
private fun PartTile(title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    val bg = color.forWhiteText()
    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 104.dp)
            .background(bg, RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall)
        Text(subtitle, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SmallTile(
    title: String,
    icon: ImageVector,
    bg: Color,
    fg: Color,
    subtitle: String?,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier
            .heightIn(min = 96.dp)
            .background(bg, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0x1F000000), RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, contentDescription = null, tint = fg)
        Text(title, style = MaterialTheme.typography.titleSmall, color = BookColors.Ink)
        if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.labelSmall, color = BookColors.Muted)
    }
}

/* ---------------- Part list ---------------- */

@Composable
fun PartScreen(content: BookContent, user: UserData, partId: Int, nav: Nav) {
    val part = content.part(partId) ?: return
    val color = parseHex(part.color)
    KidScaffold(title = part.name, color = color, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            content.storiesInPart(partId).forEach { story ->
                val read = story.id in user.readStories
                val fav = story.id in user.favourites
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(2.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable(role = Role.Button) { nav.story(story.id) }
                        .padding(12.dp)
                        .semantics(mergeDescendants = true) {
                            contentDescription = "Story ${story.id}: ${story.title}" +
                                (if (read) ". Read." else ". Not read yet.") + (if (fav) " Favourite." else "")
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    story.panels.firstOrNull()?.let {
                        ComicThumbnail(content, it, Modifier.size(width = 96.dp, height = 76.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("STORY ${story.id}", style = MaterialTheme.typography.labelSmall, color = color.forWhiteText())
                        Text(story.title, style = MaterialTheme.typography.titleSmall)
                    }
                    if (fav) Icon(Icons.Filled.Favorite, contentDescription = null, tint = BookColors.HelpRed)
                    if (read) {
                        Box(Modifier.size(36.dp).background(BookColors.Good, CircleShape), contentAlignment = Alignment.Center) {
                            Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- Meet the crew / How to use ---------------- */

@Composable
fun CrewScreen(content: BookContent, nav: Nav) {
    val book = content.book
    KidScaffold(title = "Meet the crew", onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text(book.crewIntro, style = MaterialTheme.typography.bodyLarge)
            ReadToMeButton("crew", {
                listOf(book.crewIntro) + book.crew.map { "${content.character(it.who)?.name}: ${it.text}" } + book.crewOutro
            })
            book.crew.forEach { member ->
                val spec = content.character(member.who) ?: return@forEach
                Row(
                    Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(12.dp)
                        .semantics(mergeDescendants = true) {},
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CharacterPortrait(content, member.who, Modifier.size(80.dp), background = parseHex(spec.top).copy(alpha = 0.18f))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(spec.name, style = MaterialTheme.typography.titleMedium)
                        Text(member.text, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Text(book.crewOutro, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
fun HowToUseScreen(content: BookContent, nav: Nav) {
    val book = content.book
    KidScaffold(title = "How to use this book", onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            ReadToMeButton("howto", {
                book.howToUse.mapIndexed { i, s -> "${i + 1}. ${s.title} ${s.text}" } + listOf(book.alwaysTell.title, book.alwaysTell.text)
            })
            book.howToUse.forEachIndexed { i, s ->
                Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(14.dp)) {
                    Box(Modifier.size(36.dp).background(BookColors.Friends, CircleShape), contentAlignment = Alignment.Center) {
                        Text("${i + 1}", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(s.title, style = MaterialTheme.typography.titleSmall)
                        Text(s.text, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            SectionCard(background = BookColors.Magic) {
                Text(book.alwaysTell.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(book.alwaysTell.text, style = MaterialTheme.typography.bodyLarge)
            }
            GrownUpBox(book.grownUpsReading.title) {
                Text("The full grown-up notes are in the grown-ups area (PIN needed).", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/* ---------------- Activities hub ---------------- */

@Composable
fun ActivitiesScreen(nav: Nav) {
    data class Item(val title: String, val desc: String, val route: String, val color: Color)
    val games = listOf(
        Item("What If? Cards", "Pull a card. What would you do?", Routes.WHAT_IF, BookColors.Friends),
        Item("Surprise or Tell?", "Is it a fun surprise, or should you tell?", Routes.SURPRISE, BookColors.Safe),
        Item("Good Friend or Bossy Friend?", "Sort them into two piles.", Routes.GOOD_BOSSY, BookColors.Feelings),
    )
    val pages = listOf(
        Item("My Safety Hand", "Five grown-ups I can tell anything.", Routes.HAND, BookColors.Safe),
        Item("Things I Know By Heart", "Names, numbers and our password. (Grown-up PIN)", Routes.HEART, BookColors.Safe),
        Item("My Personal Bests", "The only person you're racing is YOU!", Routes.BESTS, BookColors.Feelings),
        Item("My Calm-Down Plan", "My feelings number and flower and candle breathing.", Routes.CALM, BookColors.Feelings),
        Item("Our Tech Rules", "Tick the rules our family agrees on.", Routes.TECH, BookColors.Friends),
    )
    KidScaffold(title = "Activities", onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        ScreenColumn(pad) {
            Text("Games", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
            games.forEach { ActivityRow(it.title, it.desc, it.color) { nav.go(it.route) } }
            Text("My pages", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
            pages.forEach { ActivityRow(it.title, it.desc, it.color) { nav.go(it.route) } }
        }
    }
}

@Composable
private fun ActivityRow(title: String, desc: String, color: Color, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, color.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(14.dp)
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(16.dp).background(color, CircleShape))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted)
        }
    }
}

