# Keep serialization code
-keep,includedescriptorclasses class kotlinx.serialization.Serializable { *; }
-keep class kotlinx.serialization.internal.* { *; }
-keepclassmembers class ** { @kotlinx.serialization.Serializable *; }

# Keep names of serializable classes and their members
-keepnames class * { @kotlinx.serialization.Serializable *; }
