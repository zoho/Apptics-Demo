import {AppticsRemoteConfig} from '@zoho_apptics/apptics-react-native';
import React, {useState} from 'react';
import {StyleSheet, Switch, Text, View} from 'react-native';

import {ActionButton} from '../components/ActionButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {LabeledInput} from '../components/LabeledInput';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';
import {theme} from '../theme';

/**
 * Demonstrates server-driven configuration.
 *
 * Define parameters and targeting conditions in the Apptics console, then read
 * them here. Custom condition values let the console target a config by an
 * attribute only your app knows (e.g. the user's plan tier).
 */
export function RemoteConfigScreen() {
  useScreenTracking('RemoteConfigScreen');

  const [key, setKey] = useState('color');
  const [conditionKey, setConditionKey] = useState('user_tier');
  const [conditionValue, setConditionValue] = useState('premium');

  // Optional flags on getStringValue.
  const [coldFetch, setColdFetch] = useState(false);
  const [fallbackOffline, setFallbackOffline] = useState(false);

  return (
    <FeatureScaffold
      intro={
        'Reading an unknown key resolves to null — a normal edge case your app ' +
        'should handle by falling back to a default.'
      }>
      <SectionCard title="Read a value">
        <LabeledInput
          label="Parameter key"
          value={key}
          onChangeText={setKey}
          helperText="Try a configured key, then a made-up one (→ null)."
        />

        <ToggleRow
          label="coldFetch"
          hint="Bypass the cache and hit the network. Limited to 3 calls/minute; past that the call returns null or the offline value."
          value={coldFetch}
          onValueChange={setColdFetch}
        />
        <ToggleRow
          label="fallbackWithOfflineValue"
          hint="Return the previously fetched value if the network fails."
          value={fallbackOffline}
          onValueChange={setFallbackOffline}
        />

        <ActionButton
          label="getStringValue(key, coldFetch, fallbackWithOfflineValue)"
          icon="download"
          action={async () => {
            const value = await AppticsRemoteConfig.getStringValue(
              key,
              coldFetch,
              fallbackOffline,
            );
            // Edge case: surface null explicitly rather than silently
            // treating it as an empty string.
            return value ?? '(null — key not configured)';
          }}
        />
      </SectionCard>

      <SectionCard
        title="Custom condition"
        subtitle="Provide an app-side attribute the console can target configs on.">
        <LabeledInput
          label="Condition key"
          value={conditionKey}
          onChangeText={setConditionKey}
        />
        <LabeledInput
          label="Condition value"
          value={conditionValue}
          onChangeText={setConditionValue}
        />
        <ActionButton
          label="setCustomCondition(key, value)"
          icon="tune"
          description="Set it before reading a parameter that targets this condition."
          action={() => {
            AppticsRemoteConfig.setCustomCondition(
              conditionKey,
              conditionValue,
            );
            return `condition set: ${conditionKey}=${conditionValue}`;
          }}
        />
      </SectionCard>
    </FeatureScaffold>
  );
}

function ToggleRow({
  label,
  hint,
  value,
  onValueChange,
}: {
  label: string;
  hint: string;
  value: boolean;
  onValueChange: (v: boolean) => void;
}) {
  return (
    <View style={styles.row}>
      <View style={styles.rowText}>
        <Text style={styles.rowLabel}>{label}</Text>
        <Text style={styles.rowHint}>{hint}</Text>
      </View>
      <Switch value={value} onValueChange={onValueChange} />
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
    gap: 12,
  },
  rowText: {flex: 1},
  rowLabel: {
    fontSize: 13,
    fontWeight: '600',
    color: theme.colors.text,
    fontFamily: theme.monospace,
  },
  rowHint: {fontSize: 11, lineHeight: 15, color: theme.colors.hint, marginTop: 2},
});
