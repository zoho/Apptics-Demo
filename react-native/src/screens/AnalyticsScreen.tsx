import {
  Apptics,
  AppticsDefinedEvents,
} from '@zoho_apptics/apptics-react-native';
import React from 'react';

import {ActionButton} from '../components/ActionButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';

/**
 * Demonstrates the core analytics surface of the `Apptics` namespace: custom
 * events, the predefined ("defined") event constants, screen tracking, and
 * flushing queued stats to the server.
 */
export function AnalyticsScreen() {
  useScreenTracking('AnalyticsScreen');

  return (
    <FeatureScaffold
      intro={
        'Events and screens are batched on-device and synced periodically. ' +
        'Use flush() to force an immediate sync. Watch the console for results.'
      }>
      <SectionCard
        title="Custom events"
        subtitle={
          'addEvent(name, group, properties). Events are grouped; properties ' +
          'add structured context (String / number / boolean values).'
        }>
        <ActionButton
          label="addEvent('purchase_done', 'ecommerce', {})"
          icon="touch-app"
          description="A simple event with no properties."
          action={() => {
            Apptics.addEvent('purchase_done', 'ecommerce', {});
            return 'event queued';
          }}
        />
        <ActionButton
          label="addEvent(… , {item, isTrial, amount})"
          icon="data-object"
          description="Same event with typed properties attached."
          action={() => {
            Apptics.addEvent('purchase_done', 'ecommerce', {
              item: 'Pro Plan',
              isTrial: false,
              amount: 49.99,
            });
            return 'event with properties queued';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Defined events"
        subtitle="Apptics ships standard event/group names in AppticsDefinedEvents.">
        <ActionButton
          label="addEvent(AP_APP_FOREGROUND, AP_APP_LIFE_CYCLE)"
          icon="flag"
          description="A predefined lifecycle event."
          action={() => {
            Apptics.addEvent(
              AppticsDefinedEvents.AP_APP_FOREGROUND,
              AppticsDefinedEvents.AP_APP_LIFE_CYCLE,
              {},
            );
            return 'defined event queued';
          }}
        />
        <ActionButton
          label="addEvent(AP_USER_LOGIN, AP_USER_LIFE_CYCLE)"
          icon="login"
          action={() => {
            Apptics.addEvent(
              AppticsDefinedEvents.AP_USER_LOGIN,
              AppticsDefinedEvents.AP_USER_LIFE_CYCLE,
              {},
            );
            return 'defined event queued';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Screen tracking"
        subtitle={
          'Call screenAttached when a screen appears and screenDetached when ' +
          'it disappears to measure screen views & dwell time. This app does ' +
          'it automatically via the useScreenTracking hook — these buttons ' +
          'fire the calls for an imaginary extra screen.'
        }>
        <ActionButton
          label="screenAttached('CheckoutScreen')"
          icon="visibility"
          action={() => {
            Apptics.screenAttached('CheckoutScreen');
            return 'attached';
          }}
        />
        <ActionButton
          label="screenDetached('CheckoutScreen')"
          icon="visibility-off"
          action={() => {
            Apptics.screenDetached('CheckoutScreen');
            return 'detached';
          }}
        />
      </SectionCard>

      <SectionCard title="Sync">
        <ActionButton
          label="flush()"
          icon="cloud-upload"
          description="Forces an immediate upload of all queued events/screens/sessions."
          action={() => {
            Apptics.flush();
            return 'flush requested';
          }}
        />
      </SectionCard>
    </FeatureScaffold>
  );
}
