Remote Config lets you read values from the Apptics console at runtime to gate features, change copy,
or run experiments — all without shipping a new build. Parameters (key + value) and their targeting
conditions are defined entirely on the Apptics console; in code you just read the value by its key and
supply custom attributes the console can target on.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html).

Import the remote config API and use the `AppticsRemoteConfig.instance` singleton:

```dart
import 'package:apptics_flutter/remoteconfig/apptics_remote_config.dart';
```

The sample app demonstrates all of this in `lib/screens/remote_config_screen.dart`.

---

## Read a value

`getStringValue` fetches a parameter by the key you configured on the console and returns a
`Future<String?>`. Two optional named flags control how the value is sourced:

- `coldFetch` — defaults to `false`, which serves the cached value. Set it to `true` to force a fresh
  fetch from the server instead of reading from the cache.
- `fallbackWithOfflineValue` — defaults to `false`. Set it to `true` to fall back to the last cached
  (offline) value when a server fetch isn't possible. Android only.

An unknown key — one that isn't configured on the console — resolves to `null`. Because the result is
nullable, always supply a default with `?? 'default'` so your UI never depends on a non-null value.

```dart
final value = await AppticsRemoteConfig.instance.getStringValue(
  'color',
  coldFetch: false,
  fallbackWithOfflineValue: false,
);

// Unknown keys return null — fall back to a sensible default.
final color = value ?? 'default';
// use `color` to drive UI
```

| Method | What it does |
|--------|--------------|
| `Future<String?> getStringValue(String key, {bool coldFetch = false, bool fallbackWithOfflineValue = false})` | Fetches the console-configured value for `key`. `coldFetch: true` forces a fresh server fetch (otherwise the cache is used); `fallbackWithOfflineValue: true` falls back to the last cached value when offline (Android only). Returns `null` for an unconfigured key. |

---

## Custom condition values

Console conditions can target a parameter on app-supplied attributes (for example, a user tier).
`setCustomConditionValue` registers such an attribute as a key/value pair so the console can evaluate
conditions against it when serving a parameter. Set the relevant conditions before reading the value
they influence.

```dart
await AppticsRemoteConfig.instance.setCustomConditionValue('user_tier', 'premium');

// A subsequent read can now be targeted by the `user_tier` condition.
final value = await AppticsRemoteConfig.instance.getStringValue('color');
final color = value ?? 'default';
```

| Method | What it does |
|--------|--------------|
| `Future<void> setCustomConditionValue(String key, String value)` | Registers an app-side attribute (`key` = `value`) the console can use to evaluate targeting conditions for parameters. |

---

## Reset

`hardReset` clears the cached remote config state and restores defaults. Useful when testing, or when
you need to discard any cached values and start clean.

```dart
await AppticsRemoteConfig.instance.hardReset();
```

| Method | What it does |
|--------|--------------|
| `Future<void> hardReset()` | Clears cached config and restores defaults. |

---

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-remote_configuration.html>
