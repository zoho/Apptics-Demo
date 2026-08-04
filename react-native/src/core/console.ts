import {useSyncExternalStore} from 'react';

import {LogEntry, LogLevel} from './logEntry';

/**
 * A process-wide, observable log buffer.
 *
 * This is the heart of the sample's "show, don't tell" approach: every screen
 * invokes the *real* Apptics API and then reports what happened here. The
 * `ConsolePanel` component subscribes to this store and re-renders live, so
 * success values, `null`/edge results and thrown exceptions are all visible
 * in-app.
 *
 * It is a module-level singleton because there is conceptually one console for
 * the whole app — native push callbacks and the global crash handler write to
 * it too.
 */

/** The buffer is capped so a long-running session does not grow unbounded. */
const MAX_ENTRIES = 500;

let entries: LogEntry[] = [];
let nextId = 1;
const listeners = new Set<() => void>();

function add(level: LogLevel, message: string) {
  const entry: LogEntry = {id: nextId++, level, message, time: new Date()};
  // Replace (not mutate) the array so `useSyncExternalStore` sees a new
  // snapshot reference and re-renders.
  const next = [...entries, entry];
  entries = next.length > MAX_ENTRIES ? next.slice(-MAX_ENTRIES) : next;
  // Mirror to Metro's console too, so the same information is available in the
  // terminal logs.
  console.log(`[Apptics][${level}] ${message}`);
  listeners.forEach(l => l());
}

export const Console = {
  info: (message: string) => add('info', message),
  success: (message: string) => add('success', message),
  error: (message: string) => add('error', message),
  /** A callback fired *by* the SDK rather than a call we made. */
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

/** Live, re-rendering view of the console buffer. */
export function useConsoleEntries(): LogEntry[] {
  return useSyncExternalStore(Console.subscribe, Console.getEntries);
}

/**
 * Formats any value returned by an SDK call for display. Objects are shown as
 * JSON; `undefined`/`null` are surfaced explicitly rather than swallowed,
 * because "this API returned null" is itself a result worth seeing.
 */
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
