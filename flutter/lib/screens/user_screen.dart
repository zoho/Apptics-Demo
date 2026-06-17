import 'package:apptics_flutter/apptics_flutter.dart';
import 'package:apptics_flutter/apptics_user_property.dart';
import 'package:flutter/material.dart';

import '../widgets/action_button.dart';
import '../widgets/feature_scaffold.dart';
import '../widgets/section_card.dart';

/// Demonstrates user identity and user-property APIs:
/// setUser / removeUser / setUserWithProperty / getUserProperties /
/// isUserLoggedIn, plus the AppticsUserPropertyBuilder fluent API.
class UserScreen extends StatefulWidget {
  const UserScreen({super.key});

  @override
  State<UserScreen> createState() => _UserScreenState();
}

class _UserScreenState extends State<UserScreen> {
  // Editable so you can try valid IDs, empty values, etc. (edge cases).
  final TextEditingController _userId =
      TextEditingController(text: 'user@example.com');
  final TextEditingController _groupId =
      TextEditingController(text: 'acme-corp');

  @override
  void dispose() {
    _userId.dispose();
    _groupId.dispose();
    super.dispose();
  }

  /// Builds a rich user-property object with the fluent builder. Mixes the
  /// typed setters with custom string/number/boolean properties.
  AppticsUserProperty _buildProperties() {
    return AppticsUserPropertyBuilder()
        .setFirstName('Ada')
        .setLastName('Lovelace')
        .setEmailAddress(_userId.text)
        .setCompanyName('Analytical Engines')
        .setPlanType('enterprise')
        .setCountry('UK')
        .addStringProperty('referral', 'newsletter')
        .addNumberProperty('seats', 25)
        .addBooleanProperty('beta_optin', true)
        .build();
  }

  @override
  Widget build(BuildContext context) {
    return FeatureScaffold(
      title: 'User',
      intro:
          'Identify the signed-in user so analytics, crashes and feedback are '
          'attributed correctly. groupId is optional (e.g. an org/tenant).',
      children: [
        SectionCard(
          title: 'Identity inputs',
          children: [
            TextField(
              controller: _userId,
              decoration: const InputDecoration(
                labelText: 'User ID',
                helperText: 'Try a real value, then try clearing it.',
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: _groupId,
              decoration: const InputDecoration(
                labelText: 'Group ID (optional)',
              ),
            ),
          ],
        ),
        SectionCard(
          title: 'Set / remove user',
          children: [
            ActionButton(
              label: 'setUser(userId, groupId)',
              icon: Icons.person_add,
              action: () async {
                await AppticsFlutter.instance
                    .setUser(_userId.text, _groupId.text);
                return 'user set';
              },
            ),
            ActionButton(
              label: 'setUserWithProperty(userId, props: ...)',
              description: 'Sets the user along with profile properties.',
              icon: Icons.badge,
              action: () async {
                await AppticsFlutter.instance.setUserWithProperty(
                  _userId.text,
                  groupId: _groupId.text,
                  props: _buildProperties(),
                );
                return 'user + properties set';
              },
            ),
            ActionButton(
              label: 'removeUser(userId, groupId)',
              description: 'Clears the association (e.g. on logout).',
              icon: Icons.person_remove,
              action: () async {
                await AppticsFlutter.instance
                    .removeUser(_userId.text, _groupId.text);
                return 'user removed';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Query',
          subtitle: 'These return values — watch them in the console.',
          children: [
            ActionButton(
              label: 'getUserProperties()',
              description:
                  'Returns the stored property map (null/empty if none).',
              icon: Icons.fact_check,
              action: () => AppticsFlutter.instance.getUserProperties(),
            ),
            ActionButton(
              label: 'isUserLoggedIn()',
              icon: Icons.how_to_reg,
              action: () => AppticsFlutter.instance.isUserLoggedIn(),
            ),
          ],
        ),
      ],
    );
  }
}
