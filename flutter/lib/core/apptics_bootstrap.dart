import 'dart:async';
import 'dart:io' show Platform;

import 'package:apptics_flutter/apptics_flutter.dart';
import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';
import 'package:apptics_flutter/push_notification/apptics_push_notification.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart' show MissingPluginException;

import 'console.dart';

/// One-time runtime initialization of the Apptics SDK.
///
/// NOTE: There is no Dart-side `initialize(apiKey: ...)` call. Credentials for
/// Apptics come entirely from the native config files bundled with the app:
///   * Android: `android/app/apptics-config.json`
///   * iOS:     `ios/apptics-config.plist` (referenced via the `AP_INFOPLIST_FILE`
///              Info.plist key)
/// The native SDK reads those at launch, so by the time Dart runs the SDK is
/// already configured. This function only wires up the *runtime* behaviours a
/// host app opts into: crash capture and foreground push handling.
///
/// Call it once from `main()` after `WidgetsFlutterBinding.ensureInitialized()`.
Future<void> initApptics() async {
  // Apptics only ships native implementations for Android and iOS. On
  // web/desktop the platform channels are absent, so guard every call and fail
  // soft instead of crashing the sample.
  if (!_isSupportedPlatform) {
    Console.instance.info(
      'Apptics native SDK is Android/iOS only — running in UI-only mode on '
      'this platform. Plugin calls will report MissingPluginException.',
    );
    return;
  }

  await _enableCrashTracking();
  await _initPushNotifications();
}

bool get _isSupportedPlatform {
  if (kIsWeb) return false;
  return Platform.isAndroid || Platform.isIOS;
}

/// Enables automatic crash tracking. Internally this hooks
/// `FlutterError.onError` (framework errors) and `PlatformDispatcher.onError`
/// (uncaught async errors), so no manual `runZonedGuarded` wrapper is needed.
Future<void> _enableCrashTracking() async {
  try {
    await AppticsCrashTracker.instance.autoCrashTracker();
    Console.instance.success('autoCrashTracker() enabled');
  } catch (e) {
    Console.instance.error('autoCrashTracker() failed: $e');
  }
}

/// Registers the *foreground* push-notification handlers. The *background*
/// handler must be a top-level, `@pragma('vm:entry-point')` function registered
/// before `runApp()` — see `main.dart`.
///
/// All three callbacks funnel into the shared [Console] so notification
/// activity is visible on the Push screen and anywhere else the console is
/// shown.
Future<void> _initPushNotifications() async {
  try {
    // Register the foreground handlers FIRST — this is the important, fast
    // part and must not be gated behind the iOS-only calls below.
    await AppticsPushNotification.initialize(
      onMessageReceived: (Map<String, dynamic> message) {
        Console.instance.event('Push received (foreground): $message');
      },
      onNotificationClick: (String? clickAction, String? payload) {
        Console.instance
            .event('Notification clicked: action=$clickAction payload=$payload');
      },
      onNotificationActionClick:
          (String actionId, String? clickAction, String? payload) {
        Console.instance.event(
          'Notification action clicked: id=$actionId action=$clickAction '
          'payload=$payload',
        );
      },
    );
    Console.instance.success('Push notification handlers registered');

    // iOS-only: start the messaging service and request the OS push token.
    // These are fire-and-forget — on a simulator (no APNs) they may never
    // resolve, and we must not let them block initialization. No-ops on Android.
    if (Platform.isIOS) {
      unawaited(AppticsFlutter.instance.startService());
      unawaited(AppticsFlutter.instance.registerPushNotification());
    }
  } on MissingPluginException catch (e) {
    Console.instance.error('Push init unavailable on this platform: $e');
  } catch (e) {
    Console.instance.error('Push init failed: $e');
  }
}
