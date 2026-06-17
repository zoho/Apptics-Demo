// Smoke test for the Apptics Flutter sample app.
//
// It pumps the root widget and asserts the home screen renders its feature
// grid. Plugin calls are not exercised here (no platform channels in the test
// host); the in-app console + a real device are used for behavioural checks.

import 'package:flutter_test/flutter_test.dart';

import 'package:sample/main.dart';

void main() {
  testWidgets('Home screen shows the feature grid', (WidgetTester tester) async {
    await tester.pumpWidget(const AppticsSampleApp());
    await tester.pump();

    // App bar title and a couple of representative feature tiles.
    expect(find.text('Apptics Flutter Sample'), findsOneWidget);
    expect(find.text('Analytics'), findsOneWidget);
    expect(find.text('Crash & ANR'), findsOneWidget);
  });
}
