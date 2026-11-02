package com.beforeithappens.kidsbook.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/* ---------- stories.json (Appendix 1) ---------- */

@Serializable
data class StoriesFile(
    val parts: List<Part>,
    val stories: List<Story>,
    val games: Games,
    val activities: Activities,
)

@Serializable
data class Part(
    val id: Int,
    val startsAtStory: Int,
    val name: String,
    val color: String,
)

@Serializable
data class Story(
    val id: Int,
    val part: Int,
    val title: String,
    val panels: List<Panel>,
    val happening: String,
    val remember: List<String>,
    val sayThis: List<String>,
    val magicWords: String,
    val tryIt: String,
    val myTurnPrompt: String,
    val grownUp: GrownUpCorner,
)

@Serializable
data class Panel(
    val caption: String,
    val scene: String = "plain",
    val people: List<PanelPerson> = emptyList(),
    val speech: List<Speech> = emptyList(),
)

@Serializable
data class PanelPerson(
    val who: String,
    val x: Float = 0.5f,
    val face: String = "neutral",
    val arm: String = "down",
    val facingLeft: Boolean = false,
    val item: String? = null,
    val tears: Boolean = false,
)

@Serializable
data class Speech(
    val text: String,
    val speaker: Int? = null,
    val kind: String = "say",
)

@Serializable
data class GrownUpCorner(val ask: String, val tip: String)

@Serializable
data class Games(
    val surpriseOrTell: List<SurpriseItem>,
    val whatIfCards: List<String>,
    val goodOrBossyFriend: GoodOrBossy,
)

@Serializable
data class SurpriseItem(val statement: String, val answer: String) {
    val isSurprise: Boolean get() = answer.equals("Surprise", ignoreCase = true)
}

@Serializable
data class GoodOrBossy(val good: List<String>, val bossy: List<String>)

@Serializable
data class Activities(
    val knowByHeart: List<String>,
    val techRules: List<String>,
    val calmPlan: List<CalmLevel>,
    val calmPrompts: List<String>,
)

@Serializable
data class CalmLevel(val level: Int, val label: String, val action: String)

/* ---------- book.json (front pages, Part E, characters) ---------- */

@Serializable
data class BookFile(
    val cover: Cover,
    val belongsTo: BelongsTo,
    val crewIntro: String,
    val crewOutro: String,
    val crew: List<CrewMember>,
    val howToUse: List<HowToStep>,
    val alwaysTell: TitledText,
    val grownUpsReading: GrownUpsReading,
    val activityIntros: ActivityIntros,
    val whatIfStories: List<Int>,
    val help: Help,
    val characters: List<CharacterSpec>,
)

@Serializable
data class Cover(val title: String, val subtitle: String, val tagline: String, val features: String)

@Serializable
data class BelongsTo(val nameLabel: String, val buddyLabel: String, val text: String, val tellExplainer: String)

@Serializable
data class CrewMember(val who: String, val text: String)

@Serializable
data class HowToStep(val title: String, val text: String)

@Serializable
data class TitledText(val title: String, val text: String)

@Serializable
data class GrownUpsReading(
    val title: String,
    val points: List<String>,
    val seriousTitle: String,
    val serious: String,
)

@Serializable
data class ActivityIntro(
    val title: String,
    val text: String,
    val talk: String? = null,
    val ideas: String? = null,
    val password: String? = null,
    val breathing: String? = null,
    val extra: String? = null,
    val grownUpSignature: String? = null,
    val mySignature: String? = null,
)

@Serializable
data class ActivityIntros(
    val magicWords: ActivityIntro,
    val goodOrBossy: ActivityIntro,
    val personalBests: ActivityIntro,
    val safetyHand: ActivityIntro,
    val knowByHeart: ActivityIntro,
    val surpriseOrTell: ActivityIntro,
    val whatIf: ActivityIntro,
    val techRules: ActivityIntro,
    val calmPlan: ActivityIntro,
)

@Serializable
data class Help(
    val title: String,
    val intro: String,
    val kids: List<KidHelpLine>,
    val grownUps: List<GrownUpService>,
    val furtherReadingTitle: String,
    val furtherReading: List<String>,
    val disclaimer: String,
)

@Serializable
data class KidHelpLine(val name: String, val number: String, val text: String, val web: String? = null)

@Serializable
data class GrownUpService(val name: String, val contact: String, val isPhone: Boolean)

@Serializable
data class CharacterSpec(
    val key: String,
    val name: String,
    val shortName: String? = null,
    val skin: String,
    val hair: String,
    val hairStyle: String,
    val top: String,
    val legs: String,
    val height: Float = 1f,
    val glasses: Boolean = false,
) {
    /** Name used when reading a speech bubble aloud, e.g. "Theo" or "A grown-up". */
    val spokenName: String get() = shortName ?: name
}

/* ---------- Loaded content ---------- */

class BookContent(val stories: StoriesFile, val book: BookFile) {
    private val characterMap = book.characters.associateBy { it.key }

    val parts: List<Part> get() = stories.parts
    val allStories: List<Story> get() = stories.stories

    fun story(id: Int): Story? = stories.stories.firstOrNull { it.id == id }
    fun part(id: Int): Part? = stories.parts.firstOrNull { it.id == id }
    fun storiesInPart(partId: Int): List<Story> = stories.stories.filter { it.part == partId }
    fun character(key: String): CharacterSpec? = characterMap[key]

    /** Story a What If? card links to, or null if the mapping is missing. */
    fun storyForWhatIf(index: Int): Story? = book.whatIfStories.getOrNull(index)?.let { story(it) }

    /** Plain-language description of a comic panel for TalkBack and 'Read to me'. */
    fun describePanel(panel: Panel, number: Int): String = buildString {
        append("Panel ").append(number).append(": ").append(panel.caption).append(". ")
        panel.speech.forEach { s -> append(describeSpeech(panel, s)).append(" ") }
    }.trim()

    fun describeSpeech(panel: Panel, s: Speech): String {
        val who = s.speaker?.let { panel.people.getOrNull(it) }?.let { character(it.who)?.spokenName ?: it.who }
        return when {
            who == null -> "On screen: ${s.text}"
            s.kind == "think" -> "$who thinks: ${s.text}"
            else -> "$who says: ${s.text}"
        }
    }

    companion object {
        val json = Json { ignoreUnknownKeys = true; isLenient = true }

        fun parse(storiesJson: String, bookJson: String): BookContent =
            BookContent(
                json.decodeFromString(StoriesFile.serializer(), storiesJson),
                json.decodeFromString(BookFile.serializer(), bookJson),
            )
    }
}
