package com.beforeithappens.kidsbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.beforeithappens.kidsbook.tts.LocalSpeaker
import com.beforeithappens.kidsbook.tts.Speaker
import com.beforeithappens.kidsbook.ui.BookApp
import com.beforeithappens.kidsbook.ui.theme.BookTheme

class MainActivity : ComponentActivity() {
    private val vm: BookViewModel by viewModels()
    private lateinit var speaker: Speaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        speaker = Speaker(this)
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                // Leaving the app re-locks the grown-up pages and stops any reading aloud.
                vm.lock()
                speaker.stop()
            }
        })
        setContent {
            BookTheme {
                CompositionLocalProvider(LocalSpeaker provides speaker) {
                    BookApp(vm)
                }
            }
        }
    }

    override fun onDestroy() {
        speaker.shutdown()
        super.onDestroy()
    }
}
