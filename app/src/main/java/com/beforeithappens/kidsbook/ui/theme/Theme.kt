package com.beforeithappens.kidsbook.ui.theme

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.beforeithappens.kidsbook.R

object BookColors {
    val Friends = Color(0xFF2E7D7A)
    val Feelings = Color(0xFFD9822B)
    val Safe = Color(0xFF7E57C2)
    val Magic = Color(0xFFFFE08A)
    val TryIt = Color(0xFFEEF7F5)
    val GrownUp = Color(0xFFF1F3F6)
    val Cream = Color(0xFFFFFBF2)
    val Ink = Color(0xFF2B2B2B)
    val HelpRed = Color(0xFFE5533D)
    /** Darker red used for text on light backgrounds (meets 4.5:1 contrast). */
    val HelpRedText = Color(0xFFB8321F)
    val Muted = Color(0xFF5F6368)
    val Card = Color.White
    val Good = Color(0xFF2E7D32)
}

/** Darker shade of the orange part colour, for text and white-on-colour contrast. */
val FeelingsDark = Color(0xFFA85A12)

fun parseHex(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(BookColors.Friends)

/**
 * Part colours are used as backgrounds behind white text. Orange #D9822B is too light
 * for white body text, so we darken any colour that would fail contrast.
 */
fun Color.forWhiteText(): Color {
    fun lum(c: Float) = if (c <= 0.03928f) c / 12.92f else Math.pow(((c + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
    var c = this
    repeat(8) {
        val l = 0.2126f * lum(c.red) + 0.7152f * lum(c.green) + 0.0722f * lum(c.blue)
        if ((1.05f) / (l + 0.05f) >= 4.5f) return c
        c = Color(c.red * 0.88f, c.green * 0.88f, c.blue * 0.88f, c.alpha)
    }
    return c
}

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_bold, FontWeight.Bold),
)

/** Handwriting font for Alex's journal bits, headings, speech bubbles and Magic Words. */
val Hand = FontFamily(Font(R.font.patrick_hand, FontWeight.Normal))

/** Text style for Alex's handwriting. */
val HandStyle = TextStyle(fontFamily = Hand, fontSize = 21.sp, lineHeight = 30.sp)

/** Notebook paper: faint blue ruled lines and a red margin, like the printed journal. */
fun Modifier.notebookPaper(lineSpacing: Dp = 30.dp, margin: Boolean = true): Modifier = this
    .background(Paper)
    .drawBehind {
        val step = lineSpacing.toPx()
        var y = step
        while (y < size.height) {
            drawLine(PaperLine, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
            y += step
        }
        if (margin) drawLine(PaperMargin, Offset(14.dp.toPx(), 0f), Offset(14.dp.toPx(), size.height), 1.5.dp.toPx())
    }

val Paper = Color(0xFFFFFDF7)
val PaperLine = Color(0xFFDCE7F3)
val PaperMargin = Color(0xFFF3B9B9)

// No colour here: text takes its colour from the surface it sits on (ink on cream, white on buttons).
private val base = TextStyle(fontFamily = Poppins)

val KidsTypography = Typography(
    // Big headings use Alex's handwriting, like the printed journal.
    displaySmall = TextStyle(fontFamily = Hand, fontSize = 40.sp, lineHeight = 46.sp),
    headlineMedium = TextStyle(fontFamily = Hand, fontSize = 32.sp, lineHeight = 38.sp),
    headlineSmall = TextStyle(fontFamily = Hand, fontSize = 28.sp, lineHeight = 34.sp),
    titleLarge = base.copy(fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold),
    titleMedium = base.copy(fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.Medium),
    titleSmall = base.copy(fontSize = 18.sp, lineHeight = 26.sp, fontWeight = FontWeight.Medium),
    bodyLarge = base.copy(fontSize = 18.sp, lineHeight = 27.sp),
    bodyMedium = base.copy(fontSize = 18.sp, lineHeight = 26.sp),
    // Grown-up text may be smaller (14sp).
    bodySmall = base.copy(fontSize = 14.sp, lineHeight = 21.sp),
    labelLarge = base.copy(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium),
    labelMedium = base.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium),
    labelSmall = base.copy(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
)

private val scheme = lightColorScheme(
    primary = BookColors.Friends,
    onPrimary = Color.White,
    secondary = BookColors.Safe,
    onSecondary = Color.White,
    tertiary = FeelingsDark,
    background = BookColors.Cream,
    onBackground = BookColors.Ink,
    surface = BookColors.Cream,
    onSurface = BookColors.Ink,
    surfaceVariant = BookColors.GrownUp,
    onSurfaceVariant = BookColors.Muted,
    error = BookColors.HelpRedText,
)

@Composable
fun BookTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, typography = KidsTypography, content = content)
}
