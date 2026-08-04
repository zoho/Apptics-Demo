import React, {PropsWithChildren} from 'react';
import {StyleSheet, Text, View} from 'react-native';

import {theme} from '../theme';

export function SectionCard({
  title,
  subtitle,
  children,
}: PropsWithChildren<{title: string; subtitle?: string}>) {
  return (
    <View style={styles.card}>
      <Text style={styles.title}>{title}</Text>
      {subtitle ? <Text style={styles.subtitle}>{subtitle}</Text> : null}
      {children ? <View style={styles.body}>{children}</View> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: theme.colors.surface,
    borderRadius: theme.radius,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: theme.colors.border,
    marginHorizontal: 12,
    marginVertical: 6,
    padding: 14,
  },
  title: {fontSize: 15, fontWeight: '700', color: theme.colors.text},
  subtitle: {
    marginTop: 4,
    fontSize: 12,
    lineHeight: 17,
    color: theme.colors.hint,
  },
  body: {marginTop: 12},
});
