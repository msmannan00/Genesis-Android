-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }

-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn okhttp3.*
-dontwarn org.chromium.net.*

-keep class com.flurry.** { *; }
-dontwarn com.flurry.**
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet, int);
 }

-dontobfuscate
# https://stackoverflow.com/questions/9651703/using-proguard-with-android-without-obfuscation
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

-keep class org.torproject.android.service.vpn.Tun2Socks {
    void logTun2Socks(java.lang.String, java.lang.String, java.lang.String);
}

-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

-dontoptimize
-dontpreverify
