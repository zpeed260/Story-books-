package com.beforeithappens.kidsbook.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import java.security.MessageDigest
import java.security.SecureRandom

/** Everything the child (or their grown-up) types. Stored only on this device. */
@Serializable
data class UserData(
    /** Which edition of the book the progress below belongs to (2 = Alex's journal). */
    val contentVersion: Int = 1,
    val onboarded: Boolean = false,
    val name: String = "",
    val avatar: String = "zara",
    val buddy: String = "",
    val readStories: Set<Int> = emptySet(),
    val favourites: Set<Int> = emptySet(),
    val magicCollected: Set<Int> = emptySet(),
    val myTurn: Map<Int, String> = emptyMap(),
    val safetyHand: List<HandPerson> = List(5) { HandPerson() },
    val knowByHeart: Map<Int, String> = emptyMap(),
    val bests: List<Skill> = emptyList(),
    val calmLevel: Int? = null,
    val calmAnswers: Map<Int, String> = emptyMap(),
    val techTicks: Set<Int> = emptySet(),
    val schoolMinutes: String = "",
    val weekendMinutes: String = "",
    val extraRules: String = "",
    val grownUpSignature: String = "",
    val mySignature: String = "",
    val exitFavourites: Set<Int> = emptySet(),
    val ownExitLines: String = "",
    val quietTellGrownUps: String = "",
    val pinHash: String? = null,
    val pinSalt: String? = null,
) {
    val hasPin: Boolean get() = pinHash != null
}

@Serializable
data class HandPerson(val name: String = "", val phone: String = "")

@Serializable
data class Skill(val name: String, val entries: List<ScoreEntry> = emptyList())

@Serializable
data class ScoreEntry(val date: String, val score: Double)

const val CONTENT_VERSION = 2

/**
 * Progress tied to story numbers (read ticks, favourites, Magic Words, My Turn answers) can't
 * carry over from the 22-story edition to the 32-entry journal, so it starts fresh. Everything
 * else the child typed (name, Safety Hand, plans, scores, PIN) is kept.
 */
fun UserData.migrated(): UserData =
    if (contentVersion >= CONTENT_VERSION) this
    else copy(
        contentVersion = CONTENT_VERSION,
        readStories = emptySet(),
        favourites = emptySet(),
        magicCollected = emptySet(),
        myTurn = emptyMap(),
    )

private fun decode(raw: String?): UserData =
    raw?.let { runCatching { BookContent.json.decodeFromString(UserData.serializer(), it) }.getOrNull() }?.migrated()
        ?: UserData(contentVersion = CONTENT_VERSION)

private val Context.dataStore by preferencesDataStore(name = "kids_book")
private val USER_KEY = stringPreferencesKey("user_data")

class UserStore(private val context: Context) {

    val data: Flow<UserData> = context.dataStore.data.map { prefs -> decode(prefs[USER_KEY]) }

    suspend fun update(transform: (UserData) -> UserData) {
        context.dataStore.edit { prefs ->
            val current = decode(prefs[USER_KEY])
            prefs[USER_KEY] = BookContent.json.encodeToString(UserData.serializer(), transform(current))
        }
    }

    suspend fun reset() {
        context.dataStore.edit { it.clear() }
    }
}

object Pin {
    fun newSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    fun hash(pin: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest((salt + ":" + pin).toByteArray()).toHex()
    }

    fun matches(pin: String, data: UserData): Boolean {
        val salt = data.pinSalt ?: return false
        return hash(pin, salt) == data.pinHash
    }

    fun isValid(pin: String): Boolean = pin.length == 4 && pin.all { it.isDigit() }

    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }
}

/** Personal Bests helpers, kept pure so they can be unit tested. */
object Bests {
    /** Indices of entries that set a new best (the first entry always counts). */
    fun newBestIndices(entries: List<ScoreEntry>): Set<Int> {
        val result = mutableSetOf<Int>()
        var best = Double.NEGATIVE_INFINITY
        entries.forEachIndexed { i, e ->
            if (e.score > best) {
                best = e.score
                result += i
            }
        }
        return result
    }
}
