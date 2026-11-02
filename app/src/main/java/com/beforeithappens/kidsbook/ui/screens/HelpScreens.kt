package com.beforeithappens.kidsbook.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beforeithappens.kidsbook.BookViewModel
import com.beforeithappens.kidsbook.data.BookContent
import com.beforeithappens.kidsbook.data.UserData
import com.beforeithappens.kidsbook.ui.Nav
import com.beforeithappens.kidsbook.ui.Routes
import com.beforeithappens.kidsbook.ui.components.BigButton
import com.beforeithappens.kidsbook.ui.components.Bullet
import com.beforeithappens.kidsbook.ui.components.GrownUpBox
import com.beforeithappens.kidsbook.ui.components.KidScaffold
import com.beforeithappens.kidsbook.ui.components.PinGate
import com.beforeithappens.kidsbook.ui.components.ScreenColumn
import com.beforeithappens.kidsbook.ui.components.SectionCard
import com.beforeithappens.kidsbook.ui.theme.BookColors

/**
 * Opens the phone's dialler with the number filled in. It never places the call:
 * the child has to press call themselves.
 */
fun openDialler(context: Context, number: String) {
    val digits = number.filter { it.isDigit() || it == '+' }
    if (digits.isEmpty()) return
    try {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$digits")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: ActivityNotFoundException) {
        // No dialler on this device (e.g. some tablets). The number is still shown on screen.
    }
}

@Composable
private fun DialButton(label: String, number: String, color: Color, big: Boolean = true) {
    val context = LocalContext.current
    Button(
        onClick = { openDialler(context, number) },
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (big) 72.dp else 52.dp)
            .semantics { contentDescription = "$label. Opens the phone to call $number. You press call yourself." },
    ) {
        Icon(Icons.Filled.Call, contentDescription = null)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, style = if (big) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleSmall, color = Color.White)
            if (label != number) Text(number, style = MaterialTheme.typography.bodyMedium, color = Color.White)
        }
    }
}

/* ---------------- Who can help (kids) ---------------- */

@Composable
fun WhoCanHelpScreen(content: BookContent, user: UserData, nav: Nav) {
    val help = content.book.help
    KidScaffold(title = help.title, color = BookColors.HelpRedText, onBack = nav::back, onHome = nav::home, onHelp = null) { pad ->
        ScreenColumn(pad) {
            Text(help.intro, style = MaterialTheme.typography.titleMedium)
            help.kids.forEach { line ->
                SectionCard {
                    DialButton(line.name, line.number,
                        if (line.number == "000") BookColors.HelpRedText else BookColors.Safe)
                    Text(line.text, style = MaterialTheme.typography.bodyLarge)
                }
            }
            Text("Tapping a button opens the phone. You press call yourself.",
                style = MaterialTheme.typography.bodyMedium, color = BookColors.Muted)

            SectionCard("My grown-ups") {
                val people = user.safetyHand.filter { it.name.isNotBlank() }
                if (user.buddy.isNotBlank()) {
                    Text("My reading buddy: ${user.buddy}", style = MaterialTheme.typography.titleSmall)
                }
                if (people.isEmpty()) {
                    Text("You haven't filled in your Safety Hand yet.", style = MaterialTheme.typography.bodyLarge)
                }
                people.forEach { p ->
                    if (p.phone.any { it.isDigit() }) {
                        DialButton(p.name, p.phone, BookColors.Friends, big = false)
                    } else {
                        Text("• ${p.name}", style = MaterialTheme.typography.titleSmall)
                    }
                }
                BigButton("My Safety Hand", { nav.go(Routes.HAND) }, color = BookColors.Safe, modifier = Modifier.fillMaxWidth())
            }
            SectionCard(background = BookColors.Magic) {
                Text(content.book.alwaysTell.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(content.book.alwaysTell.text, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

/* ---------------- Grown-ups area (PIN) ---------------- */

@Composable
fun GrownUpsScreen(content: BookContent, user: UserData, vm: BookViewModel, nav: Nav) {
    val book = content.book
    var confirmReset by remember { mutableStateOf(false) }
    var changingPin by remember { mutableStateOf(false) }

    KidScaffold(title = "For grown-ups", color = BookColors.Muted, onBack = nav::back, onHome = nav::home, onHelp = nav::help) { pad ->
        Box(Modifier.padding(pad)) {
            PinGate(
                hasPin = user.hasPin && !changingPin,
                unlocked = vm.unlocked && !changingPin,
                checkPin = vm::checkPin,
                createPin = { vm.createPin(it); changingPin = false },
                onUnlocked = { vm.unlock(); changingPin = false },
            ) {
                ScreenColumn(PaddingValues()) {
                    val small = MaterialTheme.typography.bodySmall
                    GrownUpBox(book.grownUpsReading.title) {
                        book.grownUpsReading.points.forEach { Bullet(it, style = small) }
                        Text(book.grownUpsReading.seriousTitle, style = small, fontWeight = FontWeight.Bold)
                        Text(book.grownUpsReading.serious, style = small)
                    }

                    GrownUpBox("Who can help: for grown-ups") {
                        book.help.kids.forEach { k ->
                            Text("${k.name}: ${k.number}${k.web?.let { " • $it" } ?: ""}", style = small)
                        }
                        Spacer(Modifier.heightIn(min = 4.dp))
                        book.help.grownUps.forEach { s ->
                            if (s.isPhone) {
                                DialButton(s.name, s.contact, BookColors.Muted, big = false)
                            } else {
                                Text("${s.name}: ${s.contact}", style = small, fontWeight = FontWeight.Medium)
                            }
                        }
                        Text(book.help.furtherReadingTitle, style = small, fontWeight = FontWeight.Bold)
                        book.help.furtherReading.forEach { Bullet(it, style = small) }
                        Text(book.help.disclaimer, style = small, color = BookColors.Muted)
                    }

                    GrownUpBox("All Grown-up Corner tips") {
                        content.allStories.forEach { s ->
                            Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(10.dp)).padding(10.dp)) {
                                Text("${s.id}. ${s.title}", style = small, fontWeight = FontWeight.Bold)
                                Text("Ask: \"${s.grownUp.ask}\"", style = small)
                                Text("Tip: ${s.grownUp.tip}", style = small)
                            }
                        }
                    }

                    GrownUpBox("Settings") {
                        Text("Child's name: ${user.name.ifBlank { "(not set)" }} • Reading buddy: ${user.buddy.ifBlank { "(not set)" }}", style = small)
                        Text("Stories read: ${user.readStories.size} of ${content.allStories.size}", style = small)
                        OutlinedButton(onClick = { changingPin = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Text("Change PIN")
                        }
                        OutlinedButton(onClick = { confirmReset = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Text("Reset all data", color = BookColors.HelpRedText)
                        }
                        Text(
                            "Everything typed in this app stays on this device. It is not backed up or sent anywhere. " +
                                "The app has no internet access, ads, accounts or tracking.",
                            style = small, color = BookColors.Muted,
                        )
                    }
                }
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset all data?") },
            text = { Text("This deletes the child's name, answers, Safety Hand, scores, plans, ticks and the PIN. It can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmReset = false
                    vm.resetAll()
                    nav.restart()
                }) { Text("Delete everything", color = BookColors.HelpRedText) }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel") } },
        )
    }
}
