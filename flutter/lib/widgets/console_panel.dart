import 'package:flutter/material.dart';

import '../core/console.dart';
import '../core/log_entry.dart';

/// A live, colour-coded, scrollable view of the shared [Console].
///
/// Shown at the bottom of every feature screen (via `FeatureScaffold`) so the
/// result of each plugin call is immediately visible. It auto-scrolls to the
/// newest entry and offers a clear button.
class ConsolePanel extends StatefulWidget {
  const ConsolePanel({super.key, this.height = 220});

  /// Fixed height of the panel. The list inside scrolls.
  final double height;

  @override
  State<ConsolePanel> createState() => _ConsolePanelState();
}

class _ConsolePanelState extends State<ConsolePanel> {
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    // Re-render whenever a new line is logged and stick to the bottom.
    Console.instance.addListener(_onConsoleChanged);
  }

  @override
  void dispose() {
    Console.instance.removeListener(_onConsoleChanged);
    _scrollController.dispose();
    super.dispose();
  }

  void _onConsoleChanged() {
    if (!mounted) return;
    setState(() {});
    // Defer until after layout so maxScrollExtent reflects the new line.
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.jumpTo(_scrollController.position.maxScrollExtent);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final entries = Console.instance.entries;
    return Container(
      height: widget.height,
      decoration: BoxDecoration(
        color: const Color(0xFF1E1E1E),
        border: Border(top: BorderSide(color: Colors.grey.shade700)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Header with title + clear button.
          Container(
            color: const Color(0xFF2D2D2D),
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
            child: Row(
              children: [
                const Icon(Icons.terminal, size: 16, color: Colors.white70),
                const SizedBox(width: 8),
                const Text(
                  'Console',
                  style: TextStyle(
                    color: Colors.white70,
                    fontWeight: FontWeight.bold,
                    fontSize: 13,
                  ),
                ),
                const Spacer(),
                Text(
                  '${entries.length} lines',
                  style: const TextStyle(color: Colors.white38, fontSize: 11),
                ),
                IconButton(
                  tooltip: 'Clear console',
                  icon: const Icon(Icons.delete_outline,
                      size: 18, color: Colors.white54),
                  onPressed: Console.instance.clear,
                ),
              ],
            ),
          ),
          Expanded(
            child: entries.isEmpty
                ? const Center(
                    child: Text(
                      'No activity yet. Trigger an action above.',
                      style: TextStyle(color: Colors.white38, fontSize: 12),
                    ),
                  )
                : ListView.builder(
                    controller: _scrollController,
                    padding: const EdgeInsets.symmetric(
                        horizontal: 12, vertical: 6),
                    itemCount: entries.length,
                    itemBuilder: (context, index) => _LogLine(entries[index]),
                  ),
          ),
        ],
      ),
    );
  }
}

class _LogLine extends StatelessWidget {
  const _LogLine(this.entry);

  final LogEntry entry;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: RichText(
        text: TextSpan(
          style: const TextStyle(
            fontFamily: 'monospace',
            fontSize: 12,
            height: 1.3,
          ),
          children: [
            TextSpan(
              text: '${entry.formattedTime} ',
              style: const TextStyle(color: Colors.white38),
            ),
            TextSpan(
              text: '${_prefix(entry.level)} ',
              style: TextStyle(
                  color: _color(entry.level), fontWeight: FontWeight.bold),
            ),
            TextSpan(
              text: entry.message,
              style: TextStyle(color: _color(entry.level)),
            ),
          ],
        ),
      ),
    );
  }

  static String _prefix(LogLevel level) {
    switch (level) {
      case LogLevel.info:
        return 'ℹ';
      case LogLevel.success:
        return '✓';
      case LogLevel.error:
        return '✗';
      case LogLevel.event:
        return '🔔';
    }
  }

  static Color _color(LogLevel level) {
    switch (level) {
      case LogLevel.info:
        return Colors.lightBlueAccent;
      case LogLevel.success:
        return Colors.greenAccent;
      case LogLevel.error:
        return Colors.redAccent;
      case LogLevel.event:
        return Colors.amberAccent;
    }
  }
}
