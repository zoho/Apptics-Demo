import 'package:flutter/material.dart';

import 'console_panel.dart';

/// Shared scaffold for every feature screen: an app bar, a scrollable body of
/// controls, and a persistent [ConsolePanel] pinned to the bottom so the result
/// of each action is always in view.
///
/// Centralizing the layout here means each feature screen only has to supply
/// its title and its list of control widgets.
class FeatureScaffold extends StatelessWidget {
  const FeatureScaffold({
    super.key,
    required this.title,
    required this.children,
    this.intro,
  });

  final String title;

  /// Optional paragraph shown above the controls explaining the module.
  final String? intro;

  /// The control widgets (usually `SectionCard`s) for this feature.
  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(title)),
      body: Column(
        children: [
          Expanded(
            child: ListView(
              padding: const EdgeInsets.only(bottom: 16),
              children: [
                if (intro != null)
                  Padding(
                    padding: const EdgeInsets.all(16),
                    child: Text(
                      intro!,
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                  ),
                ...children,
              ],
            ),
          ),
          // The live console — shared singleton, so notifications and
          // background events show up here too.
          const ConsolePanel(),
        ],
      ),
    );
  }
}
