import {Apptics, TrackingState} from '@zoho_apptics/apptics-react-native';
import React, {useState} from 'react';
import {Platform, Pressable, StyleSheet, Text, View} from 'react-native';

import {ActionButton} from '../components/ActionButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';
import {theme} from '../theme';

/**
 * Demonstrates privacy / consent controls. Apptics models user consent as a
 * `TrackingState` — which of usage / crash data may be collected, and whether
 * the user id (PII) is attached to it — plus helper popups that let the user
 * review or change their choice.
 */
const STATES: {value: TrackingState; label: string; hint: string}[] = [
  {
    value: TrackingState.UsageAndCrashTrackingWithPII,
    label: 'UsageAndCrashTrackingWithPII',
    hint: 'Everything, associated with the user id.',
  },
  {
    value: TrackingState.UsageAndCrashTrackingWithoutPII,
    label: 'UsageAndCrashTrackingWithoutPII',
    hint: 'Everything, anonymised. The default out of the box.',
  },
  {
    value: TrackingState.OnlyUsageTrackingWithPII,
    label: 'OnlyUsageTrackingWithPII',
    hint: 'Events/screens/sessions only, with the user id.',
  },
  {
    value: TrackingState.OnlyUsageTrackingWithoutPII,
    label: 'OnlyUsageTrackingWithoutPII',
    hint: 'Events/screens/sessions only, anonymised.',
  },
  {
    value: TrackingState.OnlyCrashTrackingWithPII,
    label: 'OnlyCrashTrackingWithPII',
    hint: 'Crashes only, with the user id.',
  },
  {
    value: TrackingState.OnlyCrashTrackingWithoutPII,
    label: 'OnlyCrashTrackingWithoutPII',
    hint: 'Crashes only, anonymised.',
  },
  {
    value: TrackingState.NoTracking,
    label: 'NoTracking',
    hint: 'Collect nothing at all.',
  },
];

export function PrivacyScreen() {
  useScreenTracking('PrivacyScreen');

  const [selected, setSelected] = useState<TrackingState>(
    TrackingState.UsageAndCrashTrackingWithPII,
  );

  const selectedLabel =
    STATES.find(s => s.value === selected)?.label ?? String(selected);

  return (
    <FeatureScaffold
      intro={
        'TrackingState controls what the SDK is allowed to collect. Pick a ' +
        'state and apply it, or read back the current one.'
      }>
      <SectionCard
        title="Tracking state"
        subtitle="setTrackingState(state) / getTrackingState()">
        {STATES.map(state => (
          <Pressable
            key={state.label}
            onPress={() => setSelected(state.value)}
            style={styles.option}>
            <View
              style={[
                styles.radio,
                selected === state.value && styles.radioSelected,
              ]}
            />
            <View style={styles.optionText}>
              <Text style={styles.optionLabel}>{state.label}</Text>
              <Text style={styles.optionHint}>{state.hint}</Text>
            </View>
          </Pressable>
        ))}

        <View style={styles.spacer} />

        <ActionButton
          label="setTrackingState(selected)"
          icon="shield"
          action={() => {
            Apptics.setTrackingState(selected);
            return `set to ${selectedLabel}`;
          }}
        />
        <ActionButton
          label="getTrackingState()"
          icon="read-more"
          description="Reads the current consent state back from the SDK."
          action={async () => {
            const state = await Apptics.getTrackingState();
            return (
              STATES.find(s => s.value === state)?.label ?? `unknown (${state})`
            );
          }}
        />
      </SectionCard>

      <SectionCard
        title="Consent UI"
        subtitle="Native popups for reviewing / changing privacy choices.">
        <ActionButton
          label="presentPrivacyReviewPopup()"
          icon="policy"
          description="Shows the SDK-provided privacy review dialog."
          action={() => {
            Apptics.presentPrivacyReviewPopup();
            return 'popup requested';
          }}
        />
        <ActionButton
          label="openPrivacySettings()"
          icon="settings"
          description="Opens the SDK's tracking-settings screen."
          action={() => {
            Apptics.openPrivacySettings();
            return 'settings opened';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Disable everything"
        subtitle="Convenience wrapper that sets NoTracking (iOS only).">
        <ActionButton
          label="disable()"
          icon="block"
          disabled={Platform.OS !== 'ios'}
          disabledNote="iOS only"
          action={() => {
            Apptics.disable();
            return 'tracking disabled';
          }}
        />
      </SectionCard>
    </FeatureScaffold>
  );
}

const styles = StyleSheet.create({
  option: {flexDirection: 'row', alignItems: 'flex-start', paddingVertical: 6},
  radio: {
    width: 16,
    height: 16,
    borderRadius: 8,
    borderWidth: 2,
    borderColor: theme.colors.hint,
    marginTop: 2,
    marginRight: 10,
  },
  radioSelected: {
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primary,
  },
  optionText: {flex: 1},
  optionLabel: {fontSize: 13, fontWeight: '600', color: theme.colors.text},
  optionHint: {fontSize: 11, color: theme.colors.hint, marginTop: 1},
  spacer: {height: 14},
});
