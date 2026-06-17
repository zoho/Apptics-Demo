Analytics is part of the Apptics Flutter plugin. It lets you fire custom events with optional
properties, track which screens users visit, choose the language used by the Analytics SDK, and
flush the upload queue on demand. Each event has a **name** and a **group**; properties are sent as
a `Map<String, dynamic>` for richer reporting on the dashboard.

All calls go through the singleton `AppticsFlutter.instance` and return a `Future<void>`, so you can
`await` them. See the sample usage in `lib/screens/analytics_screen.dart`.

---

## Record a custom event

Use `AppticsFlutter.instance.addEvent`. Every event needs a **name** (`event`) and a **group**
(`group`); both are positional. The simplest call sends just the name + group; the optional named
`properties` argument attaches a map of custom key/value pairs.

```dart
// Simplest form — just a name + group.
await AppticsFlutter.instance.addEvent("button_tapped", "home");

// With custom properties attached as a map.
await AppticsFlutter.instance.addEvent(
  "button_tapped",
  "home",
  properties: {"source": "sample-app"},
);
```

`properties` is a `Map<String, dynamic>` — use `String`, `num` (int/double), or `bool` values. Build
the payload from any key/value pairs you want to report:

```dart
await AppticsFlutter.instance.addEvent(
  "upgrade_clicked",
  "billing",
  properties: {
    "source": "sample-app",
    "plan": "pro",
    "seats": 5,
    "trial": false,
  },
);
```

| Method | What it does |
|--------|--------------|
| `addEvent(String? event, String? group)` | Records a custom event by name + group. |
| `addEvent(String? event, String? group, {Map<String, dynamic>? properties})` | Same, with a map of custom properties. |

---

## Use the DefinedEvents constants

Apptics ships a `DefinedEvents` class of predefined event and group names so you don't have to hand-
type the strings for common lifecycle events. Each group constant (e.g. `AP_APP_LIFE_CYCLE`) pairs
with its event constants (e.g. `AP_APP_OPEN`). Pass them straight into `addEvent`.

```dart
import 'package:apptics_flutter/defined_events.dart';

// App lifecycle event.
await AppticsFlutter.instance.addEvent(
  DefinedEvents.AP_APP_OPEN,
  DefinedEvents.AP_APP_LIFE_CYCLE,
);

// User lifecycle event with properties.
await AppticsFlutter.instance.addEvent(
  DefinedEvents.AP_USER_LOGIN,
  DefinedEvents.AP_USER_LIFE_CYCLE,
  properties: {"method": "sso"},
);
```

Available groups include `AP_APP_LIFE_CYCLE`, `AP_APPLICATION`, `AP_USER_LIFE_CYCLE`, `AP_OS`, and
`AP_OTHERS`, each with its own set of event constants.

| Constant family | Example values |
|---|---|
| `DefinedEvents.AP_APP_LIFE_CYCLE` (group) | `AP_APP_INSTALL`, `AP_APP_OPEN`, `AP_APP_FOREGROUND`, `AP_APP_BACKGROUND`, `AP_APP_TERMINATE` |
| `DefinedEvents.AP_APPLICATION` (group) | `AP_DEEP_LINK_OPEN`, `AP_IN_APP_PURCHASE`, `AP_SEARCH`, `AP_SHARE`, `AP_BATTERY_LOW` |
| `DefinedEvents.AP_USER_LIFE_CYCLE` (group) | `AP_USER_SIGNUP`, `AP_USER_LOGIN`, `AP_USER_LOGOUT` |
| `DefinedEvents.AP_OS` (group) | `AP_OS_UNSUPPORTED`, `AP_OS_UPDATE` |
| `DefinedEvents.AP_OTHERS` (group) | `AP_NETWORK_REACHABILITY_CHANGE`, `AP_SWITCH_THEME_DARK`, `AP_SWITCH_ORIENTATION_PORTRAIT` |

---

## Screen tracking

Track how long users spend on a screen by pairing `screenAttached` (when the screen appears) with
`screenDetached` (when it leaves). Both take the screen name as a `String`. A common pattern is to
call them from a widget's `initState` / `dispose`.

```dart
class AnalyticsScreen extends StatefulWidget {
  // ...
}

class _AnalyticsScreenState extends State<AnalyticsScreen> {
  @override
  void initState() {
    super.initState();
    AppticsFlutter.instance.screenAttached("AnalyticsScreen");
  }

  @override
  void dispose() {
    AppticsFlutter.instance.screenDetached("AnalyticsScreen");
    super.dispose();
  }

  // ...
}
```

| Method | What it does |
|--------|--------------|
| `screenAttached(String screenName)` | Marks the start of a screen view (e.g. on appear). |
| `screenDetached(String screenName)` | Marks the end of a screen view (e.g. on disappear). |

---

## Set the default language

Use `setDefaultLanguage` to tell the Analytics SDK which language to use. Pass a language **code**
as a `String`. If the language isn't found in the resource bundle, the default language is selected.

```dart
await AppticsFlutter.instance.setDefaultLanguage("en");
```

| Method | What it does |
|--------|--------------|
| `setDefaultLanguage(String lang)` | Sets the language (code) used by the Analytics SDK. |

---

## Flush the queue on demand

Events are queued and uploaded during the SDK's normal sync window. Call `flush` to force queued
events / engagement data to upload immediately — useful, for example, just before logout.

```dart
await AppticsFlutter.instance.flush();
```

| Method | What it does |
|--------|--------------|
| `flush()` | Uploads queued events / engagement data immediately. |

---

## Notes

- Choose stable, low-cardinality values for `event` and `group` so events aggregate cleanly on the
  dashboard. Put high-cardinality data (IDs, free-form text) into `properties` instead.
- `properties` is a `Map<String, dynamic>`; keep values to `String`, `num`, and `bool`.
- `flush` is for on-demand uploads; the SDK still uploads automatically on its own schedule, so you
  don't need to call it after every event.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-in_app_event.html>
