FCMNotificationHandler
===============================
Android library that helps to construct and launch system tray notifications from FCM Notification messages received when the app is in foreground, mimicking the default format and behavior applied when the app is background.

Download
--------
Step 1. Add the JitPack repository in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
Step 2. Add the dependency
```groovy
dependencies {
        compile 'com.github.franmontiel:FcmNotificationHandler:1.0'
}
```
Usage
-----
Just add the following line in the `onMessageReceived` method of your `FirebaseMessagingService` and a system notification will be shown as if the FCM message were received when the app was in background:
```java
new RemoteMessageNotifier(getApplicationContext()).notify(remoteMessage);
```

You can also just map the FCM Notification to a system notification with:
```java
new RemoteMessageToNotificationMapper(getApplicationContext()).map(remoteMessage);
```
or map it to a notification builder with:
You can also just map the FCM Notification to a system notification with:
```java
new RemoteMessageToNotificationMapper(getApplicationContext()).mapToNotificationBuilder(remoteMessage);
```
Take into account that if your FCM message does not have any notification information (is a Data only message) an `IllegalArgumentException` will be thrown when calling any of the previously described methods.

License
-------
    Copyright (C) 2017 Francisco José Montiel Navarro

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
