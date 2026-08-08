# 🚀 SkillFlow - Micro-Learning, Maximum Growth

**SkillFlow** is a professional, production-ready Android application built with **Jetpack Compose**. It is designed to help users master career-critical skills through "Knowledge Nuggets"—bite-sized, interactive learning units tailored to their specific career goals.

---

## 📱 App Flow & Architecture
The app follows a **Clean Architecture** pattern (Data, Domain, Presentation) combined with **MVVM** and **MVI** principles to ensure scalability, testability, and a smooth user experience.

### **The User Journey**
1.  **Splash Screen**: Instant branding using the modern Android 12+ Splash API.
2.  **Onboarding (4-Step Flow)**:
    *   *Welcome*: Introduction to the platform.
    *   *Methodology*: Explaining the "Knowledge Nugget" concept.
    *   *Selection*: Choosing a career path (e.g., Android, Backend, UI/UX).
    *   *Ready*: Final motivation before the first lesson.
3.  **Authentication**: Secure Login/SignUp with Email & Phone validation.
4.  **Main Dashboard**:
    *   **Home**: Daily streak, search, and 3 personalized nuggets.
    *   **Roadmap**: Visual journey showing progress in the career path.
    *   **Bookmarks**: Offline access to saved knowledge.
    *   **Profile**: Learning statistics, daily summary, and level progress.
5.  **Learning Loop**:
    *   Read interactive cards → Flip for details → Mark as Mastered → Earn Level XP → Take the Daily MCQ Quiz.

---

## 🖼️ Screen Documentation

### **1. Core Screens (13+ Total)**
| Screen | Purpose | Key Features |
| :--- | :--- | :--- |
| **Splash** | Launch Branding | Modern Splash API, Post-launch theme transition. |
| **Onboarding** | First-time UX | HorizontalPager, Career goal selection, Value prop. |
| **Auth (3 Screens)** | Security | Login, SignUp (Phone/Email), Forgot Password. |
| **Home (Dashboard)** | Daily Learning | Streak counter, Search, Daily Nuggets list. |
| **Detail** | Interactive Learning | 3D Card Flip animation, Time tracking, Save/Master. |
| **Roadmap** | Progress Visualization | Vertical step-item layout, Milestone tracking. |
| **Quiz** | Knowledge Testing | MCQ with instant feedback and result breakdown. |
| **Bookmarks** | Offline Reading | Local Room DB integration, Persistent state. |
| **Profile** | User Analytics | Quiz stats, Level XP, Daily learning summary. |
| **Settings** | Configuration | Language (EN/BN), Dark Mode, Edit Profile. |
| **Privacy Policy** | Policy Compliance | Mandatory viewer for Play Store submission. |

---

## 🛠️ Technical Excellence & Standards

### **Modern Android Stack**
*   **UI Architecture**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3.
*   **Dependency Injection**: [Hilt](https://dagger.dev/hilt/) for robust modularity.
*   **Asynchronous Flow**: Kotlin [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html).
*   **Persistence**: [Room DB](https://developer.android.com/training/data-storage/room) (Local) & [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences).
*   **Networking**: [Retrofit 2](https://square.github.io/retrofit/) + OkHttp 5 + Kotlinx Serialization.
*   **Image Handling**: [Coil](https://coil-kt.github.io/coil/) for efficient image loading.
*   **Animations**: [Lottie](https://airbnb.design/lottie/) & Compose Animations for engaging UX.

### **Submission Readiness (Play Store 2024-2026)**
*   ✅ **Target SDK 37**: Built for the latest Android versions.
*   ✅ **Localization**: Full support for English and Bengali (BN).
*   ✅ **Data Privacy**: Mandatory "Delete Account" flow & Privacy Policy.
*   ✅ **Performance**: Proguard/R8 enabled for code shrinking and obfuscation.
*   ✅ **Accessibility**: Edge-to-edge support and semantic UI hierarchy.

---

## 💎 Coding Standards Followed
1.  **Deduplication**: Extracted common UI into `ui/common` (StateViews, AuthComponents).
2.  **No Hard-coding**: 100% `stringResource` and central `Theme` dimensions.
3.  **Clean Code**: Modifier-first approach, optimized imports, and standard previews.
4.  **Organized Package Structure**: Screen-specific components nested in local `components` packages.

---
*Developed with ❤️ by SkillFlow Team.*
