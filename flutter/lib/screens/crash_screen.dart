import 'dart:io' show Platform;

import 'package:apptics_flutter/crash_tracker/apptics_crash_tracker.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import '../core/console.dart';
import '../widgets/action_button.dart';
import '../widgets/feature_scaffold.dart';
import '../widgets/section_card.dart';

/// Demonstrates crash & ANR tracking via `AppticsCrashTracker.instance`.
///
/// `autoCrashTracker()` is already enabled at startup (see apptics_bootstrap),
/// so uncaught errors are reported automatically. This screen shows the manual
/// reporting APIs plus deliberately-destructive demos (a real fatal crash and,
/// on Android, an ANR) gated behind confirmation.
class CrashScreen extends StatelessWidget {
  const CrashScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return FeatureScaffold(
      title: 'Crash & ANR',
      intro:
          'Fatal crashes are reported on the next launch; non-fatals are sent '
          'in the current session. The buttons in red actually crash/hang the '
          'app — use them to verify reporting in the Apptics console.',
      children: [
        SectionCard(
          title: 'Non-fatal exceptions',
          subtitle:
              'Report a caught error without crashing the app. Pass the real '
              'exception + stack trace.',
          children: [
            ActionButton(
              label: 'sendNonFatalException(e, stack)',
              icon: Icons.report_problem,
              action: () async {
                try {
                  // Force an error to capture a genuine stack trace.
                  // ignore: unused_local_variable
                  final int _ = (1 ~/ 0);
                  return null;
                } catch (e, s) {
                  await AppticsCrashTracker.instance
                      .sendNonFatalException(e, s);
                  return 'non-fatal reported: $e';
                }
              },
            ),
            ActionButton(
              label: 'sendException(e, stack, isFatal: false)',
              description:
                  'Lower-level API; here with a custom reason and isFatal=false.',
              icon: Icons.bolt,
              action: () async {
                try {
                  throw StateError('Simulated invalid state');
                } catch (e, s) {
                  await AppticsCrashTracker.instance.sendException(
                    e,
                    s,
                    reason: 'demo: invalid state reached',
                    isFatal: false,
                  );
                  return 'exception reported (non-fatal)';
                }
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Crash metadata & history',
          children: [
            ActionButton(
              label: 'setCrashCustomProperty({...})',
              description: 'Attach custom keys to subsequent crash reports.',
              icon: Icons.label,
              action: () async {
                await AppticsCrashTracker.instance.setCrashCustomProperty(
                  <String, dynamic>{
                    'screen': 'CrashScreen',
                    'experiment': 'A',
                    'cartItems': 3,
                  },
                );
                return 'custom properties set';
              },
            ),
            ActionButton(
              label: 'setAttemptInstantSync(true)',
              description: 'Try to upload crashes immediately instead of next launch.',
              icon: Icons.sync,
              action: () async {
                await AppticsCrashTracker.instance.setAttemptInstantSync(true);
                return 'instant sync enabled';
              },
            ),
            ActionButton(
              label: 'getLastCrashInfo()',
              description: 'Returns JSON describing the previous crash (or null).',
              icon: Icons.history,
              action: () => AppticsCrashTracker.instance.getLastCrashInfo(),
            ),
            ActionButton(
              label: 'showLastSessionCrashedPopup()',
              description: 'Shows a popup if the last session crashed.',
              icon: Icons.warning_amber,
              action: () async {
                await AppticsCrashTracker.instance
                    .showLastSessionCrashedPopup();
                return 'popup requested';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'ANR tracking (Android only)',
          subtitle:
              'Application-Not-Responding detection. These no-op on iOS.',
          children: [
            ActionButton(
              label: 'enableANR()',
              icon: Icons.timer,
              action: () async {
                await AppticsCrashTracker.instance.enableANR();
                return 'ANR enabled';
              },
            ),
            ActionButton(
              label: 'disableANR()',
              icon: Icons.timer_off,
              action: () async {
                await AppticsCrashTracker.instance.disableANR();
                return 'ANR disabled';
              },
            ),
            ActionButton(
              label: 'isANREnabled()',
              icon: Icons.help_outline,
              action: () => AppticsCrashTracker.instance.isANREnabled(),
            ),
            _DestructiveButton(
              label: 'makeANR()  — hangs the UI thread',
              enabled: !kIsWeb && Platform.isAndroid,
              disabledNote: 'Android only',
              confirmTitle: 'Trigger an ANR?',
              confirmBody:
                  'This deliberately blocks the main thread so Android reports '
                  'an ANR. The app may become unresponsive.',
              onConfirmed: () async {
                Console.instance.info('→ makeANR()');
                await AppticsCrashTracker.instance.makeANR();
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Fatal crash',
          subtitle: 'Verifies automatic crash capture end-to-end.',
          children: [
            _DestructiveButton(
              label: 'Throw uncaught exception (crashes app)',
              enabled: true,
              confirmTitle: 'Crash the app?',
              confirmBody:
                  'This throws an uncaught exception. autoCrashTracker reports '
                  'it and the app will terminate. Reopen to confirm the report.',
              onConfirmed: () async {
                // Thrown outside any try/catch so the global handler captures
                // and reports it as a fatal crash.
                throw Exception('Apptics sample: deliberate fatal crash');
              },
            ),
          ],
        ),
      ],
    );
  }
}

/// A red button that confirms before running a destructive action.
class _DestructiveButton extends StatelessWidget {
  const _DestructiveButton({
    required this.label,
    required this.confirmTitle,
    required this.confirmBody,
    required this.onConfirmed,
    this.enabled = true,
    this.disabledNote,
  });

  final String label;
  final String confirmTitle;
  final String confirmBody;
  final Future<void> Function() onConfirmed;
  final bool enabled;
  final String? disabledNote;

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: SizedBox(
        width: double.infinity,
        child: FilledButton.tonalIcon(
          style: FilledButton.styleFrom(
            backgroundColor: scheme.errorContainer,
            foregroundColor: scheme.onErrorContainer,
          ),
          icon: const Icon(Icons.dangerous, size: 18),
          onPressed: enabled
              ? () async {
                  final confirmed = await showDialog<bool>(
                    context: context,
                    builder: (ctx) => AlertDialog(
                      title: Text(confirmTitle),
                      content: Text(confirmBody),
                      actions: [
                        TextButton(
                          onPressed: () => Navigator.pop(ctx, false),
                          child: const Text('Cancel'),
                        ),
                        FilledButton(
                          onPressed: () => Navigator.pop(ctx, true),
                          child: const Text('Proceed'),
                        ),
                      ],
                    ),
                  );
                  if (confirmed == true) await onConfirmed();
                }
              : null,
          label: Align(
            alignment: Alignment.centerLeft,
            child: Text(enabled
                ? label
                : '$label${disabledNote != null ? '  ($disabledNote)' : ''}'),
          ),
        ),
      ),
    );
  }
}
