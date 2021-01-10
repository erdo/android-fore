
# this lets us run android tests on the obfuscated build
-keepclassmembers class **.OG {
   public static void putMock(...);
}

