/// A single line shown in the in-app [Console].
///
/// Every plugin call routed through the sample app produces one or more of
/// these so the user can *see* what happened — the request, the response, or
/// the error — without leaving the screen or opening a terminal.
library;

/// Severity / category of a console line. Drives the colour and icon shown in
/// the `ConsolePanel` widget.
enum LogLevel {
  /// Informational — e.g. "calling addEvent(...)".
  info,

  /// The plugin call completed successfully (and, where relevant, the value it
  /// returned).
  success,

  /// A handled failure or an exception thrown by the plugin / platform channel.
  error,

  /// A push-notification event received from the native SDK.
  event,
}

/// An immutable console line: a [level], a [message], and the [time] it was
/// recorded. `LogEntry` is intentionally tiny — it is pure data so it is easy
/// to render, test, and reason about in isolation.
class LogEntry {
  LogEntry(this.level, this.message, this.time);

  final LogLevel level;
  final String message;
  final DateTime time;

  /// `HH:MM:SS` timestamp used as the prefix of each rendered line.
  String get formattedTime =>
      '${_two(time.hour)}:${_two(time.minute)}:${_two(time.second)}';

  static String _two(int n) => n.toString().padLeft(2, '0');
}
