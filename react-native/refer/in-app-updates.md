In-app version alerts prompt users on an old build to update.

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
