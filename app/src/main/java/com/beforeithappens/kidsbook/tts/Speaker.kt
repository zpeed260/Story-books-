package com.beforeithappens.kidsbook.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

/**
 * Thin wrapper around the on-device TextToSpeech engine (en-AU, falling back to the
 * device default). Nothing is recorded and nothing leaves the device.
 */
class Speaker(context: Context) : TextToSpeech.OnInitListener {
    private val tts = TextToSpeech(context.applicationContext, this)
    private var ready = false
    private var pending: List<String>? = null

    /** Id of the thing currently being read, so the UI can show a 'stop' state. */
    val speakingId = mutableStateOf<String?>(null)

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        val au = Locale("en", "AU")
        val result = tts.setLanguage(au)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.setLanguage(Locale.getDefault())
        }
        tts.setSpeechRate(0.9f)
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                if (utteranceId?.endsWith("#last") == true) speakingId.value = null
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { speakingId.value = null }
        })
        ready = true
        pending?.let { speakAll(it, speakingId.value ?: "pending") }
        pending = null
    }

    fun speak(text: String, id: String = text) = speakAll(listOf(text), id)

    fun speakAll(texts: List<String>, id: String) {
        val clean = texts.map { it.trim() }.filter { it.isNotEmpty() }
        if (clean.isEmpty()) return
        speakingId.value = id
        if (!ready) {
            pending = clean
            return
        }
        tts.stop()
        clean.forEachIndexed { i, t ->
            val uid = if (i == clean.lastIndex) "$id#last" else "$id#$i"
            tts.speak(t, if (i == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, uid)
        }
    }

    fun stop() {
        pending = null
        speakingId.value = null
        if (ready) tts.stop()
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}

val LocalSpeaker = staticCompositionLocalOf<Speaker> { error("No Speaker provided") }
