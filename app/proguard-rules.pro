# Hilt & Dagger Rules
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.EntryPoint class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.hilt.codegen.OriginatingElement class * { *; }
-keep class **.*_HiltModules* { *; }
-keep class **.*_Factory { *; }
-keep class **.*_MembersInjector { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    @javax.inject.Inject <init>(...);
}
-keep class * extends androidx.lifecycle.ViewModel { *; }

# DI Modules, Repositories, & Managers
-keep class com.example.skillflow.di.** { *; }
-keep class com.example.skillflow.data.repository.** { *; }
-keep class com.example.skillflow.domain.repository.** { *; }
-keep class com.example.skillflow.domain.manager.** { *; }
-keep class com.example.skillflow.domain.analytics.** { *; }

# Type-Safe Navigation Compose & Kotlinx Serialization
-keepattributes *Annotation*, EnclosingMethod, InnerClasses, Signature
-keep class kotlinx.serialization.json.** { *; }
-keep class com.example.skillflow.ui.navigation.** { *; }
-keepclassmembers class com.example.skillflow.ui.navigation.** { *; }
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class **$serializer {
    public static ** INSTANCE;
}
-keep class * implements kotlinx.serialization.KSerializer { *; }

# Data & Domain Models (JSON Seed, DTOs, Local Entities, DAOs, Remote APIs)
-keep class com.example.skillflow.domain.model.** { *; }
-keep class com.example.skillflow.data.remote.** { *; }
-keepclassmembers interface com.example.skillflow.data.remote.** { *; }
-keep class com.example.skillflow.data.local.entity.** { *; }
-keep class com.example.skillflow.data.local.dao.** { *; }

# Room Database
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# Retrofit Rules
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep interface com.example.skillflow.data.remote.** { *; }
-keepclassmembers interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp Rules
-keepattributes Signature
-keepattributes InnerClasses
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-dontwarn okio.**

# Firebase Rules
-keep class com.google.firebase.** { *; }

# General Shrinking & Warning Rules
-dontwarn sun.misc.**
-dontwarn com.google.errorprone.annotations.**
