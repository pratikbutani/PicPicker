# PicPicker
PicPicker is Awesome Single and Multiple Image Picker Library.

---

# Example
![](https://github.com/pratikbutani/PicPicker/blob/master/screenshot1.png)
![](https://github.com/pratikbutani/PicPicker/blob/master/screenshot2.png)
![](https://github.com/pratikbutani/PicPicker/blob/master/screenshot3.png)

---

# Usage

### Eclipse
[![GO HOME](http://ww4.sinaimg.cn/large/5e9a81dbgw1eu90m08v86j20dw09a3yu.jpg)


### Pick Photo
```java
PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
intent.setPhotoCount(9);
intent.setShowCamera(true);
intent.setShowGif(true);
startActivityForResult(intent, REQUEST_CODE);
```

### Preview Photo

```java
ArrayList<String> photoPaths = ...;

Intent intent = new Intent(mContext, PhotoPagerActivity.class);
intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, photoPaths);
intent.putExtra(PhotoPagerActivity.EXTRA_SHOW_DELETE, false); // default is true
startActivityForResult(intent, REQUEST_CODE);
```

### onActivityResult
```java
@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);

  if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
    if (data != null) {
      ArrayList<String> photos = 
          data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
    }
  }
}
```

### AndroidManifest.xml
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    >
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

  <application
    ...
    >
    ...
    
    <!-- Activity Defined for Use from Lib -->
    <activity
        android:name="com.androidbuts.picpicker.PhotoPickerActivity"
        android:theme="@style/Theme.AppCompat.Light.NoActionBar" />
    <activity
        android:name="com.androidbuts.picpicker.PhotoPagerActivity"
        android:theme="@style/Theme.AppCompat.NoActionBar" />
    
  </application>
</manifest>
```

### Custom Style
```xml
<style name="myTheme.actionBar" parent="ThemeOverlay.AppCompat.Dark.ActionBar">
  <item name="android:textColorPrimary">#0000ff</item>
</style>

<style name="myTheme" parent="Theme.AppCompat.Light.NoActionBar">
  <item name="actionBarTheme">@style/myTheme.actionBar</item>
  <item name="colorPrimary">#00FF00</item>
</style>
```

### Proguard

```
# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
# nineoldandroids
-keep interface com.nineoldandroids.view.** { *; }
-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.** { *; }
# support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
```

---

Special Thanks to [PhotoPicker](https://github.com/donglua/PhotoPicker)

Contribution: [Pratik Butani](https://github.com/pratikbutani)
- Added Permission Checker in Library
- Bug Solved (Glide, Scroll)
- Design changes


# License

    Copyright 2016 Android Buts

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

