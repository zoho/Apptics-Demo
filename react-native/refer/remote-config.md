Remote config serves values from the Apptics console to your app at runtime, so you can flip a
feature, change a threshold or run an experiment without shipping a build.

See the sample usage in `src/screens/RemoteConfigScreen.tsx`.

---

## Read a value

```ts
import {AppticsRemoteConfig} from '@zoho_apptics/apptics-react-native';

const value = await AppticsRemoteConfig.getStringValue(
  'checkout_variant',  // key — the parameter name from the console
  false,               // coldFetch
  true,                // fallbackWithOfflineValue
);
```

| Parameter | Meaning |
|---|---|
| `key` | The parameter name configured on the console. |
| `coldFetch` | Bypass the cache and hit the network. |
| `fallbackWithOfflineValue` | Return the last fetched value if the network fails. |

| Method | What it does |
|--------|--------------|
| `getStringValue(key, coldFetch, fallbackWithOfflineValue): Promise<string>` | Resolves the value for a parameter. |

**Values always come back as strings.** Parse and validate on your side:

```ts
const raw = await AppticsRemoteConfig.getStringValue('max_retries', false, true);
const maxRetries = Number.parseInt(raw, 10);
const safeMaxRetries = Number.isFinite(maxRetries) ? maxRetries : 3;
```

---

## Handle the null case

An unknown key, a throttled request, or a network failure with no offline fallback all resolve to
`null` — even though the type says `Promise<string>`. Treat a default as mandatory, not optional:

```ts
const variant = (await AppticsRemoteConfig.getStringValue('checkout_variant', false, true)) ?? 'control';
```

---

## Caching and throttling

`getStringValue` caches, so repeated reads do not each cost a network call. `coldFetch: true`
bypasses the cache — but **only 3 network calls per minute** are allowed. Past that threshold the
call returns `null`, or the offline value if `fallbackWithOfflineValue` is `true`.

Practically: use `coldFetch: false` everywhere except a deliberate "check for changes now" action,
and keep `fallbackWithOfflineValue: true` so a flaky connection does not reset your users to
defaults.

---

## Custom conditions

Conditions let the console target different values at different users. Built-in conditions (app
version, OS, country) are evaluated by the SDK; **custom** conditions are matched against values
only your app knows.

```ts
AppticsRemoteConfig.setCustomCondition('user_tier', 'premium');

// Read after setting, so the condition is applied.
const banner = await AppticsRemoteConfig.getStringValue('promo_banner', false, true);
```

| Method | What it does |
|--------|--------------|
| `setCustomCondition(conditionKey, conditionValue)` | Supplies an app-side attribute for targeting. |

Set the conditions you need as soon as the relevant state is known — after login, after the
subscription is resolved — and before the reads that depend on them.

---

## Console setup

**Developer → Remote config** → add parameters with default values, then add conditions and
per-condition values. The key you pass to `getStringValue` is the parameter name exactly as it
appears there.

---

## Notes

- Do not put secrets in remote config. It is configuration, not a secure store.
- Read once and hold the result in state for the lifetime of a screen rather than calling on every
  render.

---

## Compared with the Flutter plugin

| Flutter | React Native |
|---|---|
| `getStringValue(key, coldFetch:, fallbackWithOfflineValue:)` — named, optional | `getStringValue(key, coldFetch, fallback)` — positional, **all required** |
| `setCustomConditionValue(k, v)` | `setCustomCondition(k, v)` |
| `hardReset()` | not exposed — no way to clear the cache from JS |

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/>
