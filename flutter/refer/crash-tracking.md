Crash & ANR tracking lives in the `AppticsCrashTracker` singleton, accessed through
`AppticsCrashTracker.instance`. Once you wire up `autoCrashTracker()`, **fatal Flutter errors (both
synchronous and asynchronous) are captured and uploaded automatically**, and stack traces appear in
your Apptics dashboard on the next session. On top of automatic capture you can record **handled
(non-fatal) exceptions**, attach **custom properties** to crash reports, read back the **last crash
info**, opt into **instant sync**, and — on Android — manage **ANR (Application Not Responding)**
detection.

All methods are reached through the singleton and the tracker is imported with:

```dart
import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';
```

A runnable demo of every API below lives in `lib/screens/crash_screen.dart`.

## Automatic crash tracking

Call `autoCrashTracker()` once during app startup (before `runApp`, typically inside the same zone
that runs your app). It installs two global hooks:

- `FlutterError.onError` — catches **synchronous** framework/build errors and forwards them via
  `sendException(..., isFatal: true)`.
- `PlatformDispatcher.instance.onError` — catches **asynchronous / uncaught** errors and forwards
  them the same way, returning `true` so the error is considered handled.

With both hooks in place you do not need to do anything for fatal crashes — just let them
propagate. Reports upload on the **next app session**, so reopen the app after a crash to confirm it
reaches the dashboard.

```dart
import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';
import 'package:flutter/widgets.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Install FlutterError.onError + PlatformDispatcher.onError hooks.
  await AppticsCrashTracker.instance.autoCrashTracker();

  runApp(const MyApp());
}
```

Once installed, an uncaught exception is reported automatically as a fatal crash:

```dart
// Thrown outside any try/catch — the global handler captures and reports it.
throw Exception('Apptics sample: deliberate fatal crash');
```

| Method | What it does |
|--------|--------------|
| `Future<void> autoCrashTracker()` | Hooks `FlutterError.onError` and `PlatformDispatcher.instance.onError` to auto-report uncaught sync and async errors as fatal crashes. |
| `Future<void> sendFlutterException(FlutterErrorDetails details, {bool isFatal = true})` | Presents and reports a `FlutterErrorDetails`; fatal by default. |

## Recording non-fatal (handled) exceptions

Use `sendNonFatalException(exception, stack)` for errors you caught and handled but still want to
surface for diagnostics. It is a thin wrapper over `sendException(..., isFatal: false)`. Always pass
the real `exception` and `StackTrace` from the `catch (e, s)` clause so the dashboard gets a genuine
stack trace.

```dart
import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';

try {
  // Force an error to capture a genuine stack trace.
  final int result = 1 ~/ 0;
  print(result);
} catch (e, s) {
  await AppticsCrashTracker.instance.sendNonFatalException(e, s);
}
```

For finer control, call `sendException` directly. It lets you attach a free-form `reason`, toggle
console printing with `isCrashPrint` (defaults to `kDebugMode`), and choose fatal vs non-fatal with
`isFatal`:

```dart
import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';

try {
  throw StateError('Simulated invalid state');
} catch (e, s) {
  await AppticsCrashTracker.instance.sendException(
    e,
    s,
    reason: 'demo: invalid state reached',
    isFatal: false,
  );
}
```

| Method | What it does |
|--------|--------------|
| `Future<void> sendNonFatalException(dynamic exception, StackTrace? stack)` | Records a caught error as a non-fatal (calls `sendException` with `isFatal: false`). |
| `Future<void> sendException(dynamic exception, StackTrace? stack, {dynamic reason, bool? isCrashPrint, bool isFatal = true})` | Lower-level report; `reason` adds context, `isCrashPrint` controls console output (defaults to `kDebugMode`), `isFatal` selects fatal vs non-fatal. |
| `Future<void> sendFlutterNonFatalException(FlutterErrorDetails details)` | Presents and reports a `FlutterErrorDetails` as a non-fatal. |

## Custom crash properties

`setCrashCustomProperty(Map<String, dynamic>)` attaches custom key/value pairs to subsequent crash
reports — useful for troubleshooting (current screen, experiment bucket, cart size, etc.). Values
must be JSON-encodable. Set them before the crash occurs; they show up under `customproperties` in
the crash info.

```dart
import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';

await AppticsCrashTracker.instance.setCrashCustomProperty(<String, dynamic>{
  'screen': 'CrashScreen',
  'experiment': 'A',
  'cartItems': 3,
});
```

| Method | What it does |
|--------|--------------|
| `Future<void> setCrashCustomProperty(Map<String, dynamic> properties)` | Attaches JSON-encodable custom keys to subsequent crash reports. |

## Last-crash info & crashed-session popup

`getLastCrashInfo()` returns the previous crash as a JSON string (or `null` if there was none),
including the issue name, message, device/network state, screen name, timestamps, and any custom
properties you set. `showLastSessionCrashedPopup()` presents a popup only if the app crashed in a
previous session while crash tracking was disabled — call it on an appropriate screen.

```dart
import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';

// Read back the previous crash (JSON) for diagnostics or display.
final String? lastCrash = await AppticsCrashTracker.instance.getLastCrashInfo();
if (lastCrash != null) {
  print('Last crash: $lastCrash');
}

// Surface a popup if the previous session crashed.
await AppticsCrashTracker.instance.showLastSessionCrashedPopup();
```

A sample `getLastCrashInfo()` payload:

```json
{
  "issuename": "divide by zero",
  "message": "java.lang.ArithmeticException: divide by zero\n\tat com.zoho.apptics.MainActivity.onCreate$lambda$2(MainActivity.kt:42)",
  "networkstatus": 0,
  "serviceprovider": "T-Mobile",
  "orientation": 0,
  "batterystatus": 100,
  "ram": "2.9 GB",
  "rom": "5.8 GB",
  "sessionstarttime": 1711445408267,
  "customproperties": {},
  "screenname": "com.zoho.apptics.MainActivity",
  "happenedat": 1711445420908,
  "happenedcount": 1,
  "errortype": "native"
}
```

| Method | What it does |
|--------|--------------|
| `Future<String?> getLastCrashInfo()` | Returns the last crash as a JSON string, or `null` if there was none. |
| `Future<void> showLastSessionCrashedPopup()` | Shows a popup only if the app crashed in a previous session while tracking was disabled. |

## Instant sync

By default crashes upload on the **next app session**. `setAttemptInstantSync(true)` tries to sync
the crash instantly before the app terminates, blocking the crashing thread with a 2-second call
timeout. It is disabled by default. Note that turning it on may produce ANRs, and if the sync does
not finish before termination it is retried on the next launch, which can lead to duplicate crash
reports.

```dart
import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';

// Opt in to instant sync (off by default).
await AppticsCrashTracker.instance.setAttemptInstantSync(true);
```

| Method | What it does |
|--------|--------------|
| `Future<void> setAttemptInstantSync(bool isEnable)` | Enables/disables attempting to sync a crash instantly (2s timeout) before app termination. Disabled by default; may cause ANRs and duplicate reports. |

## ANR tracking (Android only)

ANR (Application Not Responding) detection is **Android only** — these methods are no-ops on iOS.
Use `enableANR()` / `disableANR()` to toggle detection and `isANREnabled()` to query the current
state.

```dart
import 'dart:io' show Platform;

import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';

// ANR APIs only take effect on Android.
if (Platform.isAndroid) {
  await AppticsCrashTracker.instance.enableANR();

  final bool? enabled = await AppticsCrashTracker.instance.isANREnabled();
  print('ANR enabled: $enabled');

  // ...later, to turn it back off:
  await AppticsCrashTracker.instance.disableANR();
}
```

`makeANR()` deliberately blocks the main thread so Android reports an ANR. **It is for testing
only** — use it to verify ANR reporting reaches the console, never in production.

```dart
import 'dart:io' show Platform;

import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';

// Testing only: deliberately hang the UI thread to trigger an ANR (Android).
if (Platform.isAndroid) {
  await AppticsCrashTracker.instance.makeANR();
}
```

| Method | What it does |
|--------|--------------|
| `Future<void> enableANR()` | Enables ANR detection (Android only). |
| `Future<void> disableANR()` | Disables ANR detection (Android only). |
| `Future<bool?> isANREnabled()` | Returns whether ANR detection is currently enabled (Android only). |
| `Future<void> makeANR()` | **Testing only.** Deliberately blocks the main thread to trigger an ANR (Android only). |

## Notes

- Fatal crashes and ANRs upload on the **next app session**, not at the moment they occur. Reopen
  the app after a crash to confirm the report uploads.
- ANR APIs (`enableANR` / `disableANR` / `isANREnabled` / `makeANR`) are **Android only** and no-op
  on iOS.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-crashreporting.html>
