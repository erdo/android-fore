
# this lets us run android tests on the obfuscated build
-keepclassmembers class **.OG {
   public static void putMock(...);
}
-keep class co.early.fore.core.logging.SystemLogger
-keep class co.early.fore.kt.core.logging.SystemLogger
