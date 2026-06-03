Analytics is part of the Apptics Analytics SDK. It lets you fire custom events with optional
properties, track which screens users visit, flush the upload queue on demand, and surface
Apptics' privacy / consent screens. Each event has a **name** and a **group**; properties are sent
as a JSON object for richer reporting on the dashboard.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html).

---

## Add the Analytics SDK

Using Apptics BoM (recommended):

```groovy
dependencies {
    implementation platform('com.zoho.apptics:apptics-bom:[latest-version]')
    implementation 'com.zoho.apptics:apptics-analytics'
}
```

Without BoM:

```groovy
dependencies {
    implementation 'com.zoho.apptics:apptics-analytics:[latest-version]'
}
```

`AppticsAnalytics` and `AppticsEvents` are ready to use as soon as Apptics is initialized in
`Application.onCreate()`:

```kotlin
Apptics.init(this)
```

No additional Analytics-specific initialization is required.

---

## Record a custom event

Use `AppticsEvents.addEvent`. Every event needs a **name** and a **group**. The two-arg overload
sends just the name + group; the three-arg overload attaches a JSON object of custom properties.

```kotlin
// Kotlin
import com.zoho.apptics.analytics.AppticsEvents
import org.json.JSONObject

// Simplest form — just a name + group.
AppticsEvents.addEvent(eventName = "button_tapped", eventGroup = "home")

// With custom properties attached as a JSON payload.
AppticsEvents.addEvent(
    eventName = "button_tapped",
    eventGroup = "home",
    customProperties = JSONObject().put("source", "sample-app")
)
```

```java
// Java
import com.zoho.apptics.analytics.AppticsEvents;
import org.json.JSONObject;

// Simplest form — just a name + group.
AppticsEvents.INSTANCE.addEvent("button_tapped", "home");

// With custom properties attached as a JSON payload.
JSONObject props = new JSONObject();
props.put("source", "sample-app");
AppticsEvents.INSTANCE.addEvent("button_tapped", "home", props);
```

Build the `customProperties` payload from any key/value pairs you want to report:

```kotlin
val json = JSONObject().apply {
    put("source", "sample-app")
    put("plan", "pro")
}
AppticsEvents.addEvent(
    eventName = "upgrade_clicked",
    eventGroup = "billing",
    customProperties = json
)
```

---

## Flush the queue on demand

Events are queued and uploaded during the SDK's normal sync window. Call `flush` to force queued
events / engagement data to upload immediately:

```kotlin
// Kotlin
AppticsAnalytics.flush()
```

```java
// Java
AppticsAnalytics.INSTANCE.flush();
```

---

## Consent & tracking-state controls

Apptics ships its own privacy / consent screens so users can review and toggle what is tracked.
Both calls require an `Activity`.

```kotlin
// Kotlin
import com.zoho.apptics.analytics.AppticsAnalytics

// Opens the screen where users toggle analytics / crash / personal-data tracking categories.
AppticsAnalytics.openSettings(activity)

// Shows a consent dialog to review tracking preferences.
// showOnlyOnce = true prevents it from reappearing once acknowledged.
AppticsAnalytics.showReviewTrackingSettingsPopup(activity, showOnlyOnce = true)
```

```java
// Java
AppticsAnalytics.INSTANCE.openSettings(activity);
AppticsAnalytics.INSTANCE.showReviewTrackingSettingsPopup(activity, true);
```

From a Compose screen, get the `Activity` from the current context before calling either method:

```kotlin
val context = LocalContext.current
(context as? Activity)?.let { AppticsAnalytics.openSettings(it) }
(context as? Activity)?.let {
    AppticsAnalytics.showReviewTrackingSettingsPopup(it, showOnlyOnce = true)
}
```

---

## Options / configuration

| Option | How |
|---|---|
| Attach custom properties to an event | Pass a `JSONObject` as `customProperties` to `addEvent`. |
| Upload queued events now | `AppticsAnalytics.flush()` — don't wait for the next sync window. |
| Let users manage tracking categories | `AppticsAnalytics.openSettings(activity)` opens the analytics / crash / personal-data toggles. |
| Ask users to review consent | `AppticsAnalytics.showReviewTrackingSettingsPopup(activity, showOnlyOnce)`; set `showOnlyOnce = true` to show it at most once. |

---

## API reference

| Method | What it does |
|--------|--------------|
| `AppticsEvents.addEvent(eventName, eventGroup)` | Records a custom event by name + group. |
| `AppticsEvents.addEvent(eventName, eventGroup, customProperties)` | Same, with a JSON object of custom properties. |
| `AppticsAnalytics.flush()` | Uploads queued events / engagement data immediately. |
| `AppticsAnalytics.openSettings(activity)` | Opens the screen where users toggle analytics / crash / personal-data tracking categories. |
| `AppticsAnalytics.showReviewTrackingSettingsPopup(activity, showOnlyOnce)` | Shows a consent dialog to review tracking preferences. `showOnlyOnce = true` prevents it from reappearing once acknowledged. |

---

## Notes

- Choose stable, low-cardinality values for `eventName` and `eventGroup` so events aggregate cleanly
  on the dashboard. Put high-cardinality data (IDs, free-form text) in `customProperties` instead.
- `flush` is for on-demand uploads (e.g. just before logout); the SDK still uploads automatically on
  its own schedule, so you don't need to call it after every event.
- For privacy / consent handling — what the tracking-state screens control and how user choices
  affect data collection — see the
  [Consent docs](https://www.zoho.com/apptics/resources/SDK/android-consent.html).

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-in_app_event.html>
