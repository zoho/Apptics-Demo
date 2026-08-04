import MaterialIcons from '@react-native-vector-icons/material-icons';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';
import React from 'react';
import {FlatList, Pressable, StyleSheet, Text, View} from 'react-native';

import {ConsolePanel} from '../components/ConsolePanel';
import {useScreenTracking} from '../core/useScreenTracking';
import {Feature, FEATURES, RootStackParamList} from '../navigation';
import {theme} from '../theme';

type Props = NativeStackScreenProps<RootStackParamList, 'Home'>;

// Pad to an even count so the last row lines up with the rest of the grid.
const GRID: (Feature | null)[] =
  FEATURES.length % 2 === 0 ? FEATURES : [...FEATURES, null];

/**
 * Landing screen: a grid of every Apptics module the sample demonstrates.
 *
 * Like every other screen it reports itself with `useScreenTracking`, so
 * screen views and dwell time show up correctly in the Apptics console.
 */
export function HomeScreen({navigation}: Props) {
  useScreenTracking('HomeScreen');

  return (
    <View style={styles.root}>
      <FlatList
        data={GRID}
        keyExtractor={(item, index) => item?.route ?? `spacer-${index}`}
        numColumns={2}
        contentContainerStyle={styles.grid}
        columnWrapperStyle={styles.row}
        renderItem={({item}) =>
          item ? (
            <FeatureTile
              feature={item}
              onPress={() => navigation.navigate(item.route)}
            />
          ) : (
            // Keeps a trailing odd tile at half width instead of letting it
            // stretch across the row.
            <View style={styles.tileSpacer} />
          )
        }
      />
      {/* The console is shared app-wide, so anything logged on a feature screen
          (or by a push callback) is still visible after you navigate back. */}
      <ConsolePanel height={160} />
    </View>
  );
}

function FeatureTile({
  feature,
  onPress,
}: {
  feature: Feature;
  onPress: () => void;
}) {
  return (
    <Pressable
      onPress={onPress}
      style={({pressed}) => [styles.tile, pressed && styles.tilePressed]}>
      <MaterialIcons
        name={feature.icon as never}
        size={32}
        color={theme.colors.primary}
        style={styles.icon}
      />
      <Text style={styles.title} numberOfLines={1}>
        {feature.title}
      </Text>
      <Text style={styles.subtitle} numberOfLines={2}>
        {feature.subtitle}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  root: {flex: 1, backgroundColor: theme.colors.background},
  grid: {padding: 12},
  row: {gap: 12, marginBottom: 12},
  tile: {
    flex: 1,
    backgroundColor: theme.colors.surface,
    borderRadius: theme.radius,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: theme.colors.border,
    padding: 14,
    minHeight: 110,
    alignItems: 'center',
    justifyContent: 'center',
  },
  tilePressed: {opacity: 0.6},
  tileSpacer: {flex: 1},
  icon: {marginBottom: 8},
  title: {
    fontSize: 14,
    fontWeight: '700',
    color: theme.colors.text,
    textAlign: 'center',
  },
  subtitle: {
    marginTop: 3,
    fontSize: 11,
    lineHeight: 15,
    color: theme.colors.hint,
    textAlign: 'center',
  },
});
