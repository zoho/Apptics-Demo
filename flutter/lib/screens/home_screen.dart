import 'package:apptics_flutter/apptics_flutter.dart';
import 'package:flutter/material.dart';

import '../models/feature.dart';
import '../widgets/console_panel.dart';
import 'analytics_screen.dart';
import 'api_tracking_screen.dart';
import 'crash_screen.dart';
import 'feedback_screen.dart';
import 'in_app_update_screen.dart';
import 'privacy_screen.dart';
import 'push_screen.dart';
import 'rating_screen.dart';
import 'remote_config_screen.dart';
import 'user_screen.dart';

/// Landing screen: a grid of every Apptics module the sample demonstrates.
///
/// It also shows the recommended screen-tracking pattern — `screenAttached` on
/// open and `screenDetached` on dispose — so sessions/screen analytics are
/// reported correctly. (Each feature screen could do the same; we demonstrate
/// it once here to keep the others focused on their own APIs.)
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  static const String _screenName = 'HomeScreen';

  // The list of feature demos. Declared as data so the grid stays trivial.
  final List<Feature> _features = [
    Feature(
      title: 'Analytics',
      subtitle: 'Events, screens, sessions, flush',
      icon: Icons.bar_chart,
      builder: (_) => const AnalyticsScreen(),
    ),
    Feature(
      title: 'User',
      subtitle: 'Set/remove user, properties',
      icon: Icons.person,
      builder: (_) => const UserScreen(),
    ),
    Feature(
      title: 'Privacy',
      subtitle: 'Tracking state & consent',
      icon: Icons.privacy_tip,
      builder: (_) => const PrivacyScreen(),
    ),
    Feature(
      title: 'Crash & ANR',
      subtitle: 'Fatal, non-fatal, ANR',
      icon: Icons.bug_report,
      builder: (_) => const CrashScreen(),
    ),
    Feature(
      title: 'Feedback',
      subtitle: 'Forms, shake, logs',
      icon: Icons.feedback,
      builder: (_) => const FeedbackScreen(),
    ),
    Feature(
      title: 'API Tracking',
      subtitle: '4 integration strategies',
      icon: Icons.http,
      builder: (_) => const ApiTrackingScreen(),
    ),
    Feature(
      title: 'In-App Update',
      subtitle: 'Update alerts',
      icon: Icons.system_update,
      builder: (_) => const InAppUpdateScreen(),
    ),
    Feature(
      title: 'In-App Rating',
      subtitle: 'Rating prompt & store',
      icon: Icons.star_rate,
      builder: (_) => const RatingScreen(),
    ),
    Feature(
      title: 'Remote Config',
      subtitle: 'Server-driven values',
      icon: Icons.settings_remote,
      builder: (_) => const RemoteConfigScreen(),
    ),
    Feature(
      title: 'Push',
      subtitle: 'Foreground/background',
      icon: Icons.notifications_active,
      builder: (_) => const PushScreen(),
    ),
  ];

  @override
  void initState() {
    super.initState();
    // Report that this screen became visible.
    AppticsFlutter.instance.screenAttached(_screenName);
  }

  @override
  void dispose() {
    // Report that this screen is no longer visible.
    AppticsFlutter.instance.screenDetached(_screenName);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Apptics Flutter Sample'),
        centerTitle: false,
      ),
      body: Column(
        children: [
          Expanded(
            child: GridView.builder(
              padding: const EdgeInsets.all(12),
              gridDelegate:
                  const SliverGridDelegateWithMaxCrossAxisExtent(
                maxCrossAxisExtent: 220,
                mainAxisSpacing: 12,
                crossAxisSpacing: 12,
                childAspectRatio: 0.9,
              ),
              itemCount: _features.length,
              itemBuilder: (context, index) =>
                  _FeatureTile(feature: _features[index]),
            ),
          ),
          // Console is shared app-wide, so anything logged on a feature screen
          // (or by a push callback) is still visible after you navigate back.
          const ConsolePanel(height: 160),
        ],
      ),
    );
  }
}

class _FeatureTile extends StatelessWidget {
  const _FeatureTile({required this.feature});

  final Feature feature;

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return Card(
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: () => Navigator.of(context).push(
          MaterialPageRoute(builder: feature.builder),
        ),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(feature.icon, size: 32, color: scheme.primary),
              const SizedBox(height: 8),
              Text(
                feature.title,
                style: Theme.of(context).textTheme.titleSmall,
                textAlign: TextAlign.center,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
              const SizedBox(height: 2),
              Flexible(
                child: Text(
                  feature.subtitle,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Theme.of(context).hintColor,
                      ),
                  textAlign: TextAlign.center,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
