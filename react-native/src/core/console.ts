import {useSyncExternalStore} from 'react';

import {LogEntry, LogLevel} from './logEntry';

const MAX_ENTRIES = 500;

let entries: LogEntry[] = [];
let nextId = 1;
const listeners = new Set<() => void>();

function add(level: LogLevel, message: string) {
  const entry: LogEntry = {id: nextId++, level, message, time: new Date()};
  const next = [...entries, entry];
  entries = next.length > MAX_ENTRIES ? next.slice(-MAX_ENTRIES) : next;
  console.log(`[Apptics][${level}] ${message}`);
  listeners.forEach(l => l());
}

export const Console = {
  info: (message: string) => add('info', message),
  success: (message: string) => add('success', message),
  error: (message: string) => add('error', message),
  event: (message: string) => add('event', message),

  clear() {
    entries = [];
    listeners.forEach(l => l());
  },

  subscribe(listener: () => void) {
    listeners.add(listener);
    return () => {
      listeners.delete(listener);
    };
  },

  getEntries: () => entries,
};

export function useConsoleEntries(): LogEntry[] {
  return useSyncExternalStore(Console.subscribe, Console.getEntries);
}

export function describeResult(value: unknown): string {
  if (value === undefined) {
    return 'undefined';
  }
  if (value === null) {
    return 'null';
  }
  if (typeof value === 'object') {
    try {
      return JSON.stringify(value);
    } catch {
      return String(value);
    }
  }
  return String(value);
}
