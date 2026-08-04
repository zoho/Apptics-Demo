/**
 * A single line shown in the in-app Console.
 *
 * Every SDK call routed through the sample app produces one or more of these so
 * you can *see* what happened — the request, the response, or the error —
 * without leaving the screen or opening a terminal.
 */

/**
 * Severity / category of a console line. Drives the colour and icon shown in
 * the `ConsolePanel` component.
 */
export type LogLevel =
  /** Informational — e.g. "calling addEvent(...)". */
  | 'info'
  /** The SDK call completed successfully (and the value it returned, if any). */
  | 'success'
  /** A handled failure or an exception thrown by the SDK / native module. */
  | 'error'
  /** A callback fired by the native SDK (push notification, rating prompt...). */
  | 'event';

/**
 * An immutable console line: a [level], a [message], and the [time] it was
 * recorded. `LogEntry` is intentionally tiny — it is pure data so it is easy to
 * render, test, and reason about in isolation.
 */
export interface LogEntry {
  readonly id: number;
  readonly level: LogLevel;
  readonly message: string;
  readonly time: Date;
}

const two = (n: number) => n.toString().padStart(2, '0');

/** `HH:MM:SS` timestamp used as the prefix of each rendered line. */
export function formatTime(time: Date): string {
  return `${two(time.getHours())}:${two(time.getMinutes())}:${two(
    time.getSeconds(),
  )}`;
}
