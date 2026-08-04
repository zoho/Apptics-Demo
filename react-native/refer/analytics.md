Analytics is the core of the Apptics React Native library. It lets you fire custom events with
optional properties, track which screens users visit, and flush the upload queue on demand. Each
event has a **name** and a **group**; properties are sent as a plain object for richer reporting on
the dashboard.


---

## Start the SDK

`init` starts the session/analytics engine.

```ts
import {Apptics} from '@zoho_apptics/apptics-react-native';

// Automatic native screen tracking ON (the default).
Apptics.init();

// OFF — you will report screens yourself with screenAttached/screenDetached.
Apptics.init(false);
```

Call it once, as early as possible. This sample calls it from `src/core/appticsBootstrap.ts`, which
runs at module scope in `App.tsx` — before the first render.

| Method | What it does |
|--------|--------------|
| `init(enableAutomaticScreenTracking?: boolean)` | Starts the SDK. Defaults to `true`. |

---

## Record a custom event

Every event needs a **name**, a **group**, and a **properties** object. Unlike some other Apptics
SDKs the properties argument is not optional — pass `{}` when you have nothing to attach.

```ts
// Simplest form.
Apptics.addEvent('button_tapped', 'home', {});

// With custom properties.
Apptics.addEvent('upgrade_clicked', 'billing', {
  source: 'sample-app',
  plan: 'pro',
  seats: 5,
  trial: false,
});
```

Property values may be **strings, numbers or booleans**.

| Method | What it does |
|--------|--------------|
| `addEvent(eventName, eventGroup, properties)` | Records a custom event by name + group, with properties. |

---

## Use the AppticsDefinedEvents constants

Apptics ships predefined event and group names so you don't hand-type strings for common lifecycle
events. Each group constant (e.g. `AP_APP_LIFE_CYCLE`) pairs with its event constants (e.g.
`AP_APP_OPEN`).

```ts
import {Apptics, AppticsDefinedEvents} from '@zoho_apptics/apptics-react-native';

Apptics.addEvent(
  AppticsDefinedEvents.AP_APP_OPEN,
  AppticsDefinedEvents.AP_APP_LIFE_CYCLE,
  {},
);

Apptics.addEvent(
  AppticsDefinedEvents.AP_USER_LOGIN,
  AppticsDefinedEvents.AP_USER_LIFE_CYCLE,
  {method: 'sso'},
);
```

| Group constant | Example event constants |
|---|---|
| `AP_APP_LIFE_CYCLE` | `AP_APP_INSTALL`, `AP_APP_OPEN`, `AP_APP_FOREGROUND`, `AP_APP_BACKGROUND`, `AP_APP_TERMINATE` |
| `AP_APPLICATION` | `AP_DEEP_LINK_OPEN`, `AP_IN_APP_PURCHASE`, `AP_SEARCH`, `AP_SHARE`, `AP_BATTERY_LOW` |
| `AP_USER_LIFE_CYCLE` | `AP_USER_SIGNUP`, `AP_USER_LOGIN`, `AP_USER_LOGOUT` |
| `AP_OS` | `AP_OS_UNSUPPORTED`, `AP_OS_UPDATE` |
| `AP_OTHERS` | `AP_NETWORK_REACHABILITY_CHANGE`, `AP_SWITCH_THEME_DARK`, `AP_SWITCH_ORIENTATION_PORTRAIT` |

---

## Screen tracking

Pair `screenAttached` (when the screen appears) with `screenDetached` (when it leaves) to measure
screen views and dwell time. Both take the screen name as a string.

```ts
import {useFocusEffect} from '@react-navigation/native';
import {Apptics} from '@zoho_apptics/apptics-react-native';
import {useCallback} from 'react';

export function useScreenTracking(screenName: string) {
  useFocusEffect(
    useCallback(() => {
      Apptics.screenAttached(screenName);
      return () => Apptics.screenDetached(screenName);
    }, [screenName]),
  );
}
```

```ts
// In any screen:
useScreenTracking('CheckoutScreen');
```

| Method | What it does |
|--------|--------------|
| `screenAttached(screenName)` | Marks the start of a screen view. |
| `screenDetached(screenName)` | Marks the end of a screen view. |

Note the platform difference the library hides for you: on Android these map to
`trackScreenAttached`/`trackScreenDetached`, on iOS to `trackViewEnter`/`trackViewExit`.

---

## Flush the queue on demand

Events are batched and uploaded on the SDK's own sync schedule. Call `flush` to force an immediate
upload — useful just before logout, or right after reporting a non-fatal you want to see now.

```ts
Apptics.flush();
```

| Method | What it does |
|--------|--------------|
| `flush()` | Uploads queued events / screens / sessions immediately. |

---

## Notes

- Choose stable, low-cardinality values for the event name and group so events aggregate cleanly on
  the dashboard. Put high-cardinality data (IDs, free-form text) into properties instead.
- Property values must be `string`, `number` or `boolean`.
- Analytics is gated by the tracking state — see [privacy.md](privacy.md). Under
  `OnlyCrashTracking*` or `NoTracking`, events are not collected at all.
