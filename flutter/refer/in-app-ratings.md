In-App Ratings prompts users to rate your app once they meet the engagement criteria you define on
the Apptics console (event count, screen visits, session count, etc.). On Android the SDK can show
the rate-us popup **automatically** when those criteria are satisfied; on every platform you can also
check for and display the prompt yourself, build a fully custom rating UI, tune the timing knobs
(Android only), and jump straight to the store. In the Flutter plugin every member lives on the
`AppticsInAppRating.instance` singleton.

```dart
import 'package:apptics_flutter/rateus/apptics_in_app_rating.dart';
import 'package:apptics_flutter/rateus/popup_action.dart';
```

The reference screen for this module is `lib/screens/rating_screen.dart`.

---

## Show the rating prompt

`checkForRatingPop` asks the SDK whether the console-configured criteria are met for this user; if so
it marks the prompt as shown and displays the built-in rate-us dialog (a Cupertino dialog on iOS, a
Material dialog on Android). When nothing is returned — no criteria id, or the widget is no longer
mounted — it does nothing. Pass `isFeedbackEnabled: true` to add a "Send Feedback" option alongside
the store and later actions; the feedback button opens the Apptics feedback screen.

```dart
// Default prompt: store + later actions.
await AppticsInAppRating.instance.checkForRatingPop(context);

// Add a "Send Feedback" alternative to rating.
await AppticsInAppRating.instance.checkForRatingPop(
  context,
  isFeedbackEnabled: true,
);
```

| Method | What it does |
|---|---|
| `checkForRatingPop(BuildContext context, {bool isFeedbackEnabled = false})` | Shows the built-in rate-us dialog if console criteria are met. `isFeedbackEnabled: true` adds a "Send Feedback" option. Marks the prompt as shown and reports engagement automatically. |

---

## Open the store

`openPlayStore` sends the user straight to your app's store listing (Play Store on Android, App Store
on iOS), bypassing the criteria-based popup flow entirely. `openFeedback` opens the Apptics feedback
screen directly.

```dart
// Jump to the store listing.
await AppticsInAppRating.instance.openPlayStore();

// Open the Apptics feedback screen directly.
await AppticsInAppRating.instance.openFeedback();
```

| Method | What it does |
|---|---|
| `Future<void> openPlayStore()` | Opens your app's store listing directly, bypassing the criteria flow. |
| `Future<void> openFeedback()` | Opens the Apptics feedback screen. |

---

## Build a custom rating UI

If you want your own prompt instead of the built-in dialog, drive the flow yourself:

1. Call `getCriteriaId()` to find out whether criteria are satisfied. It returns the criteria id, or
   `null`/`0` when the user has not met the criteria — in that case, do not show your UI.
2. Optionally call `updateRatingShown()` to mark the prompt as shown for bookkeeping.
3. When the user picks an action in your UI, report it with `sentStats(criteriaId, PopupAction)` so
   Apptics records the engagement.
4. Use `isAppticsFeedbackModuleAvailable()` to decide whether to offer a feedback option in your UI.

```dart
final criteriaId = await AppticsInAppRating.instance.getCriteriaId();
if (criteriaId == null || criteriaId == 0) return; // criteria not met

await AppticsInAppRating.instance.updateRatingShown();

final feedbackAvailable =
    await AppticsInAppRating.instance.isAppticsFeedbackModuleAvailable() ?? false;

// ... show your own dialog, then report the user's choice:
await AppticsInAppRating.instance
    .sentStats(criteriaId, PopupAction.RATE_IN_STORE_CLICKED);
```

`PopupAction` represents the engagement the user chose:

| Value | Meaning |
|---|---|
| `PopupAction.RATE_IN_STORE_CLICKED` | The user tapped the rate-in-store action. |
| `PopupAction.SEND_FEEDBACK_CLICKED` | The user tapped the send-feedback action. |
| `PopupAction.LATER_CLICKED` | The user dismissed the prompt with "Later". |

| Method | What it does |
|---|---|
| `Future<int?> getCriteriaId()` | Returns the criteria id when the console criteria are met now, or `null`/`0` otherwise. |
| `Future<bool?> isAppticsFeedbackModuleAvailable()` | Whether the Apptics feedback module is available, so you can offer a feedback option. |
| `Future<void> sentStats(int criteriaId, PopupAction popupAction)` | Reports the user's chosen action for the given criteria id. |
| `Future<void> updateRatingShown()` | Marks the prompt as shown (custom-UI bookkeeping). |

---

## Configuration setters

These tune how often and how the auto-prompt appears. **The day/limit/auto-prompt setters are
Android-only** — on iOS they log a notice and do nothing. `setShowStoreAlertOnFulFillingCriteria` is
forwarded on every platform. The matching getters (`daysBeforeShowingPopupAgain`,
`maxTimesToShowPopup`, `disableAutoPromptOnFulFillingCriteria`,
`showStoreAlertOnFulFillingCriteria`, `disableIfNotInstalledFromPlayStore`) are Android-only.

```dart
// Lifetime cap on how many times the popup appears (Android only).
await AppticsInAppRating.instance.setMaxTimesToShowPopup(3);

// Days to wait before re-prompting after a dismissal (Android only).
await AppticsInAppRating.instance.setDaysBeforeShowingPopupAgain(14);

// Disable the auto-prompt so you trigger the popup yourself (Android only).
await AppticsInAppRating.instance.setDisableAutoPromptOnFulFillingCriteria(true);

// Use Google Play Core's in-app review UI on satisfying criteria.
await AppticsInAppRating.instance.setShowStoreAlertOnFulFillingCriteria(true);
```

| Method | Platform | What it does |
|---|---|---|
| `Future<void> setMaxTimesToShowPopup(int maxTime)` | Android only | Lifetime cap on how many times the popup appears for one user (default `3`). |
| `Future<void> setDaysBeforeShowingPopupAgain(int dayCount)` | Android only | Days to wait before re-prompting after the user cancels/ignores the popup. |
| `Future<void> setDisableAutoPromptOnFulFillingCriteria(bool isDisable)` | Android only | When `true`, suppresses the automatic popup so you trigger it yourself. |
| `Future<void> setShowStoreAlertOnFulFillingCriteria(bool isShow)` | All | When `true`, invokes Google Play Core's in-app review UI on satisfying criteria. |

---

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/android-in_app_rating.html>
