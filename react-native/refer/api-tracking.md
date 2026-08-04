API tracking measures the latency, status codes and failure rate of your app's network calls, so
slow or failing endpoints show up next to the screens and versions they affect.

The React Native implementation is built around `fetch`. There are three ways to use it — pick the
one that matches your networking setup. All three respect the same URL exclusion list.

See the sample usage in `src/screens/ApiTrackingScreen.tsx`.

---

## 1 · Auto tracking (patch the global fetch)

```ts
import {AppticsApiTracker} from '@zoho_apptics/apptics-react-native';

AppticsApiTracker.enableAutoTracking();
```

This replaces `globalThis.fetch` with a tracked wrapper, so every `fetch` in your app — **and in any
library built on `fetch`** (axios' fetch adapter, Apollo, tRPC, …) — is measured with no per-call
code.

```ts
AppticsApiTracker.disableAutoTracking();          // restores the original fetch
AppticsApiTracker.isAutoTrackingEnabled();        // boolean, not a Promise
```

| Method | What it does |
|--------|--------------|
| `enableAutoTracking()` | Patches `globalThis.fetch`. Idempotent. |
| `disableAutoTracking()` | Restores the original `fetch`. |
| `isAutoTrackingEnabled(): boolean` | Current status. |

Enable it once at startup, before anything else can capture a reference to `fetch`. Libraries that
grab `fetch` at import time will keep the untracked version.

Note this tracks `fetch` only — `XMLHttpRequest` and native networking are not intercepted.

---

## 2 · Explicit wrappers

Leave the global `fetch` alone and opt in per call, or build a reusable client.

```ts
// One call.
const res = await AppticsApiTracker.trackedFetch('https://api.example.com/items', {
  method: 'GET',
});

// A reusable client to pass into your networking layer.
const apiFetch = AppticsApiTracker.createAppticsHttpClient(fetch);
const res2 = await apiFetch('https://api.example.com/items');
```

| Method | What it does |
|--------|--------------|
| `trackedFetch(input, init?)` | A tracked one-off request. Same signature as `fetch`. |
| `createAppticsHttpClient(baseFetch?)` | Returns a `fetch`-compatible function that tracks every call. |

`createAppticsHttpClient` takes the base `fetch` to wrap, which is useful when you already have your
own instrumented or retrying fetch — pass it in and both layers apply.

Both record the response status on success, and the error message on failure, before rethrowing so
your own error handling is unchanged.

---

## 3 · Manual tracking

For anything that is not `fetch` — a native networking module, a WebSocket handshake, gRPC — bracket
the call yourself.

```ts
const trackId = await AppticsApiTracker.startApiTracking({
  url: 'https://api.example.com/orders',
  method: 'POST',
});

try {
  const res = await doRequest();
  if (trackId) {
    await AppticsApiTracker.endApiTracking({trackId, statusCode: res.status});
  }
} catch (e) {
  if (trackId) {
    await AppticsApiTracker.endApiTracking({trackId, errorMessage: (e as Error).message});
  }
}
```

| Method | What it does |
|--------|--------------|
| `startApiTracking({url, method}): Promise<string \| null>` | Starts the timer, returns a track id. |
| `endApiTracking({trackId, statusCode?, errorMessage?})` | Stops it and records the outcome. |

**`startApiTracking` resolves to `null`** when the URL matches an exclusion pattern or the native SDK
declines to track the call. Always check before calling `endApiTracking` — the sample screen shows
the guard.

---

## URL exclusion

Any URL **containing** one of these substrings is skipped by all three modes.

```ts
AppticsApiTracker.excludedUrlPatterns.add('/healthz');
AppticsApiTracker.excludedUrlPatterns.add('analytics.internal');
AppticsApiTracker.excludedUrlPatterns.clear();
```

`excludedUrlPatterns` is a plain `Set<string>`, so `add`, `delete`, `has` and `clear` all work.
Matching is a simple substring test, not a glob or a regex.

---

## Notes

- API tracking is *usage* data, so it obeys the tracking state — nothing is recorded under
  `OnlyCrashTracking*` or `NoTracking`. See [privacy.md](privacy.md).
- Results appear under **API** on the Apptics console, grouped by endpoint with latency percentiles
  and error rates.

