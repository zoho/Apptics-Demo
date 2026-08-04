import MaterialIcons from '@react-native-vector-icons/material-icons';
import React, {useState} from 'react';
import {
  ActivityIndicator,
  Pressable,
  StyleSheet,
  Text,
  View,
} from 'react-native';

import {Console, describeResult} from '../core/console';
import {theme} from '../theme';

export interface ActionButtonProps {
  label: string;
  description?: string;
  icon?: string;
  danger?: boolean;
  disabled?: boolean;
  disabledNote?: string;
  action: () => unknown | Promise<unknown>;
}

export function ActionButton({
  label,
  description,
  icon,
  danger = false,
  disabled = false,
  disabledNote,
  action,
}: ActionButtonProps) {
  const [running, setRunning] = useState(false);

  const run = async () => {
    setRunning(true);
    Console.info(`→ ${label}`);
    try {
      const result = await action();
      if (result === undefined || result === null) {
        Console.success(`${label} completed`);
      } else {
        Console.success(`${label} → ${describeResult(result)}`);
      }
    } catch (e) {
      Console.error(`${label} threw: ${(e as Error)?.message ?? String(e)}`);
    } finally {
      setRunning(false);
    }
  };

  const isDisabled = disabled || running;

  return (
    <View style={styles.wrapper}>
      <Pressable
        onPress={run}
        disabled={isDisabled}
        style={({pressed}) => [
          styles.button,
          danger ? styles.buttonDanger : styles.buttonNormal,
          isDisabled && styles.buttonDisabled,
          pressed && styles.buttonPressed,
        ]}>
        {running ? (
          <ActivityIndicator
            size="small"
            color={danger ? theme.colors.danger : theme.colors.primary}
            style={styles.spinner}
          />
        ) : (
          <MaterialIcons
            name={(icon ?? (danger ? 'dangerous' : 'play-arrow')) as never}
            size={18}
            color={
              danger
                ? theme.colors.onDangerContainer
                : theme.colors.onPrimaryContainer
            }
            style={styles.icon}
          />
        )}
        <Text
          numberOfLines={2}
          style={[
            styles.label,
            danger ? styles.textDanger : styles.textNormal,
            isDisabled && styles.textDisabled,
          ]}>
          {disabled && disabledNote ? `${label}  (${disabledNote})` : label}
        </Text>
      </Pressable>
      {description ? (
        <Text style={styles.description}>{description}</Text>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {marginBottom: 10},
  button: {
    flexDirection: 'row',
    alignItems: 'center',
    borderRadius: 10,
    paddingVertical: 11,
    paddingHorizontal: 12,
  },
  buttonNormal: {backgroundColor: theme.colors.primaryContainer},
  buttonDanger: {backgroundColor: theme.colors.dangerContainer},
  buttonDisabled: {opacity: 0.5},
  buttonPressed: {opacity: 0.7},
  spinner: {width: 18, marginRight: 8},
  icon: {width: 18, marginRight: 8},
  label: {flex: 1, fontSize: 13, fontWeight: '600'},
  textNormal: {color: theme.colors.onPrimaryContainer},
  textDanger: {color: theme.colors.onDangerContainer},
  textDisabled: {opacity: 0.8},
  description: {
    marginTop: 3,
    marginLeft: 4,
    fontSize: 11,
    color: theme.colors.hint,
  },
});
