# Before It Happens: The Kids' Book

*What to say and do when things get tricky. A read-together book for kids aged 8 to 11, and the full content brief for building it as an interactive Android app.*

> **How to use this file with Claude Code:** Part A is the app brief (what to build). Parts B to E are the complete content, written exactly as it should appear to kids and grown-ups. Appendix 1 has the same 22 stories as structured JSON so the app can load them as data. All characters, stories and wording are original to this project.

## Contents

- Part A: App brief for Claude Code
- Part B: Front pages (welcome, meet the crew, how to use this book, grown-up notes)
- Part C: The 22 stories
- Part D: Activity pages
- Part E: Who can help
- Appendix 1: Stories as JSON
- Appendix 2: Character and visual design reference

---

# Part A: App brief for Claude Code

## Goal

Build an Android app version of *Before It Happens: The Kids' Book*. Kids aged 8 to 11 should be able to read it on their own or with a parent, practise what to say, play the games, and keep a few personal pages (Safety Hand, Personal Bests, Calm-Down Plan). It should feel friendly, simple and safe, like a picture book, not a lesson.

## Audience and principles

- **Primary user:** kids aged 8 to 11 (girls and boys). Reading level: simple, short sentences. Every screen must make sense to a confident 8-year-old reader.
- **Secondary user:** a parent or carer reading along. Grown-up content is visually separate and smaller.
- **Gender-neutral:** the cast is mixed; never label activities as for boys or girls.
- **Calm, not scary.** Safety content stays gentle. No frightening imagery.
- **Offline-first and private.** No accounts, no ads, no analytics, no network calls, no chat, no sharing to social media. Everything a child types stays on the device.
- **Accessible:** large tap targets (min 48dp), supports system font scaling, TalkBack labels on every image and button, sufficient colour contrast, no information conveyed by colour alone.

## Recommended stack

- Kotlin + Jetpack Compose, Material 3, minimum SDK 26, target the latest stable SDK.
- Content bundled as JSON in `assets/` (see Appendix 1) and loaded at startup; no hard-coded story text in UI code.
- Local storage with Room or DataStore for the child's own entries (Safety Hand, Personal Bests, Calm-Down Plan, My Turn answers, tech-rule ticks, favourites, progress).
- Text-to-speech (Android `TextToSpeech`, en-AU) for a 'Read to me' button on every story screen and every 'Say this' line.
- Comics drawn in Compose `Canvas` from the character spec in Appendix 2 (simple shapes: round head, rounded-rectangle body, line limbs, a few facial expressions). No bitmap art is required. Keep each panel's layout data-driven from the JSON.
- Font: Poppins (Regular, Medium, Bold) bundled from Google Fonts (OFL licence).

## Screens

1. **Welcome / onboarding** (first launch): 'This book belongs to' (child types their name), choose an avatar from the crew, enter a 'reading buddy' (a trusted grown-up's name). Short explainer: "You can always tell your grown-up anything."
2. **Home:** three big coloured tiles for the parts (Me and my friends, Me and my feelings, Keeping safe), a 'Magic Words' tile, an 'Activities' tile, and a 'Who can help' button that is always visible.
3. **Part list:** story cards with number, title, a mini comic thumbnail, and a tick when read.
4. **Story screen** (one per story, scrolls vertically or as swipeable steps):
   - Comic: three panels, tap a panel to hear the speech bubble read aloud. Panels can animate in one at a time.
   - What's happening?
   - Remember (three ticked points)
   - Say this: each line in a large speech-bubble button. Tap to hear it; a 'Say it out loud' microphone button lets the child practise (speech recognition is optional and must work offline or be skipped; never record or store audio).
   - Magic Words: big yellow card with a 'Got it!' button that adds it to their Magic Words collection.
   - Try it! (the game or activity).
   - My Turn: a text box (saved locally) for 'Has this happened to you or someone you know? What would you say or do?'
   - Grown-up Corner: collapsed by default, labelled clearly for grown-ups, shows the question to ask and the tip.
5. **Magic Words:** all 22 lines; tap to flip a card (story title on front, Magic Words on back). Includes a 'Quiz me' mode: show the story title, child recalls the words, then flips.
6. **What If? game:** shuffle the 27 What If cards one at a time; the child says what they'd do, then can tap 'Show me' to jump to the matching story.
7. **Surprise or Tell? game:** 11 statements, two big buttons (Surprise! / Tell!), friendly feedback, score at the end. Correct answers in the data.
8. **Good Friend or Bossy Friend? game:** sort the 14 statements into two piles (tap or drag).
9. **My Safety Hand:** a hand graphic with five fingers; the child names a trusted grown-up for each finger (phone number optional, stored on-device only).
10. **Things I Know By Heart:** fields for full name, parent names and mobiles, address, family password, plus the fixed lines. Include a 'Practise' mode that hides each answer until tapped. Protect this screen with a simple parent-set PIN because it holds personal details.
11. **My Personal Bests:** add a skill, log a dated score, highlight each new best, simple line chart.
12. **My Calm-Down Plan:** the 1 to 5 feelings scale (tap your number), the child's own answers to the four prompts, and a guided 'flower and candle' breathing animation (5 breaths).
13. **Our Tech Rules:** checklist the family ticks together, plus 'our extra rules' and screen-time limits.
14. **Who can help:** 000 and Kids Helpline as large tap-to-call buttons (use `ACTION_DIAL`, never auto-call), then the grown-ups list.
15. **Grown-ups area** (behind the PIN): the 'For grown-ups: reading together' notes, 'If your child tells you something serious', all Grown-up Corner tips in one list, reset data, and change PIN.

## Behaviour and safety requirements

- Never transmit or back up the child's entries off the device. Exclude app data from cloud backup (`android:allowBackup="false"` or backup rules).
- Phone numbers open the dialler only; the child must press call themselves.
- No external links inside the kids' area. Website addresses in Part E appear in the grown-ups area only.
- No in-app purchases, ads, notifications that pressure the child, or streak mechanics that punish missed days. Gentle progress ticks are fine.
- Every screen in the kids' area has a visible way back to Home and to 'Who can help'.

## Visual style

- Part colours: Me and my friends `#2E7D7A` (teal), Me and my feelings `#D9822B` (orange), Keeping safe `#7E57C2` (purple).
- Accents: Magic Words cards `#FFE08A`, Try it! `#EEF7F5`, Grown-up Corner `#F1F3F6`, cream backgrounds `#FFFBF2`, ink `#2B2B2B`, help red `#E5533D`.
- Rounded corners everywhere (12 to 16dp), generous spacing, one idea per card.
- Body text 18sp minimum in the kids' area; grown-up text can be 14sp.
- Characters and backgrounds: see Appendix 2.

## Acceptance checklist

- All 22 stories, the front pages, the activity pages and Who can help are present, with wording exactly as in Parts B to E.
- Stories load from the JSON asset; editing the JSON changes the app without code changes.
- Works fully offline; no network permission in the manifest.
- TalkBack reads every comic panel (caption + speech) and every button.
- Personal data screens are PIN-protected and survive app restarts.

---

# Part B: Front pages

## Cover

**Before It Happens**  
**The Kids' Book**  
What to say and do when things get tricky

22 real situations • comics • words to say • games to play • for ages 8 to 11

*Illustration: the six crew members standing in a row on grass, waving and smiling, with confetti dots in the sky.*

## This book belongs to

This book belongs to: ____________________

My reading buddy (a grown-up I trust): ____________________

Read this book together with your grown-up, one story at a time. Then keep it somewhere you can find it. Whenever something tricky happens, you can read it again on your own.

## Meet the crew

These friends pop up in the stories. They're not perfect. They get things wrong, feel big feelings, and figure things out, just like everyone.

- **Zara:** Loves drawing and basketball. Sometimes worries about fitting in.
- **Sam:** Funny, kind, and just got new glasses (always drawn wearing glasses).
- **Kai:** Gamer and joker. Has a big volcano temper sometimes.
- **Ruby:** Fast runner, honest, and hates losing.
- **Theo:** Loves Lego and hoops. Has a little sister who drives him bonkers.
- **Mia:** Loves animals and swimming. Knows she's the boss of her body.

You'll also meet some other kids, older kids and grown-ups along the way. Who do YOU think you're most like?

## How to use this book

1. **Read the comic.** See what happens to the crew.
2. **Talk about it.** Has anything like this happened to you or someone you know?
3. **Say the words out loud.** The 'Say this' bubbles are words you can really use. Saying them out loud makes them easier to say for real.
4. **Learn the Magic Words.** Every story has a few Magic Words to remember. They're all on one page at the back too.
5. **Play the 'Try it!' game.** Practise with your grown-up. Swap roles and test them!
6. **Come back any time.** You can read this book on your own whenever something tricky happens.

> **Remember: you can ALWAYS tell your grown-up anything.**  
> Even if it's embarrassing. Even if you did something wrong. Even if someone said not to. You will not be in trouble for telling.

### For grown-ups: reading together

- One story at a time, five to ten minutes. Stop while they're still interested.
- Read side by side: on the couch, at bedtime, in the car. Let older kids read it to you.
- Ask more than you tell. Use the question in each Grown-up Corner.
- Say the 'Say this' lines out loud together and play the game. Practice is what makes the words come out in real life.
- Praise every bit of telling: "I'm really glad you told me."
- **If your child tells you something serious:** stay calm, believe them, don't question them in detail, and don't promise to keep it secret. Make sure they're safe, then get advice: 000 if they're in danger now, the Child Protection Crisis Line (13 12 78) or Kids Helpline (1800 55 1800).

---

# Part C: The 22 stories

Each story has the same parts, in this order: comic (three panels), What's happening?, Remember, Say this, Magic Words, Try it!, My Turn, Grown-up Corner.

## Part 1: Me and my friends

*Part colour: `#2E7D7A`*

### Story 1: When a friend is bossy

**Comic**

1. *Panel 1: At lunch.* Setting: outside. Characters: Theo (cross), Sam (worried).
   - Theo says: "Do what I say or you're not my friend!"
2. *Panel 2: Sam stops to think.* Setting: outside. Characters: Sam (worried).
   - Sam thinks: "Hmm. That doesn't feel right."
3. *Panel 3: Sam chooses.* Setting: outside. Characters: Sam (proud), Ruby (happy).
   - Sam says: "That's bossing. I'm going to play with Ruby."
   - Ruby says: "Come on, Sam!"

**What's happening?**  
Sometimes a friend tries to boss you around. "Do it or you're not my friend" is not how good friends talk. You can be friendly without doing everything they say.

**Remember**

- Good friends take turns and share.
- You don't have to do everything a friend says.
- It's great to have lots of friends, not just one.

**Say this**

- "That's bossing. I'm not doing it."
- "You can still be my friend, but I'm not doing that."
- "I'm going to play with someone else."

**Magic Words:** Good friends don't boss.

**Try it!** Turn to Good Friend or Bossy Friend? at the back. Read each one out and decide. Which kind of friend are you?

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "Who's really fun to be around? What happens when you don't want to do what they want?" **Tip:** Don't ban the friend. Help your child handle them.

### Story 2: When 'everyone' is allowed except you

**Comic**

1. *Panel 1: Before class.* Setting: school. Characters: Kai (smirk), Zara (sad).
   - Kai says: "Everyone has a phone except you!"
2. *Panel 2: Zara counts.* Setting: school. Characters: Zara (worried).
   - Zara thinks: "Hmm... who is 'everyone'? Kai and... that's it."
3. *Panel 3: Zara answers.* Setting: school. Characters: Zara (happy), Kai (neutral).
   - Zara says: "My family's rules are different. That's OK."
   - Kai says: "Fair enough."

**What's happening?**  
It can feel like everyone is allowed to do something except you. But 'everyone' is usually just a few kids. Every family has its own rules, and rules change as you get older.

**Remember**

- 'Everyone' is almost never everyone.
- Every family has different rules, and that's OK.
- Your rules are about keeping you safe, not about not trusting you.

**Say this**

- "My mum (or dad) won't let me."
- "Not allowed yet."
- "I'll do something else."

**Magic Words:** Every family's different.

**Try it!** Play Count It: next time you say 'everyone', count the real names together. Then make a 'When I'm older' list of things you're looking forward to.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "What's something 'everyone' is allowed to do except you?" **Tip:** Give your reason in one sentence, and say when you'll look at the rule again (e.g. next birthday).

### Story 3: When friends dare you to do something silly

**Comic**

1. *Panel 1: In the backyard.* Setting: outside. Characters: Theo (laugh), Sam (worried).
   - Theo says: "I dare you to jump off the shed! Don't be a baby!"
2. *Panel 2: Sam asks the 3 questions.* Setting: outside. Characters: Sam (worried).
   - Sam thinks: "Could someone get hurt, sad or in trouble?"
3. *Panel 3: Sam says no.* Setting: outside. Characters: Sam (proud), Theo (neutral).
   - Sam says: "Nah, that's dumb. Let's play ball."
   - Theo says: "OK!"

**What's happening?**  
Sometimes friends dare you to do something risky or mean, like jumping off a roof, hiding someone's lunch or copying answers. Saying no can be the bravest thing of all.

**Remember**

- Ask: could someone get hurt, sad or in trouble? If yes, it's a no.
- 'Don't be a baby' and 'just this once' are tricks to get you to do it.
- A good friend doesn't make you prove anything.

**Say this**

- "Nah, that's dumb. Let's do something else."
- "You do it."
- "Not doing it."

**Magic Words:** Hurt, sad or trouble? Then it's a no.

**Try it!** Play Fun Dare or Dumb Dare? A grown-up calls out dares (do a chicken dance, jump off the roof, hide someone's lunch, do 10 star jumps). You sort them!

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "What's the silliest dare you've heard at school?" **Tip:** If your child admits doing one, praise them for telling you before anything else.

### Story 4: When a friend takes something that isn't theirs

**Comic**

1. *Panel 1: At the shop.* Setting: shop. Characters: Kai (smirk), Zara (worried).
   - Kai says: "Quick, hold these! No one will see."
2. *Panel 2: Zara stops and thinks.* Setting: shop. Characters: Zara (worried).
   - Zara thinks: "We didn't pay. That's stealing."
3. *Panel 3: Zara says no.* Setting: outside. Characters: Zara (proud), Kai (sad).
   - Zara says: "I'm not holding that. Put it back."
   - Kai says: "OK... fine."

**What's happening?**  
Kai put lollies in his pocket without paying, then asked Zara to hold them. Zara knew it was stealing, even though it was small. She didn't have to be the police. She just didn't join in.

**Remember**

- Taking something without paying or asking is stealing, even small things.
- Don't take it, hold it, hide it or share it. Just walk away.
- Telling a grown-up isn't dobbing. It stops your friend getting into bigger trouble.

**Say this**

- "Put it back."
- "I'm not holding that."
- "I don't want to get in trouble. I'm out."

**Magic Words:** Not mine? Not taking it.

**Try it!** Set up a pretend shop at home. A grown-up plays the friend who says 'Just hold this.' Practise saying no and walking away. And if you ever took something: "I took this. I'm sorry."

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "What would you do if a friend took something from a shop?" **Tip:** If your child took something, stay calm and go with them to return it and say sorry.

### Story 5: When you're left out (or not invited)

**Comic**

1. *Panel 1: Party invites.* Setting: school. Characters: Other kid (yellow top, long hair) (happy), Sam (happy).
   - Other kid (yellow top, long hair) says: "Here's your invite to my party!"
2. *Panel 2: Zara wasn't invited.* Setting: school. Characters: Zara (sad).
   - Zara thinks: "Everyone's going except me."
3. *Panel 3: Zara tells her grown-up.* Setting: home. Characters: Zara (sad), Grown-up (teal top, long hair) (happy).
   - Zara says: "I wasn't invited."
   - Grown-up (teal top, long hair) says: "That hurts. Let's invite a friend over!"

**What's happening?**  
Zara found out everyone in her group was invited to a party, except her. She felt really sad. Being left out hurts, and it happens to everyone sometimes, even grown-ups.

**Remember**

- Being left out doesn't mean something is wrong with you.
- Not everyone can invite everyone. Next time could be different.
- You can ask to join in. If it's a no, find someone else.

**Say this**

- "Can I play?"
- "OK, see ya. (then find someone else)"
- "To a kid on their own: Want to play?"

**Magic Words:** Ask, then find someone else.

**Try it!** Make a list of 3 other kids, games or clubs you could go to at lunch. Then practise saying 'OK, see ya' and walking off calmly.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "Who did you hang out with at lunch today?" **Tip:** Listen before fixing. Then help plan a catch-up with a different friend.

### Story 6: When someone teases you about how you look

**Comic**

1. *Panel 1: Sam's new glasses.* Setting: school. Characters: Theo (laugh), Sam (sad).
   - Theo says: "Ha! Four eyes!"
2. *Panel 2: Sam remembers the trick.* Setting: school. Characters: Sam (neutral).
   - Sam thinks: "Make it boring."
3. *Panel 3: Sam shrugs it off.* Setting: school. Characters: Sam (happy), Theo (surprised).
   - Sam says: "Yep, new glasses. So?"
   - Theo says: "...Oh. OK."

**What's happening?**  
Sam got new glasses and someone laughed. Teasers want a big reaction. When you give a boring answer, the teasing stops being fun for them.

**Remember**

- Teasing says more about the teaser than about you.
- A calm, boring answer works best.
- If it keeps happening, tell a teacher and a grown-up at home.

**Say this**

- "Yep, new glasses."
- "So? (with a shrug)"
- "I like them."

**Magic Words:** Shrug it off: "So?"

**Try it!** Play The Most Boring Answer. A grown-up teases you about something silly ('Your socks are blue!'). Give the most boring answer you can. Most boring wins!

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "If someone said something about how you look, what do you think they'd say?" **Tip:** If it keeps happening, it's bullying. Talk to the teacher.

### Story 7: When someone is mean to you or a friend

**Comic**

1. *Panel 1: In the classroom.* Setting: school. Characters: Other kid (grey top, short hair) (cross), Ruby (sad).
   - Other kid (grey top, short hair) says: "You can't sit here, loser."
2. *Panel 2: Ruby uses her strong voice.* Setting: school. Characters: Ruby (proud).
   - Ruby says: "Stop. I don't like that."
3. *Panel 3: Then Ruby tells.* Setting: school. Characters: Ruby (neutral), Grown-up (blue top, short hair) (happy).
   - Ruby says: "Can I talk to you about something?"
   - Grown-up (blue top, short hair) says: "Of course."

**What's happening?**  
Sometimes kids say mean things, in person or in a group chat. If it keeps happening to the same person, that's bullying, and a grown-up needs to know.

**Remember**

- What someone says about you isn't true just because they said it.
- Use a strong, calm voice. Then walk away.
- Telling isn't dobbing. Dobbing gets people in trouble. Telling keeps people safe.

**Say this**

- "Stop. I don't like that."
- "Telling isn't dobbing."
- "To a friend: Come hang out with us."

**Magic Words:** Stop, walk, talk.

**Try it!** Practise three voices: a mouse voice, a monster voice and a strong voice. Which works best? Then practise telling a grown-up what happened.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "Is anyone being mean to anyone at school at the moment?" **Tip:** If it's your child: "I'm so glad you told me. It's not your fault." Then talk to the teacher together.

### Story 8: When friends laugh at someone for stuttering or making a mistake

**Comic**

1. *Panel 1: Reading out loud.* Setting: school. Characters: Other kid (yellow top, long hair) (worried), Other kid (grey top, short hair) (laugh).
   - Other kid (yellow top, long hair) says: "I w-w-went to the..."
   - Other kid (grey top, short hair) says: "Ha ha ha!"
2. *Panel 2: Theo doesn't join in.* Setting: school. Characters: Theo (neutral), Other kid (grey top, short hair) (surprised).
   - Theo says: "Not funny."
3. *Panel 3: At lunch.* Setting: outside. Characters: Theo (happy), Other kid (yellow top, long hair) (happy).
   - Theo says: "Want to sit with us?"
   - Other kid (yellow top, long hair) says: "Yes please!"

**What's happening?**  
Some people stutter. Their words get stuck or repeat. It's just how their brain and mouth work, and it's not about being nervous or less smart. Everyone makes mistakes too. When people laugh, it feels awful.

**Remember**

- Laughing along is joining in, even if you didn't start it.
- When someone stutters, wait. Don't finish their words for them.
- Upstanders don't laugh, say something short, and are kind after.

**Say this**

- "Not funny."
- "Leave them alone."
- "Afterwards: Want to sit with us?"

**Magic Words:** Don't laugh. Back them up.

**Try it!** Ask your grown-up about a time they made a mistake in front of people. How did it feel? This week, try one upstander move and report back.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "Has anyone in your class been laughed at for getting something wrong?" **Tip:** If your child laughed, thank them for being honest and ask how they could make it right.

### Story 9: When play-fighting goes too far

**Comic**

1. *Panel 1: Wrestling on the grass.* Setting: outside. Characters: Kai (laugh), Sam (laugh).
   - Kai says: "Gotcha!"
   - Sam says: "Ha ha!"
2. *Panel 2: Sam has had enough.* Setting: outside. Characters: Sam (worried), Kai (surprised).
   - Sam says: "Stop! I'm out."
3. *Panel 3: Kai stops straight away.* Setting: outside. Characters: Kai (happy), Sam (happy).
   - Kai says: "OK! You OK?"
   - Sam says: "Yep, thanks."

**What's happening?**  
Wrestling, chasey and tickling are fun when everyone's having fun. But when someone says stop, the game stops. Straight away. Every time.

**Remember**

- Stop means stop. (Tickling too!)
- No hitting faces, heads or private parts.
- If someone's hurt or crying, the game's over. Check they're OK.

**Say this**

- "Stop means stop. (hand up)"
- "You OK?"
- "I'm out."

**Magic Words:** Stop means stop.

**Try it!** Have a pillow fight. Anyone can call 'Stop!' and everyone has to freeze. Take turns being the caller.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "What happens at lunch when someone wants to stop the game?" **Tip:** Use the same freeze rule with brothers and sisters.

## Part 2: Me and my feelings

*Part colour: `#D9822B`*

### Story 10: When you lose

**Comic**

1. *Panel 1: The final siren.* Setting: court. Characters: Ruby (sad), Other kid (grey top, short hair) (happy).
   - Other kid (grey top, short hair) says: "We won!"
2. *Panel 2: Ruby feels rotten.* Setting: court. Characters: Ruby (cross).
   - Ruby thinks: "Grrr. I want to stomp off!"
3. *Panel 3: Ruby chooses.* Setting: court. Characters: Ruby (proud), Other kid (grey top, short hair) (happy).
   - Ruby says: "Good game!"
   - Other kid (grey top, short hair) says: "Good game!"

**What's happening?**  
Losing feels rotten. Everyone loses sometimes, even the best players in the world. What you do next is what counts.

**Remember**

- Feeling disappointed is normal.
- A good sport says 'good game', win or lose.
- Winning nicely matters too. No gloating!

**Say this**

- "Good game. (fist bump)"
- "To yourself: Next time."
- "When you win: Good game!"

**Magic Words:** Win or lose, "good game".

**Try it!** Play a quick game with your grown-up. Whoever loses has to say 'Good game!' in their best sports-star voice.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "Who's the best sport on your team?" **Tip:** Lose on purpose and model it out loud: "I'm disappointed... good game!"

### Story 11: When you're not the best at something

**Comic**

1. *Panel 1: Basketball training.* Setting: court. Characters: Zara (sad), Other kid (yellow top, long hair) (happy).
   - Zara thinks: "I'm the worst on the team."
   - Other kid (yellow top, long hair) says: "Swish!"
2. *Panel 2: Zara talks to her grown-up.* Setting: home. Characters: Zara (sad), Grown-up (teal top, long hair) (happy).
   - Grown-up (teal top, long hair) says: "What can you do now that you couldn't before?"
3. *Panel 3: A week later.* Setting: court. Characters: Zara (proud).
   - Zara says: "4 out of 10! A new personal best!"

**What's happening?**  
Zara isn't the best player on her basketball team. Everyone's good at different things, and everyone grows at different speeds. The only person you're racing is you.

**Remember**

- Getting better matters more than being the best.
- Compare yourself with YOU last month, not with other kids.
- 'I can't do it' really means 'I can't do it YET.'

**Say this**

- "Not yet."
- "I'm getting better."
- "What can I work on?"

**Magic Words:** Not yet, but getting better.

**Try it!** Use My Personal Bests at the back. Pick one skill, like free throws out of 10 or times tables in a minute. Record your score each week and circle every new best!

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "What's one thing you want to get better at?" **Tip:** Celebrate personal bests, not where they rank.

### Story 12: When you're the one being mean (even to your brother or sister)

**Comic**

1. *Panel 1: Who gets the controller?.* Setting: home. Characters: Theo (cross), Other kid (yellow top, long hair) (sad).
   - Other kid (yellow top, long hair) says: "Hey! That's mine!"
2. *Panel 2: Theo feels the volcano.* Setting: home. Characters: Theo (cross).
   - Theo thinks: "I'm about to blow... I need a break."
3. *Panel 3: Later, Theo makes it right.* Setting: home. Characters: Theo (proud), Other kid (yellow top, long hair) (happy).
   - Theo says: "I'm sorry I grabbed it. Next time I'll ask."
   - Other kid (yellow top, long hair) says: "Thanks."

**What's happening?**  
Everyone is mean sometimes, especially to brothers and sisters. It doesn't make you a bad kid. What matters is fixing it.

**Remember**

- Being cross is OK. Hurting isn't.
- When you're about to blow, walk away.
- A real sorry has 3 parts: what you did, sorry, and how you'll fix it.

**Say this**

- "I need a break."
- "I'm sorry I ___. Next time I'll ___."

**Magic Words:** Walk away, then make it right.

**Try it!** Practise a 3-part sorry with a pretend problem: "I'm sorry I grabbed the remote. Next time I'll ask."

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "What does your brother or sister do that drives you bonkers?" **Tip:** Don't worry about who started it. Focus on what to do next time.

### Story 13: When someone says "boys don't" or "girls can't"

**Comic**

1. *Panel 1: At the court.* Setting: court. Characters: Other kid (grey top, short hair) (smirk), Ruby (cross).
   - Other kid (grey top, short hair) says: "Girls can't play basketball!"
2. *Panel 2: Sam is upset.* Setting: outside. Characters: Older kid (taller, grey top) (smirk), Sam (sad).
   - Older kid (taller, grey top) says: "Boys don't cry!"
3. *Panel 3: Ruby and Sam answer.* Setting: court. Characters: Ruby (proud), Sam (happy).
   - Ruby says: "Anyone can play!"
   - Sam says: "Everyone cries sometimes."

**What's happening?**  
Some people say there are boy things and girl things. But feelings, sports, colours and toys are for everyone. You can like what you like, and you can feel what you feel.

**Remember**

- Everyone cries sometimes. It means something matters.
- There are no boy feelings or girl feelings. Just feelings.
- Strong means kind, brave and honest.

**Say this**

- "Anyone can play."
- "I like what I like."
- "Everyone cries sometimes."

**Magic Words:** Be yourself. Strong and kind.

**Try it!** Watch a clip of athletes crying after a big game. Are they tough? Are they crying? Can you be both?

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "Have you ever heard someone say 'boys don't' or 'girls can't'?" **Tip:** Name real people in your child's life who prove these wrong.

### Story 14: When big feelings take over

**Comic**

1. *Panel 1: Kai's volcano.* Setting: home. Characters: Kai (cross).
   - Kai says: "ARGH! It's not fair!"
2. *Panel 2: Kai breathes.* Setting: home. Characters: Kai (neutral).
   - Kai thinks: "Smell the flower... blow out the candle."
3. *Panel 3: Kai names it.* Setting: home. Characters: Kai (neutral), Grown-up (blue top, short hair) (happy).
   - Kai says: "I feel angry. I need a break."
   - Grown-up (blue top, short hair) says: "Good plan."

**What's happening?**  
Sometimes feelings get really big, like a volcano. Angry, worried, sad. All feelings are OK. It's what we do with them that matters.

**Remember**

- Name it: 'I feel angry' makes the feeling smaller.
- Your body can help: slow breaths, water, a run, a big squeeze.
- Worries are your brain trying to protect you. You can talk back to them.

**Say this**

- "I feel ___."
- "I need a break."
- "Smell the flower, blow out the candle."

**Magic Words:** Name it, breathe it, talk it.

**Try it!** Fill in My Calm-Down Plan at the back. Practise flower-and-candle breathing at bedtime, so it's ready when you need it.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "If your feelings were weather, what would today be?" **Tip:** Practise calming tools when your child is calm, not in the middle of a meltdown.

### Story 15: When you make a mistake and want to hide it

**Comic**

1. *Panel 1: Crash! The mug breaks.* Setting: home. Characters: Ruby (surprised).
   - Ruby says: "Oh no!"
2. *Panel 2: Ruby is tempted.* Setting: home. Characters: Ruby (worried).
   - Ruby thinks: "Maybe I'll just hide it..."
3. *Panel 3: Ruby owns up.* Setting: home. Characters: Ruby (worried), Grown-up (teal top, long hair) (happy).
   - Ruby says: "It was me. I'm sorry."
   - Grown-up (teal top, long hair) says: "Thanks for telling me. Let's fix it."

**What's happening?**  
Everyone makes mistakes, even grown-ups. Hiding a mistake usually makes it worse. Telling the truth, even late, always goes better.

**Remember**

- Most things can be fixed.
- Telling the truth is brave.
- Your grown-ups will always love you, even when they're disappointed.

**Say this**

- "I need to tell you something."
- "It was me. I'm sorry."
- "How can I fix it?"

**Magic Words:** Tell it, fix it.

**Try it!** Ask your grown-up about the biggest mistake they made when they were your age. Did they own up?

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "What would make it easier to tell me when something goes wrong?" **Tip:** When they own up, stay calm and thank them. That reaction is the lesson.

### Story 16: When it's time to stop the game

**Comic**

1. *Panel 1: Game time.* Setting: screen. Characters: Sam (happy), Grown-up (blue top, short hair) (happy).
   - Grown-up (blue top, short hair) says: "10 minutes left!"
2. *Panel 2: Sam gets ready.* Setting: screen. Characters: Sam (neutral).
   - Sam thinks: "Finish my race... then save."
3. *Panel 3: Sam switches.* Setting: outside. Characters: Sam (happy), Grown-up (blue top, short hair) (happy).
   - Sam says: "Saved! Switching!"
   - Grown-up (blue top, short hair) says: "Legend!"

**What's happening?**  
Games and videos are made to be hard to stop. There's always one more level! A warning helps your brain get ready to switch.

**Remember**

- It's not your fault it's hard to stop.
- Screens before bed make it harder to sleep.
- 'Free Robux' and 'free skins' are tricks.

**Say this**

- "OK, finishing my level."
- "Save and switch."

**Magic Words:** Warning, save, switch.

**Try it!** For one week: 10-minute warning, then 'save and switch' with no fuss. Give yourself a tick for every good switch.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "Why do you think it's so hard to stop playing?" **Tip:** Agree the rules ahead of time (see Family Tech Rules) so you're not arguing in the moment.

## Part 3: Keeping safe

*Part colour: `#7E57C2`*

### Story 17: You're the boss of your body

**Comic**

1. *Panel 1: A family visit.* Setting: home. Characters: Grown-up (blue top, short hair) (happy), Mia (worried).
   - Grown-up (blue top, short hair) says: "Come and give me a big hug!"
2. *Panel 2: Mia checks how she feels.* Setting: home. Characters: Mia (worried).
   - Mia thinks: "I don't feel like a hug today."
3. *Panel 3: Mia chooses.* Setting: home. Characters: Mia (happy), Grown-up (blue top, short hair) (happy).
   - Mia says: "Can we high-five instead?"
   - Grown-up (blue top, short hair) says: "Sure! High-five!"

**What's happening?**  
Your body belongs to you. You get to choose about hugs and kisses, even with family. Your private parts are the parts your swimmers cover.

**Remember**

- You're the boss of your body.
- If anything feels wrong or uncomfortable, you can say no, even to a grown-up.
- You will never be in trouble for telling.

**Say this**

- "NO! (loud voice, hand up)"
- "GO: get away."
- "TELL: tell a grown-up on your Safety Hand."

**Magic Words:** No, go, tell.

**Try it!** Practise a big, loud "NO!" with your hand up like a stop sign. Then fill in My Safety Hand with 5 grown-ups you trust.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "Who's the boss of your body?" **Tip:** Use the correct names for body parts, and back your child up when they choose a high-five over a hug.

### Story 18: Surprises are fun. Secrets get told.

**Comic**

1. *Panel 1: A surprise.* Setting: home. Characters: Sam (happy), Zara (happy).
   - Sam says: "Shh! It's a surprise party for Mum!"
2. *Panel 2: A secret.* Setting: outside. Characters: Grown-up (teal top, long hair) (smirk), Kai (worried).
   - Grown-up (teal top, long hair) says: "Don't tell your mum I gave you this."
3. *Panel 3: Kai tells.* Setting: home. Characters: Kai (neutral), Grown-up (blue top, short hair) (happy).
   - Kai says: "Someone told me not to tell you something."
   - Grown-up (blue top, short hair) says: "Thank you for telling me."

**What's happening?**  
A surprise is fun, and everyone finds out soon, like a birthday present. A secret is something someone wants you to hide from your parents. Safe grown-ups don't ask kids to keep secrets.

**Remember**

- Surprises are fine.
- If anyone says 'Don't tell your mum or dad', that's your signal to tell.
- You won't be in trouble for telling.

**Say this**

- "We don't keep secrets in our family."
- "I'm telling Mum and Dad."

**Magic Words:** Surprises are fun. Secrets get told.

**Try it!** Play Surprise or Tell? at the back. A grown-up reads each one and you shout 'Surprise!' or 'Tell!'

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "What's the difference between a surprise and a secret?" **Tip:** If your child shares a real secret, stay calm, thank them, and get advice (see Who Can Help).

### Story 19: Always check first

**Comic**

1. *Panel 1: At the park.* Setting: outside. Characters: Grown-up (blue top, short hair) (happy), Ruby (surprised).
   - Grown-up (blue top, short hair) says: "Want to come and see my new puppy?"
2. *Panel 2: Ruby remembers the rule.* Setting: outside. Characters: Ruby (proud).
   - Ruby says: "I have to ask my mum first."
3. *Panel 3: Ruby finds her mum.* Setting: outside. Characters: Ruby (neutral), Grown-up (teal top, long hair) (happy).
   - Ruby says: "Someone asked me to see a puppy."
   - Grown-up (teal top, long hair) says: "You checked first. Great job!"

**What's happening?**  
Most grown-ups are kind. But grown-ups who need help ask other grown-ups, not kids. Before you go anywhere with anyone, check with your grown-up first, even if you know the person.

**Remember**

- Always check first, every time.
- Anyone picking you up who isn't on the plan must know the family password.
- Need help? Ask a parent with kids or someone working in a shop.

**Say this**

- "I have to ask my mum or dad first."
- "What's our password?"

**Magic Words:** Check first, every time.

**Try it!** Choose a silly family password together (like 'purple kangaroo'). Write it on Things I Know By Heart. Only your family and pick-up grown-ups know it.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "If you needed help and couldn't find me, who would you ask?" **Tip:** Role-play the 'puppy' trick and praise them for checking first.

### Story 20: If you get lost

**Comic**

1. *Panel 1: Theo can't see Dad.* Setting: shop. Characters: Theo (worried).
   - Theo thinks: "Where's Dad? Stop. Stay."
2. *Panel 2: Theo asks a shop worker.* Setting: shop. Characters: Theo (neutral), Grown-up (teal top, long hair) (happy).
   - Theo says: "I'm lost. My dad's number is..."
   - Grown-up (teal top, long hair) says: "Let's call him."
3. *Panel 3: On a bushwalk: hug a tree.* Setting: bush. Characters: Theo (neutral).
   - Theo thinks: "Stay put. They'll find me."

**What's happening?**  
Getting lost is scary, but if you know the plan you'll know what to do. The most important thing: stop and stay. Your grown-up will come to you.

**Remember**

- Shops: stop, ask a parent with kids or a shop worker, say your grown-up's phone number.
- Bush: stop, stay put and hug a tree.
- Beach: swim between the red and yellow flags. In trouble? Float and raise one arm.

**Say this**

- "I'm lost. My mum's number is ___."
- "Hug a tree."
- "Flags, float, wave."

**Magic Words:** Stop and stay. We'll find you.

**Try it!** Turn your grown-up's phone number into a chant or song until you know it by heart. Write it on Things I Know By Heart.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "If we got split up at the campground, what would you do?" **Tip:** At the beach, walk to the lifeguard tower and say hello.

### Story 21: When something online feels weird

**Comic**

1. *Panel 1: Kai is playing online.* Setting: screen. Characters: Kai (happy).
   - On screen: "NEW MESSAGE: What school do you go to?"
2. *Panel 2: Kai gets an uh-oh feeling.* Setting: screen. Characters: Kai (worried).
   - Kai thinks: "That feels weird. I don't share that."
3. *Panel 3: Kai tells.* Setting: home. Characters: Kai (neutral), Grown-up (teal top, long hair) (happy).
   - Kai says: "Someone in my game asked where I go to school."
   - Grown-up (teal top, long hair) says: "Thanks for telling me. Let's block them."

**What's happening?**  
Most people online are fine. But people aren't always who they say they are. If something gives you an 'uh-oh' feeling, that's your body telling you something's not right.

**Remember**

- Never share your real name, school, address or photos.
- 'Free Robux' or 'free skins' are tricks.
- Uh-oh feeling? Turn it over and tell a grown-up. You won't be in trouble.

**Say this**

- "I don't share that."
- "Block, report, tell."

**Magic Words:** Turn it over, tell a grown-up.

**Try it!** Show your grown-up your favourite game. Teach them how to block and report someone.

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "Who do you play with online?" **Tip:** Keep chat off or friends-only, and devices in shared rooms.

### Story 22: When you see or hear something rude

**Comic**

1. *Panel 1: An older kid has a phone.* Setting: school. Characters: Older kid (taller, grey top) (smirk), Zara (surprised).
   - Older kid (taller, grey top) says: "Hey, look at this!"
2. *Panel 2: Zara looks away.* Setting: school. Characters: Zara (proud).
   - Zara says: "I don't want to see that."
3. *Panel 3: Zara tells.* Setting: home. Characters: Zara (neutral), Grown-up (blue top, short hair) (happy).
   - Zara says: "Someone showed me something rude."
   - Grown-up (blue top, short hair) says: "Thank you for telling me. You're not in trouble."

**What's happening?**  
Some pictures and videos are only for grown-ups. They can show people with no clothes on. Kids' brains aren't ready for them, so they can feel yucky. Seeing something by accident is never your fault.

**Remember**

- Look away, close it or turn it over. Then tell.
- Nobody should ever ask you to take or send photos of private parts. Always tell.
- Heard a rude word? Ask a grown-up what it means, instead of searching it.

**Say this**

- "I don't want to see that."
- "What does ___ mean?"
- "Look away, close it, tell."

**Magic Words:** Look away, walk away, tell.

**Try it!** With your grown-up, check that safe search and kids' modes are turned on. They're on your team!

**My Turn:** Has this happened to you or someone you know? What would you say or do?

> **Grown-up Corner.** **Ask:** "Have you heard a word at school you weren't sure was rude?" **Tip:** Stay calm and answer simply. If anyone asked your child for photos, or an adult was involved, get advice (see Who Can Help).

---

# Part D: Activity pages

## My Magic Words

One for every story. Stick this on the fridge. Can you say them all from memory?

| # | Story | Magic Words |
|---|---|---|
| 1 | When a friend is bossy | **Good friends don't boss.** |
| 2 | When 'everyone' is allowed except you | **Every family's different.** |
| 3 | When friends dare you to do something silly | **Hurt, sad or trouble? Then it's a no.** |
| 4 | When a friend takes something that isn't theirs | **Not mine? Not taking it.** |
| 5 | When you're left out (or not invited) | **Ask, then find someone else.** |
| 6 | When someone teases you about how you look | **Shrug it off: "So?"** |
| 7 | When someone is mean to you or a friend | **Stop, walk, talk.** |
| 8 | When friends laugh at someone for stuttering or making a mistake | **Don't laugh. Back them up.** |
| 9 | When play-fighting goes too far | **Stop means stop.** |
| 10 | When you lose | **Win or lose, "good game".** |
| 11 | When you're not the best at something | **Not yet, but getting better.** |
| 12 | When you're the one being mean (even to your brother or sister) | **Walk away, then make it right.** |
| 13 | When someone says "boys don't" or "girls can't" | **Be yourself. Strong and kind.** |
| 14 | When big feelings take over | **Name it, breathe it, talk it.** |
| 15 | When you make a mistake and want to hide it | **Tell it, fix it.** |
| 16 | When it's time to stop the game | **Warning, save, switch.** |
| 17 | You're the boss of your body | **No, go, tell.** |
| 18 | Surprises are fun. Secrets get told. | **Surprises are fun. Secrets get told.** |
| 19 | Always check first | **Check first, every time.** |
| 20 | If you get lost | **Stop and stay. We'll find you.** |
| 21 | When something online feels weird | **Turn it over, tell a grown-up.** |
| 22 | When you see or hear something rude | **Look away, walk away, tell.** |

## Good Friend or Bossy Friend?

Read each one out. Is it a good friend or a bossy friend? (Cover the headings to make it a quiz!)

**Good friend**

- Takes turns and shares
- Lets you choose the game sometimes
- Sticks up for you
- Says sorry when they get it wrong
- Is happy when you do well
- Listens when you say stop
- Lets you hang out with other friends too

**Bossy friend**

- Always has to be in charge
- Says "You're not my friend unless..."
- Gets you in trouble and leaves you to cop it
- Laughs at you in front of others
- Gets cross when you play with someone else
- Dares you to do dumb stuff, or to take things
- Tells you to keep secrets from your parents

**Talk about it:** Who's a good friend to you? What kind of friend do you want to be?

## My Personal Bests

Pick a skill. Write your score each week. Circle every new best. The only person you're racing is YOU!

My skill: ____________________

| Date | My score | New best? |
|---|---|---|
|  |  |  |
|  |  |  |
|  |  |  |

**Ideas:** free throws out of 10, skips in a minute, times tables in a minute, spelling words, pages read, laps of the oval.

## My Safety Hand

Trace around your hand. On each finger, write a grown-up you can tell anything. Pick at least one who doesn't live at your house.

| Finger | Name | Phone |
|---|---|---|
| 1 |  |  |
| 2 |  |  |
| 3 |  |  |
| 4 |  |  |
| 5 |  |  |

## Things I Know By Heart

Fill this in with your grown-up. Practise one a day until you can say them all without looking.

- My full name: ____________________
- Parent 1 full name and mobile: ____________________
- Parent 2 full name and mobile: ____________________
- My address (street and suburb): ____________________
- Our family password (for pick-ups): ____________________
- If I get lost: Stop and stay. Ask a parent with kids or a shop worker.
- In an emergency I call: 000

**Our family password** is a silly phrase (like 'purple kangaroo'). Only your family and grown-ups allowed to pick you up know it. If someone doesn't know it, don't go with them!

## Surprise or Tell?

Your grown-up reads each one. You shout "SURPRISE!" (fun, and everyone finds out soon) or "TELL!" (someone wants it hidden from your parents).

| # | Statement | Answer |
|---|---|---|
| 1 | Grandma is planning a surprise party for Dad. | Surprise |
| 2 | A grown-up says, "Don't tell your mum I gave you lollies." | Tell |
| 3 | You and Dad are making Mum a card for her birthday. | Surprise |
| 4 | A friend says, "Don't tell anyone I took it from the shop." | Tell |
| 5 | Someone in a game says, "Let's keep our chats secret." | Tell |
| 6 | Your brother bought you a birthday present and won't say what it is. | Surprise |
| 7 | Someone makes you feel uncomfortable and says, "This is our secret." | Tell |
| 8 | A friend says they're being hurt and asks you not to tell. | Tell |
| 9 | You're hiding chocolate eggs for an Easter egg hunt. | Surprise |
| 10 | A grown-up gives you game money and says, "Your parents don't need to know." | Tell |
| 11 | An older kid shows you a rude video and says, "Don't tell or you'll get in trouble." | Tell |

## What If? Cards

Cut them out and keep them in a jar. Pull one out at dinner or in the car, and say what you'd do. Then test your grown-up!

1. What if... A friend says, "You're not my friend unless you do it."
2. What if... A friend says, "Everyone's allowed to play this game except you."
3. What if... Your friends dare you to jump off the shed roof.
4. What if... Your friend pockets lollies at the shop and says, "Hold this."
5. What if... Some kids say, "You can't play with us."
6. What if... You get picked last for teams.
7. What if... Someone laughs at your new glasses.
8. What if... Someone calls you a mean name.
9. What if... The class laughs when a kid stutters reading aloud.
10. What if... Your friend won't stop tickling after you say stop.
11. What if... You lose the final of your game.
12. What if... You feel like the worst player on the team.
13. What if... Your brother or sister grabs your stuff.
14. What if... You feel so angry you want to throw something.
15. What if... Someone says, "Girls can't play" or "Boys don't cry."
16. What if... You broke something and nobody saw.
17. What if... It's 10 minutes until screen time ends.
18. What if... Someone wants a hug and you don't feel like one.
19. What if... A grown-up says, "Don't tell your mum or dad."
20. What if... A neighbour asks you to come and see their new puppy.
21. What if... You can't find your parent at the shops.
22. What if... You lose sight of the tent on a bush walk.
23. What if... Someone in a game asks what school you go to.
24. What if... A video suddenly shows something rude.
25. What if... An older kid says, "Look at this," and it's a rude picture on their phone.
26. What if... A kid at school says a rude word you don't understand.
27. What if... Someone in a group chat posts a mean photo of a classmate.

## Our Tech Rules

Read these with your grown-up. Tick the ones your family agrees on, and add your own.

- [ ] Devices are used in shared rooms, not bedrooms.
- [ ] Screens off an hour before bed.
- [ ] Ask before downloading any new game or app.
- [ ] Game chat is off, or only with friends we know in real life.
- [ ] I never share my real name, school, address or photos online.
- [ ] Free Robux or free skins? That's a trick.
- [ ] Buying anything in a game needs a yes from a parent.
- [ ] Uh-oh feeling? Turn it over, tell a grown-up.
- [ ] Safe search and kids' modes stay on (e.g. YouTube Kids).
- [ ] Group chats: I don't post anything I wouldn't say to someone's face.
- [ ] 10-minute warning, then save and switch.
- [ ] School days: ______ minutes.   Weekends: ______ minutes.

Our extra rules: ____________________

Grown-up signature: __________   My signature: __________

## My Calm-Down Plan

Fill this in on a calm day. When a big feeling comes, check your number and follow your plan.

| Number | Feeling | What I do |
|---|---|---|
| 5 | Volcano | I might hurt someone or something. I need a grown-up now. |
| 4 | Really cross or scared | I need a break in my calm spot. |
| 3 | Grumpy or worried | I can breathe and tell someone. |
| 2 | A bit bothered | I can name it and keep going. |
| 1 | Calm | I feel good and ready to play. |

- When I'm getting cross, my body feels like: ____________________
- Three things that help me calm down: ____________________
- My calm spot at home is: ____________________
- A grown-up I can go to at school: ____________________

**Flower and candle breathing:** smell the flower (slow breath in through your nose)... blow out the candle (long breath out through your mouth). Do it five times.

---

# Part E: Who can help

You are never on your own. If something is wrong, tell a grown-up on your Safety Hand. You can also call:

- **000:** If someone is hurt or in danger right now.
- **Kids Helpline 1800 55 1800:** Free, any time, day or night. Kids can call by themselves. You can talk about anything. kidshelpline.com.au

### For grown-ups

| Service | Contact |
|---|---|
| Parentline Victoria (parenting support) | 13 22 89 |
| Child Protection Crisis Line, Victoria (24/7, if you're worried about a child's safety) | 13 12 78 |
| Bravehearts Information & Support Line (child safety, weekdays) | 1800 272 831 |
| eSafety Commissioner (report online abuse, parent guides to games) | esafety.gov.au |
| Poisons Information Centre (24/7) | 13 11 26 |

**Further free reading**

- raisingchildren.net.au: school-age friendships, teasing, stealing, anger, sibling fighting and body safety
- esafety.gov.au/parents: plain-English guides to Roblox, Minecraft, YouTube, group chats and parental controls
- bravehearts.org.au: 'Ditto' body safety resources for kids and parents
- Library: *Some Secrets Should Never Be Kept* by Jayneen Sanders (a picture book on body safety)
- Speech Pathology Australia (speechpathologyaustralia.org.au): plain-English information on stuttering

*Services and phone numbers are for Victoria, Australia and were checked in October 2026. This book is general information, not professional advice. If you're worried about your child's safety or wellbeing, speak to your GP, their school or one of the services above.*

---

# Appendix 1: Stories as JSON

Save this as `app/src/main/assets/stories.json`. `who` values map to characters in Appendix 2. `kind` is `say` (speech bubble) or `think` (thought bubble). `speaker` is the index into `people`, or `null` for on-screen text.

```json
{
  "parts": [
    {
      "id": 1,
      "startsAtStory": 1,
      "name": "Me and my friends",
      "color": "#2E7D7A"
    },
    {
      "id": 2,
      "startsAtStory": 10,
      "name": "Me and my feelings",
      "color": "#D9822B"
    },
    {
      "id": 3,
      "startsAtStory": 17,
      "name": "Keeping safe",
      "color": "#7E57C2"
    }
  ],
  "stories": [
    {
      "id": 1,
      "part": 1,
      "title": "When a friend is bossy",
      "panels": [
        {
          "caption": "At lunch",
          "scene": "outside",
          "people": [
            {
              "who": "theo",
              "x": 0.3,
              "face": "cross",
              "arm": "hips"
            },
            {
              "who": "sam",
              "x": 0.72,
              "face": "worried",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Do what I say or you're not my friend!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Sam stops to think",
          "scene": "outside",
          "people": [
            {
              "who": "sam",
              "x": 0.5,
              "face": "worried",
              "arm": "down"
            }
          ],
          "speech": [
            {
              "text": "Hmm. That doesn't feel right.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Sam chooses",
          "scene": "outside",
          "people": [
            {
              "who": "sam",
              "x": 0.3,
              "face": "proud",
              "arm": "point"
            },
            {
              "who": "ruby",
              "x": 0.74,
              "face": "happy",
              "arm": "wave",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "That's bossing. I'm going to play with Ruby.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Come on, Sam!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Sometimes a friend tries to boss you around. \"Do it or you're not my friend\" is not how good friends talk. You can be friendly without doing everything they say.",
      "remember": [
        "Good friends take turns and share.",
        "You don't have to do everything a friend says.",
        "It's great to have lots of friends, not just one."
      ],
      "sayThis": [
        "That's bossing. I'm not doing it.",
        "You can still be my friend, but I'm not doing that.",
        "I'm going to play with someone else."
      ],
      "magicWords": "Good friends don't boss.",
      "tryIt": "Turn to Good Friend or Bossy Friend? at the back. Read each one out and decide. Which kind of friend are you?",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "Who's really fun to be around? What happens when you don't want to do what they want?",
        "tip": "Don't ban the friend. Help your child handle them."
      }
    },
    {
      "id": 2,
      "part": 1,
      "title": "When 'everyone' is allowed except you",
      "panels": [
        {
          "caption": "Before class",
          "scene": "school",
          "people": [
            {
              "who": "kai",
              "x": 0.3,
              "face": "smirk",
              "arm": "hold",
              "item": "phone"
            },
            {
              "who": "zara",
              "x": 0.72,
              "face": "sad",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Everyone has a phone except you!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Zara counts",
          "scene": "school",
          "people": [
            {
              "who": "zara",
              "x": 0.5,
              "face": "worried",
              "arm": "down"
            }
          ],
          "speech": [
            {
              "text": "Hmm... who is 'everyone'? Kai and... that's it.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Zara answers",
          "scene": "school",
          "people": [
            {
              "who": "zara",
              "x": 0.3,
              "face": "happy",
              "arm": "hips"
            },
            {
              "who": "kai",
              "x": 0.72,
              "face": "neutral",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "My family's rules are different. That's OK.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Fair enough.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "It can feel like everyone is allowed to do something except you. But 'everyone' is usually just a few kids. Every family has its own rules, and rules change as you get older.",
      "remember": [
        "'Everyone' is almost never everyone.",
        "Every family has different rules, and that's OK.",
        "Your rules are about keeping you safe, not about not trusting you."
      ],
      "sayThis": [
        "My mum (or dad) won't let me.",
        "Not allowed yet.",
        "I'll do something else."
      ],
      "magicWords": "Every family's different.",
      "tryIt": "Play Count It: next time you say 'everyone', count the real names together. Then make a 'When I'm older' list of things you're looking forward to.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "What's something 'everyone' is allowed to do except you?",
        "tip": "Give your reason in one sentence, and say when you'll look at the rule again (e.g. next birthday)."
      }
    },
    {
      "id": 3,
      "part": 1,
      "title": "When friends dare you to do something silly",
      "panels": [
        {
          "caption": "In the backyard",
          "scene": "outside",
          "people": [
            {
              "who": "theo",
              "x": 0.28,
              "face": "laugh",
              "arm": "point"
            },
            {
              "who": "sam",
              "x": 0.72,
              "face": "worried",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "I dare you to jump off the shed! Don't be a baby!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Sam asks the 3 questions",
          "scene": "outside",
          "people": [
            {
              "who": "sam",
              "x": 0.5,
              "face": "worried",
              "arm": "down"
            }
          ],
          "speech": [
            {
              "text": "Could someone get hurt, sad or in trouble?",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Sam says no",
          "scene": "outside",
          "people": [
            {
              "who": "sam",
              "x": 0.3,
              "face": "proud",
              "arm": "give",
              "item": "ball"
            },
            {
              "who": "theo",
              "x": 0.72,
              "face": "neutral",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Nah, that's dumb. Let's play ball.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "OK!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Sometimes friends dare you to do something risky or mean, like jumping off a roof, hiding someone's lunch or copying answers. Saying no can be the bravest thing of all.",
      "remember": [
        "Ask: could someone get hurt, sad or in trouble? If yes, it's a no.",
        "'Don't be a baby' and 'just this once' are tricks to get you to do it.",
        "A good friend doesn't make you prove anything."
      ],
      "sayThis": [
        "Nah, that's dumb. Let's do something else.",
        "You do it.",
        "Not doing it."
      ],
      "magicWords": "Hurt, sad or trouble? Then it's a no.",
      "tryIt": "Play Fun Dare or Dumb Dare? A grown-up calls out dares (do a chicken dance, jump off the roof, hide someone's lunch, do 10 star jumps). You sort them!",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "What's the silliest dare you've heard at school?",
        "tip": "If your child admits doing one, praise them for telling you before anything else."
      }
    },
    {
      "id": 4,
      "part": 1,
      "title": "When a friend takes something that isn't theirs",
      "panels": [
        {
          "caption": "At the shop",
          "scene": "shop",
          "people": [
            {
              "who": "kai",
              "x": 0.32,
              "face": "smirk",
              "arm": "give",
              "item": "lolly"
            },
            {
              "who": "zara",
              "x": 0.72,
              "face": "worried",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Quick, hold these! No one will see.",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Zara stops and thinks",
          "scene": "shop",
          "people": [
            {
              "who": "zara",
              "x": 0.5,
              "face": "worried",
              "arm": "hips"
            }
          ],
          "speech": [
            {
              "text": "We didn't pay. That's stealing.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Zara says no",
          "scene": "outside",
          "people": [
            {
              "who": "zara",
              "x": 0.3,
              "face": "proud",
              "arm": "stop"
            },
            {
              "who": "kai",
              "x": 0.74,
              "face": "sad",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "I'm not holding that. Put it back.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "OK... fine.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Kai put lollies in his pocket without paying, then asked Zara to hold them. Zara knew it was stealing, even though it was small. She didn't have to be the police. She just didn't join in.",
      "remember": [
        "Taking something without paying or asking is stealing, even small things.",
        "Don't take it, hold it, hide it or share it. Just walk away.",
        "Telling a grown-up isn't dobbing. It stops your friend getting into bigger trouble."
      ],
      "sayThis": [
        "Put it back.",
        "I'm not holding that.",
        "I don't want to get in trouble. I'm out."
      ],
      "magicWords": "Not mine? Not taking it.",
      "tryIt": "Set up a pretend shop at home. A grown-up plays the friend who says 'Just hold this.' Practise saying no and walking away. And if you ever took something: \"I took this. I'm sorry.\"",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "What would you do if a friend took something from a shop?",
        "tip": "If your child took something, stay calm and go with them to return it and say sorry."
      }
    },
    {
      "id": 5,
      "part": 1,
      "title": "When you're left out (or not invited)",
      "panels": [
        {
          "caption": "Party invites",
          "scene": "school",
          "people": [
            {
              "who": "kid",
              "x": 0.3,
              "face": "happy",
              "arm": "give",
              "item": "invite"
            },
            {
              "who": "sam",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Here's your invite to my party!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Zara wasn't invited",
          "scene": "school",
          "people": [
            {
              "who": "zara",
              "x": 0.5,
              "face": "sad",
              "arm": "down",
              "tears": true
            }
          ],
          "speech": [
            {
              "text": "Everyone's going except me.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Zara tells her grown-up",
          "scene": "home",
          "people": [
            {
              "who": "zara",
              "x": 0.3,
              "face": "sad",
              "arm": "down"
            },
            {
              "who": "adult",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "I wasn't invited.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "That hurts. Let's invite a friend over!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Zara found out everyone in her group was invited to a party, except her. She felt really sad. Being left out hurts, and it happens to everyone sometimes, even grown-ups.",
      "remember": [
        "Being left out doesn't mean something is wrong with you.",
        "Not everyone can invite everyone. Next time could be different.",
        "You can ask to join in. If it's a no, find someone else."
      ],
      "sayThis": [
        "Can I play?",
        "OK, see ya. (then find someone else)",
        "To a kid on their own: Want to play?"
      ],
      "magicWords": "Ask, then find someone else.",
      "tryIt": "Make a list of 3 other kids, games or clubs you could go to at lunch. Then practise saying 'OK, see ya' and walking off calmly.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "Who did you hang out with at lunch today?",
        "tip": "Listen before fixing. Then help plan a catch-up with a different friend."
      }
    },
    {
      "id": 6,
      "part": 1,
      "title": "When someone teases you about how you look",
      "panels": [
        {
          "caption": "Sam's new glasses",
          "scene": "school",
          "people": [
            {
              "who": "theo",
              "x": 0.3,
              "face": "laugh",
              "arm": "point"
            },
            {
              "who": "sam",
              "x": 0.72,
              "face": "sad",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Ha! Four eyes!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Sam remembers the trick",
          "scene": "school",
          "people": [
            {
              "who": "sam",
              "x": 0.5,
              "face": "neutral",
              "arm": "down"
            }
          ],
          "speech": [
            {
              "text": "Make it boring.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Sam shrugs it off",
          "scene": "school",
          "people": [
            {
              "who": "sam",
              "x": 0.3,
              "face": "happy",
              "arm": "hips"
            },
            {
              "who": "theo",
              "x": 0.72,
              "face": "surprised",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Yep, new glasses. So?",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "...Oh. OK.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Sam got new glasses and someone laughed. Teasers want a big reaction. When you give a boring answer, the teasing stops being fun for them.",
      "remember": [
        "Teasing says more about the teaser than about you.",
        "A calm, boring answer works best.",
        "If it keeps happening, tell a teacher and a grown-up at home."
      ],
      "sayThis": [
        "Yep, new glasses.",
        "So? (with a shrug)",
        "I like them."
      ],
      "magicWords": "Shrug it off: \"So?\"",
      "tryIt": "Play The Most Boring Answer. A grown-up teases you about something silly ('Your socks are blue!'). Give the most boring answer you can. Most boring wins!",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "If someone said something about how you look, what do you think they'd say?",
        "tip": "If it keeps happening, it's bullying. Talk to the teacher."
      }
    },
    {
      "id": 7,
      "part": 1,
      "title": "When someone is mean to you or a friend",
      "panels": [
        {
          "caption": "In the classroom",
          "scene": "school",
          "people": [
            {
              "who": "kid2",
              "x": 0.3,
              "face": "cross",
              "arm": "point"
            },
            {
              "who": "ruby",
              "x": 0.72,
              "face": "sad",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "You can't sit here, loser.",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Ruby uses her strong voice",
          "scene": "school",
          "people": [
            {
              "who": "ruby",
              "x": 0.5,
              "face": "proud",
              "arm": "stop"
            }
          ],
          "speech": [
            {
              "text": "Stop. I don't like that.",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Then Ruby tells",
          "scene": "school",
          "people": [
            {
              "who": "ruby",
              "x": 0.3,
              "face": "neutral",
              "arm": "down"
            },
            {
              "who": "adult2",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Can I talk to you about something?",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Of course.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Sometimes kids say mean things, in person or in a group chat. If it keeps happening to the same person, that's bullying, and a grown-up needs to know.",
      "remember": [
        "What someone says about you isn't true just because they said it.",
        "Use a strong, calm voice. Then walk away.",
        "Telling isn't dobbing. Dobbing gets people in trouble. Telling keeps people safe."
      ],
      "sayThis": [
        "Stop. I don't like that.",
        "Telling isn't dobbing.",
        "To a friend: Come hang out with us."
      ],
      "magicWords": "Stop, walk, talk.",
      "tryIt": "Practise three voices: a mouse voice, a monster voice and a strong voice. Which works best? Then practise telling a grown-up what happened.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "Is anyone being mean to anyone at school at the moment?",
        "tip": "If it's your child: \"I'm so glad you told me. It's not your fault.\" Then talk to the teacher together."
      }
    },
    {
      "id": 8,
      "part": 1,
      "title": "When friends laugh at someone for stuttering or making a mistake",
      "panels": [
        {
          "caption": "Reading out loud",
          "scene": "school",
          "people": [
            {
              "who": "kid",
              "x": 0.3,
              "face": "worried",
              "arm": "hold",
              "item": "book"
            },
            {
              "who": "kid2",
              "x": 0.72,
              "face": "laugh",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "I w-w-went to the...",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Ha ha ha!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Theo doesn't join in",
          "scene": "school",
          "people": [
            {
              "who": "theo",
              "x": 0.3,
              "face": "neutral",
              "arm": "stop"
            },
            {
              "who": "kid2",
              "x": 0.72,
              "face": "surprised",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Not funny.",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "At lunch",
          "scene": "outside",
          "people": [
            {
              "who": "theo",
              "x": 0.3,
              "face": "happy",
              "arm": "wave"
            },
            {
              "who": "kid",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Want to sit with us?",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Yes please!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Some people stutter. Their words get stuck or repeat. It's just how their brain and mouth work, and it's not about being nervous or less smart. Everyone makes mistakes too. When people laugh, it feels awful.",
      "remember": [
        "Laughing along is joining in, even if you didn't start it.",
        "When someone stutters, wait. Don't finish their words for them.",
        "Upstanders don't laugh, say something short, and are kind after."
      ],
      "sayThis": [
        "Not funny.",
        "Leave them alone.",
        "Afterwards: Want to sit with us?"
      ],
      "magicWords": "Don't laugh. Back them up.",
      "tryIt": "Ask your grown-up about a time they made a mistake in front of people. How did it feel? This week, try one upstander move and report back.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "Has anyone in your class been laughed at for getting something wrong?",
        "tip": "If your child laughed, thank them for being honest and ask how they could make it right."
      }
    },
    {
      "id": 9,
      "part": 1,
      "title": "When play-fighting goes too far",
      "panels": [
        {
          "caption": "Wrestling on the grass",
          "scene": "outside",
          "people": [
            {
              "who": "kai",
              "x": 0.35,
              "face": "laugh",
              "arm": "up"
            },
            {
              "who": "sam",
              "x": 0.65,
              "face": "laugh",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Gotcha!",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Ha ha!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Sam has had enough",
          "scene": "outside",
          "people": [
            {
              "who": "sam",
              "x": 0.3,
              "face": "worried",
              "arm": "stop"
            },
            {
              "who": "kai",
              "x": 0.72,
              "face": "surprised",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Stop! I'm out.",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Kai stops straight away",
          "scene": "outside",
          "people": [
            {
              "who": "kai",
              "x": 0.3,
              "face": "happy",
              "arm": "wave"
            },
            {
              "who": "sam",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "OK! You OK?",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Yep, thanks.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Wrestling, chasey and tickling are fun when everyone's having fun. But when someone says stop, the game stops. Straight away. Every time.",
      "remember": [
        "Stop means stop. (Tickling too!)",
        "No hitting faces, heads or private parts.",
        "If someone's hurt or crying, the game's over. Check they're OK."
      ],
      "sayThis": [
        "Stop means stop. (hand up)",
        "You OK?",
        "I'm out."
      ],
      "magicWords": "Stop means stop.",
      "tryIt": "Have a pillow fight. Anyone can call 'Stop!' and everyone has to freeze. Take turns being the caller.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "What happens at lunch when someone wants to stop the game?",
        "tip": "Use the same freeze rule with brothers and sisters."
      }
    },
    {
      "id": 10,
      "part": 2,
      "title": "When you lose",
      "panels": [
        {
          "caption": "The final siren",
          "scene": "court",
          "people": [
            {
              "who": "ruby",
              "x": 0.3,
              "face": "sad",
              "arm": "hold",
              "item": "ball"
            },
            {
              "who": "kid2",
              "x": 0.7,
              "face": "happy",
              "arm": "up",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "We won!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Ruby feels rotten",
          "scene": "court",
          "people": [
            {
              "who": "ruby",
              "x": 0.5,
              "face": "cross",
              "arm": "hips"
            }
          ],
          "speech": [
            {
              "text": "Grrr. I want to stomp off!",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Ruby chooses",
          "scene": "court",
          "people": [
            {
              "who": "ruby",
              "x": 0.3,
              "face": "proud",
              "arm": "give"
            },
            {
              "who": "kid2",
              "x": 0.7,
              "face": "happy",
              "arm": "give",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Good game!",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Good game!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Losing feels rotten. Everyone loses sometimes, even the best players in the world. What you do next is what counts.",
      "remember": [
        "Feeling disappointed is normal.",
        "A good sport says 'good game', win or lose.",
        "Winning nicely matters too. No gloating!"
      ],
      "sayThis": [
        "Good game. (fist bump)",
        "To yourself: Next time.",
        "When you win: Good game!"
      ],
      "magicWords": "Win or lose, \"good game\".",
      "tryIt": "Play a quick game with your grown-up. Whoever loses has to say 'Good game!' in their best sports-star voice.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "Who's the best sport on your team?",
        "tip": "Lose on purpose and model it out loud: \"I'm disappointed... good game!\""
      }
    },
    {
      "id": 11,
      "part": 2,
      "title": "When you're not the best at something",
      "panels": [
        {
          "caption": "Basketball training",
          "scene": "court",
          "people": [
            {
              "who": "zara",
              "x": 0.3,
              "face": "sad",
              "arm": "down"
            },
            {
              "who": "kid",
              "x": 0.7,
              "face": "happy",
              "arm": "up",
              "item": "ball",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "I'm the worst on the team.",
              "speaker": 0,
              "kind": "think"
            },
            {
              "text": "Swish!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Zara talks to her grown-up",
          "scene": "home",
          "people": [
            {
              "who": "zara",
              "x": 0.3,
              "face": "sad",
              "arm": "down"
            },
            {
              "who": "adult",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "What can you do now that you couldn't before?",
              "speaker": 1,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "A week later",
          "scene": "court",
          "people": [
            {
              "who": "zara",
              "x": 0.5,
              "face": "proud",
              "arm": "up",
              "item": "ball"
            }
          ],
          "speech": [
            {
              "text": "4 out of 10! A new personal best!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Zara isn't the best player on her basketball team. Everyone's good at different things, and everyone grows at different speeds. The only person you're racing is you.",
      "remember": [
        "Getting better matters more than being the best.",
        "Compare yourself with YOU last month, not with other kids.",
        "'I can't do it' really means 'I can't do it YET.'"
      ],
      "sayThis": [
        "Not yet.",
        "I'm getting better.",
        "What can I work on?"
      ],
      "magicWords": "Not yet, but getting better.",
      "tryIt": "Use My Personal Bests at the back. Pick one skill, like free throws out of 10 or times tables in a minute. Record your score each week and circle every new best!",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "What's one thing you want to get better at?",
        "tip": "Celebrate personal bests, not where they rank."
      }
    },
    {
      "id": 12,
      "part": 2,
      "title": "When you're the one being mean (even to your brother or sister)",
      "panels": [
        {
          "caption": "Who gets the controller?",
          "scene": "home",
          "people": [
            {
              "who": "theo",
              "x": 0.3,
              "face": "cross",
              "arm": "give",
              "item": "controller"
            },
            {
              "who": "kid",
              "x": 0.72,
              "face": "sad",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Hey! That's mine!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Theo feels the volcano",
          "scene": "home",
          "people": [
            {
              "who": "theo",
              "x": 0.5,
              "face": "cross",
              "arm": "hips"
            }
          ],
          "speech": [
            {
              "text": "I'm about to blow... I need a break.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Later, Theo makes it right",
          "scene": "home",
          "people": [
            {
              "who": "theo",
              "x": 0.3,
              "face": "proud",
              "arm": "give",
              "item": "controller"
            },
            {
              "who": "kid",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "I'm sorry I grabbed it. Next time I'll ask.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Thanks.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Everyone is mean sometimes, especially to brothers and sisters. It doesn't make you a bad kid. What matters is fixing it.",
      "remember": [
        "Being cross is OK. Hurting isn't.",
        "When you're about to blow, walk away.",
        "A real sorry has 3 parts: what you did, sorry, and how you'll fix it."
      ],
      "sayThis": [
        "I need a break.",
        "I'm sorry I ___. Next time I'll ___."
      ],
      "magicWords": "Walk away, then make it right.",
      "tryIt": "Practise a 3-part sorry with a pretend problem: \"I'm sorry I grabbed the remote. Next time I'll ask.\"",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "What does your brother or sister do that drives you bonkers?",
        "tip": "Don't worry about who started it. Focus on what to do next time."
      }
    },
    {
      "id": 13,
      "part": 2,
      "title": "When someone says \"boys don't\" or \"girls can't\"",
      "panels": [
        {
          "caption": "At the court",
          "scene": "court",
          "people": [
            {
              "who": "kid2",
              "x": 0.3,
              "face": "smirk",
              "arm": "hips"
            },
            {
              "who": "ruby",
              "x": 0.72,
              "face": "cross",
              "arm": "hold",
              "item": "ball",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Girls can't play basketball!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Sam is upset",
          "scene": "outside",
          "people": [
            {
              "who": "older",
              "x": 0.3,
              "face": "smirk",
              "arm": "point"
            },
            {
              "who": "sam",
              "x": 0.72,
              "face": "sad",
              "arm": "down",
              "tears": true,
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Boys don't cry!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Ruby and Sam answer",
          "scene": "court",
          "people": [
            {
              "who": "ruby",
              "x": 0.3,
              "face": "proud",
              "arm": "up",
              "item": "ball"
            },
            {
              "who": "sam",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Anyone can play!",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Everyone cries sometimes.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Some people say there are boy things and girl things. But feelings, sports, colours and toys are for everyone. You can like what you like, and you can feel what you feel.",
      "remember": [
        "Everyone cries sometimes. It means something matters.",
        "There are no boy feelings or girl feelings. Just feelings.",
        "Strong means kind, brave and honest."
      ],
      "sayThis": [
        "Anyone can play.",
        "I like what I like.",
        "Everyone cries sometimes."
      ],
      "magicWords": "Be yourself. Strong and kind.",
      "tryIt": "Watch a clip of athletes crying after a big game. Are they tough? Are they crying? Can you be both?",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "Have you ever heard someone say 'boys don't' or 'girls can't'?",
        "tip": "Name real people in your child's life who prove these wrong."
      }
    },
    {
      "id": 14,
      "part": 2,
      "title": "When big feelings take over",
      "panels": [
        {
          "caption": "Kai's volcano",
          "scene": "home",
          "people": [
            {
              "who": "kai",
              "x": 0.5,
              "face": "cross",
              "arm": "up"
            }
          ],
          "speech": [
            {
              "text": "ARGH! It's not fair!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Kai breathes",
          "scene": "home",
          "people": [
            {
              "who": "kai",
              "x": 0.5,
              "face": "neutral",
              "arm": "hold"
            }
          ],
          "speech": [
            {
              "text": "Smell the flower... blow out the candle.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Kai names it",
          "scene": "home",
          "people": [
            {
              "who": "kai",
              "x": 0.3,
              "face": "neutral",
              "arm": "down"
            },
            {
              "who": "adult2",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "I feel angry. I need a break.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Good plan.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Sometimes feelings get really big, like a volcano. Angry, worried, sad. All feelings are OK. It's what we do with them that matters.",
      "remember": [
        "Name it: 'I feel angry' makes the feeling smaller.",
        "Your body can help: slow breaths, water, a run, a big squeeze.",
        "Worries are your brain trying to protect you. You can talk back to them."
      ],
      "sayThis": [
        "I feel ___.",
        "I need a break.",
        "Smell the flower, blow out the candle."
      ],
      "magicWords": "Name it, breathe it, talk it.",
      "tryIt": "Fill in My Calm-Down Plan at the back. Practise flower-and-candle breathing at bedtime, so it's ready when you need it.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "If your feelings were weather, what would today be?",
        "tip": "Practise calming tools when your child is calm, not in the middle of a meltdown."
      }
    },
    {
      "id": 15,
      "part": 2,
      "title": "When you make a mistake and want to hide it",
      "panels": [
        {
          "caption": "Crash! The mug breaks",
          "scene": "home",
          "people": [
            {
              "who": "ruby",
              "x": 0.5,
              "face": "surprised",
              "arm": "up"
            }
          ],
          "speech": [
            {
              "text": "Oh no!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Ruby is tempted",
          "scene": "home",
          "people": [
            {
              "who": "ruby",
              "x": 0.5,
              "face": "worried",
              "arm": "down"
            }
          ],
          "speech": [
            {
              "text": "Maybe I'll just hide it...",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Ruby owns up",
          "scene": "home",
          "people": [
            {
              "who": "ruby",
              "x": 0.3,
              "face": "worried",
              "arm": "down"
            },
            {
              "who": "adult",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "It was me. I'm sorry.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Thanks for telling me. Let's fix it.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Everyone makes mistakes, even grown-ups. Hiding a mistake usually makes it worse. Telling the truth, even late, always goes better.",
      "remember": [
        "Most things can be fixed.",
        "Telling the truth is brave.",
        "Your grown-ups will always love you, even when they're disappointed."
      ],
      "sayThis": [
        "I need to tell you something.",
        "It was me. I'm sorry.",
        "How can I fix it?"
      ],
      "magicWords": "Tell it, fix it.",
      "tryIt": "Ask your grown-up about the biggest mistake they made when they were your age. Did they own up?",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "What would make it easier to tell me when something goes wrong?",
        "tip": "When they own up, stay calm and thank them. That reaction is the lesson."
      }
    },
    {
      "id": 16,
      "part": 2,
      "title": "When it's time to stop the game",
      "panels": [
        {
          "caption": "Game time",
          "scene": "screen",
          "people": [
            {
              "who": "sam",
              "x": 0.3,
              "face": "happy",
              "arm": "hold",
              "item": "controller"
            },
            {
              "who": "adult2",
              "x": 0.75,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "10 minutes left!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Sam gets ready",
          "scene": "screen",
          "people": [
            {
              "who": "sam",
              "x": 0.4,
              "face": "neutral",
              "arm": "hold",
              "item": "controller"
            }
          ],
          "speech": [
            {
              "text": "Finish my race... then save.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Sam switches",
          "scene": "outside",
          "people": [
            {
              "who": "sam",
              "x": 0.3,
              "face": "happy",
              "arm": "up",
              "item": "ball"
            },
            {
              "who": "adult2",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Saved! Switching!",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Legend!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Games and videos are made to be hard to stop. There's always one more level! A warning helps your brain get ready to switch.",
      "remember": [
        "It's not your fault it's hard to stop.",
        "Screens before bed make it harder to sleep.",
        "'Free Robux' and 'free skins' are tricks."
      ],
      "sayThis": [
        "OK, finishing my level.",
        "Save and switch."
      ],
      "magicWords": "Warning, save, switch.",
      "tryIt": "For one week: 10-minute warning, then 'save and switch' with no fuss. Give yourself a tick for every good switch.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "Why do you think it's so hard to stop playing?",
        "tip": "Agree the rules ahead of time (see Family Tech Rules) so you're not arguing in the moment."
      }
    },
    {
      "id": 17,
      "part": 3,
      "title": "You're the boss of your body",
      "panels": [
        {
          "caption": "A family visit",
          "scene": "home",
          "people": [
            {
              "who": "adult2",
              "x": 0.32,
              "face": "happy",
              "arm": "up"
            },
            {
              "who": "mia",
              "x": 0.74,
              "face": "worried",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Come and give me a big hug!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Mia checks how she feels",
          "scene": "home",
          "people": [
            {
              "who": "mia",
              "x": 0.5,
              "face": "worried",
              "arm": "down"
            }
          ],
          "speech": [
            {
              "text": "I don't feel like a hug today.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Mia chooses",
          "scene": "home",
          "people": [
            {
              "who": "mia",
              "x": 0.3,
              "face": "happy",
              "arm": "wave"
            },
            {
              "who": "adult2",
              "x": 0.72,
              "face": "happy",
              "arm": "wave",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Can we high-five instead?",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Sure! High-five!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Your body belongs to you. You get to choose about hugs and kisses, even with family. Your private parts are the parts your swimmers cover.",
      "remember": [
        "You're the boss of your body.",
        "If anything feels wrong or uncomfortable, you can say no, even to a grown-up.",
        "You will never be in trouble for telling."
      ],
      "sayThis": [
        "NO! (loud voice, hand up)",
        "GO: get away.",
        "TELL: tell a grown-up on your Safety Hand."
      ],
      "magicWords": "No, go, tell.",
      "tryIt": "Practise a big, loud \"NO!\" with your hand up like a stop sign. Then fill in My Safety Hand with 5 grown-ups you trust.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "Who's the boss of your body?",
        "tip": "Use the correct names for body parts, and back your child up when they choose a high-five over a hug."
      }
    },
    {
      "id": 18,
      "part": 3,
      "title": "Surprises are fun. Secrets get told.",
      "panels": [
        {
          "caption": "A surprise",
          "scene": "home",
          "people": [
            {
              "who": "sam",
              "x": 0.3,
              "face": "happy",
              "arm": "hips"
            },
            {
              "who": "zara",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Shh! It's a surprise party for Mum!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "A secret",
          "scene": "outside",
          "people": [
            {
              "who": "adult",
              "x": 0.32,
              "face": "smirk",
              "arm": "give",
              "item": "lolly"
            },
            {
              "who": "kai",
              "x": 0.74,
              "face": "worried",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Don't tell your mum I gave you this.",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Kai tells",
          "scene": "home",
          "people": [
            {
              "who": "kai",
              "x": 0.3,
              "face": "neutral",
              "arm": "down"
            },
            {
              "who": "adult2",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Someone told me not to tell you something.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Thank you for telling me.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "A surprise is fun, and everyone finds out soon, like a birthday present. A secret is something someone wants you to hide from your parents. Safe grown-ups don't ask kids to keep secrets.",
      "remember": [
        "Surprises are fine.",
        "If anyone says 'Don't tell your mum or dad', that's your signal to tell.",
        "You won't be in trouble for telling."
      ],
      "sayThis": [
        "We don't keep secrets in our family.",
        "I'm telling Mum and Dad."
      ],
      "magicWords": "Surprises are fun. Secrets get told.",
      "tryIt": "Play Surprise or Tell? at the back. A grown-up reads each one and you shout 'Surprise!' or 'Tell!'",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "What's the difference between a surprise and a secret?",
        "tip": "If your child shares a real secret, stay calm, thank them, and get advice (see Who Can Help)."
      }
    },
    {
      "id": 19,
      "part": 3,
      "title": "Always check first",
      "panels": [
        {
          "caption": "At the park",
          "scene": "outside",
          "people": [
            {
              "who": "adult2",
              "x": 0.3,
              "face": "happy",
              "arm": "wave"
            },
            {
              "who": "ruby",
              "x": 0.72,
              "face": "surprised",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Want to come and see my new puppy?",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Ruby remembers the rule",
          "scene": "outside",
          "people": [
            {
              "who": "ruby",
              "x": 0.5,
              "face": "proud",
              "arm": "stop"
            }
          ],
          "speech": [
            {
              "text": "I have to ask my mum first.",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Ruby finds her mum",
          "scene": "outside",
          "people": [
            {
              "who": "ruby",
              "x": 0.3,
              "face": "neutral",
              "arm": "down"
            },
            {
              "who": "adult",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Someone asked me to see a puppy.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "You checked first. Great job!",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Most grown-ups are kind. But grown-ups who need help ask other grown-ups, not kids. Before you go anywhere with anyone, check with your grown-up first, even if you know the person.",
      "remember": [
        "Always check first, every time.",
        "Anyone picking you up who isn't on the plan must know the family password.",
        "Need help? Ask a parent with kids or someone working in a shop."
      ],
      "sayThis": [
        "I have to ask my mum or dad first.",
        "What's our password?"
      ],
      "magicWords": "Check first, every time.",
      "tryIt": "Choose a silly family password together (like 'purple kangaroo'). Write it on Things I Know By Heart. Only your family and pick-up grown-ups know it.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "If you needed help and couldn't find me, who would you ask?",
        "tip": "Role-play the 'puppy' trick and praise them for checking first."
      }
    },
    {
      "id": 20,
      "part": 3,
      "title": "If you get lost",
      "panels": [
        {
          "caption": "Theo can't see Dad",
          "scene": "shop",
          "people": [
            {
              "who": "theo",
              "x": 0.5,
              "face": "worried",
              "arm": "down"
            }
          ],
          "speech": [
            {
              "text": "Where's Dad? Stop. Stay.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Theo asks a shop worker",
          "scene": "shop",
          "people": [
            {
              "who": "theo",
              "x": 0.3,
              "face": "neutral",
              "arm": "down"
            },
            {
              "who": "adult",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "I'm lost. My dad's number is...",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Let's call him.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "On a bushwalk: hug a tree",
          "scene": "bush",
          "people": [
            {
              "who": "theo",
              "x": 0.5,
              "face": "neutral",
              "arm": "hold"
            }
          ],
          "speech": [
            {
              "text": "Stay put. They'll find me.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        }
      ],
      "happening": "Getting lost is scary, but if you know the plan you'll know what to do. The most important thing: stop and stay. Your grown-up will come to you.",
      "remember": [
        "Shops: stop, ask a parent with kids or a shop worker, say your grown-up's phone number.",
        "Bush: stop, stay put and hug a tree.",
        "Beach: swim between the red and yellow flags. In trouble? Float and raise one arm."
      ],
      "sayThis": [
        "I'm lost. My mum's number is ___.",
        "Hug a tree.",
        "Flags, float, wave."
      ],
      "magicWords": "Stop and stay. We'll find you.",
      "tryIt": "Turn your grown-up's phone number into a chant or song until you know it by heart. Write it on Things I Know By Heart.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "If we got split up at the campground, what would you do?",
        "tip": "At the beach, walk to the lifeguard tower and say hello."
      }
    },
    {
      "id": 21,
      "part": 3,
      "title": "When something online feels weird",
      "panels": [
        {
          "caption": "Kai is playing online",
          "scene": "screen",
          "people": [
            {
              "who": "kai",
              "x": 0.35,
              "face": "happy",
              "arm": "hold",
              "item": "controller"
            }
          ],
          "speech": [
            {
              "text": "NEW MESSAGE: What school do you go to?",
              "speaker": null,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Kai gets an uh-oh feeling",
          "scene": "screen",
          "people": [
            {
              "who": "kai",
              "x": 0.4,
              "face": "worried",
              "arm": "hold",
              "item": "controller"
            }
          ],
          "speech": [
            {
              "text": "That feels weird. I don't share that.",
              "speaker": 0,
              "kind": "think"
            }
          ]
        },
        {
          "caption": "Kai tells",
          "scene": "home",
          "people": [
            {
              "who": "kai",
              "x": 0.3,
              "face": "neutral",
              "arm": "down"
            },
            {
              "who": "adult",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Someone in my game asked where I go to school.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Thanks for telling me. Let's block them.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Most people online are fine. But people aren't always who they say they are. If something gives you an 'uh-oh' feeling, that's your body telling you something's not right.",
      "remember": [
        "Never share your real name, school, address or photos.",
        "'Free Robux' or 'free skins' are tricks.",
        "Uh-oh feeling? Turn it over and tell a grown-up. You won't be in trouble."
      ],
      "sayThis": [
        "I don't share that.",
        "Block, report, tell."
      ],
      "magicWords": "Turn it over, tell a grown-up.",
      "tryIt": "Show your grown-up your favourite game. Teach them how to block and report someone.",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "Who do you play with online?",
        "tip": "Keep chat off or friends-only, and devices in shared rooms."
      }
    },
    {
      "id": 22,
      "part": 3,
      "title": "When you see or hear something rude",
      "panels": [
        {
          "caption": "An older kid has a phone",
          "scene": "school",
          "people": [
            {
              "who": "older",
              "x": 0.3,
              "face": "smirk",
              "arm": "give",
              "item": "phone"
            },
            {
              "who": "zara",
              "x": 0.74,
              "face": "surprised",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Hey, look at this!",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Zara looks away",
          "scene": "school",
          "people": [
            {
              "who": "zara",
              "x": 0.5,
              "face": "proud",
              "arm": "stop"
            }
          ],
          "speech": [
            {
              "text": "I don't want to see that.",
              "speaker": 0,
              "kind": "say"
            }
          ]
        },
        {
          "caption": "Zara tells",
          "scene": "home",
          "people": [
            {
              "who": "zara",
              "x": 0.3,
              "face": "neutral",
              "arm": "down"
            },
            {
              "who": "adult2",
              "x": 0.72,
              "face": "happy",
              "arm": "down",
              "facingLeft": true
            }
          ],
          "speech": [
            {
              "text": "Someone showed me something rude.",
              "speaker": 0,
              "kind": "say"
            },
            {
              "text": "Thank you for telling me. You're not in trouble.",
              "speaker": 1,
              "kind": "say"
            }
          ]
        }
      ],
      "happening": "Some pictures and videos are only for grown-ups. They can show people with no clothes on. Kids' brains aren't ready for them, so they can feel yucky. Seeing something by accident is never your fault.",
      "remember": [
        "Look away, close it or turn it over. Then tell.",
        "Nobody should ever ask you to take or send photos of private parts. Always tell.",
        "Heard a rude word? Ask a grown-up what it means, instead of searching it."
      ],
      "sayThis": [
        "I don't want to see that.",
        "What does ___ mean?",
        "Look away, close it, tell."
      ],
      "magicWords": "Look away, walk away, tell.",
      "tryIt": "With your grown-up, check that safe search and kids' modes are turned on. They're on your team!",
      "myTurnPrompt": "Has this happened to you or someone you know? What would you say or do?",
      "grownUp": {
        "ask": "Have you heard a word at school you weren't sure was rude?",
        "tip": "Stay calm and answer simply. If anyone asked your child for photos, or an adult was involved, get advice (see Who Can Help)."
      }
    }
  ],
  "games": {
    "surpriseOrTell": [
      {
        "statement": "Grandma is planning a surprise party for Dad.",
        "answer": "Surprise"
      },
      {
        "statement": "A grown-up says, \"Don't tell your mum I gave you lollies.\"",
        "answer": "Tell"
      },
      {
        "statement": "You and Dad are making Mum a card for her birthday.",
        "answer": "Surprise"
      },
      {
        "statement": "A friend says, \"Don't tell anyone I took it from the shop.\"",
        "answer": "Tell"
      },
      {
        "statement": "Someone in a game says, \"Let's keep our chats secret.\"",
        "answer": "Tell"
      },
      {
        "statement": "Your brother bought you a birthday present and won't say what it is.",
        "answer": "Surprise"
      },
      {
        "statement": "Someone makes you feel uncomfortable and says, \"This is our secret.\"",
        "answer": "Tell"
      },
      {
        "statement": "A friend says they're being hurt and asks you not to tell.",
        "answer": "Tell"
      },
      {
        "statement": "You're hiding chocolate eggs for an Easter egg hunt.",
        "answer": "Surprise"
      },
      {
        "statement": "A grown-up gives you game money and says, \"Your parents don't need to know.\"",
        "answer": "Tell"
      },
      {
        "statement": "An older kid shows you a rude video and says, \"Don't tell or you'll get in trouble.\"",
        "answer": "Tell"
      }
    ],
    "whatIfCards": [
      "A friend says, \"You're not my friend unless you do it.\"",
      "A friend says, \"Everyone's allowed to play this game except you.\"",
      "Your friends dare you to jump off the shed roof.",
      "Your friend pockets lollies at the shop and says, \"Hold this.\"",
      "Some kids say, \"You can't play with us.\"",
      "You get picked last for teams.",
      "Someone laughs at your new glasses.",
      "Someone calls you a mean name.",
      "The class laughs when a kid stutters reading aloud.",
      "Your friend won't stop tickling after you say stop.",
      "You lose the final of your game.",
      "You feel like the worst player on the team.",
      "Your brother or sister grabs your stuff.",
      "You feel so angry you want to throw something.",
      "Someone says, \"Girls can't play\" or \"Boys don't cry.\"",
      "You broke something and nobody saw.",
      "It's 10 minutes until screen time ends.",
      "Someone wants a hug and you don't feel like one.",
      "A grown-up says, \"Don't tell your mum or dad.\"",
      "A neighbour asks you to come and see their new puppy.",
      "You can't find your parent at the shops.",
      "You lose sight of the tent on a bush walk.",
      "Someone in a game asks what school you go to.",
      "A video suddenly shows something rude.",
      "An older kid says, \"Look at this,\" and it's a rude picture on their phone.",
      "A kid at school says a rude word you don't understand.",
      "Someone in a group chat posts a mean photo of a classmate."
    ],
    "goodOrBossyFriend": {
      "good": [
        "Takes turns and shares",
        "Lets you choose the game sometimes",
        "Sticks up for you",
        "Says sorry when they get it wrong",
        "Is happy when you do well",
        "Listens when you say stop",
        "Lets you hang out with other friends too"
      ],
      "bossy": [
        "Always has to be in charge",
        "Says \"You're not my friend unless...\"",
        "Gets you in trouble and leaves you to cop it",
        "Laughs at you in front of others",
        "Gets cross when you play with someone else",
        "Dares you to do dumb stuff, or to take things",
        "Tells you to keep secrets from your parents"
      ]
    }
  },
  "activities": {
    "knowByHeart": [
      "My full name:",
      "Parent 1 full name and mobile:",
      "Parent 2 full name and mobile:",
      "My address (street and suburb):",
      "Our family password (for pick-ups):",
      "If I get lost: Stop and stay. Ask a parent with kids or a shop worker.",
      "In an emergency I call: 000"
    ],
    "techRules": [
      "Devices are used in shared rooms, not bedrooms.",
      "Screens off an hour before bed.",
      "Ask before downloading any new game or app.",
      "Game chat is off, or only with friends we know in real life.",
      "I never share my real name, school, address or photos online.",
      "Free Robux or free skins? That's a trick.",
      "Buying anything in a game needs a yes from a parent.",
      "Uh-oh feeling? Turn it over, tell a grown-up.",
      "Safe search and kids' modes stay on (e.g. YouTube Kids).",
      "Group chats: I don't post anything I wouldn't say to someone's face.",
      "10-minute warning, then save and switch.",
      "School days: ______ minutes.   Weekends: ______ minutes."
    ],
    "calmPlan": [
      {
        "level": 5,
        "label": "Volcano",
        "action": "I might hurt someone or something. I need a grown-up now."
      },
      {
        "level": 4,
        "label": "Really cross or scared",
        "action": "I need a break in my calm spot."
      },
      {
        "level": 3,
        "label": "Grumpy or worried",
        "action": "I can breathe and tell someone."
      },
      {
        "level": 2,
        "label": "A bit bothered",
        "action": "I can name it and keep going."
      },
      {
        "level": 1,
        "label": "Calm",
        "action": "I feel good and ready to play."
      }
    ],
    "calmPrompts": [
      "When I'm getting cross, my body feels like:",
      "Three things that help me calm down:",
      "My calm spot at home is:",
      "A grown-up I can go to at school:"
    ]
  }
}
```

# Appendix 2: Character and visual design reference

All characters are original and drawn from simple shapes, so they can be rebuilt in Compose `Canvas` or as vector drawables.

**Body construction (relative to character height h):** legs are two thick rounded lines from 0.30h down to the ground, with dark oval shoes; the body is a rounded rectangle from 0.30h to 0.66h, width 0.36h, filled with the top colour; the head is a circle of radius 0.17h sitting just above the body; hair is drawn as a cap shape over the top of the head, plus extras by style; arms are two-tone lines (sleeve in top colour, then skin) ending in a small round hand.

**Hair styles:** `short` (cap only), `spiky` (cap plus four triangle spikes), `long` (rounded rectangle behind the head down to the shoulders), `ponytail` (oval behind one side), `puffs` (two circles on top either side).

**Faces:** two dot eyes. `happy`/`proud`: smile arc plus pink cheeks. `sad`: frown arc. `worried`: small frown plus slanted brows. `cross`: straight mouth plus angry brows. `surprised`: bigger eyes plus small round mouth. `smirk`: half smile. `laugh`: open dark half-moon mouth. `neutral`: short straight line. Optional blue teardrop for `tears`.

**Arm poses:** `down`, `wave` (one hand up by head), `stop` (one arm straight out with flat palm), `point`, `hold` (both hands together in front, holding an item), `up` (both arms up), `hips` (hands on hips), `give` (one arm out, holding an item).

**Items:** phone, lolly, ball, controller, invite card, book.

**Scenes (panel backgrounds):** shop (shelves of coloured products), school (green chalkboard), court (basketball hoop, wooden floor), party (balloons), home (window and couch), outside (tree and grass), beach (sea and red/yellow flags), bush (three trees), screen (a monitor), plain.

| Key | Name | Skin | Hair colour | Hair style | Top | Legs | Notes |
|---|---|---|---|---|---|---|---|
| `zara` | Zara | `#8D5A3B` | `#1E1A18` | puffs | `#8E6CC9` | `#3B4A6B` |  |
| `sam` | Sam | `#F2C9A8` | `#7A4A2A` | short | `#3E8ED0` | `#3B4A6B` | always wears round glasses |
| `kai` | Kai | `#D9A877` | `#151515` | spiky | `#F08A34` | `#4A4A4A` |  |
| `ruby` | Ruby | `#F6D3BC` | `#C4502A` | ponytail | `#3BAA6E` | `#3B4A6B` |  |
| `theo` | Theo | `#5C3A26` | `#121010` | short | `#E04B4B` | `#4A4A4A` |  |
| `kid` | Other kid (yellow top, long hair) | `#E8B48F` | `#3A2A1E` | long | `#F2C94C` | `#5A5A7A` |  |
| `mia` | Mia | `#C68B59` | `#2A1A10` | ponytail | `#FF8FAB` | `#3B4A6B` |  |
| `kid2` | Other kid (grey top, short hair) | `#B9825A` | `#2A1C14` | short | `#9AA5B1` | `#4A4A4A` |  |
| `older` | Older kid (taller, grey top) | `#E6B896` | `#2D2D2D` | short | `#6B6F76` | `#2F3440` | height x1.18 |
| `adult` | Grown-up (teal top, long hair) | `#E9BE9A` | `#5A3A22` | long | `#2E7D7A` | `#3A3F4A` | height x1.35 |
| `adult2` | Grown-up (blue top, short hair) | `#7A4E33` | `#141414` | short | `#5470C6` | `#3A3F4A` | height x1.38 |

**Story page layout (print version, for reference):** coloured title band (part colour) with 'STORY n • PART NAME' kicker → three-panel comic, each panel with a numbered caption bar in the part colour → What's happening? (light-bulb icon) → two columns: Remember (star icon, ticked list) and Say this (speech-bubble icon, bubble-shaped boxes) → yellow Magic Words bar → Try it! box → My Turn box with writing lines → small grey Grown-up Corner.
