In-App Feedback lets your users send feedback or report bugs from inside the app, without leaving
for email or a browser. Apptics owns the entire feedback UI — `openFeedback()` opens a built-in
composer with attachments, screenshot and annotation tools, and auto-collected diagnostics so you
get the device and app context with every submission. A **bug-report variant** (`reportBug()`)
auto-captures a screenshot of the current screen and drops the user straight into the annotation
editor, and an optional **shake-to-send** trigger lets users shake the device anywhere in the app
to open the feedback screen. You can also submit programmatically with `sendFeedback()` /
`sendBugReport()`, and bundle the SDK's own **remote logs** and **device diagnostics** into each
report. The same Flutter Feedback screen drives both the feedback flows and the logging/diagnostics
buffer that gets attached to them.

The plugin exposes two singletons: `AppticsFeedback.instance` for the feedback flows and
`AppticsLogs.instance` for logging and diagnostics. Import them from:

```dart
import 'package:apptics_flutter/feedback/apptics_feedback.dart';
import 'package:apptics_flutter/feedback/apptics_logs.dart';
import 'package:apptics_flutter/feedback/apptics_log_type.dart';
```

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html). For a
working end-to-end example, see `lib/screens/feedback_screen.dart`.

---

## Open the feedback & bug-report forms

`openFeedback()` launches the Apptics-owned feedback screen (composer + attachments +
auto-collected diagnostics). `reportBug()` is a variant that auto-captures a screenshot of the
current screen and opens it in the annotation editor — use it for dedicated bug-report flows. Both
are async and return `Future<void>`.

```dart
// Open the built-in feedback composer.
await AppticsFeedback.instance.openFeedback();

// Open the bug-report variant (auto-captures a screenshot for annotation).
await AppticsFeedback.instance.reportBug();
```

| Method | What it does |
|--------|--------------|
| `Future<void> openFeedback()` | Opens the feedback screen (composer, attachments, auto-collected diagnostics). |
| `Future<void> reportBug()` | Variant that auto-captures a screenshot and opens it in the annotation editor. |

---

## Shake-to-feedback toggle

When enabled, the SDK listens for a strong shake and opens the feedback screen automatically — from
anywhere in the app. Drive a settings switch from `isShakeForFeedbackEnabled()` and call enable or
disable based on the user's choice. Note that `isShakeForFeedbackEnabled()` returns `Future<bool?>`,
so it may be `null` if the platform has not reported a state.

```dart
final bool? enabled =
    await AppticsFeedback.instance.isShakeForFeedbackEnabled();

if (enabled == true) {
  await AppticsFeedback.instance.disableShakeForFeedback();
} else {
  await AppticsFeedback.instance.enableShakeForFeedback();
}
```

| Method | What it does |
|--------|--------------|
| `Future<void> enableShakeForFeedback()` | Registers a shake listener; a strong shake opens the feedback screen automatically. |
| `Future<void> disableShakeForFeedback()` | Turns shake detection off. |
| `Future<bool?> isShakeForFeedbackEnabled()` | Returns whether shake-to-feedback is currently on (`null` if unknown). |

---

## Anonymous-user alert

When enabled, the SDK warns users who are not signed in (no identified email) before they submit, so
you don't collect feedback you can't follow up on. Like the shake toggle, the state is readable so
you can reflect it in a settings switch. `isAnonymousUserAlertEnabled()` returns `Future<bool?>`.

```dart
final bool? alertOn =
    await AppticsFeedback.instance.isAnonymousUserAlertEnabled();

if (alertOn == true) {
  await AppticsFeedback.instance.disableAnonymousUserAlert();
} else {
  await AppticsFeedback.instance.enableAnonymousUserAlert();
}
```

| Method | What it does |
|--------|--------------|
| `Future<void> enableAnonymousUserAlert()` | Warns anonymous (unidentified) users before they submit. |
| `Future<void> disableAnonymousUserAlert()` | Turns the anonymous-user alert off. |
| `Future<bool?> isAnonymousUserAlertEnabled()` | Returns whether the anonymous-user alert is currently on (`null` if unknown). |

---

## Set the submitter email

`setEmailId(String?)` attaches an email to submissions so replies reach the right person and the
user is no longer treated as anonymous. Pass `null` to clear a previously set email.

```dart
await AppticsFeedback.instance.setEmailId('user@example.com');

// Clear the stored email.
await AppticsFeedback.instance.setEmailId(null);
```

| Method | What it does |
|--------|--------------|
| `Future<void> setEmailId(String? emailId)` | Sets the email attached to feedback submissions; pass `null` to clear it. |

---

## Programmatic submission: sendFeedback / sendBugReport

To skip the built-in UI entirely, submit a report directly. Both methods take the message and two
positional `bool` flags, plus optional named parameters:

- **`includeLogs`** — when `true`, the SDK's buffered remote logs (everything written via
  `AppticsLogs.writeLog(...)`, plus any files added with `addLogFile(...)`) are attached to the
  report.
- **`includeDiagnostics`** — when `true`, the auto-collected device/app diagnostics plus any
  custom entries added via `addDiagnosticsInfo(...)` are attached.
- **`guestMailId`** — an email to associate with this single submission (without calling
  `setEmailId`).
- **`forceToAnonymous`** — defaults to `false`; set `true` to submit anonymously regardless of any
  identified user.
- **`attachmentsUri`** — a `List<Uri>?` of file attachments to include.

`sendBugReport` has the identical signature and submits as a bug report rather than feedback.

```dart
// Feedback, bundling logs and diagnostics.
await AppticsFeedback.instance.sendFeedback(
  'The checkout button is hard to find.',
  true, // includeLogs
  true, // includeDiagnostics
  guestMailId: 'user@example.com',
  forceToAnonymous: false,
  attachmentsUri: [Uri.file('/path/to/screenshot.png')],
);

// Bug report — same parameters.
await AppticsFeedback.instance.sendBugReport(
  'App crashes when opening the cart.',
  true, // includeLogs
  true, // includeDiagnostics
  guestMailId: 'user@example.com',
);
```

| Method | What it does |
|--------|--------------|
| `Future<void> sendFeedback(String feedbackMessage, bool includeLogs, bool includeDiagnostics, {String? guestMailId, bool forceToAnonymous = false, List<Uri>? attachmentsUri})` | Submits feedback directly, optionally attaching buffered logs, diagnostics, a guest email, and file attachments. |
| `Future<void> sendBugReport(String feedbackMessage, bool includeLogs, bool includeDiagnostics, {String? guestMailId, bool forceToAnonymous = false, List<Uri>? attachmentsUri})` | Same as `sendFeedback`, but submits as a bug report. |

---

## Logging & diagnostics

`AppticsLogs.instance` writes the structured log lines and diagnostic entries that the SDK buffers
locally and attaches to a report when `includeLogs` / `includeDiagnostics` is `true`. This is the
remote-logging surface of the Flutter plugin — useful for diagnosing issues in production builds
where you can't attach a debugger.

### Writing log lines

`writeLog(String log, Log logType)` appends a single line at the given severity. The `Log` enum
(from `apptics_log_type.dart`) has five levels: `verbose`, `debug`, `info`, `warn`, and `error`.

```dart
final logs = AppticsLogs.instance;

await logs.writeLog('Verbose detail', Log.verbose);
await logs.writeLog('Hello from the playground', Log.debug);
await logs.writeLog('User reached checkout', Log.info);
await logs.writeLog('Cache miss, refetching', Log.warn);
await logs.writeLog('Something went wrong', Log.error);
```

### Attaching a log file

`addLogFile(File)` adds an existing log file to the buffer so it ships with the next report (when
`includeLogs` is `true`).

```dart
import 'dart:io';

await AppticsLogs.instance.addLogFile(File('/path/to/app.log'));
```

### Adding diagnostics

`addDiagnosticsInfo(String heading, String key, String value)` adds a custom key/value entry under
a heading. These appear alongside the auto-collected device/app diagnostics when
`includeDiagnostics` is `true`.

```dart
await AppticsLogs.instance.addDiagnosticsInfo('App', 'build', '1.0.0+1');
await AppticsLogs.instance.addDiagnosticsInfo('User', 'plan', 'premium');
```

### Resetting the buffer

`resetLogsAndDiagnostics()` clears all buffered log lines and custom diagnostics — useful after a
successful submission or when starting a fresh session.

```dart
await AppticsLogs.instance.resetLogsAndDiagnostics();
```

| Method | What it does |
|--------|--------------|
| `Future<void> writeLog(String log, Log logType)` | Writes a single log line at the given `Log` level (`verbose`, `debug`, `info`, `warn`, `error`). |
| `Future<void> addLogFile(File file)` | Adds an existing log file to the buffer so it ships with the next report. |
| `Future<void> addDiagnosticsInfo(String heading, String key, String value)` | Adds a custom diagnostic entry under a heading. |
| `Future<void> resetLogsAndDiagnostics()` | Clears all buffered log lines and custom diagnostics. |

---

## Notes

- The `Log` enum lives in `apptics_log_type.dart`: `Log.verbose`, `Log.debug`, `Log.info`,
  `Log.warn`, `Log.error`.
- Logs and diagnostics are only attached to a submission when you pass `includeLogs: true` /
  `includeDiagnostics: true` (or check the matching boxes in the built-in form).
- `isShakeForFeedbackEnabled()` and `isAnonymousUserAlertEnabled()` return `Future<bool?>` — handle
  the `null` case when binding them to UI.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-in_app_feedback.html>
