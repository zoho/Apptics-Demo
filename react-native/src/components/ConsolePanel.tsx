import MaterialIcons from '@react-native-vector-icons/material-icons';
import React, {useEffect, useRef} from 'react';
import {
  FlatList,
  Pressable,
  StyleSheet,
  Text,
  View,
  ViewStyle,
} from 'react-native';

import {Console, useConsoleEntries} from '../core/console';
import {formatTime, LogEntry, LogLevel} from '../core/logEntry';
import {theme} from '../theme';

/**
 * A live, colour-coded, scrollable view of the shared Console.
 *
 * Shown at the bottom of every feature screen (via `FeatureScaffold`) so the
 * result of each SDK call is immediately visible. It auto-scrolls to the newest
 * entry and offers a clear button.
 */
export function ConsolePanel({height = 220}: {height?: number}) {
  const entries = useConsoleEntries();
  const listRef = useRef<FlatList<LogEntry>>(null);

  useEffect(() => {
    // Stick to the bottom whenever a new line is logged.
    if (entries.length > 0) {
      listRef.current?.scrollToEnd({animated: true});
    }
  }, [entries.length]);

  return (
    <View style={[styles.container, {height} as ViewStyle]}>
      <View style={styles.header}>
        <MaterialIcons
          name="terminal"
          size={16}
          color="#D6D6D6"
          style={styles.headerIcon}
        />
        <Text style={styles.headerTitle}>Console</Text>
        <View style={styles.spacer} />
        <Text style={styles.headerCount}>{entries.length} lines</Text>
        <Pressable
          onPress={Console.clear}
          hitSlop={8}
          accessibilityLabel="Clear console">
          <MaterialIcons name="delete-outline" size={18} color="#9CC7FF" />
        </Pressable>
      </View>

      {entries.length === 0 ? (
        <View style={styles.empty}>
          <Text style={styles.emptyText}>
            No activity yet. Trigger an action above.
          </Text>
        </View>
      ) : (
        <FlatList
          ref={listRef}
          data={entries}
          keyExtractor={item => String(item.id)}
          contentContainerStyle={styles.listContent}
          renderItem={({item}) => <LogLine entry={item} />}
          // Auto-scroll stays correct even as rows of varying height mount.
          onContentSizeChange={() =>
            listRef.current?.scrollToEnd({animated: false})
          }
        />
      )}
    </View>
  );
}

function LogLine({entry}: {entry: LogEntry}) {
  const color = colorFor(entry.level);
  return (
    <Text style={styles.line}>
      <Text style={styles.time}>{formatTime(entry.time)} </Text>
      <Text style={[styles.prefix, {color}]}>{prefixFor(entry.level)} </Text>
      <Text style={{color}}>{entry.message}</Text>
    </Text>
  );
}

function prefixFor(level: LogLevel): string {
  switch (level) {
    case 'info':
      return 'i';
    case 'success':
      return '✓';
    case 'error':
      return '✗';
    case 'event':
      return '🔔';
  }
}

function colorFor(level: LogLevel): string {
  switch (level) {
    case 'info':
      return theme.colors.consoleInfo;
    case 'success':
      return theme.colors.consoleSuccess;
    case 'error':
      return theme.colors.consoleError;
    case 'event':
      return theme.colors.consoleEvent;
  }
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: theme.colors.consoleBg,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: '#555',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: theme.colors.consoleHeaderBg,
    paddingHorizontal: 12,
    paddingVertical: 6,
  },
  headerIcon: {marginRight: 6},
  headerTitle: {
    color: '#D6D6D6',
    fontWeight: '700',
    fontSize: 13,
  },
  spacer: {flex: 1},
  headerCount: {
    color: theme.colors.consoleMuted,
    fontSize: 11,
    marginRight: 12,
  },
  empty: {flex: 1, alignItems: 'center', justifyContent: 'center'},
  emptyText: {color: theme.colors.consoleMuted, fontSize: 12},
  listContent: {paddingHorizontal: 12, paddingVertical: 6},
  line: {
    fontFamily: theme.monospace,
    fontSize: 11,
    lineHeight: 16,
    marginVertical: 1,
  },
  time: {color: theme.colors.consoleMuted},
  prefix: {fontWeight: '700'},
});
