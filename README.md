# MultiLineRadioGroup
Simple MultiLine Radio Group
[![Release](https://jitpack.io/v/jitpack/android-example.svg)](https://www.jitpack.io/#JabirDev/MultiLineRadioGroup/0.1.0)

## Setup
Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    implementation 'com.github.JabirDev:MultiLineRadioGroup:{latest version}'
}
```

## Add Layout
```xml
<com.jabirdev.multilineradiogroup.MultiLineRadioGroup
        android:id="@+id/my_radio_group"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:overScrollMode="never"
        android:scrollbars="none"
        app:addList="@array/radio_buttons"
        app:layout_constraintTop_toBottomOf="@+id/text"/>
```

## Add string-array at strings.xml
```xml
<string-array name="radio_buttons">
    <item>Mantap</item>
    <item>Jiwa</item>
    <item>Keren</item>
    <item>Uy</item>
    <item>Wow</item>
    <item>Yuhu</item>
    <item>Yey</item>
    <item>Net Not</item>
    <item>Wkwk</item>
    <item>Top</item>
</string-array>
```
## Add listener in Activity/Fragment
```kotlin
binding.myRadioGroup.setOnChoseListener = MultiLineRadioGroup.OnChoseListener {position, text ->
    binding.text.text = "Selected: $position $text"
}
```
