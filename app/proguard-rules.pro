# Keep TensorFlow Lite models & native libs
-keep class org.tensorflow.lite.** { *; }
-dontwarn org.tensorflow.lite.**

# Keep Room entities and DAOs
-keep class com.oviscan.app.data.local.entity.** { *; }
-keep class com.oviscan.app.data.local.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase
