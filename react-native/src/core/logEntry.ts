export type LogLevel =
  | 'info'
  | 'success'
  | 'error'
  | 'event';

export interface LogEntry {
  readonly id: number;
  readonly level: LogLevel;
  readonly message: string;
  readonly time: Date;
}

const two = (n: number) => n.toString().padStart(2, '0');

export function formatTime(time: Date): string {
  return `${two(time.getHours())}:${two(time.getMinutes())}:${two(
    time.getSeconds(),
  )}`;
}
