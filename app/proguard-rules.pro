-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }
-keep class org.mozilla.** {*;}
-keep class com.flurry.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keepattributes Signature
-keepattributes Annotation
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepclasseswithmembers class * { public <init>(android.content.Context, android.util.AttributeSet, int); }

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.*
-dontwarn org.chromium.net.*
-dontwarn com.flurry.**
-dontwarn okhttp3.**
-dontwarn okio.**

-dontobfuscate
-dontoptimize
-dontpreverify