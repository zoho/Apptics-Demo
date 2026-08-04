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

/**
 * A button that runs an Apptics SDK call and reports the whole lifecycle to the
 * shared Console:
 *
 *   1. logs an `info` line when the action starts (the "request"),
 *   2. logs a `success` line with the returned value when it completes,
 *   3. logs an `error` line if it throws.
 *
 * This single component is why every screen can demonstrate success, failure
 * and edge-case (e.g. `null`) outcomes with almost no boilerplate — the screens
 * just describe *what* to call; `ActionButton` handles *observing* it.
 *
 * Most Apptics React Native methods are fire-and-forget (they return `void`);
 * a few return a Promise. `action` may return either, so screens can hand back
 * a short string describing what happened when there is no value to show.
 */
export interface ActionButtonProps {
  /** Button text (usually the API name, e.g. `addEvent()`). */
  label: string;
  /** Optional one-line explanation shown beneath the button. */
  description?: string;
  /** Material Icons name. Defaults to a play arrow, as in the Flutter sample. */
  icon?: string;
  /** Renders the button in a warning colour (destructive demos). */
  danger?: boolean;
  disabled?: boolean;
  /** Shown appended to the label when `disabled` (e.g. "Android only"). */
  disabledNote?: string;
  /**
   * The SDK call to run. Return a value to have it logged as the result;
   * return nothing if there is nothing meaningful to show.
   */
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
