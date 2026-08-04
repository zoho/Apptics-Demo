import React, {PropsWithChildren} from 'react';
import {
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';

import {theme} from '../theme';
import {ConsolePanel} from './ConsolePanel';

export function FeatureScaffold({
  intro,
  children,
}: PropsWithChildren<{intro?: string}>) {
  return (
    <KeyboardAvoidingView
      style={styles.root}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled">
        {intro ? <Text style={styles.intro}>{intro}</Text> : null}
        {children}
      </ScrollView>
      {/* The live console — a shared singleton, so notifications and other
          native callbacks show up here too. */}
      <View>
        <ConsolePanel />
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  root: {flex: 1, backgroundColor: theme.colors.background},
  scroll: {flex: 1},
  scrollContent: {paddingBottom: 16},
  intro: {
    padding: 16,
    fontSize: 13,
    lineHeight: 19,
    color: theme.colors.hint,
  },
});
