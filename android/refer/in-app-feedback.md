In-App Feedback lets your users send feedback or report bugs from inside the app, without leaving
for email or a browser. Apptics owns the entire feedback UI — it opens a built-in composer with
attachments, screenshot and annotation tools, and auto-collected diagnostics so you get the device
and app context with every submission. A **bug-report variant** auto-captures a screenshot of the
current screen and drops the user straight into the annotation editor, and an optional
**shake-to-send** trigger lets users shake the device anywhere in the app to open the feedback
screen.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html).

---

## Add the Feedback SDK

Using Apptics BoM (recommended):

```groovy
dependencies {
    implementation platform('com.zoho.apptics:apptics-bom:[latest-version]')
    implementation 'com.zoho.apptics:apptics-feedback'
}
```

Without BoM:

```groovy
dependencies {
    implementation 'com.zoho.apptics:apptics-feedback:[latest-version]'
}
```

---

## Host requirement

The feedback and bug-report screens are shown as dialogs and require an **`AppCompatActivity`**
host. Pass an activity that extends `AppCompatActivity` (directly or transitively) to
`openFeedback` / `reportBug`. If you launch from Compose, resolve the hosting activity first:

```kotlin
val context = LocalContext.current
(context as? Activity)?.let { activity ->
    AppticsFeedback.openFeedback(activity)
}
```

---

## Core usage

The primary class is `AppticsFeedback`.

### Open the feedback UI

```kotlin
// Kotlin
import com.zoho.apptics.feedback.AppticsFeedback

// Launches the Apptics-owned feedback screen
// (composer + attachments + auto-collected diagnostics).
AppticsFeedback.openFeedback(activity)
```

```java
// Java
import com.zoho.apptics.feedback.AppticsFeedback;

AppticsFeedback.openFeedback(activity);
```

### Open the bug-report UI

`reportBug` is a variant of `openFeedback` that auto-captures a screenshot of the current screen
and opens it in the annotation editor. Use it for dedicated bug-report flows.

```kotlin
// Kotlin
runCatching {
    AppticsFeedback.reportBug(activity)
}.onFailure {
    // reportBug is not available on this SDK version — fall back.
    AppticsFeedback.openFeedback(activity)
}
```

```java
// Java
try {
    AppticsFeedback.reportBug(activity);
} catch (Throwable t) {
    AppticsFeedback.openFeedback(activity);
}
```

### Shake-to-send

When enabled, the SDK registers a `SensorManager` listener and opens the feedback screen
automatically on a strong shake — from anywhere in the app.

```kotlin
// Kotlin
if (AppticsFeedback.isShakeForFeedbackEnabled()) {
    AppticsFeedback.disableShakeForFeedback()
} else {
    AppticsFeedback.enableShakeForFeedback()
}
```

```java
// Java
if (AppticsFeedback.isShakeForFeedbackEnabled()) {
    AppticsFeedback.disableShakeForFeedback();
} else {
    AppticsFeedback.enableShakeForFeedback();
}
```

---

## Options / configuration

Shake detection is the one runtime toggle. It is controlled entirely through the three methods
above:

- `enableShakeForFeedback()` — turn shake detection **on**.
- `disableShakeForFeedback()` — turn shake detection **off**.
- `isShakeForFeedbackEnabled()` — read the current state (useful to reflect it in your UI).

A common pattern is to drive a settings switch from `isShakeForFeedbackEnabled()` and call enable
or disable based on the user's choice.

---

## API reference

| Method | What it does |
|--------|--------------|
| `openFeedback(activity)` | Opens the feedback screen (composer, attachments, auto-collected diagnostics). |
| `reportBug(activity)` | Variant that auto-captures a screenshot and opens it in the annotation editor. May not exist on older SDK versions — fall back to `openFeedback`. |
| `enableShakeForFeedback()` | Registers a shake listener; a strong shake opens the feedback screen automatically. |
| `disableShakeForFeedback()` | Turns shake detection off. |
| `isShakeForFeedbackEnabled()` | Returns whether shake-to-feedback is currently on. |

---

## Notes

- **`reportBug` may not exist on older SDK versions.** Wrap the call in `runCatching` (Kotlin) or
  `try/catch` (Java) and fall back to `openFeedback` on failure, as shown above.
- **Dialogs require an `AppCompatActivity` host.** Passing a non-`AppCompatActivity` context will
  prevent the feedback or bug-report screen from showing.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-in_app_feedback.html>
