Identifying the signed-in user ties every other statistic — events, screens, crashes, feedback — to
a person, so you can answer "which users hit this bug" rather than just "how many". Apptics also
lets you attach **user properties** for segmentation.

See the sample usage in `src/screens/UserScreen.tsx`.

---

## Set the user

```ts
import {Apptics} from '@zoho_apptics/apptics-react-native';

// Just the id.
Apptics.setUser('user@example.com');

// With an organization / tenant id.
Apptics.setUserWithOrgId('user@example.com', 'acme-corp');
```

| Method | What it does |
|--------|--------------|
| `setUser(userId, properties?)` | Associates subsequent stats with this user id. |
| `setUserWithOrgId(userId, orgId, properties?)` | Same, plus an organization id. |
| `isUserLoggedIn(): Promise<boolean>` | Resolves `true` once a user id has been set. |

---

## Attach user properties

Properties are a single flat object mixing Apptics' **predefined** keys with any **custom** keys you
need. Values may be strings, numbers or booleans.

```ts
import {Apptics, AppticsUserProperty} from '@zoho_apptics/apptics-react-native';

const props: AppticsUserProperty = {
  // Predefined — recognised and displayed specially by the console.
  first_name: 'Ada',
  last_name: 'Lovelace',
  email_address: 'ada@example.com',
  company_name: 'Analytical Engines',
  plan_type: 'enterprise',
  country: 'UK',

  // Custom — anything else you want to segment on.
  referral: 'newsletter',
  seats: 25,
  beta_optin: true,
};

Apptics.setUser('ada@example.com', props);
```

**Predefined keys:** `first_name`, `last_name`, `company_name`, `contact_number`, `email_address`,
`country`, `region`, `city`, `geo_location`, `gender`, `plan_type`, `timezone`, `language`,
`date_of_birth`.

**Limits:**

| Limit | Value |
|---|---|
| Custom properties per project | 30 unique keys |
| Key length | 50 characters |
| Value length | 250 characters |

---

## The user id is PII

The user id you pass to `setUser` is what Apptics means by *personally identifiable information*.
It is only attached to statistics when the current tracking state allows PII:

```ts
import {Apptics, TrackingState} from '@zoho_apptics/apptics-react-native';

// The default out of the box — stats are collected, but anonymised.
Apptics.setTrackingState(TrackingState.UsageAndCrashTrackingWithoutPII);
```

So if you call `setUser` and still see anonymous data on the dashboard, check the tracking state
first — see [privacy.md](privacy.md).

---

## Logging out

There is no `removeUser` in the React Native library. On logout, drop the association by moving to a
state that excludes PII, and flush anything queued under the old identity:

```ts
Apptics.flush();
Apptics.setTrackingState(TrackingState.UsageAndCrashTrackingWithoutPII);
```

If your app supports account switching, call `setUser` again with the new id on the next login.

---

## Notes

- Call `setUser` as soon as the session is restored on launch, not only at the moment of login, so
  crashes early in the session are attributed correctly.
- Prefer a stable internal id over an email address if the user can change their email.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/>
