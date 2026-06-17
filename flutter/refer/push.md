Push notifications are part of the Apptics Flutter plugin. They let your app receive
remote notifications, react to them while the app is in the foreground, and run code when a
notification is delivered, tapped, or has one of its action buttons tapped — whether the app is
running, backgrounded, or terminated. Apptics routes notifications through the platform messaging
stack, so before any of this works you must configure **FCM (Android)** and **APNs (iOS)**
credentials in the Apptics console; without those, no notifications are delivered to the device.

There is no Dart-side `initialize(apiKey: ...)` for Apptics — credentials come from the native
config files bundled with the app (`android/app/apptics-config.json`,
`ios/apptics-config.plist`). The Dart APIs below only wire up the *runtime* push behaviours your
app opts into.

Two namespaces are involved:

```dart
import 'package:apptics_flutter/push_notification/apptics_push_notification.dart';
import 'package:apptics_flutter/apptics_flutter.dart';
```

- `AppticsPushNotification` — static methods for registering the background handler and the
  foreground callbacks.
- `AppticsFlutter.instance` — a one-call alternative for the foreground callbacks, plus the
  iOS-only `startService()` / `registerPushNotification()`.

---

## Background handler

When a notification arrives while the app is **backgrounded or terminated**, Flutter spins up a
separate background isolate to run your handler. The Flutter engine can only find an entry point
in that fresh isolate if it is a **top-level (or static) function annotated with
`@pragma('vm:entry-point')`** — a closure or instance method will not work, because the new isolate
does not share the UI isolate's memory.

Register it with `AppticsPushNotification.setOnMessageHandlerListener(handler)`. This must run in
`main()` **before `runApp()`** and after `WidgetsFlutterBinding.ensureInitialized()`, so the handle
is recorded before any notification can arrive. The handler receives the notification payload as a
`Map<String, dynamic>`.

Because the handler runs in its own isolate, it does **not** share singletons or in-memory state
with your UI isolate. Do durable work here (persist the payload, schedule a local notification),
not UI updates.

```dart
import 'package:apptics_flutter/push_notification/apptics_push_notification.dart';
import 'package:flutter/material.dart';

// Top-level + vm:entry-point so the background isolate can find it.
@pragma('vm:entry-point')
Future<void> appticsBackgroundMessageHandler(Map<String, dynamic> message) async {
  // Runs in a *separate* isolate from the UI. Persist / process the payload here.
  print('Push received (background isolate): $message');
}

Future<void> main() async {
  // Always initialize the binding before touching a plugin / platform channel.
  WidgetsFlutterBinding.ensureInitialized();

  // Register the background handler up front, before runApp().
  AppticsPushNotification.setOnMessageHandlerListener(appticsBackgroundMessageHandler);

  runApp(const MyApp());
}
```

In the sample app this is `appticsBackgroundMessageHandler` in `lib/main.dart`, registered in
`main()` before `runApp()`.

| Method | What it does |
|--------|--------------|
| `AppticsPushNotification.setOnMessageHandlerListener(OnMessageHandler handler)` | Registers the top-level `@pragma('vm:entry-point')` background message handler. Call in `main()` before `runApp()`. `OnMessageHandler` is `void Function(Map<String, dynamic>)`. |
| `AppticsPushNotification.setNotificationClickListener(NotificationClick handler)` | Registers a background handler for notification taps. `NotificationClick` is `void Function(String?, String?)` — `(clickAction, payload)`. |
| `AppticsPushNotification.setNotificationActionClickListener(NotificationActionClick handler)` | Registers a background handler for action-button taps. `NotificationActionClick` is `void Function(String, String?, String?)` — `(actionId, clickAction, payload)`. |

---

## Foreground handlers

While the app is in the **foreground**, notifications are delivered to callbacks you register via
`AppticsPushNotification.initialize(...)`. All three callbacks are **required**. Call this once
during startup (after `runApp()` is fine — the sample does it in `apptics_bootstrap.dart`).

The three callbacks and their exact signatures:

- `onMessageReceived` — `Function(Map<String, dynamic>)`. Fires when a push is received in the
  foreground; receives the full payload map.
- `onNotificationClick` — `Function(String?, String?)`. Fires when the notification itself is
  tapped; receives `(clickAction, payload)`, both nullable strings.
- `onNotificationActionClick` — `Function(String, String?, String?)`. Fires when an action button
  on the notification is tapped; receives `(actionId, clickAction, payload)` — `actionId` is
  non-null, the other two are nullable.

```dart
import 'package:apptics_flutter/push_notification/apptics_push_notification.dart';

Future<void> initPush() async {
  await AppticsPushNotification.initialize(
    onMessageReceived: (Map<String, dynamic> message) {
      print('Push received (foreground): $message');
    },
    onNotificationClick: (String? clickAction, String? payload) {
      print('Notification clicked: action=$clickAction payload=$payload');
    },
    onNotificationActionClick: (String actionId, String? clickAction, String? payload) {
      print('Action clicked: id=$actionId action=$clickAction payload=$payload');
    },
  );
}
```

In the sample, `_initPushNotifications()` in `lib/core/apptics_bootstrap.dart` calls this and
funnels all three callbacks into the shared console.

| Method | What it does |
|--------|--------------|
| `AppticsPushNotification.initialize({required Function(Map<String, dynamic>) onMessageReceived, required Function(String?, String?) onNotificationClick, required Function(String, String?, String?) onNotificationActionClick})` | Registers all three foreground callbacks and wires up the foreground method-channel handler. Returns `Future<void>`. |
| `onMessageReceived(Map<String, dynamic> payload)` | Called when a push is received in the foreground. |
| `onNotificationClick(String? clickAction, String? payload)` | Called when the notification is tapped. |
| `onNotificationActionClick(String actionId, String? clickAction, String? payload)` | Called when an action button is tapped. |

---

## One-call alternative: `setPushNotificationListener`

If you don't need to register background handlers separately, `AppticsFlutter.instance` exposes a
single call that sets all three foreground callbacks at once. It takes the **same named, required
callbacks** as `AppticsPushNotification.initialize` and is handy for re-binding the foreground
listeners later (for example, to point them at a different sink).

```dart
import 'package:apptics_flutter/apptics_flutter.dart';

await AppticsFlutter.instance.setPushNotificationListener(
  onMessageReceived: (msg) => print('Push received: $msg'),
  onNotificationClick: (action, payload) =>
      print('Clicked: action=$action payload=$payload'),
  onNotificationActionClick: (id, action, payload) =>
      print('Action: id=$id action=$action payload=$payload'),
);
```

The sample wires this to a button on `lib/screens/push_screen.dart` ("Re-bind foreground
listeners").

| Method | What it does |
|--------|--------------|
| `AppticsFlutter.instance.setPushNotificationListener({required Function(Map<String, dynamic>) onMessageReceived, required Function(String?, String?) onNotificationClick, required Function(String, String?, String?) onNotificationActionClick})` | Sets all three foreground callbacks in one call. Same callback signatures as `AppticsPushNotification.initialize`. Returns `Future<void>`. |

---

## iOS-only: `startService()` and `registerPushNotification()`

On iOS you must start the messaging service and explicitly request the OS push token. Both are
exposed on `AppticsFlutter.instance` and are **no-ops on Android** (they return an immediately
completed `Future<void>` when `Platform.isIOS` is false), so it's safe to call them
unconditionally — though guarding with `Platform.isIOS` makes intent clear.

- `startService()` — starts the iOS push/messaging service.
- `registerPushNotification()` — requests the OS push token (triggers the APNs registration flow).

These are best treated as fire-and-forget: on an iOS **simulator** there is no APNs, so
`registerPushNotification()` may never resolve. Do **not** `await` them in a way that blocks your
first frame.

```dart
import 'dart:io' show Platform;
import 'package:apptics_flutter/apptics_flutter.dart';

if (Platform.isIOS) {
  // Fire-and-forget — may never resolve on a simulator (no APNs).
  unawaited(AppticsFlutter.instance.startService());
  unawaited(AppticsFlutter.instance.registerPushNotification());
}
```

The sample exposes both as buttons on `lib/screens/push_screen.dart` and also calls them (guarded
by `Platform.isIOS`) during init in `lib/core/apptics_bootstrap.dart`.

| Method | What it does |
|--------|--------------|
| `AppticsFlutter.instance.startService()` | Starts the iOS messaging service. No-op on Android. Returns `Future<void>`. |
| `AppticsFlutter.instance.registerPushNotification()` | Requests the OS push token (APNs registration). No-op on Android. Returns `Future<void>`. |

---

## Notes

- Configure FCM (Android) and APNs (iOS) credentials in the Apptics console first — without them no
  notifications reach the device.
- The background handler must be top-level/static, annotated with `@pragma('vm:entry-point')`, and
  registered in `main()` before `runApp()`.
- The background isolate does not share memory with the UI isolate; persist or process payloads
  there rather than updating UI state.
- Test iOS push on a **physical device** — the simulator has no APNs.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-push_notification.html>
