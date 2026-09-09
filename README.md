# 🚀 SkillFlow - Micro-Learning, Maximum Growth

**SkillFlow** is a modern, high-performance Android application built with **Jetpack Compose** and **Clean Architecture**. Designed for busy professionals and lifelong learners, SkillFlow delivers bite-sized "Knowledge Nuggets" tailored to career roadmaps, helping users build daily learning habits in just 5–10 minutes a day.

---

## 📑 Table of Contents
- [Executive Summary](#-executive-summary)
- [✨ Key Features](#-key-features)
- [🛠 Tech Stack & Libraries](#-tech-stack--libraries)
- [🏗 Architecture & Design Patterns](#-architecture--design-patterns)
- [📂 Project Directory Structure](#-project-directory-structure)
- [📱 Screens & Navigation Flow](#-screens--navigation-flow)
- [⚙️ Setup & Build Instructions](#%EF%B8%8F-setup--build-instructions)
- [🧪 Testing & Quality Assurance](#-testing--quality-assurance)
- [🤝 Contributing & License](#-contributing--license)

---

## 🎯 Executive Summary

In today's fast-paced world, traditional long-form courses can cause information overload and burnout. **SkillFlow** solves this by breaking down complex career topics into concise, actionable lessons (Nuggets), reinforced by:
* Interactive quizzes
* 3D card flips & visual roadmaps
* Integrated note-taking
* Gamification (streaks, XP, and leveling)

---

## ✨ Key Features

### 💎 Knowledge Nuggets & Interactive Cards
* **3D Card Flip Animation**: Smooth front/back card rotations for engaging micro-lessons.
* **Smart Filtering & Search**: Find nuggets instantly by career path, difficulty level, or search keywords.
* **In-App Note Taking**: Create, edit, and delete custom personal notes attached directly to specific knowledge nuggets (`UserNoteEntity`).
* **Bookmarks & Offline Access**: Save essential nuggets for quick offline reading.

### 🎮 Gamification & Learning Progress
* **Bento Grid Dashboard**: Asymmetrical Bento Grid header with 24dp rounded corners, soft grey/light backgrounds, featuring a large Progress card alongside balanced Streak and Saved Items tiles.
* **Category Pills with Distinct Icons**: Simplified horizontal filter pills ("All", "Today", "History", "Pick Date") with unique icons for quick date and topic filtering.
* **Daily Streak Tracker**: Automatically calculates learning streaks based on completion dates (`StreakCalculatorTest` verified).
* **XP & Level Progression**: Earn XP points by completing nuggets and passing quizzes to level up your career profile.

### 🎯 Quizzes & Knowledge Evaluation
* **Interactive MCQ Engine**: End-of-nugget quizzes with immediate answer feedback and score summaries.
* **In-App Review Integration**: Triggers Google Play In-App Review prompts upon high score accomplishments ("Aha!" moments).

### 🎨 Modern UI & UX Excellence
* **100% Jetpack Compose & Material 3**: Declarative UI with Edge-to-Edge drawing and dynamic light/dark mode support.
* **Custom Shimmer Skeleton Loaders**: Smooth loading states across Home (`NuggetCardSkeleton`, `ProgressCardSkeleton`), Roadmaps (`RoadmapStepSkeleton`), and Onboarding (`CareerPathSkeleton`).
* **Multi-Language Localization**: Full dynamic runtime switching between English and Bengali (EN/BN) using `UiText` wrappers.

### 🔒 Secure Authentication & Data Privacy
* **Firebase Auth**: Complete authentication suite including Login, Sign Up, and Password Reset.
* **Full Account Deletion**: One-click account removal compliant with Google Play Policy (purges Firebase Auth data along with local Room DB & DataStore entries).

### 📦 Play Store & Background Work
* **Automatic JSON Data Seeding**: `DataSeedWorker` populates Room database on initial application launch using **WorkManager**.
* **In-App Updates**: Flexible and Immediate Play Store update prompt mechanisms.

---

## 🛠 Tech Stack & Libraries

| Category | Technology / Library |
| :--- | :--- |
| **Language** | Kotlin 2.0+ (Coroutines & Flow) |
| **UI Framework** | Jetpack Compose + Material 3 |
| **SDK Versions** | `minSdk: 24`, `targetSdk: 37`, `compileSdk: 37` |
| **Architecture** | Clean Architecture + MVVM + MVI State Management |
| **Dependency Injection** | Hilt (Dagger Hilt + Hilt Work + Hilt Navigation Compose) |
| **Local Database** | Room Persistence Library |
| **Preferences & State** | DataStore Preferences |
| **Networking & JSON** | Retrofit 2 + OkHttp 5 + Kotlinx Serialization |
| **Background Processing**| WorkManager (`DataSeedWorker`) |
| **Image Loading** | Coil Compose |
| **Animations** | Jetpack Compose Graphics + Lottie Compose |
| **Firebase Stack** | Firebase Auth, Analytics, Crashlytics, Cloud Messaging (FCM) |
| **Play Services** | Play In-App Review & Play In-App Update APIs |
| **Logging** | Timber |

---

## 🏗 Architecture & Design Patterns

SkillFlow strictly adheres to **Clean Architecture** principles to promote testability, maintainability, and scalability.

```mermaid
graph TD
    A[Presentation Layer: Jetpack Compose UI & ViewModels] -->|Observes StateFlow / SharedFlow| B[Domain Layer: Use Cases, Models & Repository Interfaces]
    C[Data Layer: Room DB, Network, DataStore & WorkManager] -->|Implements Repositories| B
```

1. **Presentation Layer (`ui/` & `presentation/`)**:
   * Uses **MVVM** pattern with `StateFlow` for state rendering and `SharedFlow` for single-event notifications.
   * Modularized composable screens and reusable component architecture.
2. **Domain Layer (`domain/`)**:
   * Contains core business models (`KnowledgeNugget`, `CareerPath`, `UserNote`, `QuizQuestion`) and repository interfaces.
   * Completely independent of framework specifics.
3. **Data Layer (`data/`)**:
   * **Local Data**: Room DAO (`SkillDao`) and Database (`SkillDatabase`).
   * **Preferences**: DataStore for user theme, onboarding state, and language settings.
   * **Worker**: `DataSeedWorker` for background JSON asset ingestion.
   * **Repositories**: Concrete implementations handling caching, Room operations, and remote synchronization.

---

## 📂 Project Directory Structure

```text
com.example.skillflow
├── SkillFlowApp.kt            # Application Class & Hilt/Timber Setup
├── MainActivity.kt             # Main Entry Point with Edge-to-Edge NavHost
├── data/
│   ├── analytics/             # Firebase Analytics Helper Implementation
│   ├── local/                 # Room Database, DAO & Entities (Nugget, Note, Career)
│   ├── manager/               # Play Store In-App Review & Update Managers
│   ├── remote/                # Retrofit API & DTO definitions
│   ├── repository/            # Concrete Repository Implementations
│   ├── util/                  # Asset Managers & JSON Parsers
│   └── worker/                # Background WorkManager Jobs
├── di/                        # Hilt Modules (Database, Network, Firebase, Repositories)
├── domain/
│   ├── analytics/             # Analytics Interfaces
│   ├── manager/               # Manager Interfaces
│   ├── model/                 # Pure Domain Data Models
│   ├── repository/            # Repository Interfaces
│   └── util/                  # Resource wrappers & UiText helpers
├── presentation/              # ViewModels (Home, Detail, Auth, Quiz, Profile, etc.)
└── ui/
    ├── auth/                  # Login, SignUp & Forgot Password Screens
    ├── bookmarks/             # Saved Nuggets Screen
    ├── common/                # Reusable UI Cards, Shimmers, Animations & TopBars
    ├── detail/                # Knowledge Detail, Note Input & 3D Flip Card
    ├── home/                  # Bento Grid Header, Category Pills & Skeleton Loaders
    ├── navigation/            # Type-Safe Screen Navigation Routes & NavHost
    ├── onboarding/            # Onboarding Pager & Career Selection
    ├── profile/               # Profile Summary, Settings & Privacy Policy
    ├── quiz/                  # Interactive Quiz Screen
    ├── roadmap/               # Career Path Roadmap Step Flow
    └── theme/                 # Material 3 Color Schemes, Typography & Spacing
```

---

## 📱 Screens & Navigation Flow

The app leverages **Type-Safe Jetpack Compose Navigation** with Kotlinx Serialization (`@Serializable` routes):

1. **Splash Screen**: Native Android 12+ Splash API transition.
2. **Onboarding**: Multi-page introduction with career path selection.
3. **Authentication**:
   * **Login**: Email/password authentication via Firebase.
   * **Sign Up**: Account creation with validation.
   * **Forgot Password**: Password reset dispatch.
4. **Main Dashboard (Home)**: Modern Bento Grid layout (Progress, Daily Streak & Saved Bookmarks tiles), Category Pills with distinct icons, search bar, and recommended nuggets.
5. **Roadmap**: Visual progress node flow through selected career path steps.
6. **Detail & Notes**: 3D flip card learning view with integrated personal note-taking capabilities.
7. **Quiz Screen**: Multiple-choice assessment with instant score computation and reward prompts.
8. **Bookmarks**: Saved offline nuggets.
9. **Profile & Settings**: Level/XP progress visualizer, English/Bengali language toggle, dark mode toggle, and privacy settings.

---

## ⚙️ Setup & Build Instructions

### Prerequisites
* **Android Studio**: Ladybug / Meerkat or newer recommended.
* **JDK Version**: Java 17.
* **Android SDK**: API level 37 (Minimum API level 24).

### Building the Project
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/Skillflow-Kotlin.git
   cd Skillflow-Kotlin
   ```
2. **Firebase Setup**:
   * Add your `google-services.json` file inside the `app/` directory.
3. **Build & Run**:
   * Sync Gradle dependencies.
   * Select an emulator or physical device running Android 7.0 (API 24) or higher.
   * Run the `:app` configuration.

---

## 🧪 Testing & Quality Assurance

* **Unit Testing**: Business logic verified via JUnit 4 (e.g., `StreakCalculatorTest`).
* **UI Testing**: Compose UI test harness configured with Espresso and Compose UI Test Manifest.
* **Code Shrinking & Security**: Proguard/R8 rules configured for release builds with `isMinifyEnabled = true` and `isShrinkResources = true`.

---

## 🤝 Contributing & License

Contributions are welcome! Feel free to open issues or submit pull requests.

Developed with ❤️ by **MD. AL-MUHEETU**
