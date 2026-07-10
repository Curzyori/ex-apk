# ExAPK — APK Extractor & Backupper

**Version:** 1.1.0  
**Status:** Implemented  
**Last Updated:** 2026-07-03

---

## 1. Overview

### Project Name
**ExAPK** — APK Extractor & Backupper

### Project Type
Android Native Utility Application

### Core Functionality
Aplikasi Android untuk menyalin file `.apk` dari aplikasi terinstal ke folder publik (`/Download/ExAPK/`) agar bisa di-backup atau dibagikan ke perangkat lain tanpa Play Store.

### Target Users
Campuran:
- **General users** — backup sebelum factory reset /换 HP
- **Developer/QA** — extract APK buat testing
- **Collectors** — arsipin app favorit
- **Power users** — distribute app tanpa Play Store

---

## 2. Goals & Non-Goals

### Goals
- Extract APK apapun dari HP tanpa root
- UI clean dan fast — iterate 50 apps dalam <1 menit
- User bisa share APK ke orang lain dan berfungsi (compatibility)
- Fully offline — tidak butuh internet

### Non-Goals
- ❌ No root required
- ❌ No split APKs combining — only raw original APK
- ❌ No re-signing / modify APK content
- ❌ No install from extracted APK — bukan installer
- ❌ No Play Store release
- ❌ No ads
- ❌ No analytics / tracking
- ❌ No internet permission

---

## 3. Design Language

### Color Palette
| Name | Hex | Usage |
|------|-----|-------|
| Abu Primary | `#6B7280` | Primary elements, icons |
| Abu Dark | `#374151` | Dark mode accents |
| Putih | `#FFFFFF` | Background, cards |
| Hitam | `#111827` | Dark mode background |
| Abu Ringan | `#F3F4F6` | Light mode background |
| Success | `#10B981` | Extraction success |
| Error | `#EF4444` | Error states |
| Warning | `#F59E0B` | Warnings |

### Typography
- **System Font** (Roboto) — clean, readable
- **Title:** 20sp Bold
- **Subtitle:** 16sp Medium
- **Body:** 14sp Regular
- **Caption:** 12sp Regular

### Theme
- **Manual Toggle:** Dark / Light / Auto (follow system)
- **Material 3** Design Components

---

## 4. Features

### 4.1 App List Screen

**Functionality:**
- List semua app terinstal (user + sistem)
- Filter chips: All / User / Sys (grouped with label)
- Sort chips: Name / Size / Date (grouped with label)
- Real-time search by app name
- Checkbox multi-select untuk batch
- "Select All" shortcut button di top bar
- FAB dengan icon + text "Extract (N)" saat ada selection
- App count subtitle di top bar (e.g., "247 apps")

**Data Displayed:**
- App icon (from PackageManager)
- App name
- System indicator icon (Android icon)
- Version name
- APK size (formatted: KB/MB/GB)

**Interaction:**
- Tap anywhere di row → open detail bottom sheet
- Checkbox di kanan untuk selection
- Selected state dengan background highlight

**States:**
- Loading: Shimmer placeholder dengan icon + text lines
- Empty (filter): Icon + message + suggestion text
- Empty (search): Icon + message + suggestion text
- Error: Icon + message + Retry button

### 4.2 App Detail Bottom Sheet

**Trigger:** Tap app item

**Content:**
- App icon + name
- Package name (truncated with ellipsis)
- Version name + version code
- APK size
- Type (User App / System App)
- Install date

**Actions:**
- Extract APK button (primary)
- Share button (secondary, with share icon)

**Layout:** Horizontal button row for compact design

### 4.3 Extraction

**Single Extract:**
1. User tap app → bottom sheet
2. Tap "Extract APK"
3. Show progress indicator
4. Success: toast + open file option
5. Error: show error message

**Batch Extract:**
1. User select multiple apps via checkbox
2. FAB shows count: "Extract (3)"
3. Tap FAB → confirmation dialog
4. Sequential extraction dengan progress
5. Summary: "Extracted 47/50 (1 skipped, 2 failed)"

**Output:**
- Default folder: `/Download/ExAPK/`
- Filename pattern: `<packageName>_<version>.apk`
- Example: `com_whatsapp_2.24.22.11.apk`
- Dots in package name replaced with underscores for file system compatibility
- Conflict handling: overwrite existing

### 4.4 Share

- System share intent via `Intent.ACTION_SEND`
- MIME type: `application/vnd.android.package-archive`
- Multiple files: `EXTRA_STREAM` dengan `ArrayList<Uri>`

### 4.5 Settings Screen

**Accessed via:** Top-right icon ⚙️

**Sections:**

**APPEARANCE:**
| Setting | Type | Default |
|---------|------|---------|
| Dark Mode | BottomSheet with RadioButton options | Auto (follow system) |

**LANGUAGE:**
| Setting | Type | Options |
|---------|------|---------|
| Bahasa | BottomSheet with RadioButton options | English, Indonesia |

**ABOUT:**
| Setting | Action |
|---------|--------|
| GitHub Repository | Open https://github.com/Curzyori/ex-apk |
| Website | Open https://ex-apk.curzy.dev/ |
| Support This Project | Show donate sheet |

**Donate Sheet:**
```
┌─────────────────────────────────────┐
│ ☕ Support This Project              │
│                                     │
│ [💜 Donate]                         │
│                                     │
│ Thank you for your support!         │
└─────────────────────────────────────┘
```

**Behavior:** Tap button → open `https://donate.curzy.dev/` in browser via `Intent.ACTION_VIEW`. No internet permission required — system browser handles it.

**Settings UX:** Theme and Language use `ModalBottomSheet` instead of AlertDialog for better mobile UX and gesture support.

---

## 5. Technical Specification

### 5.1 Build Configuration

**Gradle:** Kotlin DSL (`build.gradle.kts`)  
**Build Command:** `./gradlew assembleDebug`  
**Min SDK:** 29 (Android 10)  
**Target SDK:** 34 (Android 14)  
**Compile SDK:** 34

### 5.2 Dependencies

**Core:**
- `androidx.core:core-ktx`
- `androidx.lifecycle:lifecycle-runtime-ktx`
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `androidx.activity:activity-compose`
- `androidx.compose.ui:ui`
- `androidx.compose.material3:material3`

**Navigation:**
- `androidx.navigation:navigation-compose`

**Dependency Injection:**
- `com.google.dagger:hilt-android`
- `androidx.hilt:hilt-navigation-compose`

**Preferences:**
- `androidx.datastore:datastore-preferences`

### 5.3 Architecture

```
com.curzyori.exapk/
├── di/                     # Hilt modules
├── data/
│   ├── model/              # AppInfo, etc.
│   ├── repository/         # AppRepository
│   └── source/             # PackageManager wrapper
├── domain/
│   └── usecase/            # ExtractUseCase
├── ui/
│   ├── theme/              # Color, Typography, Theme
│   ├── components/         # AppItem, FilterChip, FAB, etc.
│   ├── screens/
│   │   ├── MainScreen.kt
│   │   ├── SettingsScreen.kt
│   │   └── DonateSheet.kt
│   └── viewmodel/          # MainViewModel, SettingsViewModel
├── util/                   # Extensions, Formatters
└── ExAPKApplication.kt     # Hilt Application
```

### 5.4 Data Models

```kotlin
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val apkPath: String,
    val apkSize: Long,
    val installTime: Long,
    val isSystemApp: Boolean
)

enum class FilterType { ALL, USER_APPS, SYSTEM_APPS }
enum class SortType { NAME, SIZE, DATE }
enum class ThemeMode { LIGHT, DARK, AUTO }
enum class Language { EN, ID }
```

### 5.5 Permissions Required

| Permission | Reason |
|-----------|--------|
| `QUERY_ALL_PACKAGES` | Read all installed apps |
| `READ_EXTERNAL_STORAGE` | Read APK files |
| `WRITE_EXTERNAL_STORAGE` | Write to /Download (legacy, API < 29) |
| `MANAGE_EXTERNAL_STORAGE` | Optional, for full file access |

**Note:** Android 10+ scoped storage via MediaStore — no MANAGE_EXTERNAL_STORAGE required for normal use.

### 5.6 API Calls (None)

**Zero network permissions** — fully offline utility.

---

## 6. Edge Cases & Error Handling

| Scenario | Handling |
|----------|----------|
| APK path not accessible | Show "App not accessible" + skip in batch |
| Storage full | Show toast "Storage full" + abort batch |
| APK deleted during extract | Show "App uninstalled" + refresh list |
| App protected by DRM | Show "Cannot extract" + continue batch |
| App with same name (diff package) | Show package name below app name |
| Very long app name | Ellipsize with `maxLines=1` |
| APK size > 2GB | Show warning but proceed |
| App uninstalled mid-batch | Skip, show count "Extracted 47/50 (1 skipped)" |
| No apps found (filtered) | Empty state: "No apps match this filter" |
| Search no results | Empty state: "No apps found for 'xxx'" |
| Storage permission denied | Block extract button, show permission dialog |

---

## 7. Testing Strategy

### Unit Tests
- `AppRepository` — verify app filtering, sorting
- `ExtractManager` — verify path construction, error propagation
- `PreferencesManager` — verify DataStore read/write
- ViewModel state transitions

### Instrumented Tests
- Extract APK from real installed app
- Verify file exists in `/Download/ExAPK/`
- Share intent fires correctly
- Permission flow (grant/deny)

### Manual QA Checklist
- [ ] All apps listed correctly
- [ ] Search filters in real-time
- [ ] Filter chips toggle correctly
- [ ] Sort changes list order
- [ ] Single extract works
- [ ] Batch extract (10+ apps) works
- [ ] Progress shows accurate count
- [ ] Share opens system picker
- [ ] Settings language change reflects immediately
- [ ] Theme toggle works
- [ ] External links open browser
- [ ] Permission denied shows guide
- [ ] App uninstalled during batch handled gracefully

---

## 8. Distribution

- **Platform:** Direct APK download (no Play Store)
- **Website:** https://ex-apk.curzy.dev/
- **Repository:** https://github.com/Curzyori/ex-apk
- **Updates:** Manual (check website for new versions)

---

## 9. Appendix

### Decision Log

| # | Decision | Alternatives | Why |
|---|----------|-------------|-----|
| 1 | Min SDK 29 | 26, 28 | Scoped storage mandatory, modern approach |
| 2 | Sequential batch extract | Parallel | Simpler tracking, cancelable, sufficient |
| 3 | APK only (no OBB) | Include OBB | Most users cuma butuh APK, simpler |
| 4 | Manual dark/light/auto | System default only | User control + flexibility |
| 5 | Output: /Download/ExAPK/ | Custom folder per-app | Default works, user bisa ubah |
| 6 | System apps toggle | Always show / Always hide | User flexibility |
| 7 | Settings: lang, theme, github, website, donate | — | Core settings yang needed |
|| 13 | Local only, zero network | Cloud sync / analytics | Privacy-first, security-first |
| 14 | Color: Abu + Putih | — | Minimalist, monochromatic |
| 15 | Single Activity + Bottom Sheets | Multiple screens | Flat navigation, fast |
| 16 | Material 3 + MVVM + Hilt | XML layouts, no DI | Modern Android stack |
| 17 | DataStore for preferences | SharedPreferences | Modern, type-safe |
| 18 | Manual QA (no UI automation) | Espresso tests | Utility app, simple enough |

### v1.0.1 UI Refresh Decisions

| # | Decision | Alternatives | Why |
|---|----------|-------------|-----|
| U1 | AppItem: checkbox di kanan | Checkbox di kiri | Swipe-friendly, tap area tidak interfere |
| U2 | AppItem: tap anywhere → detail | Long press untuk detail | More discoverable, standard pattern |
| U3 | AppItem: background highlight selected | Border/outline | Subtle, Material 3 convention |
| U4 | System badge: icon vs text "SYS" | Text "SYS" | More visual, hemat space |
| U5 | Detail sheet: horizontal buttons | Stacked buttons | Compact, utilize full width |
| U6 | Detail sheet: truncate package name | Full width scrollable | Prevent overflow |
| U7 | Detail sheet: add version code | Version name only | More complete info |
| U8 | FilterChips: grouped with labels | Single row all chips | Clearer organization |
| U9 | Settings: AlertDialog → BottomSheet | Keep AlertDialog | Native, gesture-friendly |
| U10 | MainScreen: app count subtitle | Badge/counter elsewhere | Contextual tanpa clutter |
| U11 | MainScreen: Select All shortcut | Only checkbox selection | Faster batch select |
| U12 | Empty states: icons + retry action | Text only | Better UX, actionable |
| U13 | Progress: bottom bar vs full overlay | Full screen overlay | Less intrusive, context retained |

---

## v1.1.0 Implementation Changes

### Decision Log

| # | Decision | Alternatives | Why |
|---|----------|-------------|-----|
| B1 | Icon fallback: logo.png | Generic Android icon | Brand consistency, better UX |
| B2 | Simple null check for icon | remember() caching | Avoided compile issues |
| C1 | Android string resources (strings.xml) | Data class Map | Standard, compile-time safe |
| C2 | attachBaseContext for locale | Dynamic context switching | Simpler, more reliable |
| C3 | Activity recreation on language change | In-place recomposition | Ensures full locale refresh |
| D1 | File naming: `<packageName>_<version>.apk` | `<appName>_<version>_<hash>.apk` | Simpler, package name already unique |
| D2 | Package dots converted to underscores | Keep dots | File system compatibility |

### Technical Implementation Notes

**i18n:**
- English (default): `res/values/strings.xml`
- Indonesian: `res/values-id/strings.xml`
- All UI strings extracted to string resources
- Language preference persisted to SharedPreferences (sync) + DataStore (async)
- Activity recreated on language change to apply new locale

**Icon Fallback:**
- Logo source: `images/logo.png`
- Resource: `res/drawable/logo.png`
- Fallback displayed when app icon unavailable

**File Naming Convention:**
- Pattern: `<packageName>_<version>.apk`
- Example: `com_whatsapp_2.24.22.11.apk`
- Prevents collision between apps with same display name

---

*Document Status: Ready for Implementation*
