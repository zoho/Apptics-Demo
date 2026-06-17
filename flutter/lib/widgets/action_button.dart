import 'package:flutter/material.dart';

import '../core/console.dart';

/// A button that runs an asynchronous Apptics plugin call and reports the whole
/// lifecycle to the shared [Console]:
///
///   1. logs an `info` line when the action starts (the "request"),
///   2. logs a `success` line with the returned value when it completes,
///   3. logs an `error` line if it throws.
///
/// This single widget is why every screen can demonstrate success, failure and
/// edge-case (e.g. `null`) outcomes with almost no boilerplate — the screens
/// just describe *what* to call; `ActionButton` handles *observing* it.
class ActionButton extends StatefulWidget {
  const ActionButton({
    super.key,
    required this.label,
    required this.action,
    this.description,
    this.icon,
    this.danger = false,
  });

  /// Button text (usually the API name, e.g. `addEvent()`).
  final String label;

  /// Optional one-line explanation shown beneath the button.
  final String? description;

  final IconData? icon;

  /// Renders the button in a warning colour (used for destructive demos like
  /// triggering a real crash).
  final bool danger;

  /// The plugin call to run. Return a value to have it logged as the result;
  /// return `null`/void if there is nothing meaningful to show.
  final Future<Object?> Function() action;

  @override
  State<ActionButton> createState() => _ActionButtonState();
}

class _ActionButtonState extends State<ActionButton> {
  bool _running = false;

  Future<void> _run() async {
    setState(() => _running = true);
    Console.instance.info('→ ${widget.label}');
    try {
      final result = await widget.action();
      if (result == null) {
        Console.instance.success('${widget.label} completed');
      } else {
        Console.instance.success('${widget.label} → $result');
      }
    } catch (e, s) {
      Console.instance.error('${widget.label} threw: $e');
      debugPrintStack(stackTrace: s, label: widget.label);
    } finally {
      if (mounted) setState(() => _running = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          width: double.infinity,
          child: FilledButton.tonalIcon(
            style: widget.danger
                ? FilledButton.styleFrom(
                    backgroundColor: scheme.errorContainer,
                    foregroundColor: scheme.onErrorContainer,
                  )
                : null,
            onPressed: _running ? null : _run,
            icon: _running
                ? const SizedBox(
                    width: 16,
                    height: 16,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : Icon(widget.icon ?? Icons.play_arrow, size: 18),
            label: Align(
              alignment: Alignment.centerLeft,
              child: Text(widget.label),
            ),
          ),
        ),
        if (widget.description != null)
          Padding(
            padding: const EdgeInsets.only(top: 2, left: 4, bottom: 4),
            child: Text(
              widget.description!,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: Theme.of(context).hintColor,
                  ),
            ),
          ),
      ],
    );
  }
}
