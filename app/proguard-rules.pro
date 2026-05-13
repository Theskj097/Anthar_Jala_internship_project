# Add project specific ProGuard rules here.
# For more details, see http://developer.android.com/guide/developing/tools/proguard.html

# Keep Google Maps classes
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-keep class com.google.maps.android.** { *; }

# Keep Room entities
-keep class com.antharjala.watch.data.model.** { *; }

# Keep Kotlin coroutines
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
