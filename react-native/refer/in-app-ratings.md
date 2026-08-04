In-app ratings ask satisfied users to rate you at a moment when they are likely to say something
nice. Apptics decides *when* the moment has arrived from criteria you configure on the console; the
prompt itself is the platform's own review sheet.

See the sample usage in `src/screens/RatingScreen.tsx`.

---

## The module starts itself

The ratings module is initialized by `Apptics.init()` — there is nothing extra to set up. On Android
the SDK's own review prompt appears automatically once the configured criteria are met.

---

## Show the platform review sheets

```ts
import {AppticsInAppRatings} from '@zoho_apptics/apptics-react-native';

// Android — Google Play's in-app review sheet.
AppticsInAppRatings.shouldShowPlayCoreAlertForAndroid();

// iOS — StoreKit's rating prompt.
AppticsInAppRatings.showAppStoreRatings();
```

| Method | What it does |
|--------|--------------|
| `shouldShowPlayCoreAlertForAndroid()` | Requests the Play Core review sheet. No-op on iOS. |
| `showAppStoreRatings()` | Requests the App Store rating prompt. |

Neither method guarantees a prompt. Both are subject to two independent limits:

1. **Apptics criteria** — the mode and thresholds you set on the console.
2. **Platform quotas** — Google and Apple both cap how often a user can be asked, and silently
   ignore requests beyond that. Apple's limit is a few times per year, per user, per app.

So a successful call with no visible prompt is the normal case, not a bug.

---

## Know when the iOS sheet is about to appear

The library exposes a callback slot rather than an `addListener` API. Assign a function to it once,
at startup:

```ts
import {AppticsRateUsModuleEmitter} from '@zoho_apptics/apptics-react-native';

AppticsRateUsModuleEmitter.willDisplayReviewPrompt = () => {
  // Pause a video, stop a game loop, dismiss your own overlay.
};
```

Fires just before the StoreKit sheet is presented (iOS only). This sample wires it up in
`src/core/appticsBootstrap.ts` and logs a `🔔` line to the in-app console.

---

## Console setup

**Developer → Growth → In-app rating** → select your app **and platform** → **Configure**:

- **App versions** the criteria apply to.
- **Mode** — score-based (points accumulate from anchor points you define) or hit-based (a plain
  count of sessions or events).
- **Anchor points** — the events that earn points, so you can prompt after a *success* rather than
  after any old session.

Then **Save**. Configure Android and iOS separately.

---

## Testing

- **Android:** the Play Core sheet only works for builds installed by Google Play. Sideloaded debug
  builds get nothing. Use an internal testing track.
- **iOS:** run on a real device. The prompt is unreliable on the simulator and suppressed in some
  TestFlight configurations.
- Reset expectations between runs: the platform quota is per user/device, not per install.

---

## Notes

- Ask after a win — an order placed, a file exported, a level cleared — not on launch.
- Because you cannot know whether the sheet appeared, never gate app behaviour on it.

---

## Compared with the Flutter plugin

This is the largest gap between the two SDKs. Flutter drives the prompt itself
and exposes the whole criteria mechanism; React Native only asks the platform
to show its own review sheet.

| Flutter | React Native |
|---|---|
| `checkForRatingPop(context, isFeedbackEnabled:)` | `shouldShowPlayCoreAlertForAndroid()` / `showAppStoreRatings()` |
| `openPlayStore()` | not exposed |
| `getCriteriaId()` / `sentStats(id, action)` / `updateRatingShown()` | not exposed |
| `isAppticsFeedbackModuleAvailable()` | not exposed |
| `setMaxTimesToShowPopup(n)`, `setDaysBeforeShowingPopupAgain(n)`, `setShowStoreAlertOnFulFillingCriteria(b)`, `setDisableAutoPromptOnFulFillingCriteria(b)` | not exposed — configure on the console instead |
| — | `willDisplayReviewPrompt` callback (React Native only) |

So a custom rating UI is not buildable from JavaScript here: without
`getCriteriaId`/`sentStats` you cannot report what the user chose.

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/>
