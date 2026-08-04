Apptics can deliver push notifications through FCM on Android and APNs on iOS, and reports delivery
and open rates back to the console alongside your other analytics.

See the sample usage in `src/screens/PushScreen.tsx` and `src/core/appticsBootstrap.ts`.

📖 Platform setup (credentials, FCM/APNs keys): <https://www.zoho.com/apptics/resources/SDK/react-native-push-notifications.html>

---

## Register the handlers

The library does **not** expose `addListener`. It subscribes to the native event emitter itself and
forwards each event to whatever function is currently assigned to a slot on
`AppticsPushModuleEmitter`:

```ts
import {AppticsPushModuleEmitter} from '@zoho_apptics/apptics-react-native';

AppticsPushModuleEmitter.onMessageReceived = payload => {
  // A notification arrived while the app was in the foreground.
};

AppticsPushModuleEmitter.onNotificationClick = (clickAction, payload) => {
  // The user tapped the notification.
};

AppticsPushModuleEmitter.onNotificationActionClick = (actionId, clickAction, payload) => {
  // The user tapped an action button on the notification.
};
```

Assign them **once, at startup** — before the first render, not inside a screen — so notifications
that arrive before any screen is mounted are still handled. Assigning again replaces the previous
handler; there is no list of subscribers.

| Slot | Fires when |
|---|---|
| `onMessageReceived(payload)` | A message arrives with the app in the foreground. |
| `onNotificationClick(clickAction, payload)` | The notification itself is tapped. |
| `onNotificationActionClick(actionId, clickAction, payload)` | An action button is tapped. |

`clickAction` is the deep-link / action string configured for the campaign; `payload` is your custom
data. Route on them rather than parsing the notification text.

---

## Start the service

```ts
import {AppticsPushMessages} from '@zoho_apptics/apptics-react-native';

AppticsPushMessages.startService();
AppticsPushMessages.registerPushNotification();
```

| Method | Android | iOS |
|---|---|---|
| `startService()` | no-op | Starts the Apptics messaging service. |
| `registerPushNotification()` | Signals that JS listeners are ready so queued events are delivered. | Requests the OS push token (triggers the permission prompt). |

Call them after assigning the handlers — otherwise a queued event can be delivered with nothing
listening.

---

## Foreground presentation (iOS)

By default iOS does not display a notification banner while the app is open. Choose what it should
do:

```ts
import {APNotificationOption, AppticsPushMessages} from '@zoho_apptics/apptics-react-native';

AppticsPushMessages.setForegroundNotificationOptions(APNotificationOption.all);
```

| Option | Result |
|---|---|
| `all` | Banner and sound. |
| `banner` | Banner only. |
| `sound` | Sound only. |
| `none` | Nothing — `onMessageReceived` still fires, so you can render your own in-app UI. |

Android ignores this call.

---

## Android build setup

The Apptics React Native library declares the push dependencies as `compileOnly`, so the **app** has
to add them. In `android/app/build.gradle` (they are present but commented out in this sample):

```groovy
implementation "com.zoho.apptics:apptics-pns:0.3.15"
implementation "com.google.firebase:firebase-messaging:24.0.1"
```

You also need:

1. A **`google-services.json`** in `android/app/` from your Firebase project.
2. The Google Services Gradle plugin — `com.google.gms:google-services` on the project classpath and
   `apply plugin: "com.google.gms.google-services"` in the app module.
3. The **`POST_NOTIFICATIONS`** permission in `AndroidManifest.xml` for Android 13+, and a runtime
   request for it.
4. Your **FCM server credentials** uploaded to the Apptics console.

This sample ships without a `google-services.json`, so Android push is inert until you add one — the
rest of the app builds and runs regardless.

---

## iOS build setup

1. Enable the **Push Notifications** capability and **Background Modes → Remote notifications** on
   the target.
2. Upload your **APNs key or certificate** to the Apptics console.
3. Run on a **physical device** — the simulator has no APNs connection.

The `AppticsMessaging` pod comes in through the library's podspec; no extra pod is needed.

---

## Testing

1. Upload FCM (Android) / APNs (iOS) credentials to the Apptics console.
2. Build and run; accept the notification permission prompt.
3. Send a campaign or test notification from the console.
4. **Foreground:** `onMessageReceived` fires — the sample logs a `🔔` line to the in-app console.
5. **Background / terminated:** the OS shows the notification; tapping it fires
   `onNotificationClick` once JS is running.

---

## Notes

- Because the handlers are plain assignable slots, keep them in one module. Assigning them from two
  places means the last one wins, silently.
