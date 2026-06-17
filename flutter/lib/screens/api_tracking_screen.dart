import 'package:apptics_flutter/api_tracker/apptics_api_tracker.dart';
import 'package:apptics_flutter/api_tracker/apptics_http_client.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

import '../widgets/action_button.dart';
import '../widgets/feature_scaffold.dart';
import '../widgets/section_card.dart';

/// Demonstrates the API call-tracking module (plugin 0.0.14+), which measures
/// latency, status codes and failures of your network calls. Four integration
/// strategies are shown:
///   1. Auto tracking (HttpOverrides) — zero per-call code.
///   2. AppticsHttpClient — wrap the `http` package client.
///   3. Manual start/end — for any custom transport.
///   4. URL exclusion — keep noisy/internal endpoints out of reports.
class ApiTrackingScreen extends StatelessWidget {
  const ApiTrackingScreen({super.key});

  // A real public endpoint (success) and an unresolvable host (failure).
  static final Uri _ok = Uri.parse('https://jsonplaceholder.typicode.com/todos/1');
  static final Uri _bad = Uri.parse('https://this-host-does-not-exist.invalid/x');

  @override
  Widget build(BuildContext context) {
    return FeatureScaffold(
      title: 'API Tracking',
      intro:
          'Track network performance with Apptics. The wrapper/manual demos '
          'below make real HTTP requests — including a deliberately failing '
          'one — so you can see both success and error paths reported.',
      children: [
        SectionCard(
          title: '1 · Auto tracking',
          subtitle:
              'enableAutoTracking() installs HttpOverrides so every dart:io '
              'HTTP call is tracked with no extra code.',
          children: [
            ActionButton(
              label: 'enableAutoTracking()',
              icon: Icons.autorenew,
              action: () async {
                AppticsApiTracker.instance.enableAutoTracking();
                return 'auto tracking ON';
              },
            ),
            ActionButton(
              label: 'isAutoTrackingEnabled',
              icon: Icons.help_outline,
              action: () async =>
                  AppticsApiTracker.instance.isAutoTrackingEnabled,
            ),
            ActionButton(
              label: 'disableAutoTracking()',
              icon: Icons.sync_disabled,
              action: () async {
                AppticsApiTracker.instance.disableAutoTracking();
                return 'auto tracking OFF';
              },
            ),
          ],
        ),
        SectionCard(
          title: '2 · AppticsHttpClient wrapper',
          subtitle:
              'Wrap an http.Client; every request through it is tracked.',
          children: [
            ActionButton(
              label: 'GET /todos/1  (success)',
              icon: Icons.cloud_done,
              action: () async {
                final client = AppticsHttpClient(http.Client());
                try {
                  final res = await client.get(_ok);
                  return 'HTTP ${res.statusCode} (${res.body.length} bytes)';
                } finally {
                  client.close();
                }
              },
            ),
            ActionButton(
              label: 'GET invalid host  (failure / edge case)',
              description: 'Demonstrates a tracked request that errors out.',
              icon: Icons.cloud_off,
              action: () async {
                final client = AppticsHttpClient(http.Client());
                try {
                  final res = await client.get(_bad);
                  return 'HTTP ${res.statusCode}';
                } finally {
                  client.close();
                }
                // The thrown SocketException is logged by ActionButton's
                // catch, and the tracker records the failed call.
              },
            ),
          ],
        ),
        SectionCard(
          title: '3 · Manual tracking',
          subtitle:
              'For custom transports: bracket the call with start/end, or use '
              'the trackApiCall convenience method.',
          children: [
            ActionButton(
              label: 'startApiTracking() → endApiTracking()',
              icon: Icons.timeline,
              action: () async {
                final tracker = AppticsApiTracker.instance;
                final trackId = await tracker.startApiTracking(
                  url: 'https://api.example.com/orders',
                  method: 'POST',
                );
                // ... your real request would happen here ...
                await tracker.endApiTracking(
                  trackId: trackId,
                  statusCode: 201,
                );
                return 'tracked manually (id=$trackId)';
              },
            ),
            ActionButton(
              label: 'trackApiCall(... errorMessage: ...)',
              description: 'One-shot record of an already-completed call.',
              icon: Icons.fact_check,
              action: () async {
                await AppticsApiTracker.instance.trackApiCall(
                  url: 'https://api.example.com/profile',
                  method: 'GET',
                  statusCode: 500,
                  errorMessage: 'Internal Server Error',
                );
                return 'call recorded (500)';
              },
            ),
          ],
        ),
        SectionCard(
          title: '4 · URL exclusion',
          subtitle:
              'Add substrings to excludedUrlPatterns to skip matching URLs.',
          children: [
            ActionButton(
              label: "excludedUrlPatterns.add('/healthz')",
              icon: Icons.filter_alt,
              action: () async {
                AppticsApiTracker.instance.excludedUrlPatterns.add('/healthz');
                return 'patterns: '
                    '${AppticsApiTracker.instance.excludedUrlPatterns}';
              },
            ),
          ],
        ),
      ],
    );
  }
}
