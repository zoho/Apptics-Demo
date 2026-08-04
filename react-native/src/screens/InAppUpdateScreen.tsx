import {
  AppticsAppUpdate,
  UpdateStats,
} from '@zoho_apptics/apptics-react-native';
import React, {useState} from 'react';
import {Platform} from 'react-native';

import {ActionButton} from '../components/ActionButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';

/**
 * Demonstrates in-app version alerts.
 *
 * The update policy (flexible / immediate / forced, target version, copy) is
 * configured server-side in the Apptics console; the SDK fetches it and
 * `showVersionAlertPopup()` renders the appropriate native dialog. Nothing
 * appears unless the console is configured with a newer version targeting this
 * build.
 *
 * `checkForUpdate()` returns the raw configuration instead, so you can build
 * your own UI — and then report what the user did with `sendUpdateStat()`.
 */
export function InAppUpdateScreen() {
  useScreenTracking('InAppUpdateScreen');

  // Captured from checkForUpdate() so the stat buttons can reference a real id.
  const [updateId, setUpdateId] = useState<string | null>(null);

  return (
    <FeatureScaffold
      intro={
        'Update prompts only appear when the console is configured with a ' +
        'newer version targeting this build. Otherwise the calls succeed but ' +
        'no dialog is shown and checkForUpdate() returns null.'
      }>
      <SectionCard title="Built-in alert">
        <ActionButton
          label="showVersionAlertPopup()"
          icon="system-update"
          description="Fetches the update config and shows the native alert if applicable."
          action={() => {
            AppticsAppUpdate.showVersionAlertPopup();
            return 'update check requested';
          }}
        />
        <ActionButton
          label="disableUpdatePopupIfNotInstalledFromPlayStore(true)"
          icon="store"
          description="Suppresses the prompt for sideloaded builds."
          disabled={Platform.OS !== 'android'}
          disabledNote="Android only"
          action={() => {
            AppticsAppUpdate.disableUpdatePopupIfNotInstalledFromPlayStore(true);
            return 'sideload suppression enabled';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Build your own flow"
        subtitle={
          'checkForUpdate() resolves to the console configuration — updateid, ' +
          'featureTitle, features, option (1 flexible / 2 immediate / 3 force), ' +
          'reminderDays, customStoreUrl … — or null when nothing is configured.'
        }>
        <ActionButton
          label="checkForUpdate()"
          icon="data-object"
          description="Returns the raw update configuration."
          action={async () => {
            const data = (await AppticsAppUpdate.checkForUpdate()) as Record<
              string,
              unknown
            > | null;
            const id = data?.updateid;
            setUpdateId(id == null ? null : String(id));
            return data ?? '(null — no update configured for this version)';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Report engagement"
        subtitle={
          updateId
            ? `Using updateid ${updateId} from the last checkForUpdate().`
            : 'Run checkForUpdate() first to pick up a real update id.'
        }>
        {(
          [
            ['Impression', UpdateStats.Impression, 'visibility', 'Your custom prompt was shown.'],
            ['UpdateClick', UpdateStats.UpdateClick, 'download', 'The user tapped Update.'],
            ['RemindLaterClick', UpdateStats.RemindLaterClick, 'schedule', 'The user postponed.'],
            ['IgnoreClick', UpdateStats.IgnoreClick, 'block', 'The user dismissed it for good.'],
          ] as const
        ).map(([name, stat, icon, description]) => (
          <ActionButton
            key={name}
            label={`sendUpdateStat(id, UpdateStats.${name})`}
            icon={icon}
            description={description}
            disabled={!updateId}
            disabledNote="needs an update id"
            action={() => {
              AppticsAppUpdate.sendUpdateStat(updateId as string, stat);
              return `${name} reported for ${updateId}`;
            }}
          />
        ))}
      </SectionCard>
    </FeatureScaffold>
  );
}
