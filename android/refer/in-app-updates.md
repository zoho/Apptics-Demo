In-App Updates lets you prompt users to install a newer build of your app. Apptics checks the
**target version configured on the console** against the installed build and surfaces an upgrade
dialog when an update is available — so you can roll out and enforce new versions without writing
your own version-check logic.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html).

---

## Add the App Updates SDK

Using Apptics BoM (recommended):

```groovy
dependencies {
    implementation platform('com.zoho.apptics:apptics-bom:[latest-version]')
    implementation 'com.zoho.apptics:apptics-appupdates'
}
```

Without BoM:

```groovy
dependencies {
    implementation 'com.zoho.apptics:apptics-appupdates:[latest-version]'
}
```

---

## Host requirement

The upgrade dialog is shown on top of your UI, so the entry point needs an `AppCompatActivity`
host. Make sure the activity you pass in extends `AppCompatActivity` (directly, or via
`ComponentActivity` in a Compose app whose host activity is an `AppCompatActivity`):

```kotlin
// Kotlin — resolve the AppCompatActivity from a Compose context
val context = LocalContext.current
(context as? AppCompatActivity)?.let { activity ->
    AppticsInAppUpdates.checkAndShowVersionAlert(activity)
}
```

---

## Check for an update

`checkAndShowVersionAlert` is the standard entry point. It checks the console-configured target
version against the installed build and shows the Apptics upgrade dialog if an update is available.

```kotlin
// Kotlin
import com.zoho.apptics.appupdates.AppticsInAppUpdates

AppticsInAppUpdates.checkAndShowVersionAlert(activity)
```

```java
// Java
import com.zoho.apptics.appupdates.AppticsInAppUpdates;

AppticsInAppUpdates.checkAndShowVersionAlert(activity);
```

This is all most apps need — call it at a natural checkpoint (e.g. app launch or returning to the
home screen) and Apptics handles the version comparison and dialog.

---

## Force a fresh check (worker thread)

By default the SDK uses cached update info. To bypass the cache and hit the network directly, call
`coldCheckForUpdate`. It returns the update info (or `null` when no update is available).

`coldCheckForUpdate()` is annotated `@WorkerThread` and makes a **blocking network call** — run it
off the main thread, inside a `Thread { ... }` or a coroutine on `Dispatchers.IO`.

```kotlin
// Kotlin
Thread {
    val data = runCatching { AppticsInAppUpdates.coldCheckForUpdate() }.getOrNull()
    if (data != null) {
        // Update available — surface your own prompt or call checkAndShowVersionAlert(activity)
    } else {
        // No update available
    }
}.start()
```

---

## Flexible vs immediate updates

Apptics builds on Play Core's update flows:

- **Immediate** — a full-screen, blocking update the user must complete before continuing. Used for
  critical releases. Driven by the dialog `checkAndShowVersionAlert` shows.
- **Flexible** — downloads in the background while the user keeps using the app. Once the download
  finishes, you trigger the install step yourself.

After a flexible update has finished downloading, call `installFlexibleUpdate` to complete the
install. It is a no-op if nothing is queued.

```kotlin
// Kotlin
runCatching { AppticsInAppUpdates.installFlexibleUpdate() }
    .onFailure { /* installFlexibleUpdate failed: ${it.message} */ }
```

```java
// Java
AppticsInAppUpdates.installFlexibleUpdate();
```

---

## Options

### Suppress prompts for non–Play Store builds

`disableIfNotInstalledFromPlayStore` controls whether update prompts are shown for builds that were
not installed from the Play Store (e.g. side-loaded debug or internal builds). When `true`, prompts
are suppressed for those installs.

```kotlin
// Kotlin — read or toggle the flag
val suppressed = AppticsInAppUpdates.disableIfNotInstalledFromPlayStore
AppticsInAppUpdates.disableIfNotInstalledFromPlayStore = true
```

---

## API reference

| Method / property | What it does |
|-------------------|--------------|
| `checkAndShowVersionAlert(activity)` | Checks the console-configured target version against the installed build and shows the upgrade dialog if an update exists. Requires an `AppCompatActivity`. |
| `coldCheckForUpdate()` | Bypasses cached update info and hits the network directly. **Worker thread only.** Returns update info or `null`. |
| `installFlexibleUpdate()` | Triggers the install after a flexible update has downloaded. No-op if nothing is queued. |
| `disableIfNotInstalledFromPlayStore` | When `true`, suppresses update prompts for builds not installed from the Play Store. |

---

## Notes

- `coldCheckForUpdate()` is `@WorkerThread` and makes a **blocking network call** — always call it
  off the main thread (a `Thread { ... }` or a coroutine on `Dispatchers.IO`).
- The upgrade dialogs require an `AppCompatActivity` host. Resolve the activity before calling
  `checkAndShowVersionAlert`.
- `checkAndShowVersionAlert` uses the SDK's cached update info; use `coldCheckForUpdate` when you
  need to force a fresh check.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-in_app_update.html>
