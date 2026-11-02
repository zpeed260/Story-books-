package com.beforeithappens.kidsbook

import com.beforeithappens.kidsbook.data.Bests
import com.beforeithappens.kidsbook.data.BookContent
import com.beforeithappens.kidsbook.data.Pin
import com.beforeithappens.kidsbook.data.ScoreEntry
import com.beforeithappens.kidsbook.data.UserData
import com.beforeithappens.kidsbook.data.CONTENT_VERSION
import com.beforeithappens.kidsbook.data.migrated
import com.beforeithappens.kidsbook.data.spokenLine
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

    private val scenes = setOf("shop", "school", "court", "party", "home", "outside", "beach", "bush", "screen", "plain", "canteen", "car")
    private val faces = setOf("happy", "proud", "sad", "worried", "cross", "surprised", "smirk", "laugh", "neutral")
    private val arms = setOf("down", "wave", "stop", "point", "hold", "up", "hips", "give")
    private val items = setOf("phone", "lolly", "ball", "controller", "invite", "book", "money")

    @Test fun has32EntriesInFiveSections() {
        assertEquals(32, content.allStories.size)
        assertEquals((1..32).toList(), content.allStories.map { it.id })
        assertEquals(
            listOf("Friends and fitting in", "Tricky choices", "Phones, games and group chats", "Big feelings", "Keeping safe"),
            content.parts.map { it.name },
        )
        assertEquals(listOf(8, 9, 6, 6, 3), content.parts.map { content.storiesInPart(it.id).size })
        content.parts.forEach { part ->
            assertEquals(part.startsAtStory, content.storiesInPart(part.id).first().id)
        }
    }

    @Test fun everyEntryIsComplete() {
        content.allStories.forEach { s ->
            assertEquals("entry ${s.id} panels", 3, s.panels.size)
            assertEquals("entry ${s.id} figured", 3, s.figured.size)
            assertTrue("entry ${s.id} say now", s.sayNow.isNotEmpty())
            assertTrue(s.day.isNotBlank() && s.journal.length > 100)
            assertTrue(s.magicWords.isNotBlank() && s.tryIt.isNotBlank())
            assertTrue(s.grownUp.ask.isNotBlank() && s.grownUp.tip.isNotBlank())
        }
        // 'Later' is left out of six entries in the journal.
        assertEquals(26, content.allStories.count { it.later != null })
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
                    sp.speaker?.let { assertTrue("entry ${s.id} speaker $it", it in p.people.indices) }
                    assertTrue(sp.kind == "say" || sp.kind == "think")
                }
            }
        }
    }

    @Test fun panelDescriptionsNameTheSpeaker() {
        val p = content.story(1)!!.panels[0]
        assertEquals("Panel 1: Lunch. Theo in charge (again). Theo says: We're playing MY game. Or you're not my friend.",
            content.describePanel(p, 1))
        assertTrue(content.describePanel(content.story(1)!!.panels[1], 2).contains("Alex thinks:"))
        assertTrue(content.describePanel(content.story(13)!!.panels[0], 1).startsWith("Panel 1: A message from Kai. On screen:"))
    }

    @Test fun sayNowLinesAreSpokenWithoutStageDirections() {
        assertEquals("You do it.", spokenLine("\"You do it.\" (and walk off)"))
        assertEquals("Stop and stay.", spokenLine("Stop and stay."))
        assertEquals("Message the person privately: You OK?".let { "You OK?" }, spokenLine("Message the person privately: \"You OK?\""))
    }

    @Test fun gamesMatchTheJournal() {
        val g = content.stories.games
        assertEquals(12, g.keepOrTell.size)
        assertEquals(listOf(1, 3, 5, 7, 10, 12), g.keepOrTell.withIndex().filter { it.value.isKeep }.map { it.index + 1 })
        assertEquals(27, g.whatIfCards.size)
        assertEquals(27, content.book.whatIfStories.size)
        g.whatIfCards.indices.forEach { assertNotNull(content.storyForWhatIf(it)) }
        assertEquals(8, g.goodOrBossyFriend.good.size)
        assertEquals(8, g.goodOrBossyFriend.bossy.size)
        val a = content.stories.activities
        assertEquals(5, a.calmPlan.size)
        assertEquals(4, a.calmPrompts.size)
        assertEquals(14, a.techRules.size)
        assertEquals(10, a.exitLines.size)
    }

    @Test fun crewAreAllDefined() {
        assertEquals(listOf("alex", "zara", "sam", "kai", "ruby", "theo", "mia"), content.book.crew.map { it.who })
        content.book.crew.forEach { assertNotNull(content.character(it.who)) }
        assertTrue(content.character("sam")!!.glasses)
        assertEquals("7", content.character("kai")!!.number)
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

    @Test fun oldProgressIsClearedForTheNewEdition() {
        val old = UserData(name = "Alex", readStories = setOf(1, 2), magicCollected = setOf(3), myTurn = mapOf(1 to "x"))
        val m = old.migrated()
        assertEquals(CONTENT_VERSION, m.contentVersion)
        assertTrue(m.readStories.isEmpty() && m.magicCollected.isEmpty() && m.myTurn.isEmpty())
        assertEquals("Alex", m.name)
        val fresh = UserData(contentVersion = CONTENT_VERSION, readStories = setOf(5))
        assertEquals(fresh, fresh.migrated())
    }

    @Test fun userDataRoundTrips() {
        val d = UserData(name = "Alex", readStories = setOf(1, 2), myTurn = mapOf(3 to "I'd tell Mum"), knowByHeart = mapOf(0 to "Alex Smith"))
        val json = BookContent.json.encodeToString(UserData.serializer(), d)
        assertEquals(d, BookContent.json.decodeFromString(UserData.serializer(), json))
    }
}
