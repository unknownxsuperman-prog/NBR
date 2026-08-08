# Glass — Android

A native Android port of the desktop Glass browser: real WebView-based
browsing, Kotlin + Jetpack Compose UI, same Nothing OS black/white/red
palette and New Tab layout (search pill, quicklinks, Shortcuts/Privacy
cards, overflow menu) — but rendered natively instead of as HTML in a
WebView, so it's fast and feels like a real Android app.

## Opening the project

1. Open **Android Studio** (Koala/2024.1 or newer recommended).
2. **File → Open** → select the `glass-android` folder (the one containing
   `settings.gradle.kts`).
3. Let Gradle sync — first sync will download the Android Gradle Plugin,
   Kotlin, and Compose dependencies, so it needs an internet connection and
   can take a few minutes the first time.
4. Run on an emulator or a physical device via the ▶ button — **minSdk 24**
   (Android 7.0+), so it'll run on basically anything from the last several
   years.

## What's here

- **Real WebView browsing** — each tab holds an actual `android.webkit.WebView`
  instance, reused across tab switches (so switching tabs doesn't reload the
  page or lose scroll position/history)
- **Native New Tab page** — the Nothing OS home screen (search, quicklinks,
  Shortcuts card, Privacy card, `⋯` overflow menu) is built entirely in
  Compose, not HTML — it's the actual browser chrome, not a loaded web page
- **Toolbar pill** — back / forward / reload / home + address field, same
  visual language as the desktop app's toolbar
- **Tab badge** — shows the tab count; tap to reveal a chip-style tab strip
  (hidden by default, mobile-app style)
- **Mobile-style tab shifts** — switching between New Tab and web content
  fades rather than cutting instantly (`AnimatedContent`)
- **System back button** — navigates page history first, only exits the app
  once there's nowhere left to go back to
- **Adaptive app icon** — the same logo mark, white background + black/red
  foreground, following Android's adaptive icon spec (masks correctly to
  circle/squircle/rounded-square depending on the launcher)

## Project layout

```
app/src/main/java/com/unknownxsuperman/glass/
  MainActivity.kt          entry point
  TabState.kt               per-tab state (title, url, WebView instance)
  BrowserViewModel.kt        tab list + current-index management
  ui/
    BrowserApp.kt            toolbar, tab strip, tab-switch animation
    NewTabScreen.kt           the Nothing OS new-tab UI
    WebViewHost.kt            AndroidView wrapper around WebView
    theme/
      Color.kt                palette (mirrors the desktop app's CSS vars)
      Type.kt
      Theme.kt

app/src/main/res/
  drawable/ic_logo_mono.xml       all-white logo (on the red quicklink tile)
  drawable/ic_logo_brand.xml      white + brand-red logo (dropdown menu)
  drawable/ic_logo_foreground.xml adaptive-icon foreground layer
  mipmap-anydpi-v26/               adaptive icon definitions
  values/                          strings, colors, base theme
```

## Notes / things you may want to change

- **Package name / app ID** is `com.unknownxsuperman.glass` — change it in
  `app/build.gradle.kts` (`namespace`, `applicationId`) and move the Kotlin
  package folders to match if you want something else.
- **Fonts**: the "Browser" wordmark uses `FontFamily.Serif` as a stand-in
  for DM Serif Display (no downloadable-fonts setup here to keep the
  project buildable offline-first). Drop a DM Serif Display `.ttf` into
  `res/font/` and swap it in `Type.kt` for an exact match to the desktop
  version.
- **minSdk 24** means devices on Android 7.0/7.1 (API 24–25) won't get the
  adaptive icon (that needs API 26+) — they'll fall back to a system
  default icon. Not a functional issue, just cosmetic on very old devices.
- **JavaScript is enabled** in the WebView (`settings.javaScriptEnabled =
  true`) since most modern sites need it to render at all — standard for
  any real browser, just flagging it since it's sometimes a lint warning.
