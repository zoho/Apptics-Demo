import 'dart:io' show Platform;

import 'package:apptics_flutter/apptics_flutter.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import '../core/console.dart';
import '../widgets/action_button.dart';
import '../widgets/feature_scaffold.dart';
import '../widgets/section_card.dart';

/// Demonstrates push notifications.
///
/// The handlers are registered at startup:
///   * background: `AppticsPushNotification.setOnMessageHandlerListener(...)`
///     in `main()` (before runApp), via a top-level vm:entry-point function.
///   * foreground: `AppticsPushNotification.initialize(...)` in
///     `apptics_bootstrap.dart`.
/// Both funnel into the shared Console, so any notification you send from the
/// Apptics console will appear as a 🔔 line below.
class PushScreen extends StatelessWidget {
  const PushScreen({super.key});

  bool get _isIOS => !kIsWeb && Platform.isIOS;

  @override
  Widget build(BuildContext context) {
    return FeatureScaffold(
      title: 'Push Notifications',
      intro:
          'Handlers are already wired up at app start. Send a test push from '
          'the Apptics console and watch it appear in the console below '
          '(foreground) — or open the app from a notification to see the click '
          'callback. Requires FCM (Android) / APNs (iOS) to be configured.',
      children: [
        SectionCard(
          title: 'iOS registration',
          subtitle: _isIOS
              ? 'Start the service and request the APNs token.'
              : 'These are iOS-only (no-ops elsewhere).',
          children: [
            ActionButton(
              label: 'startService()',
              icon: Icons.play_circle,
              action: () async {
                await AppticsFlutter.instance.startService();
                return 'service started';
              },
            ),
            ActionButton(
              label: 'registerPushNotification()',
              description: 'Requests the OS push token.',
              icon: Icons.app_registration,
              action: () async {
                await AppticsFlutter.instance.registerPushNotification();
                return 'registration requested';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Re-bind foreground listeners',
          subtitle:
              'Alternative one-call API on AppticsFlutter.instance that sets '
              'all three foreground callbacks at once.',
          children: [
            ActionButton(
              label: 'setPushNotificationListener(...)',
              icon: Icons.notifications_active,
              action: () async {
                await AppticsFlutter.instance.setPushNotificationListener(
                  onMessageReceived: (msg) =>
                      Console.instance.event('Push received: $msg'),
                  onNotificationClick: (action, payload) => Console.instance
                      .event('Clicked: action=$action payload=$payload'),
                  onNotificationActionClick: (id, action, payload) =>
                      Console.instance.event(
                          'Action: id=$id action=$action payload=$payload'),
                );
                return 'listeners re-bound';
              },
            ),
          ],
        ),
        const SectionCard(
          title: 'How to test',
          children: [
            Text(
              '1. Ensure FCM (Android) / APNs (iOS) credentials are uploaded to '
              'the Apptics console.\n'
              '2. Run on a physical device for iOS.\n'
              '3. Send a campaign / test notification from the console.\n'
              '4. Foreground: a 🔔 line appears here.\n'
              '   Background/terminated: the OS shows the notification; tapping '
              'it fires the click callback.',
            ),
          ],
        ),
      ],
    );
  }
}
