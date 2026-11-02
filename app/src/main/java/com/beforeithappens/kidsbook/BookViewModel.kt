package com.beforeithappens.kidsbook

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.beforeithappens.kidsbook.data.BookContent
import com.beforeithappens.kidsbook.data.Pin
import com.beforeithappens.kidsbook.data.UserData
import com.beforeithappens.kidsbook.data.UserStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookViewModel(app: Application) : AndroidViewModel(app) {

    /** Book content, loaded from the JSON assets so editing them changes the app. */
    val content: BookContent = BookContent.parse(
        app.assets.open("stories.json").bufferedReader().use { it.readText() },
        app.assets.open("book.json").bufferedReader().use { it.readText() },
    )

    private val store = UserStore(app)

    /** Null until the saved data has been read for the first time. */
    val user: StateFlow<UserData?> = store.data.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** PIN unlock lasts until the app goes into the background. */
    var unlocked by mutableStateOf(false)
        private set

    fun update(transform: (UserData) -> UserData) {
        viewModelScope.launch { store.update(transform) }
    }

    fun checkPin(pin: String): Boolean = user.value?.let { Pin.matches(pin, it) } == true

    fun createPin(pin: String) {
        if (!Pin.isValid(pin)) return
        val salt = Pin.newSalt()
        update { it.copy(pinSalt = salt, pinHash = Pin.hash(pin, salt)) }
    }

    fun unlock() { unlocked = true }
    fun lock() { unlocked = false }

    /** Wipes everything the child has typed, plus the PIN, and returns to onboarding. */
    fun resetAll() {
        viewModelScope.launch { store.reset() }
        unlocked = false
    }
}
