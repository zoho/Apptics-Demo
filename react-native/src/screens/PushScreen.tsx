import {
  APNotificationOption,
  AppticsPushMessages,
} from '@zoho_apptics/apptics-react-native';
import React from 'react';
import {Platform} from 'react-native';

import {ActionButton} from '../components/ActionButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';

/**
 * Demonstrates push notifications.
 *
 * The handlers are registered once at startup in `core/appticsBootstrap.ts` by
 * assigning to `AppticsPushModuleEmitter` — the library forwards every native
 * event to whatever is assigned there. All three callbacks funnel into the
 * shared Console, so any notification you send from the Apptics console shows
 * up as a 🔔 line below.
 */
export function PushScreen() {
  useScreenTracking('PushScreen');

  const isIOS = Platform.OS === 'ios';

  return (
    <FeatureScaffold
      intro={
        'Handlers are already wired up at app start. Send a test push from the ' +
        'Apptics console and watch it appear below (foreground) — or open the ' +
        'app from a notification to see the click callback. Requires FCM ' +
        '(Android) / APNs (iOS) credentials uploaded to the console.'
      }>
      <SectionCard
        title="Service & registration"
        subtitle={
          isIOS
            ? 'Start the messaging service and request the APNs token.'
            : 'On Android startService() is a no-op and registerPushNotification() ' +
              'just tells the native side that JS listeners are ready.'
        }>
        <ActionButton
          label="startService()"
          icon="play-circle"
          description="iOS only — starts the Apptics messaging service."
          action={() => {
            AppticsPushMessages.startService();
            return 'service started';
          }}
        />
        <ActionButton
          label="registerPushNotification()"
          icon="app-registration"
          description="iOS: requests the OS push token. Android: flushes queued events to JS."
          action={() => {
            AppticsPushMessages.registerPushNotification();
            return 'registration requested';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Foreground presentation (iOS)"
        subtitle="Controls what iOS shows when a notification arrives while the app is open.">
        {(
          [
            ['all', APNotificationOption.all, 'notifications-active', 'Banner and sound.'],
            ['banner', APNotificationOption.banner, 'chat-bubble-outline', 'Banner only.'],
            ['sound', APNotificationOption.sound, 'volume-up', 'Sound only.'],
            ['none', APNotificationOption.none, 'notifications-off', 'Nothing — handle it yourself.'],
          ] as const
        ).map(([name, option, icon, description]) => (
          <ActionButton
            key={name}
            label={`setForegroundNotificationOptions('${name}')`}
            icon={icon}
            description={description}
            disabled={!isIOS}
            disabledNote="iOS only"
            action={() => {
              AppticsPushMessages.setForegroundNotificationOptions(option);
              return `presentation = ${name}`;
            }}
          />
        ))}
      </SectionCard>

      <SectionCard
        title="How to test"
        subtitle={
          '1. Upload FCM (Android) / APNs (iOS) credentials to the Apptics console.\n' +
          '2. Run on a physical device for iOS.\n' +
          '3. Send a campaign or test notification from the console.\n' +
          '4. Foreground: a 🔔 line appears in the console below.\n' +
          '5. Background/terminated: the OS shows the notification; tapping it ' +
          'fires the click callback once JS is up.'
        }
      />
    </FeatureScaffold>
  );
}
