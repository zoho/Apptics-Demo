In-App Updates lets you prompt users to install a newer build of your app. The **update policy is
configured entirely on the Apptics console** — you pick the flow (flexible, immediate, or force),
the target version, the dialog copy, and how often to remind users. Apptics fetches that
configuration, compares it against the installed build, and surfaces the right upgrade dialog when
an update is available — so you can roll out and enforce new versions without writing your own
version-check logic.

All of this hangs off the singleton `AppticsInAppUpdates.instance`:

```dart
import 'package:apptics_flutter/appupdate/apptics_in_app_update.dart';
```

> The in-app update functionality in Flutter does not currently support Material 3.

---

## checkAndUpdateAlert(context)

`checkAndUpdateAlert` is the standard entry point. It fetches the console-configured update data,
compares the target version against the installed build, and shows the appropriate Apptics dialog
(flexible / immediate / force, or the non-supported-OS popup) if an update applies. If no update is
configured for this build, the call still succeeds but no dialog is shown.

It needs a `BuildContext` because it presents its dialog on top of your widget tree. Call it after
your theme is set and a `Navigator` exists — for example at app launch or when returning to the
home screen. The context is used synchronously inside the plugin call, so capture it from `build`.

```dart
import 'package:apptics_flutter/appupdate/apptics_in_app_update.dart';
import 'package:flutter/material.dart';

// inside a widget's build / a callback that has a BuildContext
await AppticsInAppUpdates.instance.checkAndUpdateAlert(context);
```

This is all most apps need — Apptics handles the version comparison, dialog selection, and the
update / remind / ignore actions for you.

| Method | What it does |
|--------|--------------|
| `checkAndUpdateAlert(BuildContext context)` → `Future<void>` | Fetches the console-configured update data and shows the Apptics upgrade dialog if an update applies. Requires a `BuildContext` and must run after the theme and `Navigator` are set up. |

---

## getInAppUpdateData()

`getInAppUpdateData` returns the **raw update configuration map** that backs `checkAndUpdateAlert`,
without showing any dialog. Use it when you want to build your own update UI or branch on the
configuration yourself.

It returns `Future<Map<String, dynamic>?>`. The result is one of three things, distinguished by the
`'category'` value:

- **`'1'`** — normal update data (keys like `updateid`, `currentversion`, `featureTitle`,
  `features`, `remindMeLaterText`, `updateNowText`, `neverAgainText`, `option`, `reminderDays`,
  `forceInDays`, `alertType`, `customStoreUrl`).
- **`'2'`** — non-supported-OS popup data (keys `title`, `description`, `continueBtTxt`,
  `alertType`, `updateid`).
- **`null`** — no update is available.

When you render your own UI, report the impression with `onSendImpressionStatus(updateId)` and wire
your buttons to `onClickUpdate`, `onClickReminder`, `onClickIgnore`, or `onClickNonSupportAlert`.

```dart
import 'package:apptics_flutter/appupdate/apptics_in_app_update.dart';

final updates = AppticsInAppUpdates.instance;
final Map<String, dynamic>? data = await updates.getInAppUpdateData();

if (data == null) {
  // No update available.
} else if (data['category'] == '1') {
  // Normal update — build your own dialog from the keys above.
  await updates.onSendImpressionStatus(data['updateid']);
  // Then, on button taps:
  // await updates.onClickUpdate();
  // await updates.onClickReminder();
  // await updates.onClickIgnore();
} else if (data['category'] == '2') {
  // Non-supported-OS popup.
  await updates.onSendImpressionStatus(data['updateid']);
  // await updates.onClickNonSupportAlert();
}
```

| Method | What it does |
|--------|--------------|
| `getInAppUpdateData()` → `Future<Map<String, dynamic>?>` | Returns the raw update configuration map (`category` `'1'` = normal, `'2'` = non-supported OS) or `null` when no update is available. |
| `onClickUpdate()` → `Future<void>` | Reports the "Update" action for the current update. |
| `onClickReminder()` → `Future<void>` | Reports the "Remind me later" action. |
| `onClickIgnore()` → `Future<void>` | Reports the "Ignore" action. |
| `onClickNonSupportAlert()` → `Future<void>` | Reports the action for the non-supported-OS popup. |
| `onSendImpressionStatus(String updateId)` → `Future<void>` | Sends the impression status for the given update. The `updateId` comes from `AppUpdateData.updateId` or `AppticsAppUpdateNotSupported.updateId`. |

---

## Custom-alert helpers (AppticsInAppUpdateAlert + AppUpdateData)

If you want the Apptics-styled dialogs but driven from your own flow, the plugin ships ready-made
alert widgets in `AppticsInAppUpdateAlert` plus typed models that parse the raw map. Construct an
`AppUpdateData` (or `AppticsAppUpdateNotSupported`) via its `fromJson` factory from the
`getInAppUpdateData()` result, then hand it to the matching `show` helper. The helper renders the
right dialog for the data's `alertType` and calls your action callbacks; you remain responsible for
reporting impressions and the click actions through `AppticsInAppUpdates.instance`.

```dart
import 'package:apptics_flutter/appupdate/apptics_in_app_update.dart';
import 'package:apptics_flutter/appupdate/apptics_app_update_alerts.dart';
import 'package:apptics_flutter/appupdate/appupdate_util.dart';

final updates = AppticsInAppUpdates.instance;
final Map<String, dynamic>? data = await updates.getInAppUpdateData();

if (data != null && data['category'] == '1') {
  final appUpdateData = AppUpdateData.fromJson(data);
  await updates.onSendImpressionStatus(appUpdateData.updateId);

  AppticsInAppUpdateAlert.show(
    context: context,
    appUpdateData: appUpdateData,
    onClickUpdate: () => updates.onClickUpdate(),
    onClickReminder: () => updates.onClickReminder(),
    onClickIgnore: () => updates.onClickIgnore(),
  );
} else if (data != null && data['category'] == '2') {
  final notSupported = AppticsAppUpdateNotSupported.fromJson(data);
  await updates.onSendImpressionStatus(notSupported.updateId);

  AppticsInAppUpdateAlert.showNonSupportAlert(
    context: context,
    appUpdateData: notSupported,
    onClick: () => updates.onClickNonSupportAlert(),
  );
}
```

`AppUpdateData` exposes: `updateId`, `currentVersion`, `featureTitle`, `features`,
`remindMeLaterText`, `updateNowText`, `neverAgainText`, `option`, `reminderDays`, `forceInDays`,
`alertType`, `customStoreUrl`. `AppticsAppUpdateNotSupported` exposes: `title`, `description`,
`continueBtTxt`, `alertType`, `updateId`.

| Member | What it does |
|--------|--------------|
| `AppticsInAppUpdateAlert.show({required BuildContext context, required AppUpdateData appUpdateData, required void Function() onClickUpdate, required void Function() onClickReminder, required void Function() onClickIgnore})` → `Future` | Shows the Apptics update dialog for `appUpdateData.alertType` (`0` native, `1` custom, `2` Android in-app), wiring your three action callbacks. |
| `AppticsInAppUpdateAlert.showNonSupportAlert({required BuildContext context, required AppticsAppUpdateNotSupported appUpdateData, required void Function() onClick})` → `Future` | Shows the non-supported-OS popup with a single continue callback. |
| `AppUpdateData.fromJson(Map<String, dynamic> json)` | Builds an `AppUpdateData` from a `category` `'1'` map returned by `getInAppUpdateData()`. |
| `AppticsAppUpdateNotSupported.fromJson(Map<String, dynamic> json)` | Builds an `AppticsAppUpdateNotSupported` from a `category` `'2'` map returned by `getInAppUpdateData()`. |

See `lib/screens/in_app_update_screen.dart` for a working example.

---

📖 Docs: <https://www.zoho.com/apptics/resources/SDK/flutter-in_app_update.html>
