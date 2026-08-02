# Professional Localization and Refactoring Plan

This plan focuses on removing all hardcoded strings and implementing a professional, bilingual (English and Bengali) localization setup using Android string resources. It also includes using enums and constants where appropriate.

## User Review Required

> [!IMPORTANT]
> - **Quiz Questions Localization**: For 20 questions, I will extract both the questions and their explanations into `strings.xml`. This ensures the entire quiz experience is available in both English and Bengali.
> - **Bengali Translations**: I will provide high-quality Bengali translations for all newly added UI elements and quiz content.

## Proposed Changes

### Resource Files

#### [MODIFY] [strings.xml (en)](file:///home/almuheetu-shihab/ProjectFile/Skillflow-Kotlin/app/src/main/res/values/strings.xml)
- Add all UI strings for Profile Stats, Quiz, and Edit Profile.
- Add 20 Quiz questions, options, and explanations.

#### [MODIFY] [strings.xml (bn)](file:///home/almuheetu-shihab/ProjectFile/Skillflow-Kotlin/app/src/main/res/values-bn/strings.xml)
- Add Bengali translations for all new strings.

### Presentation & UI Layer

#### [MODIFY] [QuizViewModel.kt](file:///home/almuheetu-shihab/ProjectFile/Skillflow-Kotlin/app/src/main/java/com/example/skillflow/presentation/quiz/QuizViewModel.kt)
- Refactor `QuizQuestion` to use string resource IDs instead of hardcoded strings.
- This keeps the ViewModel "clean" and delegates translation to the UI layer.

#### [MODIFY] [ProfileScreen.kt](file:///home/almuheetu-shihab/ProjectFile/Skillflow-Kotlin/app/src/main/java/com/example/skillflow/ui/profile/ProfileScreen.kt)
- Replace all hardcoded strings with `stringResource(R.string...)`.

#### [MODIFY] [QuizScreen.kt](file:///home/almuheetu-shihab/ProjectFile/Skillflow-Kotlin/app/src/main/java/com/example/skillflow/ui/quiz/QuizScreen.kt)
- Replace all hardcoded strings with `stringResource(R.string...)`.
- Update to handle resource IDs from the `QuizQuestion` model.

#### [MODIFY] [SettingsScreen.kt](file:///home/almuheetu-shihab/ProjectFile/Skillflow-Kotlin/app/src/main/java/com/example/skillflow/ui/profile/SettingsScreen.kt)
- Replace hardcoded "Edit Profile", "Full Name", and "Email Address".

### Domain Layer (Constants/Enums)

#### [NEW] [QuizConstants.kt](file:///home/almuheetu-shihab/ProjectFile/Skillflow-Kotlin/app/src/main/java/com/example/skillflow/util/QuizConstants.kt)
- Define constants or enums if there are specific logic-related strings (e.g., category names).

## Verification Plan

### Manual Verification
- Switch system language to Bengali and verify all screens (Profile, Quiz, Settings) update correctly.
- Verify the quiz shows questions and explanations in the selected language.
- Check that "Edit Profile" labels are translated.
