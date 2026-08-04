import {APLogger} from '@zoho_apptics/apptics-react-native';
import React, {useState} from 'react';

import {ActionButton} from '../components/ActionButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {LabeledInput} from '../components/LabeledInput';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';

/**
 * Demonstrates the remote logger — real-time application logs streamed to the
 * Apptics console so you can debug an issue on a device you do not have.
 *
 * It is disabled by default and must be turned on explicitly. In debug builds
 * the lines are only printed to Metro unless `Apptics.enableDevTesting()` was
 * called (this sample calls it at startup).
 */
export function RemoteLoggerScreen() {
  useScreenTracking('RemoteLoggerScreen');

  const [message, setMessage] = useState('Checkout started');

  return (
    <FeatureScaffold
      intro={
        'APLogger sends logs to Apptics from release builds. Enable it first, ' +
        'then send lines at each level. Every method is variadic — extra ' +
        'arguments (objects, arrays) are stringified into the line.'
      }>
      <SectionCard
        title="Enable / disable"
        subtitle="The logger is off by default; the setting persists across launches.">
        <ActionButton
          label="APLogger.enable()"
          icon="toggle-on"
          action={() => {
            APLogger.enable();
            return 'logger enabled';
          }}
        />
        <ActionButton
          label="APLogger.disable()"
          icon="toggle-off"
          action={() => {
            APLogger.disable();
            return 'logger disabled';
          }}
        />
        <ActionButton
          label="APLogger.isEnabled()"
          icon="help-outline"
          description="Resolves the current status."
          action={() => APLogger.isEnabled()}
        />
      </SectionCard>

      <SectionCard title="Send a log line">
        <LabeledInput
          label="Message"
          value={message}
          onChangeText={setMessage}
        />
        <ActionButton
          label="APLogger.log(message)"
          icon="notes"
          description="Verbose level."
          action={() => {
            APLogger.log(message);
            return 'log sent';
          }}
        />
        <ActionButton
          label="APLogger.debug(message)"
          icon="bug-report"
          action={() => {
            APLogger.debug(message);
            return 'debug sent';
          }}
        />
        <ActionButton
          label="APLogger.info(message)"
          icon="info"
          action={() => {
            APLogger.info(message);
            return 'info sent';
          }}
        />
        <ActionButton
          label="APLogger.warn(message)"
          icon="warning"
          action={() => {
            APLogger.warn(message);
            return 'warn sent';
          }}
        />
        <ActionButton
          label="APLogger.error(message)"
          icon="error"
          action={() => {
            APLogger.error(message);
            return 'error sent';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Structured payloads"
        subtitle="Any extra arguments are appended to the line — pass arrays or objects for context.">
        <ActionButton
          label="APLogger.log(message, [..], {..})"
          icon="data-object"
          action={() => {
            APLogger.log(message, ['cart', 'checkout'], {
              plan: 'Premium',
              seats: 25,
            });
            return 'structured log sent';
          }}
        />
        <ActionButton
          label="APLogger.error(new Error(...))"
          icon="error-outline"
          description="Errors are logged by their message."
          action={() => {
            APLogger.error(new Error('Payment gateway timed out'));
            return 'error object logged';
          }}
        />
      </SectionCard>
    </FeatureScaffold>
  );
}
