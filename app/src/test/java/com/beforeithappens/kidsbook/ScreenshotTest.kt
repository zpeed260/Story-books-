package com.beforeithappens.kidsbook

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.beforeithappens.kidsbook.ui.theme.BookColors
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.beforeithappens.kidsbook.data.BookContent
import com.beforeithappens.kidsbook.tts.LocalSpeaker
import com.beforeithappens.kidsbook.tts.Speaker
import com.beforeithappens.kidsbook.ui.comic.ComicPanel
import com.beforeithappens.kidsbook.ui.BookApp
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.beforeithappens.kidsbook.ui.theme.BookTheme
import com.beforeithappens.kidsbook.ui.theme.parseHex
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Renders every story's comic to PNG files in app/build/screenshots so the art can be
 * checked without a device. Run with: ./gradlew :app:testDebugUnitTest --tests '*ScreenshotTest*'
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h1600dp-xhdpi")
class ScreenshotTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private val content = BookContent.parse(
        File("src/main/assets/stories.json").readText(),
        File("src/main/assets/book.json").readText(),
    )
    private val outDir = File("build/screenshots").apply { mkdirs() }

    private fun save(name: String) {
        compose.waitForIdle()
        val view = compose.activity.window.decorView
        val bmp = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bmp))
        File(outDir, "$name.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    /** Sets content once, then steps through [count] pages, saving each one. */
    private fun shootAll(prefix: String, count: Int, page: @Composable (Int) -> Unit) {
        val speaker = Speaker(ApplicationProvider.getApplicationContext())
        var index by mutableIntStateOf(0)
        compose.setContent {
            BookTheme { CompositionLocalProvider(LocalSpeaker provides speaker) { page(index) } }
        }
        for (i in 0 until count) {
            index = i
            save("%s_%02d".format(prefix, i + 1))
        }
    }

    @Test fun storyComics() {
        shootAll("comic", content.allStories.size) { i ->
            val story = content.allStories[i]
            val color = parseHex(content.part(story.part)!!.color)
            Column(
                Modifier.fillMaxSize().background(BookColors.Cream).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                story.panels.forEachIndexed { n, p -> ComicPanel(content, p, n + 1, color, onTap = {}) }
            }
        }
    }

    /** Walks through the real app, saving a picture of each main screen. */
    @Test fun appFlow() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = BookViewModel(app)
        val speaker = Speaker(app)
        compose.setContent {
            BookTheme { CompositionLocalProvider(LocalSpeaker provides speaker) { BookApp(vm) } }
        }
        compose.waitUntil(5000) { vm.user.value != null }
        save("app_01_cover")
        compose.onNodeWithText("Let's start").performClick()
        compose.onNodeWithText("This book belongs to:").performTextInput("Alex")
        save("app_02_belongs")
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText("Kai").performClick()
        save("app_03_avatar")
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText("Open my book").performClick()
        compose.waitForIdle()
        save("app_04_home")
        compose.onNodeWithText("Keeping safe").performClick()
        save("app_05_part")
        compose.onNodeWithText("You're the boss of your body").performClick()
        compose.mainClock.advanceTimeBy(3000)
        save("app_06_story_top")
        compose.onNodeWithText("Got it!").performScrollTo()
        save("app_07_story_magic")
        compose.onNodeWithText("Grown-up Corner (for grown-ups)").performScrollTo().performClick()
        compose.onNodeWithText("I've read this story").performScrollTo()
        save("app_08_story_bottom")
        compose.onNodeWithText("Who can help").performClick()
        save("app_09_help")
        compose.onNodeWithContentDescription("Home").performClick()
        compose.onNodeWithText("Activities").performClick()
        save("app_10_activities")
        compose.onNodeWithText("Surprise or Tell?").performClick()
        compose.onNodeWithText("Tell!").performClick()
        save("app_11_surprise")
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("My Calm-Down Plan").performClick()
        save("app_12_calm")
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("Things I Know By Heart").performClick()
        save("app_13_pin")
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("My Safety Hand").performClick()
        save("app_14_hand")
    }
}
