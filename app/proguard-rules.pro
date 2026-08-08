# Hilt rules
-keep,allowobfuscation,allowshrinking @dagger.hilt.EntryPoint class *

# Retrofit rules
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep @retrofit2.http.* class * { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, EnclosingMethod, InnerClasses, Signature
-keep,allowobfuscation,allowshrinking class kotlinx.serialization.json.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class **$serializer {
    public static ** INSTANCE;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# OkHttp
-keepattributes Signature
-keepattributes InnerClasses
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-dontwarn okio.**

# General shrinking rules
-dontwarn sun.misc.**
-dontwarn com.google.errorprone.annotations.**
