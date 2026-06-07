# Keep Kotlin serialization metadata used by typed Navigation routes.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Koin resolves constructors reflectively in release builds.
-keep class org.koin.** { *; }
-keep class com.oshchyrov.tvshowtracker.di.** { *; }

# Ktor engines and plugins are loaded through service metadata.
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Room generates implementations referenced by name from generated code.
-keep class **_Impl { *; }
