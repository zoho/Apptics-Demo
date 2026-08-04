The remote logger streams application logs to Apptics in real time, so you can debug an issue on a
device you do not have in your hand. 

See the sample usage in `src/screens/RemoteLoggerScreen.tsx`.

---

## Enable it

The logger is **disabled by default** and the setting persists across launches, so turn it on
deliberately — for a beta channel, for an internal build, or from a support toggle.

```ts
import {APLogger} from '@zoho_apptics/apptics-react-native';

APLogger.enable();
APLogger.disable();

const on = await APLogger.isEnabled();  // Promise<boolean>
```

| Method | What it does |
|--------|--------------|
| `enable()` | Starts sending logs to Apptics. |
| `disable()` | Stops sending. |
| `isEnabled(): Promise<boolean>` | Reads the current status. |

---

## Write log lines

```ts
APLogger.log('Checkout started');    // verbose
APLogger.debug('Cart hydrated');
APLogger.info('User signed in');
APLogger.warn('Retrying payment');
APLogger.error('Payment failed');
```

| Method | Level |
|--------|-------|
| `log(...msgs)` | verbose |
| `debug(...msgs)` | debug |
| `info(...msgs)` | info |
| `warn(...msgs)` | warning |
| `error(...msgs)` | error |


```ts
APLogger.log('Checkout started', ['cart', 'checkout'], {plan: 'Premium', seats: 25});
APLogger.error(new Error('Payment gateway timed out'));   // logged by its message
```

Objects are JSON-stringified, `Error`s are reduced to their message, and functions are shown as
`[function name()]`.

---

## Debug builds print instead of sending

In debug builds (`__DEV__`) the lines go to the Metro console with a `method@line:column` prefix and
nothing is uploaded. Call `Apptics.enableDevTesting()` *before* logging if you want to exercise the
upload path from a debug build — the same switch used by crash tracking
([crash-tracking.md](crash-tracking.md)).

