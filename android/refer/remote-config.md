Remote Config lets you read values from the Apptics console at runtime to gate features, change copy,
or run experiments — all without shipping a new build. You configure a parameter (key + value) on the
console, optionally attach conditions, and fetch its value in your app whenever you need it.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html).

---

## Add the Remote Config SDK

Using Apptics BoM (recommended):

```groovy
dependencies {
    implementation platform('com.zoho.apptics:apptics-bom:[latest-version]')
    implementation 'com.zoho.apptics:apptics-rc'
}
```

Without BoM:

```groovy
dependencies {
    implementation 'com.zoho.apptics:apptics-rc:[latest-version]'
}
```

---

## Fetch a value

Fetch a parameter by the key you configured on the console. `fetchValue` serves the offline cached
value first (if any), then refreshes from the server; the `onComplete` callback fires with the final
value on the main thread, so it's safe to drive UI directly from it.

```kotlin
// Kotlin
import com.zoho.apptics.remoteconfig.AppticsRemoteConfig

AppticsRemoteConfig.fetchValue("welcome_message") { value ->
    val message = value ?: "Welcome!"   // fall back to a default if no value
    // use `message` to drive UI
}
```

The named-argument form (as used in the demo screen) reads identically:

```kotlin
AppticsRemoteConfig.fetchValue(
    paramName = "welcome_message",
    onComplete = { value ->
        val message = value ?: "Welcome!"
        // use `message` to drive UI
    }
)
```

```java
// Java
import com.zoho.apptics.remoteconfig.AppticsRemoteConfig;

AppticsRemoteConfig.INSTANCE.fetchValue("welcome_message", value -> {
    String message = value != null ? value : "Welcome!";
    // use `message` to drive UI
    return Unit.INSTANCE;
});
```

Always provide a sensible default fallback. The first call after install may return a cached value or
`null` before the server refresh completes, so your code should never assume a non-null value.

---

## Configuration (on the console)

Parameters, conditions, and defaults are all configured on the Apptics console — there is nothing to
configure in code beyond the `fetchValue` call.

In the console, go to **Developer → Remote configuration** and:

1. Add a **parameter** — a key plus its default value.
2. Optionally create **conditions** (by device type, OS / app version, country, or custom key-values)
   and attach them to the parameter. The first condition that evaluates true wins; if none match, the
   default value is served.
3. **Preview & publish** to make the values live.

Don't store any sensitive data or PII in parameters.

---

## API reference

| Method | What it does |
|--------|--------------|
| `fetchValue(paramName, onComplete)` | Fetches a console-configured value by key. Serves the cached value first, then refreshes; `onComplete(value)` fires on the main thread with the final value (or `null`). |

---

## Notes

- Parameter keys, conditions, and defaults are defined entirely on the console — match the key string
  in `fetchValue` to the key you published.
- The first call for a key may return a cached value or `null` before the server refresh lands, so
  always supply a default fallback (`value ?: default`).
- `onComplete` is delivered on the main thread, so you can update UI state from it without dispatching.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-remote_configuration.html>
