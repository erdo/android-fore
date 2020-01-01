# include in this file any rules you want applied to a
# consumer of this library when it proguards itself.

-dontwarn junit.**
-dontwarn org.junit.**

# Make crash call-stacks debuggable.
-keepnames class ** { *; }
-keepattributes SourceFile,LineNumberTable

-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8