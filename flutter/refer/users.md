The user-identity APIs associate the current device and session with a real user from your system, so the events, crashes and feedback Apptics collects are attributed to a specific person in the dashboard. The user ID is opaque to Apptics — pass whatever identifier your backend already uses (an email, a numeric account ID, a UUID); Apptics never interprets it, it only groups data by it. In the Flutter plugin every call lives on the `AppticsFlutter.instance` singleton and returns a `Future`, so `await` them (or chain `.then`).

The sample app demonstrates all of these APIs on `lib/screens/user_screen.dart`.

## Set the current user

`setUser` associates the device with a user. Subsequent events, crashes and feedback are attributed to this user until you change or clear it. The optional `groupId` lets you attach an organization / tenant the user belongs to.

```dart
import 'package:apptics_flutter/apptics_flutter.dart';

// Identify the signed-in user.
await AppticsFlutter.instance.setUser('demo@apptics.dev');

// ...or identify the user together with their org / tenant.
await AppticsFlutter.instance.setUser('demo@apptics.dev', 'acme-corp');
```

| Method | What it does |
|--------|--------------|
| `setUser(String? userId, [String? groupId])` | Associates the device with `userId`. Pass the optional positional `groupId` to attach an org/tenant. Returns `Future<void>`. |

## Set the current user with properties

`setUserWithProperty` identifies the user and attaches a profile of user properties in one call. Properties such as the email address are also used for feedback user identification. Build the property bag with the fluent `AppticsUserPropertyBuilder`: chain the typed setters (`setFirstName`, `setEmailAddress`, `setPlanType`, …), add your own custom keys with `addStringProperty` / `addNumberProperty` / `addBooleanProperty`, then call `build()`. The `groupId` and `props` arguments are both named and optional.

```dart
import 'package:apptics_flutter/apptics_flutter.dart';
import 'package:apptics_flutter/apptics_user_property.dart';

final props = AppticsUserPropertyBuilder()
    .setFirstName('Ada')
    .setLastName('Lovelace')
    .setEmailAddress('demo@apptics.dev')
    .setCompanyName('Analytical Engines')
    .setPlanType('enterprise')
    .setCountry('UK')
    .addStringProperty('referral', 'newsletter')
    .addNumberProperty('seats', 25)
    .addBooleanProperty('beta_optin', true)
    .build();

await AppticsFlutter.instance.setUserWithProperty(
  'demo@apptics.dev',
  groupId: 'acme-corp',
  props: props,
);
```

| Method | What it does |
|--------|--------------|
| `setUserWithProperty(String userId, {String? groupId, AppticsUserProperty? props})` | Identifies `userId` and attaches the built property bag. `groupId` and `props` are optional named args. Returns `Future<void>`. |
| `AppticsUserPropertyBuilder()` | Creates a fluent builder; every setter returns the builder so calls can be chained. |
| `setFirstName / setLastName / setEmailAddress / setCompanyName / setContactNumber / setCountry / setRegion / setCity / setGeoLocation / setGender / setPlanType / setTimezone / setLanguage` | Typed `String` setters for the well-known user-property keys. |
| `setEngagementScore(int) / setDateOfBirth(int)` | Typed `int` setters for numeric well-known keys. |
| `addStringProperty(String key, String value)` | Adds a custom string property under `key`. |
| `addNumberProperty(String key, num value)` | Adds a custom numeric property under `key`. |
| `addBooleanProperty(String key, bool value)` | Adds a custom boolean property under `key`. |
| `build()` | Returns the `AppticsUserProperty` to hand to `setUserWithProperty`. |

## Remove the current user

`removeUser` dissociates the active user from this device — for example, on logout. Subsequent events become anonymous until `setUser` is called again. Pass the same `userId` (and optional `groupId`) you used to identify the user.

```dart
// e.g. on logout
await AppticsFlutter.instance.removeUser('demo@apptics.dev', 'acme-corp');
```

| Method | What it does |
|--------|--------------|
| `removeUser(String userId, [String? groupId])` | Dissociates the user from this device; events become anonymous until `setUser` is called again. Returns `Future<void>`. |

## Read the current user's properties

`getUserProperties` returns the user-property map currently stored for the device, or `null` when no user/properties are set.

```dart
final Map<String, dynamic>? props =
    await AppticsFlutter.instance.getUserProperties();

if (props == null || props.isEmpty) {
  debugPrint('No user properties set.');
} else {
  debugPrint('Plan: ${props['plan_type']}, seats: ${props['seats']}');
}
```

| Method | What it does |
|--------|--------------|
| `getUserProperties()` | Returns `Future<Map<String, dynamic>?>` — the stored property map, or `null` if none. |

## Check whether a user is logged in

`isUserLoggedIn` reports the logged-in status of the user associated with the device.

```dart
final bool? loggedIn = await AppticsFlutter.instance.isUserLoggedIn();

if (loggedIn == true) {
  debugPrint('A user is currently identified.');
} else {
  debugPrint('No user is identified on this device.');
}
```

| Method | What it does |
|--------|--------------|
| `isUserLoggedIn()` | Returns `Future<bool?>` — `true` if a user is logged in, `false` otherwise (`null` if undetermined). |

## Notes

- **Every call is asynchronous.** All methods return a `Future`, so `await` them or chain `.then`; query methods (`getUserProperties`, `isUserLoggedIn`) return nullable values.
- **The user ID is opaque to Apptics.** Use whatever identifier your backend already uses; Apptics only uses it as a grouping key and never inspects its contents.
- **Avoid PII in `userId`.** To protect user privacy, do not pass personally identifiable information directly as the user identifier.

📖 Docs: <https://www.zoho.com/apptics/>
