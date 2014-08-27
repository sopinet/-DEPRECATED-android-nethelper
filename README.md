android-nethelper
=================

NetHelper Android Library

Installation
============

Android-NetHelper is build using Gradle.

Include the following in your proyect's <b>build.gradle</b> and Android-NetHelper will be downloaded automatically from Maven Central repository.

    repositories {
        mavenCentral()
    }
    
    dependencies {
        compile 'com.sopinet:android-nethelper:1.0.0'
    }

Alternatively you can include Android-NetHelper as a sub-module. You just would have to add the Android-NetHelper folder as a sub-module of your proyect.

Using it
========

Include in manifiest:
```
 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```
Use it:
```
SimpleContent sc = new SimpleContent(this, "waydo", 0);
try {
    result = sc.getUrlContent(Var.Redmine_Projects_URL, data);
} catch (SimpleContent.ApiException e) {
    e.printStackTrace();
}
```
