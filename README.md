# 🚀 SkillFlow - Micro-Learning, Maximum Growth

SkillFlow is a state-of-the-art Android application designed to empower busy professionals and students through the power of **Micro-learning**. By delivering interactive "Knowledge Nuggets" tailored to specific career paths, SkillFlow transforms learning into a manageable, daily habit.

---

## 📑 Table of Contents
- [Executive Summary](#-executive-summary)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [App Screens & Flow](#-app-screens--flow)
- [Core Features](#-core-features)
- [Navigation System](#-navigation-system)
- [Data Management](#-data-management)
- [Project Highlights](#-project-highlights)

---

## 🎯 Executive Summary
SkillFlow addresses the "Information Overload" problem in professional education. Instead of lengthy courses, it provides short, interactive lessons (Nuggets) and reinforces them through quizzes and visual roadmaps. The goal is to help users stay consistent in their career journey with just 5-10 minutes of daily commitment.

---

## 🛠 Tech Stack
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (100% Declarative UI)
- **Design System**: Material 3 (M3) with full Dark Mode & Edge-to-Edge support.
- **Language**: Kotlin + [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) for reactive programming.
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/) (Dagger-based DI).
- **Local Database**: [Room Persistence Library](https://developer.android.com/training/data-storage/room).
- **Preferences**: [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Type-safe key-value storage).
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) & OkHttp 5.
- **Serialization**: Kotlinx Serialization (Type-safe JSON handling).
- **Firebase Stack**: Analytics, Crashlytics, Cloud Messaging (FCM), and Authentication.
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/) (Coroutine-based).
- **Logging**: [Timber](https://github.com/JakeWharton/timber).
- **Background Tasks**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for data seeding.

---

## 🏗 Architecture
SkillFlow strictly follows **Clean Architecture** principles, divided into three main layers:

1.  **Data Layer**:
    *   **Repositories**: Implementation of domain interfaces.
    *   **Local**: Room DAO and Database definitions.
    *   **Remote**: API interfaces and DTOs.
    *   **Workers**: Background tasks like initial JSON seeding.
2.  **Domain Layer**:
    *   **Models**: Pure Kotlin data classes (POJOs).
    *   **Repositories**: Interface definitions to decouple data logic from UI.
3.  **Presentation Layer (UI)**:
    *   Uses **MVVM (Model-View-ViewModel)** pattern.
    *   **State Management**: `StateFlow` and `SharedFlow` for handling UI states and one-time events.
    *   **Components**: Extracted reusable UI elements for maintainability.

---

## 📱 App Screens & Flow
SkillFlow features **13+ professional screens**:

-   **Splash Screen**: Instant branding using the Android 12+ Splash API.
-   **Onboarding (4 Pages)**: Guides users through value proposition and career selection.
-   **Authentication (3 Screens)**: Secure Login, SignUp (Email/Phone), and Forgot Password.
-   **Home (Dashboard)**: Displays Daily Streak, Search, and recommended Knowledge Nuggets.
-   **Roadmap**: A visual, step-by-step guide showing the user's progress in their career path.
-   **Detail Screen**: Interactive learning card with **3D Flip Animation** and time tracking.
-   **Quiz Screen**: MCQ-based assessment with instant feedback and reward logic.
-   **Bookmarks**: Persistent storage for offline reading of saved nuggets.
-   **Profile Screen**: User analytics, XP progress, and Level tracking.
-   **Settings**: Language toggle (EN/BN), Dark Mode, and Edit Profile.
-   **Privacy Policy**: In-app viewer for policy compliance.

---

## 🌟 Core Features

### 💎 Knowledge Nuggets System
Data is seeded from a massive `seed_data.json` asset on the first launch using **WorkManager**. Each nugget includes complexity levels (Beginner to Advanced) and linked quiz questions.

### 🎮 Gamification & Rewards
-   **Streak System**: Tracks consecutive days of learning (Logic verified with Unit Tests).
-   **XP & Leveling**: Users earn XP by mastering nuggets. Levels increase automatically based on topics learned.
-   **Quiz Engine**: Reinforces learning with instant feedback and requests for Play Store reviews after high scores.

### 🔒 User Management & Policy
-   **Firebase Auth**: Robust authentication flow.
-   **Account Deletion**: Mandatory "Delete Account" flow that wipes both Firebase user data and local Room DB/DataStore for privacy compliance.

### 🚀 Play Store Integration
-   **In-App Updates**: Prompts for Flexible or Immediate updates to keep the app current.
-   **In-App Reviews**: Requests user ratings at the perfect "Aha!" moment.
-   **Shimmer Loading**: Professional skeleton loaders for a smooth perceived performance.

---

## 🗺 Navigation System
SkillFlow implements **Type-Safe Navigation** using Kotlin Serialization. No more string-based routes; destinations are defined as `@Serializable` objects/classes, preventing runtime crashes and making argument passing effortless.

---

## 💾 Data Management
-   **Data Seeding**: A dedicated `DataSeedWorker` reads a professional JSON structure and populates the local database.
-   **Persistence**: Room ensures all learning progress, bookmarks, and streaks are available offline.
-   **Synchronization**: Firebase Analytics and Crashlytics track app health and user engagement in real-time.

---

## ✨ Project Highlights
-   **Full Edge-to-Edge**: Optimized UI that utilizes the entire screen, including the area behind status and navigation bars.
-   **Localization (EN/BN)**: 100% support for English and Bengali languages via `UiText` wrapper.
-   **Security**: Proguard/R8 rules configured for code shrinking and obfuscation.
-   **Accessibility**: Full TalkBack support with meaningful `contentDescriptions`.

---
*Developed with ❤️ by MD. AL-MUHEETU.*
