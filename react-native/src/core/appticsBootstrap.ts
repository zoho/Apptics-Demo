import {
  Apptics,
  AppticsPushMessages,
  AppticsPushModuleEmitter,
  AppticsRateUsModuleEmitter,
} from '@zoho_apptics/apptics-react-native';

import {Console, describeResult} from './console';

/**
 * One-time runtime initialization of the Apptics SDK.
 *
 * NOTE: there is no JS-side `initialize(apiKey: ...)` call. Credentials come
 * entirely from the native config files bundled with the app:
 *   * Android: `android/app/apptics-config.json`
 *   * iOS:     `ios/apptics-config.plist`
 * The native SDK reads those at launch, so by the time JS runs the SDK is
 * already configured. `Apptics.init()` starts the session/analytics engine and
 * `initCrashTracker()` installs the JS error handler; everything else here just
 * wires up the runtime behaviours a host app opts into.
 *
 * Call it once, as early as possible — see `App.tsx`.
 */
export function initApptics() {
  // 1) In debug builds the SDK deliberately skips crash reports and remote
  //    logs. Opting in lets you exercise those paths from a `npm run android` /
  //    `npm run ios` build; it is a no-op in release builds. Remove this line
  //    if you only want production-accurate behaviour.
  Apptics.enableDevTesting();
  Console.info('enableDevTesting() — crash/log upload enabled in debug builds');

  // 2) Start Apptics. `false` turns OFF automatic native screen tracking so
  //    this sample can demonstrate the manual screenAttached/screenDetached
  //    pair from React Navigation (see `useScreenTracking`). Pass `true` (the
  //    default) to let the SDK track native screens itself.
  Apptics.init(false);
  Console.success('Apptics.init(false) — automatic screen tracking disabled');

  // 3) Install the global JS error handler so uncaught JS errors are reported
  //    as crashes. Must be called once, early.
  Apptics.initCrashTracker();
  Console.success('initCrashTracker() — JS crash handler installed');

  registerPushHandlers();
  registerRatingHandlers();
}

/**
 * Registers the push-notification callbacks.
 *
 * The SDK exposes these as assignable slots on `AppticsPushModuleEmitter`
 * rather than as an `addListener` API: the library itself subscribes to the
 * native event emitter and forwards each event to whatever function is
 * currently assigned. Assign them once, at startup, so notifications that
 * arrive before any screen is mounted are still handled.
 */
function registerPushHandlers() {
  AppticsPushModuleEmitter.onMessageReceived = payload => {
    Console.event(`Push received (foreground): ${describeResult(payload)}`);
  };
  AppticsPushModuleEmitter.onNotificationClick = (clickAction, payload) => {
    Console.event(
      `Notification clicked: action=${clickAction} payload=${describeResult(
        payload,
      )}`,
    );
  };
  AppticsPushModuleEmitter.onNotificationActionClick = (
    actionId,
    clickAction,
    payload,
  ) => {
    Console.event(
      `Notification action clicked: id=${actionId} action=${clickAction} ` +
        `payload=${describeResult(payload)}`,
    );
  };
  Console.success('Push notification handlers registered');

  // iOS: start the messaging service and ask the OS for a push token. Both are
  // no-ops on Android (the library guards on Platform.OS), and on Android
  // `registerPushNotification` simply tells the native side that JS listeners
  // are ready so queued events can be delivered.
  AppticsPushMessages.startService();
  AppticsPushMessages.registerPushNotification();
}

/**
 * iOS only: fires just before the App Store review prompt is shown, so you can
 * pause video/audio or stop a game loop first.
 */
function registerRatingHandlers() {
  AppticsRateUsModuleEmitter.willDisplayReviewPrompt = () => {
    Console.event('willDisplayReviewPrompt — App Store prompt is about to show');
  };
}
