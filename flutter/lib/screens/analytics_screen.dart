import 'package:apptics_flutter/apptics_flutter.dart';
import 'package:apptics_flutter/defined_events.dart';
import 'package:flutter/material.dart';

import '../widgets/action_button.dart';
import '../widgets/feature_scaffold.dart';
import '../widgets/section_card.dart';

/// Demonstrates the core analytics surface of `AppticsFlutter.instance`:
/// custom events, predefined ("defined") events, screen tracking, default
/// language, and flushing queued stats to the server.
class AnalyticsScreen extends StatelessWidget {
  const AnalyticsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return FeatureScaffold(
      title: 'Analytics',
      intro:
          'Events and screens are batched on-device and synced periodically. '
          'Use Flush to force an immediate sync. Watch the console for results.',
      children: [
        SectionCard(
          title: 'Custom events',
          subtitle:
              'addEvent(event, group, {properties}). Events are grouped; '
              'properties add structured context.',
          children: [
            ActionButton(
              label: "addEvent('purchase_done', 'ecommerce')",
              description: 'A simple event with no properties.',
              icon: Icons.touch_app,
              action: () async {
                await AppticsFlutter.instance
                    .addEvent('purchase_done', 'ecommerce');
                return 'event queued';
              },
            ),
            ActionButton(
              label: 'addEvent(... , properties: {...})',
              description:
                  'Same event with typed properties (String/bool/num).',
              icon: Icons.data_object,
              action: () async {
                await AppticsFlutter.instance.addEvent(
                  'purchase_done',
                  'ecommerce',
                  properties: <String, dynamic>{
                    'item': 'Pro Plan',
                    'isTrial': false,
                    'amount': 49.99,
                  },
                );
                return 'event with properties queued';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Defined events',
          subtitle:
              'Apptics ships standard event/group constants in DefinedEvents.',
          children: [
            ActionButton(
              label: 'addEvent(AP_APP_FOREGROUND, AP_APP_LIFE_CYCLE)',
              description: 'A predefined lifecycle event.',
              icon: Icons.flag,
              action: () async {
                await AppticsFlutter.instance.addEvent(
                  DefinedEvents.AP_APP_FOREGROUND,
                  DefinedEvents.AP_APP_LIFE_CYCLE,
                );
                return 'defined event queued';
              },
            ),
            ActionButton(
              label: 'addEvent(AP_USER_LOGIN, AP_USER_LIFE_CYCLE)',
              icon: Icons.login,
              action: () async {
                await AppticsFlutter.instance.addEvent(
                  DefinedEvents.AP_USER_LOGIN,
                  DefinedEvents.AP_USER_LIFE_CYCLE,
                );
                return 'defined event queued';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Screen tracking',
          subtitle:
              'Call screenAttached when a screen appears and screenDetached '
              'when it disappears to measure screen views & dwell time.',
          children: [
            ActionButton(
              label: "screenAttached('CheckoutScreen')",
              icon: Icons.visibility,
              action: () async {
                await AppticsFlutter.instance
                    .screenAttached('CheckoutScreen');
                return 'attached';
              },
            ),
            ActionButton(
              label: "screenDetached('CheckoutScreen')",
              icon: Icons.visibility_off,
              action: () async {
                await AppticsFlutter.instance
                    .screenDetached('CheckoutScreen');
                return 'detached';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Misc',
          children: [
            ActionButton(
              label: "setDefaultLanguage('en')",
              description: 'Sets the language reported with analytics.',
              icon: Icons.language,
              action: () async {
                await AppticsFlutter.instance.setDefaultLanguage('en');
                return 'language set';
              },
            ),
            ActionButton(
              label: 'flush()',
              description:
                  'Forces an immediate upload of all queued events/screens/'
                  'sessions.',
              icon: Icons.cloud_upload,
              action: () async {
                await AppticsFlutter.instance.flush();
                return 'flush requested';
              },
            ),
          ],
        ),
      ],
    );
  }
}
