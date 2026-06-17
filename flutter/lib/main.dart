import 'dart:async';

import 'package:apptics_flutter/push_notification/apptics_push_notification.dart';
import 'package:flutter/material.dart';

import 'core/apptics_bootstrap.dart';
import 'core/console.dart';
import 'screens/home_screen.dart';

/// Background push-notification handler.
///
/// This MUST be a top-level (or static) function annotated with
/// `@pragma('vm:entry-point')` so the Flutter engine can find and run it in a
/// separate isolate when a notification arrives while the app is in the
/// background or terminated. It must be registered BEFORE `runApp()`.
@pragma('vm:entry-point')
Future<void> appticsBackgroundMessageHandler(Map<String, dynamic> message) async {
  // Runs in its own isolate — the in-app Console singleton here is a *separate*
  // instance from the UI isolate's, so this mainly demonstrates correct
  // registration. In a real app you would persist / process the payload here.
  Console.instance.event('Push received (background isolate): $message');
}

Future<void> main() async {
  // 1) Always initialize the binding before touching any plugin / platform
  //    channel.
  WidgetsFlutterBinding.ensureInitialized();

  // 2) Register the background message handler up front (see the pragma note
  //    above). Safe to call on every platform; it is a no-op where unsupported.
  AppticsPushNotification.setOnMessageHandlerListener(
      appticsBackgroundMessageHandler);

  // 3) Render the UI immediately, THEN initialize Apptics in the background.
  //    initApptics() makes native plugin calls (crash tracking, push) — we must
  //    NOT `await` it before runApp, or a slow/never-returning native call
  //    (e.g. iOS push registration on a simulator) would block the first frame
  //    and leave a blank white screen. Credentials still come from the native
  //    config files (see README); this only wires up runtime behaviours.
  runApp(const AppticsSampleApp());

  unawaited(initApptics());
}

class AppticsSampleApp extends StatelessWidget {
  const AppticsSampleApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Apptics Flutter Sample',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF1565C0)),
        useMaterial3: true,
      ),
      darkTheme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF1565C0),
          brightness: Brightness.dark,
        ),
        useMaterial3: true,
      ),
      home: const HomeScreen(),
    );
  }
}
