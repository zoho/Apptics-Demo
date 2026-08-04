import {Apptics} from '@zoho_apptics/apptics-react-native';
import React from 'react';
import {Platform} from 'react-native';

import {ActionButton} from '../components/ActionButton';
import {DestructiveButton} from '../components/DestructiveButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';

/**
 * Demonstrates crash reporting.
 *
 * `Apptics.initCrashTracker()` is already called at startup (see
 * `core/appticsBootstrap.ts`), so uncaught JS errors — and native crashes —
 * are reported automatically. This screen shows the manual reporting API plus
 * a deliberately-destructive demo gated behind a confirmation.
 *
 * IMPORTANT: by default the SDK does not upload crashes from debug builds. The
 * sample calls `Apptics.enableDevTesting()` at startup to bypass that, so you
 * can verify reporting without making a release build. Remove that call for
 * production-accurate behaviour.
 */
export function CrashScreen() {
  useScreenTracking('CrashScreen');

  return (
    <FeatureScaffold
      intro={
        'Fatal crashes are reported on the next launch; non-fatals are sent in ' +
        'the current session. The red button actually crashes the app — use it ' +
        'to verify reporting end-to-end in the Apptics console.'
      }>
      <SectionCard
        title="Debug builds"
        subtitle={
          'Crash reports and remote logs are suppressed while __DEV__ is true. ' +
          'The sample already calls this at startup; the button is here so you ' +
          'can see the API.'
        }>
        <ActionButton
          label="enableDevTesting()"
          icon="science"
          description="Bypasses the __DEV__ guard. No effect in release builds."
          action={() => {
            Apptics.enableDevTesting();
            return 'dev testing enabled';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Non-fatal exceptions"
        subtitle="Report a caught error without crashing the app. Pass the real Error so its stack trace is captured."
        >
        <ActionButton
          label="reportError(error)"
          icon="report-problem"
          description="Catches a genuine TypeError and reports it."
          action={() => {
            try {
              // Force a real error so the stack trace is genuine.
              const nothing: {crash?: () => void} = {};
              (nothing.crash as () => void)();
              return null;
            } catch (e) {
              Apptics.reportError(e);
              return `non-fatal reported: ${(e as Error).message}`;
            }
          }}
        />
        <ActionButton
          label="reportError(new Error('...'))"
          icon="bolt"
          description="Report a synthetic error with your own message."
          action={() => {
            Apptics.reportError(
              new Error('Apptics sample: simulated invalid state'),
            );
            return 'exception reported (non-fatal)';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Crash metadata & history"
        subtitle="Extra context attached to subsequent crash reports.">
        <ActionButton
          label="setCrashCustomProperty({...})"
          icon="label"
          description="Attach custom keys to every following crash report."
          action={() => {
            Apptics.setCrashCustomProperty({
              screen: 'CrashScreen',
              experiment: 'A',
              cartItems: 3,
            });
            return 'custom properties set';
          }}
        />
        <ActionButton
          label="showLastSessionCrashedPopup()"
          icon="warning-amber"
          description="Prompts the user about the previous session's crash."
          disabled={Platform.OS !== 'android'}
          disabledNote="Android only"
          action={() => {
            Apptics.showLastSessionCrashedPopup();
            return 'popup requested';
          }}
        />
        <ActionButton
          label="flush()"
          icon="cloud-upload"
          description="Uploads batched stats (including non-fatals) immediately."
          action={() => {
            Apptics.flush();
            return 'flush requested';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Fatal crash"
        subtitle="Verifies automatic crash capture end-to-end.">
        <DestructiveButton
          label="Throw uncaught exception (crashes app)"
          confirmTitle="Crash the app?"
          confirmBody={
            'This throws an uncaught error outside any try/catch. The global ' +
            'handler installed by initCrashTracker() reports it and the app ' +
            'terminates. Reopen the app to confirm the report was sent.'
          }
          onConfirmed={() => {
            // Thrown outside any try/catch so the handler installed by
            // initCrashTracker() reports it as a fatal crash.
            setTimeout(() => {
              throw new Error('Apptics sample: deliberate fatal crash');
            }, 0);
          }}
        />
      </SectionCard>
    </FeatureScaffold>
  );
}
