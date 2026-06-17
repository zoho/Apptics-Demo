import 'package:flutter/material.dart';

/// Descriptor for one Apptics module, used to build the home grid. Keeping the
/// list of features as data (rather than hard-coded tiles) makes the home
/// screen trivial and the set of demos easy to extend.
class Feature {
  const Feature({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.builder,
  });

  final String title;
  final String subtitle;
  final IconData icon;

  /// Builds the screen to open when the tile is tapped.
  final WidgetBuilder builder;
}
