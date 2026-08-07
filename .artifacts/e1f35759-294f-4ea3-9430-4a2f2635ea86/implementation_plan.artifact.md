# Play Store Submission Readiness Plan (Skillflow)

This plan addresses missing professional components and technical requirements for Google Play Store submission as per the 2024-2026 standards.

## User Review Required

> [!IMPORTANT]
> - **Privacy Policy URL**: You need to provide a hosted URL for your Privacy Policy. I will use a placeholder (`https://skillflow.app/privacy`) which you must replace later.
> - **Contact Info**: I will add placeholders for developer contact and support links in the "About" section.
> - **Visual Assets**: I cannot generate the 512x512 icon, 1024x500 feature graphic, or screenshots. You must prepare these manually.

## Proposed Changes

### [Technical & Configuration]

#### [MODIFY] [libs.versions.toml](file:///Users/shihab/ProjectFile/Skillflow-Kotlin/gradle/libs.versions.toml)
- Add `androidx-core-splashscreen` library.

#### [MODIFY] [app/build.gradle.kts](file:///Users/shihab/ProjectFile/Skillflow-Kotlin/app/build.gradle.kts)
- Add Splash Screen dependency.
- Enable Proguard/R8 for release builds (`minifyEnabled = true`).
- Add standard Proguard rules.

#### [MODIFY] [AndroidManifest.xml](file:///Users/shihab/ProjectFile/Skillflow-Kotlin/app/src/main/AndroidManifest.xml)
- Add `INTERNET` permission.
- Configure `Theme.App.Starting` for the Splash Screen.

---

### [Mandatory App Screens]

#### [MODIFY] [MainActivity.kt](file:///Users/shihab/ProjectFile/Skillflow-Kotlin/app/src/main/java/com/example/skillflow/MainActivity.kt)
- Integrate `installSplashScreen()` for Android 12+ support.

#### [MODIFY] [OnboardingScreen.kt](file:///Users/shihab/ProjectFile/Skillflow-Kotlin/app/src/main/java/com/example/skillflow/ui/onboarding/OnboardingScreen.kt)
- Convert the current single-screen selection into a 4-screen `HorizontalPager`:
  1. Welcome & Value Proposition.
  2. Micro-learning Concept.
  3. Career Path Selection (existing logic).
  4. Final Goal Confirmation.

#### [MODIFY] [SettingsScreen.kt](file:///Users/shihab/ProjectFile/Skillflow-Kotlin/app/src/main/java/com/example/skillflow/ui/profile/SettingsScreen.kt)
- Add "Privacy Policy" link.
- Add "About" section with App Version and Developer Info.
- Add "Delete Account" option with a confirmation dialog (Mandatory).

#### [NEW] [PrivacyPolicyScreen.kt](file:///Users/shihab/ProjectFile/Skillflow-Kotlin/app/src/main/java/com/example/skillflow/ui/profile/PrivacyPolicyScreen.kt)
- A screen to display the Privacy Policy content (either via Webview or local text).

---

### [Visuals & Assets]

#### [MODIFY] [themes.xml](file:///Users/shihab/ProjectFile/Skillflow-Kotlin/app/src/main/res/values/themes.xml)
- Define `Theme.App.Starting` with `windowSplashScreenBackground` and `windowSplashScreenAnimatedIcon`.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleRelease` to verify Proguard doesn't break the build.
- Verify navigation to new screens via UI tests.

### Manual Verification
- Deploy to a device running Android 12+ to check the Splash Screen.
- Walk through the new Onboarding flow.
- Test the "Delete Account" confirmation dialog.
- Verify "Privacy Policy" opens correctly.
