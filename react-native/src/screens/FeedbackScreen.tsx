import {AppticsFeedback, LogType} from '@zoho_apptics/apptics-react-native';
import React, {useState} from 'react';

import {ActionButton} from '../components/ActionButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {LabeledInput} from '../components/LabeledInput';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';

/**
 * Demonstrates the feedback module: the built-in feedback / bug-report forms,
 * shake-to-feedback, anonymous handling, programmatic submission, and the
 * feedback-scoped logs + diagnostics that get attached to reports.
 *
 * Note the two different logging APIs in this sample:
 *   * `AppticsFeedback.writeLog` (here) buffers lines that are *attached to a
 *     feedback report* when `includeLogs` is true.
 *   * `APLogger` (Remote Logging screen) streams lines to the Apptics console
 *     in real time.
 */
export function FeedbackScreen() {
  useScreenTracking('FeedbackScreen');

  const [message, setMessage] = useState(
    'The checkout button is hard to find.',
  );
  const [email, setEmail] = useState('user@example.com');

  return (
    <FeatureScaffold
      intro={
        'Collect user feedback and bug reports, optionally bundling logs and ' +
        'device diagnostics. You can open the built-in forms or submit ' +
        'programmatically from your own UI.'
      }>
      <SectionCard title="Built-in forms">
        <ActionButton
          label="openFeedback()"
          icon="rate-review"
          description="Opens the native feedback form."
          action={() => {
            AppticsFeedback.openFeedback();
            return 'feedback form opened';
          }}
        />
        <ActionButton
          label="reportBug()"
          icon="bug-report"
          description="Captures a screenshot and opens the annotate + report flow."
          action={() => {
            AppticsFeedback.reportBug();
            return 'bug report form opened';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Shake-to-feedback"
        subtitle="Let users shake the device to open the feedback prompt.">
        <ActionButton
          label="enableShakeForFeedback()"
          icon="vibration"
          action={() => {
            AppticsFeedback.enableShakeForFeedback();
            return 'shake enabled';
          }}
        />
        <ActionButton
          label="disableShakeForFeedback()"
          icon="do-not-touch"
          action={() => {
            AppticsFeedback.disableShakeForFeedback();
            return 'shake disabled';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Anonymous handling"
        subtitle="Warn users who have not been identified before they submit.">
        <ActionButton
          label="enableAnonymousAlert()"
          icon="person-off"
          action={() => {
            AppticsFeedback.enableAnonymousAlert();
            return 'anonymous alert enabled';
          }}
        />
        <ActionButton
          label="disableAnonymousAlert()"
          icon="person"
          action={() => {
            AppticsFeedback.disableAnonymousAlert();
            return 'anonymous alert disabled';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Programmatic submission"
        subtitle="Send feedback / bug reports from your own UI — or silently."
        >
        <LabeledInput
          label="Message"
          value={message}
          onChangeText={setMessage}
          multiline
        />
        <LabeledInput
          label="Guest email"
          value={email}
          onChangeText={setEmail}
          helperText="Used when the sender is not a signed-in user."
        />
        <ActionButton
          label="sendFeedback(message, logs, diagnostics, email, false, null)"
          icon="send"
          description="Submits with logs and diagnostics attached, no attachments."
          action={() => {
            AppticsFeedback.sendFeedback(
              message,
              true, // includeLogs
              true, // includeDiagnostics
              email, // guestMailId
              false, // forceToAnonymous
              null, // attachments — array of local file paths
            );
            return 'feedback submitted';
          }}
        />
        <ActionButton
          label="sendFeedback(… forceToAnonymous: true)"
          icon="send-and-archive"
          description="Strips the user identity from the report."
          action={() => {
            AppticsFeedback.sendFeedback(message, true, true, null, true, null);
            return 'anonymous feedback submitted';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Logs & diagnostics"
        subtitle={
          'Captured by the SDK and attached when includeLogs / ' +
          'includeDiagnostics is true.'
        }>
        <ActionButton
          label="writeLog(...) — all levels"
          icon="notes"
          description="debug, info, warn, error"
          action={() => {
            AppticsFeedback.writeLog(LogType.Debug, 'Debug entry');
            AppticsFeedback.writeLog(LogType.Info, 'Info entry');
            AppticsFeedback.writeLog(LogType.Warn, 'Warning entry');
            AppticsFeedback.writeLog(LogType.Error, 'Error entry');
            return '4 log lines written';
          }}
        />
        <ActionButton
          label="addDiagnosticsInfo('App', 'build', '1.0 (1)')"
          icon="info"
          description="Key/value pairs grouped under a heading."
          action={() => {
            AppticsFeedback.addDiagnosticsInfo('App', 'build', '1.0 (1)');
            AppticsFeedback.addDiagnosticsInfo(
              'Session',
              'screen',
              'FeedbackScreen',
            );
            return '2 diagnostics added';
          }}
        />
        <ActionButton
          label="resetLogsAndDiagnostics()"
          icon="cleaning-services"
          description="Clears everything buffered so far."
          action={() => {
            AppticsFeedback.resetLogsAndDiagnostics();
            return 'logs & diagnostics cleared';
          }}
        />
      </SectionCard>
    </FeatureScaffold>
  );
}
