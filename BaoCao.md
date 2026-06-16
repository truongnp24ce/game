# Mobile Programming Project Report (Android – Kotlin)

**Project Title:** Animal Villa
**Repository:** [truongnp24ce/game](https://github.com/truongnp24ce/game)
**Main Module:** `Mobile-Device-Programming-Animal-Villa`
**Language:** Kotlin (100%)
**Date:** 2026-06-16

---

## 1. Introduction

### 1.1 Background

**Animal Villa** is a mobile *visual-novel / life-simulation* game on the Android platform, developed with Kotlin and Jetpack Compose. The player takes the role of a new resident who has just moved into the town of Animal Villa, interacts with the surrounding animal characters across a week (Monday → Sunday), and makes choices that lead to one of four different endings. The application integrates with **Firebase** (Authentication + Firestore + Storage) to manage user accounts and store gameplay progress in the cloud.

### 1.2 Objectives

- Build a single-module Android application using **Kotlin + Jetpack Compose**.
- Provide **registration / login** for player accounts via Firebase Authentication.
- Support **save/load progress** automatically to Firestore and locally with Room.
- Implement **choice-based gameplay (swipe / tap left-right)** with a stat system (Energy ❤️, Status 🔥, Money 💲) that drives the storyline.
- Provide **multiple endings (4 endings)** based on the player's final stats.

### 1.3 Scope

- The application runs on Android devices (**minSdk 32, targetSdk 34, compileSdk 34**).
- Targeted at portrait mode, single-player, offline-first gameplay with sync when online.
- Scope covers the following components: `app` (Activity, ViewModel), `DAO`, `DTO`, `JSON` (story scripts), `Service`, `UI` (Compose screens).
- Backend dependencies: **Firebase Firestore + Firebase Storage + Firebase Authentication** (configured via `google-services.json`).

---

## 2. System Requirements

### 2.1 Functional Requirements

| ID  | Function                  | Description                                                                                          |
| --- | ------------------------- | ---------------------------------------------------------------------------------------------------- |
| F1  | User Registration         | Users create an account via `RegistrationActivity` (Firebase Auth + profile stored in Firestore).    |
| F2  | Login                     | Users sign in at `LoginActivity` to unlock cloud progress saving.                                    |
| F3  | Title Screen / Start Game | `TitleScreenActivity` lets the player start a new game or continue from the latest save.             |
| F4  | Gameplay & Choices        | `GamePlayModel` displays a prompt + 2 choices (left/right) and updates Energy/Status/Money stats.    |
| F5  | Save & Load Progress      | `GameSave` + `MainViewModel` persist and restore progress via Room (local) and Firestore (cloud).    |
| F6  | Multiple Endings          | The end of the week (Sunday) triggers 1 of 4 endings based on the final stats: Bad / Exhausted / Penniless / Good. |

> **Detailed choices that trigger each ending** are fully documented in the [module README.md](https://github.com/truongnp24ce/game/blob/main/Mobile-Device-Programming-Animal-Villa/README.md#endings).

### 2.2 Non-Functional Requirements

| ID   | Requirement       | Description                                                                                                                              |
| ---- | ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| NF1  | Performance       | Compose UI responds smoothly when switching prompts; stat indicators update in real time on screen.                                      |
| NF2  | Security          | Authentication via Firebase Authentication; passwords are hashed and stored securely by Firebase.                                        |
| NF3  | Usability         | Simple interface, two choice buttons (left/right), portrait orientation, easy one-hand operation.                                        |
| NF4  | Compatibility     | Supports Android 12L+ (minSdk 32, targetSdk 34). JVM target 11.                                                                          |
| NF5  | Maintainability   | Source code is layered (DAO / DTO / Service / UI / ViewModel) following MVVM + Koin DI.                                                  |
| NF6  | Storyboard Driven | Story scripts are extracted into JSON files in `app/src/main/java/app/JSON`, making content easy to edit without rebuilding the logic.   |

---

## 3. System Design

### 3.1 System Architecture

Animal Villa is organized using the **MVVM (Model–View–ViewModel)** pattern in a single module (`:app`), with dependencies managed through **Koin (Dependency Injection)**.

**Main layers:**

- **UI Layer** (`app/src/main/java/app/UI` + the `*Activity.kt` files): screens written in **Jetpack Compose**, using `androidx.navigation:navigation-compose` for navigation and `constraintlayout-compose` for complex layouts.
- **ViewModel Layer** (`MainViewModel.kt`, `GamePlayModel.kt`): holds game state (player stats, current prompt) and processes choice-response logic.
- **Domain / Logic** (`AppMethods.kt`, `GetInformation.kt`, `GameSave.kt`): business functions — reads prompt data, applies stat changes, decides the ending.
- **Data Layer**:
  - `DAO/` — Room DAOs for local persistence.
  - `DTO/` — Data Transfer Objects (Prompt, Player, Character, etc.).
  - `JSON/` — Content resources (daily story scripts).
  - `Service/` — Network / Firebase wrappers (Retrofit, Firestore, Storage).
- **DI** (`AppModule.kt`, `AnimalVillaApplication.kt`): initializes the Koin module for the whole application.

**Runtime flow:**
`Compose UI → ViewModel (GamePlayModel) → Logic (AppMethods/GameSave) → DAO/Service → Room (local) + Firebase Firestore/Storage (cloud)`

### 3.2 UI Design

| Screen              | Description                                                                                   |
| ------------------- | --------------------------------------------------------------------------------------------- |
| Login Screen        | Email/password login via Firebase Auth (`LoginActivity`).                                     |
| Registration Screen | Create a new account (`RegistrationActivity`).                                                |
| Title Screen        | Title screen to start a new game or load a save (`TitleScreenActivity`).                      |
| Main / Home Screen  | Entry point opened by the launcher (`MainActivity` — declared as `LAUNCHER` in AndroidManifest). |
| Gameplay Screen     | Displays a prompt + 2 choices + 3 stat indicators (Energy ❤️, Status 🔥, Money 💲) (`GamePlayModel`). |
| Ending Screen       | Displays one of the 4 endings; *Back to Start* button returns to the Title screen.            |

**UI/UX notes:**

- All Activities lock orientation with `screenOrientation="portrait"`.
- Only `MainActivity` is exported as LAUNCHER; the other Activities are `exported="false"` for security.

### 3.3 Database Design

Data is stored in **two layers**:

- **Local:** Room (`androidx.room:room-ktx:2.4.2`) — stores the latest game save, enabling offline play.
- **Cloud:** Firebase **Firestore** stores profiles + game saves; Firebase **Storage** is used for assets (character images, large files if needed).

**Main entities (DTO):**

| Entity        | Key Fields                                                  | Description                                               |
| ------------- | ----------------------------------------------------------- | --------------------------------------------------------- |
| Player        | `userId`, `displayName`, `energy`, `status`, `money`        | The player's current state.                               |
| GameSave      | `userId`, `day`, `promptIndex`, `stats`                     | Progress snapshot used for restoration.                   |
| Prompt (JSON) | `id`, `day`, `text`, `leftChoice`, `rightChoice`, `effects` | One story step + its impact on the stats.                 |
| Character     | `id`, `name`, `metAt`                                       | Information about characters the player has met.          |
| Ending        | `type`, `priority`, `condition`                             | Defines the 4 endings: Bad / Exhausted / Penniless / Good. |

**Ending selection logic (per the README):**

| Priority | Ending       | Trigger (final stat)                          |
| -------- | ------------ | --------------------------------------------- |
| 1        | Bad ❤️‍🔥       | `Status < 30`                                 |
| 2        | Exhausted ❤️  | `Status ≥ 30` and `Energy < 30`               |
| 3        | Penniless 💲  | `Status ≥ 30`, `Energy ≥ 30`, `Money < 40`    |
| 4        | Good ✨       | All three stats above their thresholds        |

---

## 4. System Development

### 4.1 Technologies Used

| Component            | Technology                                                                                                                      |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| Programming Language | Kotlin 1.9.20                                                                                                                   |
| Build System         | Gradle (Groovy DSL), Android Gradle Plugin 8.1.4                                                                                |
| Java Target          | JVM 11                                                                                                                          |
| IDE                  | Android Studio                                                                                                                  |
| Architecture         | MVVM (single-module)                                                                                                            |
| UI Framework         | Jetpack Compose 1.5.4 + Material                                                                                                |
| Navigation           | androidx.navigation:navigation-compose 2.5.1                                                                                    |
| Dependency Injection | Koin (`io.insert-koin:koin-android:3.2.0`)                                                                                      |
| Local Database       | Room 2.4.2                                                                                                                      |
| Networking           | Retrofit 2.9.0 + OkHttp 5.0.0-alpha + Gson converter                                                                            |
| Backend / Auth       | Firebase BoM 30.1.0 (Authentication, Firestore, Storage, Analytics) + FirebaseUI-Auth 8.0.1 + Google Play Services Auth 20.2.0 |
| Background Work      | WorkManager 2.7.1                                                                                                               |
| Responsive Sizing    | `com.intuit.sdp:sdp-android:1.0.6`                                                                                              |
| Testing              | JUnit 4.13.2, MockK 1.12.4, kotlinx-coroutines-test 1.6.3, Espresso 3.4.0, Compose UI Test                                      |
| Version Control      | Git / GitHub                                                                                                                    |

### 4.2 Main APIs & Services

| API / Service               | Purpose                                                 |
| --------------------------- | ------------------------------------------------------- |
| Firebase Authentication     | Player registration / login.                            |
| Firebase Firestore          | Stores profiles and game saves in the cloud.            |
| Firebase Storage            | Stores assets (character images, large files).          |
| Room DAO (local)            | Caches game saves for offline play.                     |
| Retrofit + OkHttp (network) | Ready for additional REST API calls if needed.          |

### 4.3 Main Libraries

| Library                                            | Purpose                              |
| -------------------------------------------------- | ------------------------------------ |
| Jetpack Compose + Material                         | Declarative UI                       |
| Navigation Compose                                 | Navigation between screens           |
| Koin                                               | Dependency Injection                 |
| Retrofit + OkHttp + Gson Converter                 | HTTP client + serialization          |
| Room                                               | Local persistence                    |
| Firebase BoM (Auth, Firestore, Storage, Analytics) | Backend-as-a-Service                 |
| WorkManager                                        | Background sync (e.g., save sync when online) |
| ConstraintLayout (Compose)                         | Complex layouts                      |
| SDP-Android                                        | Responsive sizing units              |
| JUnit / MockK / Espresso                           | Unit testing & UI testing            |

---

## 5. Deployment

### 5.1 Source Code

- Repository: **[truongnp24ce/game](https://github.com/truongnp24ce/game)** (branch `main`)
- Android module: [`Mobile-Device-Programming-Animal-Villa/`](https://github.com/truongnp24ce/game/tree/main/Mobile-Device-Programming-Animal-Villa)
- Entry file: [`AndroidManifest.xml`](https://github.com/truongnp24ce/game/blob/main/Mobile-Device-Programming-Animal-Villa/app/src/main/AndroidManifest.xml) → the LAUNCHER is `app.MainActivity`.

### 5.2 Build & Run

1. Clone the repository:

   ```bash
   git clone https://github.com/truongnp24ce/game.git
   ```

2. Open the `Mobile-Device-Programming-Animal-Villa` directory with Android Studio (Giraffe or newer, AGP 8.1.4).
3. Make sure the `app/google-services.json` file is present (already included in the repo with the default Firebase configuration) — **it is recommended to replace it with your own Firebase configuration for a production environment**.
4. Sync Gradle, then **Run** on an emulator or an Android device running ≥ 12L (API 32).

### 5.3 Installation Guide

1. Build a debug APK:

   ```bash
   cd Mobile-Device-Programming-Animal-Villa
   ./gradlew assembleDebug
   ```

2. The APK is generated at: `app/build/outputs/apk/debug/app-debug.apk`.
3. On the phone: enable *Install from unknown sources* and open the APK, or use:

   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 6. Conclusion

### 6.1 Achievements

- Delivered a **complete visual-novel game** on Android with 4 endings and a three-dimensional stat system (Energy / Status / Money).
- Adopted a fully **modern stack**: Kotlin + Jetpack Compose + MVVM + Koin DI + Room + Firebase.
- Externalized the story script into **JSON** files outside of the code, enabling flexible content editing.
- Integrated **Firebase Authentication + Firestore** for login and cloud progress saving.

### 6.2 Limitations

- `google-services.json` is committed directly into the repository — it should be replaced with environment variables or removed from VCS for production.
- Several libraries use older versions (Retrofit 2.9.0, OkHttp 5.0.0-alpha, Room 2.4.2) — upgrading should be considered.
- There is no CI/CD pipeline yet (the `.github/` directory is empty) and no signed release build (release uses `minifyEnabled false`).
- The current repository description (`jskdhfsjhdbdksbsdjk`) should be updated to a meaningful description.

### 6.3 Future Work

- Add **CI/CD** with GitHub Actions (build + test + lint + signed assembleRelease).
- Enable **R8/ProGuard** in the release build (`minifyEnabled true`) to reduce APK size.
- Add **unit tests for `GamePlayModel`** (ending selection logic) and **UI tests for Compose screens**.
- Add **fully offline play mode** with Firestore sync via WorkManager.
- Add **internationalization (i18n)** and **dark theme** support for Compose.
- Manage Firebase secrets via `local.properties` or GitHub Secrets instead of committing `google-services.json`.

---

> [truongnp24ce/game](https://github.com/truongnp24ce/game) 
