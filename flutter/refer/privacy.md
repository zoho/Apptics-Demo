The Apptics Flutter plugin exposes a privacy / consent layer built around a single `TrackingState`
value that describes exactly what the SDK is allowed to collect for the current user. You can read
that state, set it programmatically, or hand the decision to the user through Apptics' built-in
privacy screens. All four methods live on `AppticsFlutter.instance`; the sample wires them up in
`lib/screens/privacy_screen.dart`.

The `TrackingState` enum is **not** in the main `apptics_flutter.dart` file — import it from:

```dart
import 'package:apptics_flutter/apptics_flutter_util.dart';
```

---

## What TrackingState is

`TrackingState` is an enum (each value carries an underlying `num value`) describing the user's
current tracking preference — which combination of usage analytics and crash reporting is allowed,
and whether personally identifiable information (PII) may be attached.

| Value | Underlying value | Meaning |
|-------|------------------|---------|
| `TrackingState.noTracking` | `-1` | Nothing is tracked. No usage, no crashes, no PII. |
| `TrackingState.usageAndCrashTrackingWithPII` | `1` | Track usage analytics and crashes, with PII attached. |
| `TrackingState.onlyUsageTrackingWithPII` | `2` | Track usage analytics only, with PII attached. |
| `TrackingState.onlyCrashTrackingWithPII` | `3` | Track crashes only, with PII attached. |
| `TrackingState.usageAndCrashTrackingWithoutPII` | `4` | Track usage analytics and crashes, without PII. |
| `TrackingState.onlyUsageTrackingWithoutPII` | `5` | Track usage analytics only, without PII. |
| `TrackingState.onlyCrashTrackingWithoutPII` | `6` | Track crashes only, without PII. |

You can map back from a raw number with the static helper:

```dart
import 'package:apptics_flutter/apptics_flutter_util.dart';

final state = TrackingState.getByValue(4); // -> usageAndCrashTrackingWithoutPII
final raw = TrackingState.usageAndCrashTrackingWithoutPII.value; // -> 4
```

| Member | What it does |
|--------|--------------|
| `TrackingState.values` | All seven enum values, in declaration order. |
| `TrackingState.getByValue(num i)` | Returns the `TrackingState` whose `value` equals `i`. |
| `<state>.value` | The underlying `num` for a given state. |

---

## Reading the current state

Use `getTrackingState()` to find out what the user has currently consented to. It returns a
`Future<TrackingState?>` — the value is nullable, so handle the case where no state has been
resolved yet (e.g. before the user has made a choice).

```dart
import 'package:apptics_flutter/apptics_flutter.dart';
import 'package:apptics_flutter/apptics_flutter_util.dart';

final TrackingState? state = await AppticsFlutter.instance.getTrackingState();

if (state == null) {
  // No tracking state resolved yet.
} else {
  print('Current tracking state: $state (${state.value})');
}
```

| Method | What it does |
|--------|--------------|
| `AppticsFlutter.instance.getTrackingState()` | Returns the current `TrackingState` for the user as `Future<TrackingState?>` (null if not yet resolved). |

---

## Setting the state

Use `setTrackingState(TrackingState)` to apply a tracking preference programmatically — for example,
when you collect consent through your own UI rather than the Apptics screens. It takes one of the
`TrackingState` enum values and returns a `Future<void>`.

```dart
import 'package:apptics_flutter/apptics_flutter.dart';
import 'package:apptics_flutter/apptics_flutter_util.dart';

// User opted into full tracking without PII.
await AppticsFlutter.instance.setTrackingState(
  TrackingState.usageAndCrashTrackingWithoutPII,
);

// User opted out entirely.
await AppticsFlutter.instance.setTrackingState(TrackingState.noTracking);
```

| Method | What it does |
|--------|--------------|
| `AppticsFlutter.instance.setTrackingState(TrackingState state)` | Sets the tracking preference for the current user. Returns `Future<void>`. |

---

## Built-in consent UIs

If you'd rather let Apptics handle the consent UX, the plugin ships two ready-made screens. Both
return `Future<void>` and take no arguments — Apptics resolves the resulting `TrackingState` for you.

- `presentPrivacyReviewPopup()` shows the privacy options dialog built into the Apptics SDK, asking
  the user to review their tracking preferences.
- `openPrivacySettings()` opens the screen where the user can change their analytics privacy
  settings at any time.

```dart
import 'package:apptics_flutter/apptics_flutter.dart';

// Ask the user to review consent (e.g. on first launch).
await AppticsFlutter.instance.presentPrivacyReviewPopup();

// Let the user revisit and change their settings later.
await AppticsFlutter.instance.openPrivacySettings();
```

After the user makes a choice, call `getTrackingState()` to read the resolved value if you need to
reflect it in your own UI.

| Method | What it does |
|--------|--------------|
| `AppticsFlutter.instance.presentPrivacyReviewPopup()` | Shows the Apptics built-in privacy / consent review popup. Returns `Future<void>`. |
| `AppticsFlutter.instance.openPrivacySettings()` | Opens the Apptics analytics privacy settings screen so users can change their preferences. Returns `Future<void>`. |

---

## Notes

- `TrackingState`, `PrivacyStatus`, `PrivacyConsentType`, and `AnonymousType` all live in
  `package:apptics_flutter/apptics_flutter_util.dart` — import that file wherever you reference an
  enum value.
- `getTrackingState()` can return `null`; always null-check before using the value.
- The "WithPII" / "WithoutPII" distinction controls whether personally identifiable information is
  attached to the data Apptics collects — pick the variant that matches the consent you obtained.
- For what each tracking category controls and how user choices affect data collection, see the
  [Consent docs](https://www.zoho.com/apptics/resources/SDK/).

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/>
