import 'package:flutter/foundation.dart';

import 'log_entry.dart';

/// A process-wide, observable log buffer.
///
/// This is the heart of the sample's "show, don't tell" approach: every screen
/// invokes the *real* Apptics plugin API and then reports what happened here.
/// The `ConsolePanel` widget listens to this notifier and re-renders live, so
/// success values, `null`/edge results and thrown exceptions are all visible
/// in-app.
///
/// It is a singleton because there is conceptually one console for the whole
/// app (push-notification callbacks fired from background isolates / native
/// code also write to it). Access it via [Console.instance].
class Console extends ChangeNotifier {
  Console._();

  static final Console instance = Console._();

  /// Most recent entries are kept at the *end* of the list. The buffer is
  /// capped so a long-running session does not grow unbounded.
  final List<LogEntry> _entries = <LogEntry>[];
  static const int _maxEntries = 500;

  /// Read-only view for the UI.
  List<LogEntry> get entries => List.unmodifiable(_entries);

  void info(String message) => _add(LogLevel.info, message);

  void success(String message) => _add(LogLevel.success, message);

  void error(String message) => _add(LogLevel.error, message);

  void event(String message) => _add(LogLevel.event, message);

  void clear() {
    _entries.clear();
    notifyListeners();
  }

  void _add(LogLevel level, String message) {
    _entries.add(LogEntry(level, message, DateTime.now()));
    if (_entries.length > _maxEntries) {
      _entries.removeRange(0, _entries.length - _maxEntries);
    }
    // Mirror to the debug console too, so the same information is available in
    // `flutter run` logs.
    debugPrint('[Apptics][${level.name}] $message');
    notifyListeners();
  }
}
