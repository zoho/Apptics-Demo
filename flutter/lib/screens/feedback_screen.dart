import 'package:apptics_flutter/feedback/apptics_feedback.dart';
import 'package:apptics_flutter/feedback/apptics_log_type.dart';
import 'package:apptics_flutter/feedback/apptics_logs.dart';
import 'package:flutter/material.dart';

import '../widgets/action_button.dart';
import '../widgets/feature_scaffold.dart';
import '../widgets/section_card.dart';

/// Demonstrates the feedback module: the built-in feedback/bug-report forms,
/// shake-to-feedback, anonymous handling, programmatic submission, and the
/// in-SDK logging + diagnostics that get attached to reports.
class FeedbackScreen extends StatefulWidget {
  const FeedbackScreen({super.key});

  @override
  State<FeedbackScreen> createState() => _FeedbackScreenState();
}

class _FeedbackScreenState extends State<FeedbackScreen> {
  final TextEditingController _message =
      TextEditingController(text: 'The checkout button is hard to find.');
  final TextEditingController _email =
      TextEditingController(text: 'user@example.com');

  @override
  void dispose() {
    _message.dispose();
    _email.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return FeatureScaffold(
      title: 'Feedback',
      intro:
          'Collect user feedback and bug reports, optionally bundling SDK logs '
          'and device diagnostics. You can open the built-in forms or submit '
          'programmatically.',
      children: [
        SectionCard(
          title: 'Built-in forms',
          children: [
            ActionButton(
              label: 'openFeedback()',
              description: 'Opens the native feedback form.',
              icon: Icons.rate_review,
              action: () async {
                await AppticsFeedback.instance.openFeedback();
                return 'feedback form opened';
              },
            ),
            ActionButton(
              label: 'reportBug()',
              description: 'Opens the native bug-report form.',
              icon: Icons.bug_report,
              action: () async {
                await AppticsFeedback.instance.reportBug();
                return 'bug report form opened';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Shake-to-feedback',
          subtitle: 'Let users shake the device to open feedback.',
          children: [
            ActionButton(
              label: 'enableShakeForFeedback()',
              icon: Icons.vibration,
              action: () async {
                await AppticsFeedback.instance.enableShakeForFeedback();
                return 'shake enabled';
              },
            ),
            ActionButton(
              label: 'disableShakeForFeedback()',
              icon: Icons.do_not_touch,
              action: () async {
                await AppticsFeedback.instance.disableShakeForFeedback();
                return 'shake disabled';
              },
            ),
            ActionButton(
              label: 'isShakeForFeedbackEnabled()',
              icon: Icons.help_outline,
              action: () =>
                  AppticsFeedback.instance.isShakeForFeedbackEnabled(),
            ),
          ],
        ),
        SectionCard(
          title: 'Anonymous handling & email',
          children: [
            ActionButton(
              label: 'enableAnonymousUserAlert()',
              description: 'Warn anonymous users before they submit.',
              icon: Icons.person_off,
              action: () async {
                await AppticsFeedback.instance.enableAnonymousUserAlert();
                return 'anonymous alert enabled';
              },
            ),
            ActionButton(
              label: 'disableAnonymousUserAlert()',
              icon: Icons.person,
              action: () async {
                await AppticsFeedback.instance.disableAnonymousUserAlert();
                return 'anonymous alert disabled';
              },
            ),
            ActionButton(
              label: 'isAnonymousUserAlertEnabled()',
              icon: Icons.help_outline,
              action: () =>
                  AppticsFeedback.instance.isAnonymousUserAlertEnabled(),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _email,
              decoration: const InputDecoration(labelText: 'Submitter email'),
            ),
            const SizedBox(height: 8),
            ActionButton(
              label: 'setEmailId(email)',
              icon: Icons.email,
              action: () async {
                await AppticsFeedback.instance.setEmailId(_email.text);
                return 'email set';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Programmatic submission',
          subtitle:
              'Send feedback/bug reports directly, with logs & diagnostics.',
          children: [
            TextField(
              controller: _message,
              maxLines: 2,
              decoration: const InputDecoration(labelText: 'Message'),
            ),
            const SizedBox(height: 8),
            ActionButton(
              label: 'sendFeedback(message, logs:true, diagnostics:true)',
              icon: Icons.send,
              action: () async {
                await AppticsFeedback.instance.sendFeedback(
                  _message.text,
                  true, // includeLogs
                  true, // includeDiagnostics
                  guestMailId: _email.text,
                );
                return 'feedback submitted';
              },
            ),
            ActionButton(
              label: 'sendBugReport(message, logs:true, diagnostics:true)',
              icon: Icons.send_and_archive,
              action: () async {
                await AppticsFeedback.instance.sendBugReport(
                  _message.text,
                  true,
                  true,
                  guestMailId: _email.text,
                );
                return 'bug report submitted';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Logs & diagnostics',
          subtitle:
              'These are captured by the SDK and attached when includeLogs / '
              'includeDiagnostics is true.',
          children: [
            ActionButton(
              label: 'writeLog(...) — all levels',
              description: 'verbose, debug, info, warn, error',
              icon: Icons.notes,
              action: () async {
                final logs = AppticsLogs.instance;
                await logs.writeLog('Verbose entry', Log.verbose);
                await logs.writeLog('Debug entry', Log.debug);
                await logs.writeLog('Info entry', Log.info);
                await logs.writeLog('Warning entry', Log.warn);
                await logs.writeLog('Error entry', Log.error);
                return '5 log lines written';
              },
            ),
            ActionButton(
              label: "addDiagnosticsInfo('App', 'build', '1.0.0+1')",
              icon: Icons.info,
              action: () async {
                await AppticsLogs.instance
                    .addDiagnosticsInfo('App', 'build', '1.0.0+1');
                return 'diagnostic added';
              },
            ),
            ActionButton(
              label: 'resetLogsAndDiagnostics()',
              icon: Icons.cleaning_services,
              action: () async {
                await AppticsLogs.instance.resetLogsAndDiagnostics();
                return 'logs & diagnostics cleared';
              },
            ),
          ],
        ),
      ],
    );
  }
}
