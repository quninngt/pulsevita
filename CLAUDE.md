# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PulseVita - Android health tracking app with TCM wellness features. Tracks water intake, exercise, mood, diet and provides seasonal health advice. Full Chinese-language UI with localized English name.

**Current Version**: v1.4.1 (2026-06-06) — Stable, tested on OnePlus MT2110 (Android 12)

## Build & Test Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install to device
./gradlew installDebug

# Run all unit tests
./gradlew testDebugUnitTest

# Run specific test class
./gradlew testDebugUnitTest --tests "com.healthapp.util.BmiUtilsTest"

# Run single test method (use backtick name from source)
./gradlew testDebugUnitTest --tests "com.healthapp.util.BmiUtilsTest.calculateBMI with valid values returns correct BMI"

# Generate test report (after running tests)
# Report: app/build/reports/tests/testDebugUnitTest/index.html

# Clean build
./gradlew clean
```

## Architecture

**Stack**: Kotlin + Jetpack Compose + Material 3 + Room + Hilt + Coroutines/Flow + Retrofit + OkHttp + Moshi

**Pattern**: MVVM with Repository pattern

```
UI (Compose) → ViewModel → Repository → Room DAO → SQLite
                    ↓               ↓
            HealthConnectManager   Retrofit → open-meteo / hitokoto / ip-api
```

### UI Layer (`ui/`)

One package per feature, each containing `*Screen.kt` and `*ViewModel.kt`:

| Package | Features | Key Data |
|---------|----------|----------|
| `home/` | Dashboard overview, quick actions, health tips | Water/exercise/mood today summary, weather, streak, TCM tip |
| `diet/` | Water tracker (100/250/500ml), diet records by meal | Water goal 2000ml, meals: breakfast/lunch/dinner/snack |
| `exercise/` | Duration + steps tracking, office exercise tutorials | Walking/office/yoga types, Health Connect integration |
| `mental/` | Mood diary (1-5 scale), 4-7-8 breathing exercise | Mood with emoji icons, breathing timer, hitokoto quote |
| `profile/` | User profile, BMI calculation, health indicators | Name/gender/height/weight/occupation |

ViewModels use `MutableStateFlow<*UiState>` with `data class` state holders, collected in Composables via `val uiState by viewModel.uiState.collectAsState()`. Flow collection in ViewModels uses the `.onEach {}.launchIn(viewModelScope)` pattern (not `collect` directly). Dialog states (show/hide) are managed inside the UiState.

**HomeUiState composition**: HomeViewModel combines 5 independent sub-states:
```kotlin
data class HomeUiState(
    val weather: WeatherState,
    val water: WaterState,
    val exercise: ExerciseState,
    val mood: MoodState,
    val dailyExtras: DailyExtrasState,
    val greeting: String,
    val tip: String,
    val streak: Int
)
```
Each sub-state has its own `isLoading`/`errorMessage`. ViewModel merges them via `combine()`.

### Data Layer (`data/`)

- `local/entity/` — Room entities: UserEntity, WaterRecord, ExerciseRecord, MoodRecord, DietRecord, HealthTip. Dates stored as `String` (format: `yyyy-MM-dd`).
- `local/dao/` — DAOs with `Flow`-based queries for reactive observation and `suspend` functions for writes. Aggregate queries use `COALESCE` to handle null-safety. Key queries: `getAllDistinctDates()` for streak calculation.
- `local/AppDatabase.kt` — Room v2, `exportSchema = true`, `fallbackToDestructiveMigration()`. Schema exported to `app/schemas/`.
- `local/Migrations.kt` — Migration template (MIGRATION_1_2 placeholder).
- `repository/` — One repository per entity, injected via Hilt `@Singleton`. All use `java.time.LocalDate` + `DateTimeFormatter` for date formatting (no `SimpleDateFormat`).
- `remote/` — Retrofit-based API layer:
  - `NetworkResult<T>` — sealed class: Success/Loading/Error
  - `ApiModels.kt` — Moshi JSON models for weather + hitokoto
  - `ApiServices.kt` — Retrofit service interfaces (`WeatherService`, `HitokotoService`)
  - `WeatherRepository` — open-meteo.com weather + ip-api.com geolocation (HTTPS)
  - `HitokotoRepository` — v1.hitokoto.cn random quotes
- `di/NetworkModule.kt` — Hilt module providing Retrofit (Moshi converter) + OkHttp (logging interceptor) instances.
- `di/AppModule.kt` — Hilt module providing all DAOs from `AppDatabase` singleton.

### Network Error Handling

ViewModels expose `isLoading: Boolean` and `errorMessage: String?`. Repositories return `NetworkResult<T>` which ViewModels unpack. HomeScreen shows `CircularProgressIndicator` while loading and `Snackbar` on error.

### Database Initialization

`AppDatabase` pre-populates 10 health tips in `DatabaseCallback.onCreate` across categories (tcm, diet, exercise, mental) with seasonal variants (spring/summer/autumn/winter). Database name: `"health_app_database"`, version 1.

### Navigation (`navigation/`)

Bottom navigation with 4 tabs: Home, Diet, Exercise, Mental. Profile is accessed from Home's top bar. Navigation uses `NavHost` + `composable()` routes, with `saveState`/`restoreState` for tab persistence.

### Theme

Orange/black color scheme. Colors in `ui/theme/Color.kt` include brand orange variants, mood colors (1-5 scale mapped to red→blue), water blue, and status colors (error/warning/success). Dynamic color disabled to maintain brand consistency. App name: "健康助手".

### Display Mappings (`ui/DisplayMappings.kt`)

Centralized object for Chinese UI labels: `moodLevelName()`, `exerciseTypeName()`, `mealTypeName()`, plus `moodOptions` (5 levels with emoji icons) and `mealTypes` (breakfast/lunch/dinner/snack).

## Testing

Unit tests in `app/src/test/` with backtick-named test methods (Spock-style). Test names describe the scenario and expected outcome, e.g. `"calculateBMI with valid values returns correct BMI"`. Tests exist for:
- `BmiUtilsTest` — BMI calculation, categories, weight ranges (11 tests)
- `DateUtilsTest` — date formatting, season detection, greetings (8 tests)
- `ConstantsTest` — constant value validation (8 tests)
- `SolarTermUtilTest` — solar term calculation (3 tests)

**Total: 30 unit tests, 100% pass rate**

## Health Connect Integration

`HealthConnectManager` (`util/`) provides step data via Android Health Connect API. Only reads `StepsRecord` data (aggregate query for total count). Health Connect is optional — app functions without it. Permissions: `READ_STEPS` only. Runtime permission flows handled in `ExerciseViewModel`.

## Utilities

- `BmiUtils` — Static BMI calculations and healthy weight ranges
- `DateUtils` — Date formatting, season detection (`getCurrentSeason()`), time-based Chinese greetings (`getGreeting()`), age calculation from birth timestamp. Uses `java.time` throughout.
- `SolarTermUtil` — 24 Chinese solar terms (节气) calculation with health tips, no API needed
- `HealthChallengeProvider` — Curated daily health challenges cycling by day-of-year
- `FoodKnowledgeProvider` — Curated food/nutrition knowledge
- `Constants` — App-wide constant values (water goal, date format, database name)
- `CrashLogger` — Global uncaught exception handler, saves crash logs to app-private file

## Known Pitfalls

### Compose BOM Version

**DO NOT** use Compose BOM 2024.01.00. It causes `NoSuchMethodError` on Android 12 devices
because `CircularProgressIndicator` uses `KeyframesSpec.at()` which doesn't exist in the
bundled `compose.animation.core` version.

**Use Compose BOM 2023.10.01** — all versions are compatible with Android 12+.

```kotlin
// ✅ Correct
implementation(platform("androidx.compose:compose-bom:2023.10.01"))

// ❌ Wrong — crashes on Android 12
implementation(platform("androidx.compose:compose-bom:2024.01.00"))
```

### AutoMirrored Icons

`Icons.AutoMirrored.Filled.*` icons are NOT available in Compose BOM 2023.10.01.
Use standard `Icons.Default.*` instead:

```kotlin
// ✅ Correct
Icons.Default.DirectionsWalk
Icons.Default.KeyboardArrowRight

// ❌ Wrong — compile error
Icons.AutoMirrored.Filled.DirectionsWalk
```

## Environment

- JDK 17, Android SDK 34 (compileSdk/targetSdk), Min SDK 26
- Kotlin 1.9.22, Compose compiler 1.5.8
- Gradle 8.5, AGP 8.2.2
- `local.properties` must set `sdk.dir` to Android SDK path
- Hilt DI with KSP (not kapt)
- Retrofit 2.9.0 + OkHttp 4.12.0 + Moshi 1.15.0 for networking

## Documentation

- [README.md](README.md) — Project overview
- [docs/CHANGELOG.md](docs/CHANGELOG.md) — Version history
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — Technical architecture
- [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) — Development guide
- [测试报告.md](测试报告.md) — Test report
