import 'package:apptics_flutter/apptics_flutter.dart';
import 'package:apptics_flutter/apptics_flutter_util.dart';
import 'package:flutter/material.dart';

import '../widgets/action_button.dart';
import '../widgets/feature_scaffold.dart';
import '../widgets/section_card.dart';

/// Demonstrates privacy / consent controls. Apptics models user consent as a
/// [TrackingState] (what may be collected, and whether PII is included), plus
/// helper popups to let the user review/manage their choice.
class PrivacyScreen extends StatefulWidget {
  const PrivacyScreen({super.key});

  @override
  State<PrivacyScreen> createState() => _PrivacyScreenState();
}

class _PrivacyScreenState extends State<PrivacyScreen> {
  TrackingState _selected = TrackingState.usageAndCrashTrackingWithPII;

  @override
  Widget build(BuildContext context) {
    return FeatureScaffold(
      title: 'Privacy & Consent',
      intro:
          'TrackingState controls what the SDK is allowed to collect. Pick a '
          'state and apply it, or read the current one.',
      children: [
        SectionCard(
          title: 'Tracking state',
          subtitle: 'setTrackingState(state) / getTrackingState()',
          children: [
            DropdownButtonFormField<TrackingState>(
              initialValue: _selected,
              decoration: const InputDecoration(labelText: 'TrackingState'),
              items: TrackingState.values
                  .map(
                    (s) => DropdownMenuItem(
                      value: s,
                      child: Text(s.name, overflow: TextOverflow.ellipsis),
                    ),
                  )
                  .toList(),
              onChanged: (s) => setState(() => _selected = s ?? _selected),
            ),
            const SizedBox(height: 12),
            ActionButton(
              label: 'setTrackingState(selected)',
              icon: Icons.shield,
              action: () async {
                await AppticsFlutter.instance.setTrackingState(_selected);
                return 'set to ${_selected.name}';
              },
            ),
            ActionButton(
              label: 'getTrackingState()',
              description: 'Reads the current consent state.',
              icon: Icons.read_more,
              action: () async {
                final state =
                    await AppticsFlutter.instance.getTrackingState();
                return state?.name ?? 'null';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Consent UI',
          subtitle: 'Native popups for reviewing / changing privacy choices.',
          children: [
            ActionButton(
              label: 'presentPrivacyReviewPopup()',
              description: 'Shows the SDK-provided privacy review dialog.',
              icon: Icons.policy,
              action: () async {
                await AppticsFlutter.instance.presentPrivacyReviewPopup();
                return 'popup requested';
              },
            ),
            ActionButton(
              label: 'openPrivacySettings()',
              description: 'Opens the privacy settings screen.',
              icon: Icons.settings,
              action: () async {
                await AppticsFlutter.instance.openPrivacySettings();
                return 'settings opened';
              },
            ),
          ],
        ),
      ],
    );
  }
}
