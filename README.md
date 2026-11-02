# Before It Happens: The Kids' Book (Android)

An Android picture-book app for kids aged 8 to 11, built from the brief in
[`docs/Before_It_Happens_Kids_Book.md`](docs/Before_It_Happens_Kids_Book.md).
It covers 22 everyday situations, each with a comic, words to say, Magic Words and a game.

![Home, cover and a story screen](docs/screenshots/app.png)
![Comics drawn in Compose Canvas](docs/screenshots/comics.png)

## What's in it

- **Onboarding:** "This book belongs to", reading buddy, pick an avatar from the crew.
- **Home:** the three parts, Magic Words, Activities, Meet the crew, How to use this book, and a grown-ups area.
  A red **Who can help** button is on every kids' screen.
- **22 stories:** a three-panel comic (panels animate in; tap one to hear it), What's happening?, Remember,
  Say this (tap to hear, plus a "Say it out loud" practice prompt), Magic Words with **Got it!**, Try it!
  (with a shortcut to the matching activity), My Turn (saved on the device), and a collapsed Grown-up Corner.
  "Read to me" reads the whole story. Stories get a tick when read and can be favourited.
- **Games:** Magic Words flip cards with a Quiz me mode, What If? cards (Show me jumps to the story),
  Surprise or Tell? with a score, and Good Friend or Bossy Friend? sorting.
- **My pages:** Safety Hand, Things I Know By Heart (behind the PIN, with a Practise mode), Personal Bests
  (new bests starred, with a line chart), Calm-Down Plan (1 to 5 scale, four prompts, 5-breath flower and
  candle animation), Our Tech Rules.
- **Who can help:** 000 and Kids Helpline open the dialler (`ACTION_DIAL`); the child presses call.
  Their Safety Hand grown-ups appear underneath.
- **Grown-ups area (PIN):** reading-together notes, "if your child tells you something serious", services
  and further reading (websites appear only here), every Grown-up Corner tip, change PIN, and reset all data.

## Privacy and safety

- No `INTERNET` permission (it's removed in the manifest even if a library asks for it). No ads, accounts,
  analytics, notifications or purchases.
- Everything typed is stored with DataStore on the device only. `allowBackup="false"` and the data extraction
  rules exclude it from cloud backup and device transfer.
- The PIN is stored as a salted SHA-256 hash. Unlocking lasts until the app goes into the background.
- "Say it out loud" never uses the microphone. Speech recognition was optional in the brief, so it is skipped
  and nothing is recorded. Read-aloud uses the on-device text-to-speech engine (en-AU when available).

## Content is data

All wording lives in `app/src/main/assets/`:

- `stories.json`: Appendix 1 from the brief, unchanged (stories, comic panels, games, activities).
- `book.json`: the front pages, activity page intros, Part E (Who can help), the character specs from
  Appendix 2, and `whatIfStories`, which maps each of the 27 What If? cards to a story.

Editing these files changes the app without code changes. The comics are drawn in Compose `Canvas` from
each panel's `scene`, `people` (`who`, `x`, `face`, `arm`, `item`, `facingLeft`, `tears`) and `speech`.

## Build

Requirements: JDK 17 or newer, Android SDK 36.

```bash
./gradlew :app:assembleDebug          # APK in app/build/outputs/apk/debug/
./gradlew :app:testDebugUnitTest      # content checks + screenshot tests
```

`ContentTest` checks that all 22 stories, the games and the comic data are complete and drawable.
`ScreenshotTest` uses Robolectric to render every comic and a walk through the app to
`app/build/screenshots/`, so you can check the art without a device.

Stack: Kotlin, Jetpack Compose, Material 3, Navigation Compose, DataStore, kotlinx.serialization;
min SDK 26, target SDK 36. Font: Poppins (SIL Open Font License, see `app/src/main/assets/OFL-Poppins.txt`).

## Notes and choices

- **What If? → story mapping** isn't in the brief's data, so it was added to `book.json`. Two judgement calls:
  card 6 (picked last for teams) goes to Story 5 (left out), and card 27 (mean photo in a group chat) goes to
  Story 7 (someone is mean), which mentions group chats.
- **The PIN protects** Things I Know By Heart and the grown-ups area, as the brief describes. The Safety Hand
  stays open so a child can always reach their grown-ups' numbers.
- **The orange part colour** (`#D9822B`) is darkened automatically where white text sits on it, to meet
  contrast guidelines.
- Services and numbers are for Victoria, Australia, as in the brief (checked October 2026).
