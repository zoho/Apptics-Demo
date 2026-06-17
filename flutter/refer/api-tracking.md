API Tracking is part of the Apptics Flutter plugin (0.0.14+). It measures the latency, status
codes and failures of your network calls and reports them to the Apptics dashboard. The plugin
offers four integration strategies — pick the one that matches how your app makes HTTP requests.
For the simplest setup, enable automatic tracking once at startup; for finer control, wrap your
`http.Client`, add a `dio` interceptor, or bracket calls manually. All strategies funnel through
`AppticsApiTracker`, so URL exclusion rules apply uniformly. The sample screen
`lib/screens/api_tracking_screen.dart` demonstrates each approach with live requests.

The relevant imports are:

```dart
import 'package:apptics_flutter/api_tracker/apptics_api_tracker.dart';
import 'package:apptics_flutter/api_tracker/apptics_http_client.dart';
import 'package:apptics_flutter/api_tracker/apptics_dio_interceptor.dart';
```

---

## Automatic tracking via `enableAutoTracking()`

The zero-per-call approach. `AppticsApiTracker.instance.enableAutoTracking()` installs a global
[`HttpOverrides`], so every HTTP call made through `dart:io`'s `HttpClient` is tracked
automatically. This captures requests from the `http` package, Dio (when using the default
adapter), and any library built on `dart:io` — no changes at the call site.

Call `enableAutoTracking()` once during app startup, before any network requests are made. Because
only one global `HttpOverrides` can be active at a time, the tracker remembers the previous
overrides and restores them when you call `disableAutoTracking()`. If another library also sets
`HttpOverrides.global`, there may be conflicts.

```dart
import 'package:apptics_flutter/api_tracker/apptics_api_tracker.dart';

void main() {
  // Install the global HttpOverrides before running the app.
  AppticsApiTracker.instance.enableAutoTracking();

  runApp(const MyApp());
}

// Elsewhere, query or toggle tracking at runtime:
final bool on = AppticsApiTracker.instance.isAutoTrackingEnabled;
AppticsApiTracker.instance.disableAutoTracking(); // restores previous overrides
```

| Method | Signature | Description |
|---|---|---|
| `enableAutoTracking` | `void enableAutoTracking()` | Installs a global `HttpOverrides` so all `dart:io` HTTP calls are tracked. No-op if already enabled. |
| `disableAutoTracking` | `void disableAutoTracking()` | Removes the override and restores the previous `HttpOverrides`, if any. No-op if not enabled. |
| `isAutoTrackingEnabled` | `bool get isAutoTrackingEnabled` | Whether automatic tracking is currently enabled. |

---

## `AppticsHttpClient` — wrapping `package:http`

If you use the `http` package and prefer explicit, scoped tracking over a global override, wrap
your `http.Client` in an `AppticsHttpClient`. Every request sent through it is tracked: the timer
starts before the request leaves and ends when the response (or error) arrives, so latency is
measured accurately. On error the exception message is recorded and the error is re-thrown, leaving
your existing error handling unaffected.

`AppticsHttpClient` extends `http.BaseClient`, so it is a drop-in replacement supporting `get`,
`post`, `send`, and the rest of the `http.Client` API. By default it uses the shared
`AppticsApiTracker.instance`; pass a `tracker` if you need a custom one.

```dart
import 'package:http/http.dart' as http;
import 'package:apptics_flutter/api_tracker/apptics_http_client.dart';

final client = AppticsHttpClient(http.Client());
try {
  final response = await client.get(
    Uri.parse('https://jsonplaceholder.typicode.com/todos/1'),
  );
  print('HTTP ${response.statusCode}');
} finally {
  client.close();
}
```

| Member | Signature | Description |
|---|---|---|
| Constructor | `AppticsHttpClient(http.Client inner, {AppticsApiTracker? tracker})` | Wraps `inner`; defaults to `AppticsApiTracker.instance` when `tracker` is omitted. |
| `send` | `Future<http.StreamedResponse> send(http.BaseRequest request)` | Tracks the request, forwards it to the inner client, and records the status code (or error message on failure, then rethrows). |
| `close` | `void close()` | Closes the wrapped inner client. |

---

## `AppticsDioInterceptor` — for the `dio` package

If your app uses [`dio`], add an `AppticsDioInterceptor`. Because `dio` is not a dependency of the
plugin, the interceptor exposes three handler methods (`onRequest`, `onResponse`, `onError`) that
you wire into Dio via an `InterceptorsWrapper`. The interceptor starts tracking on each request,
records the status code on response, and records the status code plus error message on failure.
Add `dio: ^5.0.0` to your app's `pubspec.yaml`.

```dart
import 'package:dio/dio.dart';
import 'package:apptics_flutter/api_tracker/apptics_dio_interceptor.dart';

final appticsInterceptor = AppticsDioInterceptor();
final dio = Dio();
dio.interceptors.add(
  InterceptorsWrapper(
    onRequest: appticsInterceptor.onRequest,
    onResponse: appticsInterceptor.onResponse,
    onError: appticsInterceptor.onError,
  ),
);

// Every call through this Dio instance is now tracked.
final response = await dio.get('https://api.example.com/data');
```

| Member | Signature | Description |
|---|---|---|
| Constructor | `AppticsDioInterceptor({AppticsApiTracker? tracker})` | Defaults to `AppticsApiTracker.instance` when `tracker` is omitted. |
| `onRequest` | `void onRequest(dynamic options, dynamic handler)` | Starts tracking using `options.uri` and `options.method`, then calls `handler.next(options)`. |
| `onResponse` | `void onResponse(dynamic response, dynamic handler)` | Ends tracking with `response.statusCode`, then calls `handler.next(response)`. |
| `onError` | `void onError(dynamic error, dynamic handler)` | Ends tracking with the error's status code and message, then calls `handler.next(error)`. |

---

## Manual tracking with `startApiTracking` / `endApiTracking`

For custom transports — or any case the wrappers don't cover — bracket your network call with
`startApiTracking` and `endApiTracking`. `startApiTracking` returns a `Future<String?>` resolving
to the `trackId` issued by the native SDK, or `null` if the call could not be tracked (URL
excluded, native error, or framework unavailable). Pass that `trackId` to `endApiTracking` once the
response arrives. The native SDK measures latency between the two calls, so call `start` at the
real request begin. `endApiTracking` is a no-op when `trackId` is `null`, so no guard code is
needed.

```dart
import 'package:apptics_flutter/api_tracker/apptics_api_tracker.dart';

final tracker = AppticsApiTracker.instance;

final trackId = await tracker.startApiTracking(
  url: 'https://api.example.com/orders',
  method: 'POST',
);

// ... make your real network request here ...

await tracker.endApiTracking(
  trackId: trackId,
  statusCode: 201,
);
```

When timing isn't available — for example recording an already-completed call — use the
`trackApiCall` convenience, which runs `start` and `end` back-to-back (so the SDK records ~0ms
latency). Use one of the auto-tracking strategies above if you need real latency.

```dart
await AppticsApiTracker.instance.trackApiCall(
  url: 'https://api.example.com/profile',
  method: 'GET',
  statusCode: 500,
  errorMessage: 'Internal Server Error',
);
```

| Method | Signature | Description |
|---|---|---|
| `startApiTracking` | `Future<String?> startApiTracking({required String url, required String method})` | Starts a tracking span; resolves to the `trackId`, or `null` if the URL is excluded or tracking can't start. |
| `endApiTracking` | `Future<void> endApiTracking({required String? trackId, int? statusCode, String? errorMessage})` | Closes the span opened by `startApiTracking`. No-op when `trackId` is `null`. |
| `trackApiCall` | `Future<void> trackApiCall({required String url, required String method, int? statusCode, String? errorMessage})` | Convenience that calls `start` then `end` back-to-back; records ~0ms latency. |
| `instance` | `static AppticsApiTracker get instance` | The shared singleton tracker used by all strategies. |

---

## Excluding URLs with `excludedUrlPatterns`

`AppticsApiTracker` exposes a `Set<String>` of URL patterns to exclude from tracking. If a request
URL *contains* any string in the set (substring match), that call is skipped. This applies across
all four strategies, since they all route through the shared tracker — useful for keeping noisy or
internal endpoints (health checks, analytics, your own logging) out of reports.

Configure the set once at startup, before requests are made.

```dart
import 'package:apptics_flutter/api_tracker/apptics_api_tracker.dart';

// Any URL containing one of these substrings is not tracked.
AppticsApiTracker.instance.excludedUrlPatterns.add('/healthz');
AppticsApiTracker.instance.excludedUrlPatterns.addAll({
  'analytics.example.com',
  '/internal/',
});
```

| Member | Signature | Description |
|---|---|---|
| `excludedUrlPatterns` | `Set<String> excludedUrlPatterns` | URL substrings to skip. A request is excluded if its URL contains any pattern in the set. |

---

For more details, see the [Apptics documentation](https://www.apptics.com/docs).
