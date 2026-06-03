Crash Tracking is part of the Apptics Crash Tracker SDK. Once the SDK is initialized, **fatal
crashes and ANRs (Application Not Responding) are captured and uploaded automatically** — no code
is required. Stack traces appear in your Apptics dashboard on the next session. You can also record
**handled (non-fatal) exceptions** you caught but still want to surface for diagnostics.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html).

---

## Add the Crash Tracker SDK

Using Apptics BoM (recommended):

```groovy
dependencies {
    implementation platform('com.zoho.apptics:apptics-bom:[latest-version]')
    implementation 'com.zoho.apptics:apptics-crash-tracker'
}
```

Without BoM:

```groovy
dependencies {
    implementation 'com.zoho.apptics:apptics-crash-tracker:[latest-version]'
}
```

---

## Automatic capture

Once `Apptics.init(this)` has run in `Application.onCreate()`, crash tracking is active. No further
code is needed:

- **Fatal crashes** — uncaught exceptions are captured automatically. Just let them propagate.
- **ANRs** — when the main thread is blocked long enough to trigger an Application Not Responding
  state, Apptics records it.

Both are uploaded on the **next app session** (i.e. the next time the app is opened after the
crash). Reopen the app to confirm the report reaches your dashboard.

---

## Recording non-fatal (handled) exceptions

Use `AppticsNonFatals.recordException(throwable)` for errors you caught and handled, but still want
to track for diagnostics. Fatal/uncaught crashes are picked up automatically, so only call this for
handled errors.

```kotlin
// Kotlin
import com.zoho.apptics.crash.AppticsNonFatals

try {
    riskyCall()
} catch (t: Throwable) {
    AppticsNonFatals.recordException(t)
}
```

```java
// Java
import com.zoho.apptics.crash.AppticsNonFatals;

try {
    riskyCall();
} catch (Throwable t) {
    AppticsNonFatals.recordException(t);
}
```

Fatal crashes still require no code — let the exception propagate and the SDK captures it:

```kotlin
// Fatal — let the exception propagate; captured automatically
throw NullPointerException("forced crash")
```

---

## API reference

| Method | What it does |
|--------|--------------|
| _(automatic)_ | Fatal crashes and ANRs are captured without any code once `Apptics.init()` has run. |
| `AppticsNonFatals.recordException(throwable)` | Records a caught throwable as a non-fatal for diagnostics. |

---

## Notes

- Crashes, ANRs, and non-fatals upload on the **next app session**, not at the moment they occur.
  Reopen the app after a crash to confirm the report uploads.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-crashreporting.html>
