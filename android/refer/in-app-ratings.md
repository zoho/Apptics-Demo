In-App Ratings prompts users to rate your app once they meet the engagement criteria you define on
the Apptics console (event count, screen visits, session count, etc.). When those criteria are
satisfied, the SDK shows the rate-us popup **automatically** — no extra code is required. You can
also tune the timing knobs, switch the auto-prompt off and trigger the popup yourself, delegate to
Google Play's in-app review UI, or send the user straight to your Play Store page.

Before you begin, make sure Apptics is integrated by following the
[Integration Guide](https://www.zoho.com/apptics/resources/SDK/android-integrations.html).

---

## Add the Ratings SDK

Using Apptics BoM (recommended):

```groovy
dependencies {
    implementation platform('com.zoho.apptics:apptics-bom:[latest-version]')
    implementation 'com.zoho.apptics:apptics-ratings'
}
```

Without BoM:

```groovy
dependencies {
    implementation 'com.zoho.apptics:apptics-ratings:[latest-version]'
}
```

---

## How the auto-prompt works

You configure the engagement criteria (event count, screen visits, session count, etc.) on the
Apptics console. The SDK evaluates those criteria as your users interact with the app and, when they
are met, shows the rate-us popup **automatically** — you do not need to call anything.

The cooldown and limit knobs below let you control how often and how many times the popup can appear
per user. You can also turn the auto-prompt off and trigger the popup yourself, or replace the
Apptics popup with Google Play's in-app review UI.

---

## Core usage

Set the cooldown and limits, choose the auto-prompt behavior, and (optionally) trigger the popup
manually. All members live on `com.zoho.apptics.rateus.AppticsInAppRatings`.

```kotlin
// Kotlin
import com.zoho.apptics.rateus.AppticsInAppRatings

// Cooldown & limits — how long to wait before re-prompting, and the lifetime cap.
AppticsInAppRatings.daysBeforeShowingPopupAgain = 10
AppticsInAppRatings.maxTimesToShowPopup = 3

// Auto-prompt behavior
AppticsInAppRatings.disableAutoPromptOnFulFillingCriteria = false       // true = you trigger it yourself
AppticsInAppRatings.showAndroidPlayCoreAlertOnFulFillingCriteria = false // true = use Google Play in-app review UI

// Trigger manually: shows the popup only if console criteria are satisfied for this user.
AppticsInAppRatings.showPopupIfCriteriaFulfilled()

// Or send the user straight to the Play Store entry, bypassing criteria entirely.
AppticsInAppRatings.openStore(activity)
```

```java
// Java
import com.zoho.apptics.rateus.AppticsInAppRatings;

AppticsInAppRatings.INSTANCE.setDaysBeforeShowingPopupAgain(10);
AppticsInAppRatings.INSTANCE.setMaxTimesToShowPopup(3);

AppticsInAppRatings.INSTANCE.setDisableAutoPromptOnFulFillingCriteria(false);
AppticsInAppRatings.INSTANCE.setShowAndroidPlayCoreAlertOnFulFillingCriteria(false);

AppticsInAppRatings.INSTANCE.showPopupIfCriteriaFulfilled();
AppticsInAppRatings.INSTANCE.openStore(activity);
```

### Cooldown & limits

`daysBeforeShowingPopupAgain` controls how long to wait before re-prompting after a dismissal, and
`maxTimesToShowPopup` caps how many times the popup can ever appear for one user. The sample's
defaults (`10` days, `3` times) are reasonable for most apps — tune them to your session cadence.

### Auto-prompt toggle

By default the SDK auto-shows the popup the moment criteria are met. Set
`disableAutoPromptOnFulFillingCriteria = true` to suppress that and take control: the popup will only
appear when you call `showPopupIfCriteriaFulfilled()` yourself (e.g. at a natural break in your UI).

### Google Play in-app review

Set `showAndroidPlayCoreAlertOnFulFillingCriteria = true` to delegate the rate-us dialog to Google
Play Core's in-app review UI instead of showing the Apptics popup. The criteria still gate when the
review is requested.

### Trigger manually / open the store

- `showPopupIfCriteriaFulfilled()` asks the SDK whether the console-configured criteria are met right
  now for this user; if so, it displays the rate-us popup. Useful when you've disabled the
  auto-prompt and want to choose the moment.
- `openStore(activity)` sends the user straight to your app's Play Store page, bypassing the
  criteria-based popup flow entirely.

---

## Options / configuration

| Option | Type | Default | What it controls |
|---|---|---|---|
| `daysBeforeShowingPopupAgain` | `Int` | `10` | Days to wait before re-prompting after a dismissal. |
| `maxTimesToShowPopup` | `Int` | `3` | Lifetime cap on how many times the popup appears for one user. |
| `disableAutoPromptOnFulFillingCriteria` | `Boolean` | `false` | When `true`, the SDK won't auto-show the popup — you call `showPopupIfCriteriaFulfilled()` yourself. |
| `showAndroidPlayCoreAlertOnFulFillingCriteria` | `Boolean` | `false` | When `true`, uses Google Play Core's in-app review UI instead of the Apptics popup. |

---

## API reference

| Method / Property | What it does |
|---|---|
| `daysBeforeShowingPopupAgain` | Days to wait before re-prompting after a dismissal. |
| `maxTimesToShowPopup` | Lifetime cap on how many times the popup appears for one user. |
| `disableAutoPromptOnFulFillingCriteria` | When `true`, the SDK won't auto-show the popup — call `showPopupIfCriteriaFulfilled()` yourself. |
| `showAndroidPlayCoreAlertOnFulFillingCriteria` | When `true`, delegates to Google Play Core's in-app review UI instead of the Apptics popup. |
| `showPopupIfCriteriaFulfilled()` | Asks the SDK whether criteria are met now; shows the rate-us popup if so. |
| `openStore(activity)` | Opens your app's Play Store page directly, bypassing the criteria flow. |

---

## Notes

- The popup is shown **automatically** when the console-configured criteria are met — no code needed
  unless you set `disableAutoPromptOnFulFillingCriteria = true`.
- Google Play Core's in-app review UI is **rate-limited by Google**: even when you request it, the
  system decides whether to actually show the review card, so it may not appear on every call.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-in_app_rating.html>
