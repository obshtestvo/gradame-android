# Reduce the size of the output some more.
-repackageclasses ''
-allowaccessmodification

# Keep a fixed source file attribute and all line number tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Preserve the special fields of all Parcelable implementations.
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
    public static final ** CREATOR;
}

# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve static fields of inner classes of R classes that might be accessed through introspection.
-keepclassmembers class **.R$* {
  public static <fields>;
}

# The Android Support Compatibility / Design library
-dontwarn android.support.**

## com.android.support:appcompat-v7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

## com.android.support:appcompat-v4
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

## com.android.support:design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# Otto
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# Models used by Gson
-keep class me.grada.io.model.** { *; }
-keep class me.grada.io.event.** { *; }

#  Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}