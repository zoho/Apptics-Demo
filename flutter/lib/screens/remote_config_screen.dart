import 'package:apptics_flutter/remoteconfig/apptics_remote_config.dart';
import 'package:flutter/material.dart';

import '../widgets/action_button.dart';
import '../widgets/feature_scaffold.dart';
import '../widgets/section_card.dart';

/// Demonstrates server-driven configuration via `AppticsRemoteConfig.instance`.
///
/// Define parameters and targeting conditions in the Apptics console, then read
/// them here. Custom condition values let you target configs by app-supplied
/// attributes (e.g. user tier).
class RemoteConfigScreen extends StatefulWidget {
  const RemoteConfigScreen({super.key});

  @override
  State<RemoteConfigScreen> createState() => _RemoteConfigScreenState();
}

class _RemoteConfigScreenState extends State<RemoteConfigScreen> {
  final TextEditingController _key =
      TextEditingController(text: 'color');
  final TextEditingController _condKey =
      TextEditingController(text: 'user_tier');
  final TextEditingController _condValue =
      TextEditingController(text: 'premium');

  // Optional flags on getStringValue.
  bool _coldFetch = false;
  bool _fallbackOffline = false;

  @override
  void dispose() {
    _key.dispose();
    _condKey.dispose();
    _condValue.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return FeatureScaffold(
      title: 'Remote Config',
      intro:
          'Reading an unknown key returns null — a normal edge case the app '
          'should handle by falling back to a default.',
      children: [
        SectionCard(
          title: 'Read a value',
          children: [
            TextField(
              controller: _key,
              decoration: const InputDecoration(
                labelText: 'Parameter key',
                helperText: "Try a configured key, then a made-up one (→ null).",
              ),
            ),
            SwitchListTile(
              contentPadding: EdgeInsets.zero,
              title: const Text('coldFetch'),
              subtitle: const Text('Force a fresh fetch from the server'),
              value: _coldFetch,
              onChanged: (v) => setState(() => _coldFetch = v),
            ),
            SwitchListTile(
              contentPadding: EdgeInsets.zero,
              title: const Text('fallbackWithOfflineValue'),
              subtitle: const Text('Use the last cached value if offline'),
              value: _fallbackOffline,
              onChanged: (v) => setState(() => _fallbackOffline = v),
            ),
            ActionButton(
              label: 'getStringValue(key, ...)',
              icon: Icons.download,
              action: () async {
                final value = await AppticsRemoteConfig.instance.getStringValue(
                  _key.text,
                  coldFetch: _coldFetch,
                  fallbackWithOfflineValue: _fallbackOffline,
                );
                // Edge case: explicitly surface null rather than silently
                // ignoring it.
                return value ?? '(null — key not configured)';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Custom condition',
          subtitle:
              'Provide an app-side attribute the console can target configs on.',
          children: [
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _condKey,
                    decoration: const InputDecoration(labelText: 'Key'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: TextField(
                    controller: _condValue,
                    decoration: const InputDecoration(labelText: 'Value'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            ActionButton(
              label: 'setCustomConditionValue(key, value)',
              icon: Icons.tune,
              action: () async {
                await AppticsRemoteConfig.instance
                    .setCustomConditionValue(_condKey.text, _condValue.text);
                return 'condition set: ${_condKey.text}=${_condValue.text}';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Reset',
          children: [
            ActionButton(
              label: 'hardReset()',
              description: 'Clears cached config and restores defaults.',
              icon: Icons.restart_alt,
              action: () async {
                await AppticsRemoteConfig.instance.hardReset();
                return 'remote config reset';
              },
            ),
          ],
        ),
      ],
    );
  }
}
