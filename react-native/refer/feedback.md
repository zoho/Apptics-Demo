In-app feedback lets users tell you what is wrong without leaving the app. Apptics ships ready-made
feedback and bug-report screens (the bug-report flow captures and lets the user annotate a
screenshot), a shake gesture to open them, and APIs to submit reports from your own UI.

See the sample usage in `src/screens/FeedbackScreen.tsx`.

---

## Open the built-in forms

```ts
import {AppticsFeedback} from '@zoho_apptics/apptics-react-native';

AppticsFeedback.openFeedback();  // plain feedback form
AppticsFeedback.reportBug();     // screenshot capture → annotate → report
```

| Method | What it does |
|--------|--------------|
| `openFeedback()` | Presents the native feedback screen. |
| `reportBug()` | Captures a screenshot and presents the annotation + bug-report flow. |

---

## Shake for feedback

The prompt opens when the user shakes the device with the app in the foreground.

```ts
AppticsFeedback.enableShakeForFeedback();
AppticsFeedback.disableShakeForFeedback();
```

| Method | What it does |
|--------|--------------|
| `enableShakeForFeedback()` | Turns the shake gesture on. |
| `disableShakeForFeedback()` | Turns it off. |

Worth disabling on screens where shaking is part of the app's own interaction — and note that in
debug builds shaking also opens React Native's dev menu.

---

## Anonymous senders

If no user has been identified (see [users.md](users.md)), the report arrives without a way to reply.
The anonymous alert warns the user about that before they submit.

```ts
AppticsFeedback.enableAnonymousAlert();
AppticsFeedback.disableAnonymousAlert();
```

---

## Submit programmatically

Use this to build your own feedback UI, or to send a report silently from an error path.

```ts
AppticsFeedback.sendFeedback(
  'The checkout button is hard to find.',  // feedback
  true,                                     // includeLogs
  true,                                     // includeDiagnostics
  'user@example.com',                       // guestMailId — null if you have an identified user
  false,                                    // forceToAnonymous
  null,                                     // attachments — array of local file paths
);
```

| Parameter | Type | Meaning |
|---|---|---|
| `feedback` | `string` | The message body. |
| `includeLogs` | `boolean` | Attach the lines buffered by `writeLog`. |
| `includeDiagnostics` | `boolean` | Attach the key/values added by `addDiagnosticsInfo`. |
| `guestMailId` | `string \| null` | Reply-to address when the sender is not a signed-in user. |
| `forceToAnonymous` | `boolean` | Strip the user identity from the report. |
| `attachments` | `string[] \| null` | Local file paths to attach. |

Attachments must be readable local files. With a document picker, copy the file into the app's cache
directory first and pass that path:

```ts
const res = await DocumentPicker.pickSingle({
  type: [DocumentPicker.types.allFiles],
  copyTo: 'cachesDirectory',
});

AppticsFeedback.sendFeedback('Screenshot attached', true, true, null, false, [res.fileCopyUri]);
```

---

## Logs and diagnostics attached to reports

These are **not** the same as the remote logger. They fill two files that ride along with a feedback
report when `includeLogs` / `includeDiagnostics` is true:

```ts
import {AppticsFeedback, LogType} from '@zoho_apptics/apptics-react-native';

AppticsFeedback.writeLog(LogType.Debug, 'Cart rebuilt with 3 items');
AppticsFeedback.writeLog(LogType.Error, 'Payment gateway timed out');

AppticsFeedback.addDiagnosticsInfo('App', 'build', '1.0 (1)');
AppticsFeedback.addDiagnosticsInfo('Session', 'screen', 'CheckoutScreen');

AppticsFeedback.resetLogsAndDiagnostics();
```

| Method | What it does |
|--------|--------------|
| `writeLog(logType, message)` | Appends a line to the feedback log file. |
| `addLogFile(filePath)` | Attaches an existing log file you produced yourself. |
| `addDiagnosticsInfo(header, key, value)` | Adds a key/value pair under a heading. |
| `resetLogsAndDiagnostics()` | Clears both buffers. |

`LogType` values: `Debug`, `Info`, `Warn`, `Error`.

For logs you want to see on the dashboard *without* the user submitting anything, use `APLogger`
instead — see [remote-logging.md](remote-logging.md).

---

## Notes

- Reports land under **Feedback** on the Apptics console, with the attached logs, diagnostics and
  device information.
- Call `Apptics.setUser` before submitting where you can — an identified report is far easier to
  follow up on.

---

## Compared with the Flutter plugin

| Flutter | React Native |
|---|---|
| `openFeedback()` / `reportBug()` | same |
| `enable`/`disableShakeForFeedback()` | same |
| `isShakeForFeedbackEnabled()` | not exposed |
| `enable`/`disableAnonymousUserAlert()` | `enable`/`disableAnonymousAlert()` |
| `isAnonymousUserAlertEnabled()` | not exposed |
| `setEmailId(email)` | not exposed — pass `guestMailId` per call |
| `sendFeedback(msg, logs, diagnostics, guestMailId:)` | plus `forceToAnonymous` and `attachments` |
| `sendBugReport(...)` | not exposed — only `sendFeedback` |
| `writeLog(msg, Log.verbose\|debug\|info\|warn\|error)` | `writeLog(LogType.Debug\|Info\|Warn\|Error, msg)` — **no verbose**, arguments reversed |
| — | `addLogFile(path)` (React Native only) |

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/>
