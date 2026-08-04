Apptics models user consent as a single **tracking state**: which of usage / crash data may be
collected, and whether the user id (PII) is attached to it. Everything else in the SDK obeys it, so
this is the one switch to wire your consent UI to.

See the sample usage in `src/screens/PrivacyScreen.tsx`.

---

## The seven states

```ts
import {Apptics, TrackingState} from '@zoho_apptics/apptics-react-native';

Apptics.setTrackingState(TrackingState.OnlyCrashTrackingWithPII);

const state = await Apptics.getTrackingState(); // Promise<TrackingState>
```

| State | Usage data | Crash data | User id attached |
|---|:--:|:--:|:--:|
| `UsageAndCrashTrackingWithPII` | ✅ | ✅ | ✅ |
| `UsageAndCrashTrackingWithoutPII` **(default)** | ✅ | ✅ | ❌ |
| `OnlyUsageTrackingWithPII` | ✅ | ❌ | ✅ |
| `OnlyUsageTrackingWithoutPII` | ✅ | ❌ | ❌ |
| `OnlyCrashTrackingWithPII` | ❌ | ✅ | ✅ |
| `OnlyCrashTrackingWithoutPII` | ❌ | ✅ | ❌ |
| `NoTracking` | ❌ | ❌ | ❌ |

- **Usage** covers events, screens, sessions and API tracking.
- **Crash** covers fatal and non-fatal exception reports.
- **PII** means the value you passed to `Apptics.setUser` — see [users.md](users.md).

`UsageAndCrashTrackingWithoutPII` is the state you start in, so an app that never calls
`setTrackingState` still collects anonymised data.

| Method | What it does |
|--------|--------------|
| `setTrackingState(state)` | Applies a consent state. Persists across launches. |
| `getTrackingState(): Promise<TrackingState>` | Reads the current state back. |
| `disable()` | iOS-only convenience for `setTrackingState(NoTracking)`. |

The enum values are not sequential (`WithoutPII` variants are 4–6, `NoTracking` is `-1`), so compare
against the enum members rather than raw numbers.

---

## Built-in consent UI

If you don't want to build your own consent screen, the SDK ships two:

```ts
// A dialog summarising what is collected, with an accept/decline choice.
Apptics.presentPrivacyReviewPopup();

// A full settings screen where the user can change the tracking state.
Apptics.openPrivacySettings();
```

| Method | What it does |
|--------|--------------|
| `presentPrivacyReviewPopup()` | Shows the SDK's privacy review dialog. |
| `openPrivacySettings()` | Opens the SDK's tracking-settings screen. |

On Android these are native activities themed by the `apptics*` colour tokens in
`android/app/src/main/res/values/styles.xml`.

---

## Crash-related prompt

```ts
// Android only — no-ops (with a __DEV__ warning) elsewhere.
Apptics.showLastSessionCrashedPopup();
```

Shows a prompt when the previous session ended in a crash, inviting the user to describe what
happened. Call it once after launch, not on every screen. See [crash-tracking.md](crash-tracking.md).

---

## Wiring it to your own consent flow

```ts
async function onConsentChosen(allowAnalytics: boolean, allowCrash: boolean, allowPII: boolean) {
  let state = TrackingState.NoTracking;
  if (allowAnalytics && allowCrash) {
    state = allowPII
      ? TrackingState.UsageAndCrashTrackingWithPII
      : TrackingState.UsageAndCrashTrackingWithoutPII;
  } else if (allowAnalytics) {
    state = allowPII
      ? TrackingState.OnlyUsageTrackingWithPII
      : TrackingState.OnlyUsageTrackingWithoutPII;
  } else if (allowCrash) {
    state = allowPII
      ? TrackingState.OnlyCrashTrackingWithPII
      : TrackingState.OnlyCrashTrackingWithoutPII;
  }
  Apptics.setTrackingState(state);
}
```

---

## Notes

- Set the state **before** you start firing events, ideally right after `Apptics.init()`, so nothing
  is collected under an assumption the user has not agreed to.
- Changing the state does not retroactively delete data already uploaded.
