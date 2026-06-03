Remote Logging is part of the Apptics Logger SDK. `AppticsLogger` writes structured log lines that
are buffered locally and uploaded with the next sync — useful for diagnosing issues in production
builds where you can't attach a debugger. Each line carries a level (Verbose / Debug / Info /
Warning / Error), a tag, a message, and an optional throwable.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html).

---

## Add the Logger SDK

Using Apptics BoM (recommended):

```groovy
dependencies {
    implementation platform('com.zoho.apptics:apptics-bom:[latest-version]')
    implementation 'com.zoho.apptics:apptics-logger'
}
```

Without BoM:

```groovy
dependencies {
    implementation 'com.zoho.apptics:apptics-logger:[latest-version]'
}
```

---

## Enable the logger

Enable the remote logger once in `Application.onCreate()` so logging is on from launch:

```kotlin
import com.zoho.apptics.logger.AppticsLogger

AppticsLogger.enable()
```

You can turn the logger on or off globally at any time, and query its current state:

```kotlin
AppticsLogger.enable()
AppticsLogger.disable()
val on = AppticsLogger.isEnabled()
```

While the logger is disabled, every `v / d / i / w / e` call is a no-op — no buffering, no upload —
so it's safe to leave log calls in place and toggle the logger off in regulated builds.

---

## Writing log lines

Call the level method that matches the severity. Each takes a `tag` and a `message`, and the
error-style calls accept an optional `throwable`. Lines are buffered locally and uploaded with the
next sync.

```kotlin
// Kotlin
AppticsLogger.v("AppticsSample", "Verbose detail")
AppticsLogger.d("AppticsSample", "Hello from the playground")
AppticsLogger.i("AppticsSample", "User reached checkout")
AppticsLogger.w("AppticsSample", "Cache miss, refetching")
AppticsLogger.e("AppticsSample", "Something went wrong", throwable)
```

```java
// Java
AppticsLogger.INSTANCE.d("AppticsSample", "Hello from the playground");
AppticsLogger.INSTANCE.e("AppticsSample", "Something went wrong", throwable);
```

## Flushing buffered logs

Buffered entries upload automatically on the next scheduled sync. To push them immediately, call
`flushLogs()`. It is a **suspend** function, so call it from a coroutine:

```kotlin
lifecycleScope.launch {
    AppticsLogger.flushLogs()
}
```

---

## API reference

| Method | What it does |
|--------|--------------|
| `enable()` / `disable()` | Turns the remote logger on / off globally. |
| `isEnabled()` | Returns whether the logger is currently on. |
| `v(tag, message)` | Writes a Verbose log line. |
| `d(tag, message)` | Writes a Debug log line. |
| `i(tag, message)` | Writes an Info log line. |
| `w(tag, message)` | Writes a Warning log line. |
| `e(tag, message [, throwable])` | Writes an Error log line, with an optional throwable attached. |
| `flushLogs()` | Uploads buffered log entries immediately. **Suspend function** — call from a coroutine. |

---

## Notes

- `flushLogs()` is a **suspend** function — call it from a coroutine (e.g. `lifecycleScope.launch`).
- `v / d / i / w / e` calls are **no-ops while the logger is disabled** (`isEnabled()` is `false`).
- When enabled, log lines are buffered locally and upload on the **next scheduled sync** unless you
  flush them manually.
- All level methods accept a `tag` and a `message`; the error-style call also accepts an optional
  `throwable`.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-remote_logger.html>
