package com.beforeithappens.kidsbook

import com.beforeithappens.kidsbook.data.Bests
import com.beforeithappens.kidsbook.data.BookContent
import com.beforeithappens.kidsbook.data.Pin
import com.beforeithappens.kidsbook.data.ScoreEntry
import com.beforeithappens.kidsbook.data.UserData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ContentTest {
    private val content = BookContent.parse(
        File("src/main/assets/stories.json").readText(),
        File("src/main/assets/book.json").readText(),
    )

    private val scenes = setOf("shop", "school", "court", "party", "home", "outside", "beach", "bush", "screen", "plain")
    private val faces = setOf("happy", "proud", "sad", "worried", "cross", "surprised", "smirk", "laugh", "neutral")
    private val arms = setOf("down", "wave", "stop", "point", "hold", "up", "hips", "give")
    private val items = setOf("phone", "lolly", "ball", "controller", "invite", "book")

    @Test fun hasAll22StoriesInThreeParts() {
        assertEquals(22, content.allStories.size)
        assertEquals((1..22).toList(), content.allStories.map { it.id })
        assertEquals(3, content.parts.size)
        content.parts.forEach { part ->
            assertEquals(part.startsAtStory, content.storiesInPart(part.id).first().id)
        }
    }

    @Test fun everyStoryIsComplete() {
        content.allStories.forEach { s ->
            assertEquals("story ${s.id} panels", 3, s.panels.size)
            assertEquals("story ${s.id} remember", 3, s.remember.size)
            assertTrue("story ${s.id} say this", s.sayThis.isNotEmpty())
            assertTrue(s.magicWords.isNotBlank() && s.happening.isNotBlank() && s.tryIt.isNotBlank())
            assertTrue(s.grownUp.ask.isNotBlank() && s.grownUp.tip.isNotBlank())
        }
    }

    @Test fun comicDataIsDrawable() {
        content.allStories.forEach { s ->
            s.panels.forEach { p ->
                assertTrue("scene ${p.scene}", p.scene in scenes)
                p.people.forEach { person ->
                    assertNotNull("character ${person.who}", content.character(person.who))
                    assertTrue("face ${person.face}", person.face in faces)
                    assertTrue("arm ${person.arm}", person.arm in arms)
                    person.item?.let { assertTrue("item $it", it in items) }
                    assertTrue(person.x in 0f..1f)
                }
                p.speech.forEach { sp ->
                    sp.speaker?.let { assertTrue("story ${s.id} speaker $it", it in p.people.indices) }
                    assertTrue(sp.kind == "say" || sp.kind == "think")
                }
            }
        }
    }

    @Test fun panelDescriptionsNameTheSpeaker() {
        val p = content.story(1)!!.panels[0]
        assertEquals("Panel 1: At lunch. Theo says: Do what I say or you're not my friend!", content.describePanel(p, 1))
        val think = content.story(1)!!.panels[1]
        assertTrue(content.describePanel(think, 2).contains("Sam thinks:"))
    }

    @Test fun gamesMatchTheBook() {
        val g = content.stories.games
        assertEquals(11, g.surpriseOrTell.size)
        assertEquals(4, g.surpriseOrTell.count { it.isSurprise })
        assertEquals(27, g.whatIfCards.size)
        assertEquals(27, content.book.whatIfStories.size)
        g.whatIfCards.indices.forEach { assertNotNull(content.storyForWhatIf(it)) }
        assertEquals(7, g.goodOrBossyFriend.good.size)
        assertEquals(7, g.goodOrBossyFriend.bossy.size)
        assertEquals(5, content.stories.activities.calmPlan.size)
        assertEquals(4, content.stories.activities.calmPrompts.size)
    }

    @Test fun crewAreAllDefined() {
        assertEquals(6, content.book.crew.size)
        content.book.crew.forEach { assertNotNull(content.character(it.who)) }
        assertTrue(content.character("sam")!!.glasses)
    }

    @Test fun kidsHelpNumbersAreDialable() {
        val kids = content.book.help.kids
        assertEquals(listOf("000", "1800 55 1800"), kids.map { it.number })
    }

    @Test fun personalBestsHighlightsEachNewBest() {
        val e = listOf(3.0, 5.0, 4.0, 5.0, 7.0).mapIndexed { i, v -> ScoreEntry("2026-01-0${i + 1}", v) }
        assertEquals(setOf(0, 1, 4), Bests.newBestIndices(e))
        assertEquals(emptySet<Int>(), Bests.newBestIndices(emptyList()))
    }

    @Test fun pinIsHashedAndChecked() {
        val salt = Pin.newSalt()
        val data = UserData(pinSalt = salt, pinHash = Pin.hash("1234", salt))
        assertTrue(Pin.matches("1234", data))
        assertFalse(Pin.matches("4321", data))
        assertFalse(Pin.matches("1234", UserData()))
        assertTrue(Pin.isValid("0007"))
        assertFalse(Pin.isValid("12a4"))
        assertFalse(data.pinHash!!.contains("1234"))
    }

    @Test fun userDataRoundTrips() {
        val d = UserData(name = "Alex", readStories = setOf(1, 2), myTurn = mapOf(3 to "I'd tell Mum"), knowByHeart = mapOf(0 to "Alex Smith"))
        val json = BookContent.json.encodeToString(UserData.serializer(), d)
        assertEquals(d, BookContent.json.decodeFromString(UserData.serializer(), json))
    }
}
