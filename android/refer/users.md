Identify User associates the current device and session with a real user from your system, so that
the events, crashes and feedback Apptics collects are attributed to a specific person in the
dashboard. The user ID is opaque to Apptics — pass whatever identifier your backend already uses
(an email, a numeric account ID, a UUID); Apptics never interprets it, it only groups data by it.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html).

---

## Add the Analytics SDK

The user APIs ship in the Apptics common module, which is pulled in by `apptics-analytics`.

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

Initialize Apptics in `Application.onCreate()` — this is the prerequisite for every user call:

```kotlin
Apptics.init(this)
```

---

## Identify a user

All of the user APIs live on `com.zoho.apptics.common.AppticsUser`.

```kotlin
import com.zoho.apptics.common.AppticsUser
```

### Set the current user

Associate the device with a user. Subsequent events, crashes and feedback are attributed to this
user until you change or clear it.

```kotlin
// Kotlin
AppticsUser.setUser(userId = "demo@apptics.dev")
```

```java
// Java
AppticsUser.setUser("demo@apptics.dev");
```

### Set the current user with an organization ID

Use this when your backend models users under an organization. The org ID is attached alongside
the user ID.

```kotlin
// Kotlin
AppticsUser.setUserWithOrgId(userId = "demo@apptics.dev", orgId = "ACME-123")
```

```java
// Java
AppticsUser.setUserWithOrgId("demo@apptics.dev", "ACME-123");
```

### Remove the current user

Dissociate the active user from this device — for example, on logout. Subsequent events become
anonymous until `setUser` is called again.

```kotlin
// Kotlin
AppticsUser.removeCurrentUser()
```

```java
// Java
AppticsUser.removeCurrentUser();
```

### Read the current user

Return the user currently attached to the device. The returned object exposes `userId` and
`orgId`. This call performs a blocking local DB read, so run it off the main thread.

```kotlin
// Kotlin
val info = withContext(Dispatchers.IO) {
    AppticsUser.getCurrentUserInfo()
}
val userId = info?.userId
val orgId = info?.orgId
```

```java
// Java — call on a background thread
AppticsUserInfo info = AppticsUser.getCurrentUserInfo();
String userId = info.getUserId();
String orgId = info.getOrgId();
```

---

## API reference

| Method | What it does |
|--------|--------------|
| `setUser(userId)` | Associates the device with a user. |
| `setUserWithOrgId(userId, orgId)` | Same, but also attaches an organization ID. |
| `removeCurrentUser()` | Dissociates the active user; events become anonymous until `setUser` is called again. |
| `getCurrentUserInfo()` | Returns the user currently attached to the device. **Worker thread only** (blocking DB read). |

---

## Notes

- **`getCurrentUserInfo()` is `@WorkerThread`.** It runs a blocking DB read, so never call it on
  the main thread — dispatch it to `Dispatchers.IO` (Kotlin) or a background thread (Java).
- **The user ID is opaque to Apptics.** Use whatever identifier your backend already uses; Apptics
  only uses it as a grouping key and never inspects its contents.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-users.html>
