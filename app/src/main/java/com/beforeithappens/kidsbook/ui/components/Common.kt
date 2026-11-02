package com.beforeithappens.kidsbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.beforeithappens.kidsbook.tts.LocalSpeaker
import com.beforeithappens.kidsbook.ui.theme.BookColors
import com.beforeithappens.kidsbook.ui.theme.forWhiteText
import kotlinx.coroutines.delay

/**
 * Screen frame for everything in the kids' area: a back arrow, a title, a Home button and an
 * always-visible red 'Who can help' button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidScaffold(
    title: String,
    color: Color = BookColors.Friends,
    onBack: (() -> Unit)?,
    onHome: (() -> Unit)?,
    onHelp: (() -> Unit)?,
    content: @Composable (PaddingValues) -> Unit,
) {
    val barColor = color.forWhiteText()
    Scaffold(
        containerColor = BookColors.Cream,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = barColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                ),
                title = {
                    Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 2,
                        overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (onHome != null) {
                        IconButton(onClick = onHome) { Icon(Icons.Filled.Home, contentDescription = "Home") }
                    }
                },
            )
        },
        bottomBar = {
            if (onHelp != null) HelpBar(onHelp)
        },
        content = content,
    )
}

@Composable
fun HelpBar(onHelp: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(BookColors.Cream).padding(horizontal = 16.dp, vertical = 8.dp)) {
        Button(
            onClick = onHelp,
            colors = ButtonDefaults.buttonColors(containerColor = BookColors.HelpRedText, contentColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        ) {
            Icon(Icons.Filled.SupportAgent, contentDescription = null)
            Spacer(Modifier.width(10.dp))
            Text("Who can help", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}

/** Standard scrolling column used inside screens. */
@Composable
fun ScreenColumn(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@Composable
fun SectionCard(
    title: String? = null,
    icon: ImageVector? = null,
    background: Color = BookColors.Card,
    titleColor: Color = BookColors.Ink,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0x1F000000), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (title != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = titleColor, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(title, style = MaterialTheme.typography.titleLarge, color = titleColor,
                    modifier = Modifier.semantics { heading() })
            }
        }
        content()
    }
}

/** Button that reads text aloud with on-device text-to-speech; tap again to stop. */
@Composable
fun ReadToMeButton(id: String, texts: () -> List<String>, label: String = "Read to me", modifier: Modifier = Modifier) {
    val speaker = LocalSpeaker.current
    val speaking = speaker.speakingId.value == id
    OutlinedButton(
        onClick = { if (speaking) speaker.stop() else speaker.speakAll(texts(), id) },
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.heightIn(min = 48.dp),
    ) {
        Icon(if (speaking) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(if (speaking) "Stop reading" else label)
    }
}

@Composable
fun BigButton(
    text: String,
    onClick: () -> Unit,
    color: Color = BookColors.Friends,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = color.forWhiteText(), contentColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.heightIn(min = 56.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
    }
}

@Composable
fun Bullet(text: String, mark: String = "•", style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge) {
    Row {
        Text(mark, style = style, modifier = Modifier.width(28.dp))
        Text(text, style = style)
    }
}

/** Grown-up text block: visually separate, grey, smaller. */
@Composable
fun GrownUpBox(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .fillMaxWidth()
            .background(BookColors.GrownUp, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = BookColors.Muted,
            modifier = Modifier.semantics { heading() })
        content()
    }
}

/**
 * Asks for the grown-up PIN (or lets a grown-up create one) before showing [content].
 */
@Composable
fun PinGate(
    hasPin: Boolean,
    unlocked: Boolean,
    checkPin: (String) -> Boolean,
    createPin: (String) -> Unit,
    onUnlocked: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (unlocked) {
        content()
        return
    }
    var pin by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("🔒", style = MaterialTheme.typography.displaySmall)
        Text(
            if (hasPin) "Grown-ups only. Please enter the 4-digit PIN." else "Grown-ups: make a 4-digit PIN to keep this page private.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) pin = it; error = null },
            label = { Text("PIN") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
        )
        if (!hasPin) {
            OutlinedTextField(
                value = confirm,
                onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) confirm = it; error = null },
                label = { Text("Type the PIN again") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        error?.let { Text(it, color = BookColors.HelpRedText, style = MaterialTheme.typography.bodyMedium) }
        BigButton(
            text = if (hasPin) "Unlock" else "Save PIN",
            onClick = {
                when {
                    pin.length != 4 -> error = "The PIN needs 4 numbers."
                    hasPin && checkPin(pin) -> onUnlocked()
                    hasPin -> { error = "That PIN isn't right. Try again."; pin = "" }
                    pin != confirm -> error = "The two PINs don't match."
                    else -> { createPin(pin); onUnlocked() }
                }
            },
            color = BookColors.Safe,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Small 'Well done!' style message that disappears after a moment. */
@Composable
fun rememberFlash(): Pair<String?, (String) -> Unit> {
    var msg by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(msg) {
        if (msg != null) {
            delay(1800)
            msg = null
        }
    }
    return msg to { m: String -> msg = m }
}

@Composable
fun Pill(text: String, color: Color, modifier: Modifier = Modifier, minHeight: Dp = 0.dp) {
    Box(
        modifier.background(color, RoundedCornerShape(50)).heightIn(min = minHeight).padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun SmallTextButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.heightIn(min = 48.dp)) { Text(text) }
}
