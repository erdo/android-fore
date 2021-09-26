# include in this file any rules you want applied to a
# consumer of this library when it proguards itself.
-keep public class co.early.fore.core.observer.Observer
-keep public class co.early.fore.core.observer.Observable
-keep class co.early.fore.core.logging.SystemLogger
-keep class co.early.fore.kt.core.logging.SystemLogger
-keep class co.early.fore.kt.core.delegate.** { *; }
-keep class co.early.fore.core.testhelpers.** { *; }
-dontwarn co.early.fore.kt.net.**
-dontwarn co.early.fore.net.**
-keep class okhttp3.Request { *;}
-keep class okhttp3.Response { *;}
-dontwarn com.apollographql.apollo.ApolloCall$Callback
-dontwarn com.apollographql.apollo3.ApolloResponse