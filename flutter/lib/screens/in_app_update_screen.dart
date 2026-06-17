import 'package:apptics_flutter/appupdate/apptics_in_app_update.dart';
import 'package:flutter/material.dart';

import '../widgets/action_button.dart';
import '../widgets/feature_scaffold.dart';
import '../widgets/section_card.dart';

/// Demonstrates in-app update alerts via `AppticsInAppUpdates.instance`.
///
/// The update policy (flexible / immediate / forced, version, copy) is
/// configured server-side in the Apptics console; the SDK fetches it and
/// `checkAndUpdateAlert` renders the appropriate native dialog.
class InAppUpdateScreen extends StatelessWidget {
  const InAppUpdateScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return FeatureScaffold(
      title: 'In-App Update',
      intro:
          'Update prompts only appear when the console is configured with a '
          'newer version targeting this build. Otherwise the calls succeed but '
          'no dialog is shown.',
      children: [
        SectionCard(
          title: 'Update alert',
          children: [
            ActionButton(
              label: 'checkAndUpdateAlert(context)',
              description:
                  'Fetches update config and shows the alert if applicable.',
              icon: Icons.system_update,
              action: () async {
                // checkAndUpdateAlert needs a BuildContext to present its
                // dialog. The context is captured from build() and used
                // synchronously inside the plugin call.
                await AppticsInAppUpdates.instance.checkAndUpdateAlert(context);
                return 'update check requested';
              },
            ),
            ActionButton(
              label: 'getInAppUpdateData()',
              description:
                  'Returns the raw update configuration map (or null).',
              icon: Icons.data_object,
              action: () => AppticsInAppUpdates.instance.getInAppUpdateData(),
            ),
          ],
        ),
      ],
    );
  }
}
