import React from 'react';
import {StyleSheet, Text, TextInput, View} from 'react-native';

import {theme} from '../theme';

export function LabeledInput({
  label,
  value,
  onChangeText,
  helperText,
  multiline = false,
  autoCapitalize = 'none',
}: {
  label: string;
  value: string;
  onChangeText: (text: string) => void;
  helperText?: string;
  multiline?: boolean;
  autoCapitalize?: 'none' | 'sentences' | 'words' | 'characters';
}) {
  return (
    <View style={styles.wrapper}>
      <Text style={styles.label}>{label}</Text>
      <TextInput
        style={[styles.input, multiline && styles.inputMultiline]}
        value={value}
        onChangeText={onChangeText}
        multiline={multiline}
        autoCapitalize={autoCapitalize}
        autoCorrect={false}
        placeholderTextColor={theme.colors.hint}
      />
      {helperText ? <Text style={styles.helper}>{helperText}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {marginBottom: 12},
  label: {
    fontSize: 11,
    fontWeight: '600',
    color: theme.colors.hint,
    marginBottom: 4,
    textTransform: 'uppercase',
  },
  input: {
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: theme.colors.border,
    borderRadius: 8,
    paddingHorizontal: 10,
    paddingVertical: 8,
    fontSize: 14,
    color: theme.colors.text,
    backgroundColor: theme.colors.background,
  },
  inputMultiline: {minHeight: 64, textAlignVertical: 'top'},
  helper: {marginTop: 4, fontSize: 11, color: theme.colors.hint},
});
