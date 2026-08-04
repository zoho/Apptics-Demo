import MaterialIcons from '@react-native-vector-icons/material-icons';
import React from 'react';
import {Alert, Pressable, StyleSheet, Text, View} from 'react-native';

import {Console} from '../core/console';
import {theme} from '../theme';

/**
 * A red button that asks for confirmation before running something the user
 * cannot undo — crashing the app, for instance. Used by the Crash screen so a
 * mistaken tap does not kill the session.
 */
export function DestructiveButton({
  label,
  confirmTitle,
  confirmBody,
  onConfirmed,
  disabled = false,
  disabledNote,
}: {
  label: string;
  confirmTitle: string;
  confirmBody: string;
  onConfirmed: () => void | Promise<void>;
  disabled?: boolean;
  disabledNote?: string;
}) {
  const press = () => {
    Alert.alert(confirmTitle, confirmBody, [
      {text: 'Cancel', style: 'cancel'},
      {
        text: 'Proceed',
        style: 'destructive',
        onPress: async () => {
          Console.info(`→ ${label}`);
          try {
            await onConfirmed();
          } catch (e) {
            Console.error(
              `${label} threw: ${(e as Error)?.message ?? String(e)}`,
            );
          }
        },
      },
    ]);
  };

  return (
    <View style={styles.wrapper}>
      <Pressable
        onPress={press}
        disabled={disabled}
        style={({pressed}) => [
          styles.button,
          disabled && styles.disabled,
          pressed && styles.pressed,
        ]}>
        <MaterialIcons
          name="dangerous"
          size={18}
          color={theme.colors.onDangerContainer}
          style={styles.icon}
        />
        <Text style={styles.label}>
          {disabled && disabledNote ? `${label}  (${disabledNote})` : label}
        </Text>
      </Pressable>
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
    backgroundColor: theme.colors.dangerContainer,
  },
  disabled: {opacity: 0.5},
  pressed: {opacity: 0.7},
  icon: {width: 18, marginRight: 8},
  label: {
    flex: 1,
    fontSize: 13,
    fontWeight: '700',
    color: theme.colors.onDangerContainer,
  },
});
