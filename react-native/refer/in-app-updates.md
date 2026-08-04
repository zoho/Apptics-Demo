In-app version alerts prompt users on an old build to update. The policy — which version to promote,
how insistent to be, what the copy says — lives on the Apptics console, so you can change it without
shipping anything.

See the sample usage in `src/screens/InAppUpdateScreen.tsx`.

---

## Show the built-in alert

```ts
import {AppticsAppUpdate} from '@zoho_apptics/apptics-react-native';

AppticsAppUpdate.showVersionAlertPopup();
```

The SDK fetches the configuration and shows the appropriate dialog — or does nothing if the
installed version is already at or above the target. Call `Apptics.init()` first.

| Method | What it does |
|--------|--------------|
| `showVersionAlertPopup()` | Checks the console config and shows the native alert if applicable. |
| `disableUpdatePopupIfNotInstalledFromPlayStore(status)` | Android only — suppress the prompt for sideloaded builds. |

On Android the dialog is themed by the `apptics*` colour tokens in
`android/app/src/main/res/values/styles.xml`:

```xml
<item name="appticsTextColor">@color/apptics_text</item>
<item name="appticsSecondaryTextColor">@color/apptics_secondary_text</item>
<item name="appticsUpdateActionButtonColor">@color/apptics_accent</item>
<item name="appticsUpdateActionButtonTextColor">@color/apptics_on_accent</item>
<item name="appticsRemindAndIgnoreActionButtonColor">@color/apptics_accent</item>
```

---

## Build your own flow

`checkForUpdate()` returns the same configuration as data, so you can render the prompt yourself.

```ts
const data = await AppticsAppUpdate.checkForUpdate();
// → null when no update is configured for this version.
```

| Key | Meaning |
|---|---|
| `updateid` | Id of this update configuration — pass it back with `sendUpdateStat`. |
| `currentversion` | The version installed on the device. |
| `featureTitle` | Heading for the alert. |
| `features` | What's-new text. |
| `updateNowText` / `remindMeLaterText` / `neverAgainText` | Localized action labels. |
| `option` | `1` flexible · `2` immediate · `3` force. |
| `reminderDays` | Days to wait before prompting again after "Remind me later". |
| `forceInDays` | Days until the update becomes mandatory. |
| `alertType` | `0` custom UI · `1` native UI · `2` Android in-app updates. |
| `customStoreUrl` | Alternative store URL to open instead of the default listing. |

---

## Report engagement from a custom prompt

If you render your own UI, tell Apptics what happened so the console's update funnel stays accurate.

```ts
import {AppticsAppUpdate, UpdateStats} from '@zoho_apptics/apptics-react-native';

AppticsAppUpdate.sendUpdateStat(updateId, UpdateStats.Impression);       // you showed it
AppticsAppUpdate.sendUpdateStat(updateId, UpdateStats.UpdateClick);      // they tapped Update
AppticsAppUpdate.sendUpdateStat(updateId, UpdateStats.RemindLaterClick); // they postponed
AppticsAppUpdate.sendUpdateStat(updateId, UpdateStats.IgnoreClick);      // they dismissed for good
```

| `UpdateStats` | Wire value |
|---|---|
| `Impression` | `impression` |
| `UpdateClick` | `download` |
| `IgnoreClick` | `ignore` |
| `RemindLaterClick` | `later` |

The built-in alert reports these for you — only send them yourself when you replaced the UI.

---

## Console setup

**Developer → In-app update** → select your app → **Configure**. Set the version to promote, the
alert type (flexible / immediate / force), the minimum OS and SDK, upload your localized messages,
and set the reminder interval. Then **Publish**.

Nothing appears until this is published *and* the installed version is below the target — the most
common reason "the call succeeded but no dialog showed".

---

## Notes

- Where to call it matters more than how often: a launch or a home screen, not mid-task.
- Force updates lock the user out of the app until they update. Reserve them for genuinely broken
  builds.
- `disableUpdatePopupIfNotInstalledFromPlayStore(true)` is worth setting if you distribute APKs
  outside Play — the Play in-app update flow cannot service those installs.

---

## Compared with the Flutter plugin

| Flutter | React Native |
|---|---|
| `checkAndUpdateAlert(context)` — needs a BuildContext | `showVersionAlertPopup()` — no context |
| `getInAppUpdateData()` | `checkForUpdate()` — same payload |
| — | `sendUpdateStat(id, stat)` (React Native only) |
| — | `disableUpdatePopupIfNotInstalledFromPlayStore(b)` (React Native only) |

📖 Docs: <https://www.zoho.com/apptics/resources/user-guide/in-app-updates.html>
