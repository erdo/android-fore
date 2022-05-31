
# this lets us run android tests on the obfuscated build
-keepclassmembers class **.OG {
   public static void putMock(...);
}

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations
# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
# kotlinx.serialization - change foo.bar.example to your app's package
-keep,includedescriptorclasses class foo.bar.example.**$$serializer { *; }
-keepclassmembers class foo.bar.example.** {
    *** Companion;
}
-keepclasseswithmembers class foo.bar.example.** {
    kotlinx.serialization.KSerializer serializer(...);
}
