Crash tracking captures both the native crashes the platform SDKs see and the JavaScript errors that
would otherwise only show up as a red screen. Fatal crashes are uploaded on the **next** launch;
non-fatals go out during the current session.

See the sample usage in `src/screens/CrashScreen.tsx` and `src/core/appticsBootstrap.ts`.

---

## Install the JS crash handler

```ts
import {Apptics} from '@zoho_apptics/apptics-react-native';

Apptics.init();
Apptics.initCrashTracker();
```

`initCrashTracker` replaces React Native's global error handler (`ErrorUtils.setGlobalHandler`) with
one that parses the JS stack, reports it to Apptics, and then calls the original handler — so the
red box and your other error reporting still work.

Call it once, early. This sample does it in `src/core/appticsBootstrap.ts`, which runs at module
scope in `App.tsx`, so errors thrown during the first render are already covered.

| Method | What it does |
|--------|--------------|
| `initCrashTracker()` | Installs the global JS error handler (and activates the native crash listener on iOS). |

---

## Debug builds do not upload

By design, `initCrashTracker` installs the handler but sends nothing while `__DEV__` is true, and
`reportError` returns immediately. Otherwise every error you hit during development would land on
your dashboard.

To test the flow without making a release build:

```ts
Apptics.enableDevTesting();   // call BEFORE initCrashTracker()
```

It is a no-op in release builds. **Remove it before shipping** — this sample calls it at startup
purely so the demo is exercisable.

| Method | What it does |
|--------|--------------|
| `enableDevTesting()` | Bypasses the `__DEV__` guard on crash reporting and remote logging. |

---

## Report a caught error (non-fatal)

```ts
try {
  await checkout();
} catch (error) {
  Apptics.reportError(error);   // pass the real Error so the stack is genuine
}
```

Pass the actual `Error` object — the library reads `error.name`, `error.message` and `error.stack`.
Constructing a fresh `new Error('...')` at the report site works too, but its stack will point at
the reporting code rather than the failure.

| Method | What it does |
|--------|--------------|
| `reportError(error)` | Reports a caught error as a non-fatal exception. |

---

## Attach context to crash reports

```ts
Apptics.setCrashCustomProperty({
  screen: 'CheckoutScreen',
  experiment: 'A',
  cartItems: 3,
});
```

The properties apply to every crash reported **after** the call, so set them when the relevant state
changes (screen entered, feature flag resolved, cart updated) rather than inside the catch block.

| Method | What it does |
|--------|--------------|
| `setCrashCustomProperty(properties)` | Attaches custom keys to subsequent crash reports. |

---

## Prompt the user about the last crash

```ts
// Android only.
Apptics.showLastSessionCrashedPopup();
```

Shows a dialog when the previous session ended in a crash, so the user can add what they were doing.
On iOS it logs a `__DEV__` warning and does nothing.

---

## Verify it end-to-end

1. Build a **release** build (or keep `enableDevTesting()` in a debug build).
2. Trigger a fatal crash from outside any `try`/`catch` — the sample throws from a timer callback so
   the error escapes React's error boundaries:

   ```ts
   setTimeout(() => {
     throw new Error('deliberate fatal crash');
   }, 0);
   ```

3. The app terminates. **Reopen it** — the report uploads on the next launch.
4. Check **Crash → Issues** on the Apptics console.

For non-fatals you can shorten the wait with `Apptics.flush()`.

---

## Notes

- Crash tracking obeys the tracking state: under `OnlyUsageTracking*` or `NoTracking` nothing is
  reported. See [privacy.md](privacy.md).
- Native crashes (in a native module, or in the platform itself) are captured by the underlying
  Android/iOS SDKs without any JS involvement.
- iOS symbolication depends on the `Apptics pre build` script phase uploading dSYMs — see the
  Podfile and the README.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/>
