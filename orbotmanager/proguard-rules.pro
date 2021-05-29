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
 -keepnames class * implements android.os.Parcelable {
  public static final ** CREATOR;
 }

 -dontobfuscate
 -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
