API Tracking is part of the Apptics Analytics SDK. It measures the success rate and response time
of your network calls and reports them to the Apptics dashboard.

**What's new:** API tracking is now fully automatic — no web console registration or annotations
required. Add the interceptor once and every OkHttp request is tracked automatically. Dynamic path
segments (numeric IDs, UUIDs, JWT tokens) are normalized to `*` so `/users/123` and `/users/456`
are grouped as a single `/users/*` endpoint in your dashboard.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://git.csez.zohocorpin.com/greengarage/apptics-android-sdk/-/blob/master/docs/getting_started.md).

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

Initialize Analytics in `Application.onCreate()`:

```kotlin
AppticsAnalytics.init(this)
```

---

## Automatic tracking with OkHttp / Retrofit

Add `AppticsApiTrackingInterceptor` to your `OkHttpClient`. That's all — every request made
through this client is tracked automatically.

```kotlin
// Kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(AppticsApiTrackingInterceptor())
    .build()
```

```java
// Java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new AppticsApiTrackingInterceptor())
    .build();
```

The interceptor:
- Starts a timer before the request is sent.
- Records the response code and message when the response arrives.
- Records response code `0` with the exception message if a network error occurs, and re-throws the
  exception so your existing error handling is unaffected.
- Silently skips URLs that are filtered out by your domain or endpoint rules (see
  [Filtering](#filtering-which-apis-are-tracked) below).

> **Migration note:** The `@TrackApiWith` annotation and numeric `apiId` parameter are no longer
> required. Remove any existing `@TrackApiWith` annotations — the interceptor now tracks all
> requests without them.

---

## Endpoint normalization

Before recording a request, the SDK normalizes the URL path to group dynamic segments together.

| Raw URL | Normalized path |
|---|---|
| `GET /users/42/orders` | `/users/*/orders` |
| `GET /users/550e8400-e29b-41d4-a716-446655440000/profile` | `/users/*/profile` |
| `DELETE /sessions/eyJhbGci...` | `/sessions/*` |
| `GET /products/search` | `/products/search` _(static, unchanged)_ |

The following segment types are detected automatically and replaced with `*`:

- **Numeric IDs** — pure digit sequences (e.g. `42`, `1000203`)
- **UUIDs** — standard 8-4-4-4-12 hex format
- **JWT tokens** — three base64url segments separated by dots

Query strings are stripped before recording.

---

## Filtering which APIs are tracked

By default, all requests through the interceptor are tracked. Use `AppticsApiTracker.configure`
to restrict or customize what gets tracked.

Call `configure` once during app startup, before any network requests are made (e.g. in
`Application.onCreate()` after `AppticsAnalytics.init()`).

### Ignore specific third-party domains

```kotlin
AppticsApiTracker.configure {
    ignoreDomains("crashlytics.com", "analytics.google.com")
}
```

### Track only your own APIs (allow-list)

```kotlin
AppticsApiTracker.configure {
    allowOnlyDomains("api.yourapp.com", "cdn.yourapp.com")
}
```

Any domain not in the list is silently skipped.

### Filter by top-level domain (TLD)

Track only requests to `.com` and `.io` domains:

```kotlin
AppticsApiTracker.configure {
    allowOnlyTLDs(".com", ".io")
}
```

Ignore all requests to `.internal` domains:

```kotlin
AppticsApiTracker.configure {
    ignoreTLDs(".internal", ".local")
}
```

### Ignore domains by prefix or suffix

```kotlin
AppticsApiTracker.configure {
    ignoreDomainPrefixes("dev.", "staging.")  // ignores dev.api.com, staging.api.com, etc.
    ignoreDomainSuffixes(".cdn.net")          // ignores assets.cdn.net, images.cdn.net, etc.
}
```

### Group regional domains

If your API is served from multiple regional domains (e.g. `api.myapp.com`, `api.myapp.in`,
`api.myapp.ae`), use `groupDomains` to aggregate them under a single wildcard hostname in your
analytics:

```kotlin
AppticsApiTracker.configure {
    groupDomains("api.myapp.*")
}
```

| Original Domain | Grouped As |
|-----------------|------------|
| `api.myapp.com` | `api.myapp.*` |
| `api.myapp.in` | `api.myapp.*` |
| `api.myapp.ae` | `api.myapp.*` |
| `api.myapp.eu` | `api.myapp.*` |

You can specify multiple domain groups:

```kotlin
AppticsApiTracker.configure {
    groupDomains(
        "api.myapp.*",       // api.myapp.com, api.myapp.in, api.myapp.ae
        "analytics.myapp.*", // analytics.myapp.com, analytics.myapp.in
        "cdn.myapp.*"        // cdn.myapp.com, cdn.myapp.net
    )
}
```

This reduces cardinality in your analytics dashboard by treating all regional variants as a single
logical API endpoint.

### Ignore specific endpoint paths

```kotlin
AppticsApiTracker.configure {
    ignoreEndpoint(
        "/health",          // exact match
        "/internal/*",      // wildcard — matches /internal/ping, /internal/metrics, etc.
        "/debug/**"         // prefix match — matches anything under /debug/
    )
}
```

Pattern syntax:
- **Exact** — no wildcards, must match the full path (e.g. `/health`)
- **Wildcard `*`** — matches any single path segment (e.g. `/internal/*`)
- **Placeholder `{name}`** — same as `*`, matches any single segment (e.g. `/users/{id}/profile`)
- **Prefix `**`** — must appear at the end, matches everything after the prefix (e.g. `/debug/**`)

Both `*` and `{placeholder}` syntax are supported for consistency with `addPattern()`:

```kotlin
AppticsApiTracker.configure {
    // These are equivalent:
    ignoreEndpoint("/users/*/orders")
    ignoreEndpoint("/users/{userId}/orders")
}
```

---

## Customizing normalization

### Define explicit patterns for your endpoints

If auto-detection normalizes a segment it shouldn't (or misses one), define an explicit pattern
using `{placeholder}` syntax for dynamic segments:

```kotlin
AppticsApiTracker.configure {
    addPattern("/v1/accounts/{accountId}/users/{userId}")
    addPattern("/api/{version}/products/{sku}/reviews")
}
```

Patterns are matched before auto-detection runs. If a pattern matches, the result is used as-is and
auto-detection is skipped for that path.

### Protect static segments from being normalized

If a segment looks numeric or UUID-like but is actually meaningful (e.g. a version number like
`v2` or a fixed resource name like `404`), preserve it:

```kotlin
AppticsApiTracker.configure {
    preserveSegments("v1", "v2", "v3", "404", "500")
}
```

Preserved segments are never replaced with `*`, regardless of their format.

### Disable auto-detection entirely

If you want to control normalization exclusively through explicit patterns:

```kotlin
AppticsApiTracker.configure {
    autoDetection(false)
    addPattern("/users/{id}")
    addPattern("/orders/{orderId}/items/{itemId}")
}
```

With auto-detection off, only paths matching a defined pattern are normalized; all other paths are
recorded as-is.

---

## Combining options

All options can be combined in a single `configure` block:

```kotlin
AppticsApiTracker.configure {
    // Only track your own APIs
    allowOnlyDomains("api.yourapp.com", "api.yourapp.in", "api.yourapp.ae")

    // Group regional domains for cleaner analytics
    groupDomains("api.yourapp.*")

    // Skip health and internal endpoints
    ignoreEndpoint("/health", "/ping", "/internal/**")

    // Explicit patterns take priority over auto-detection
    addPattern("/v2/catalog/{categoryId}/items/{itemId}")

    // Protect version segments
    preserveSegments("v1", "v2")
}
```

---

## Manual tracking (non-OkHttp networking)

If you use a networking library other than OkHttp, call `startTrackApi` before the request and
`endTrackApi` after you receive the response. The same normalization and filtering rules apply.

```kotlin
// Kotlin
val trackId = AppticsApiTracker.startTrackApi(url, "POST")

// ... make your network call ...

AppticsApiTracker.endTrackApi(trackId, responseCode, responseMessage)
```

```java
// Java
int trackId = AppticsApiTracker.startTrackApi(url, "POST");

// ... make your network call ...

AppticsApiTracker.endTrackApi(trackId, responseCode, responseMessage);
```

- `startTrackApi` returns `-1` if the URL is filtered out. `endTrackApi` is a no-op when passed
  `-1`, so no guard code is needed on your side.
- Pass an empty string for `responseMessage` if your client does not provide one.
- `responseMessage` is optional in Java (overloaded method with two parameters is also available).

---

## Runtime configuration updates

`configure` can be called multiple times. Each call replaces the previous configuration entirely.
This is useful if your app's tracking requirements change after startup (e.g. after the user logs
in and you know which domain they use):

```kotlin
// After login
AppticsApiTracker.configure {
    allowOnlyDomains("api.${user.tenantDomain}")
}
```

---

## Backward compatibility

If you're upgrading from an older version of the SDK that used numeric `apiId` values from the
Apptics web console, the legacy API is still supported but deprecated.

### Deprecated: `@TrackApiWith` annotation

The `@TrackApiWith` annotation is no longer required. Remove it from your Retrofit interface
methods — the interceptor now tracks all requests automatically.

```kotlin
// Before (deprecated)
@TrackApiWith(apiId = 123456L)
@GET("users/{id}")
suspend fun getUser(@Path("id") id: String): User

// After (recommended)
@GET("users/{id}")
suspend fun getUser(@Path("id") id: String): User
```

### Deprecated: `startTrackApi(apiId: Long, ...)`

The overload that accepts `apiId: Long` is deprecated. Migrate to the URL-based overload:

```kotlin
// Before (deprecated)
val trackId = AppticsApiTracker.startTrackApi(123456L, "GET")

// After (recommended)
val trackId = AppticsApiTracker.startTrackApi("https://api.example.com/users/42", "GET")
```

The deprecated method continues to work and will track the request using the legacy flow, but new
integrations should use the URL-based API.

### Migration steps

1. **Remove `@TrackApiWith` annotations** from all Retrofit interface methods.
2. **Replace `startTrackApi(apiId, method)`** calls with `startTrackApi(url, method)`.
3. **Remove API registrations** from the Apptics web console — they are no longer needed.
4. Optionally, call `AppticsApiTracker.configure { }` to customize domain filtering or endpoint
   normalization.
