import 'dart:io' show Platform;

import 'package:apptics_flutter/rateus/apptics_in_app_rating.dart';
import 'package:apptics_flutter/rateus/popup_action.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import '../widgets/action_button.dart';
import '../widgets/feature_scaffold.dart';
import '../widgets/section_card.dart';

/// Demonstrates the in-app rating module via `AppticsInAppRating.instance`.
///
/// The prompt normally appears automatically once console-defined criteria are
/// met; the APIs here let you drive it manually, tune the behaviour, build a
/// custom UI (via getCriteriaId + sentStats), and jump to the store.
class RatingScreen extends StatelessWidget {
  const RatingScreen({super.key});

  bool get _isAndroid => !kIsWeb && Platform.isAndroid;

  @override
  Widget build(BuildContext context) {
    return FeatureScaffold(
      title: 'In-App Rating',
      intro:
          'checkForRatingPop shows the rating prompt when criteria are met. '
          'Several configuration setters are Android-only.',
      children: [
        SectionCard(
          title: 'Prompt',
          children: [
            ActionButton(
              label: 'checkForRatingPop(context)',
              description: 'Shows the prompt if criteria are satisfied.',
              icon: Icons.star,
              action: () async {
                await AppticsInAppRating.instance.checkForRatingPop(context);
                return 'rating check requested';
              },
            ),
            ActionButton(
              label: 'checkForRatingPop(context, isFeedbackEnabled: true)',
              description: 'Offers feedback as an alternative to rating.',
              icon: Icons.star_half,
              action: () async {
                await AppticsInAppRating.instance
                    .checkForRatingPop(context, isFeedbackEnabled: true);
                return 'rating check (with feedback) requested';
              },
            ),
            ActionButton(
              label: 'openPlayStore()',
              description: 'Opens the store listing directly.',
              icon: Icons.store,
              action: () async {
                await AppticsInAppRating.instance.openPlayStore();
                return 'store opened';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Custom rating UI',
          subtitle:
              'Build your own prompt: read the criteria id, then report the '
              "user's choice back with sentStats.",
          children: [
            ActionButton(
              label: 'getCriteriaId()',
              icon: Icons.tag,
              action: () => AppticsInAppRating.instance.getCriteriaId(),
            ),
            ActionButton(
              label: 'isAppticsFeedbackModuleAvailable()',
              icon: Icons.help_outline,
              action: () =>
                  AppticsInAppRating.instance.isAppticsFeedbackModuleAvailable(),
            ),
            ActionButton(
              label: 'sentStats(criteriaId, RATE_IN_STORE_CLICKED)',
              description: 'Report the chosen action for the current criteria.',
              icon: Icons.thumb_up,
              action: () async {
                final id =
                    await AppticsInAppRating.instance.getCriteriaId() ?? 0;
                await AppticsInAppRating.instance
                    .sentStats(id, PopupAction.RATE_IN_STORE_CLICKED);
                return 'stats sent for criteria $id';
              },
            ),
            ActionButton(
              label: 'updateRatingShown()',
              description: 'Marks the prompt as shown (custom-UI bookkeeping).',
              icon: Icons.done_all,
              action: () async {
                await AppticsInAppRating.instance.updateRatingShown();
                return 'rating marked shown';
              },
            ),
          ],
        ),
        SectionCard(
          title: 'Configuration (Android only)',
          subtitle: _isAndroid
              ? 'Tune how often and how the prompt appears.'
              : 'These are no-ops on this platform (Android only).',
          children: [
            ActionButton(
              label: 'setMaxTimesToShowPopup(3)',
              icon: Icons.repeat,
              action: () async {
                await AppticsInAppRating.instance.setMaxTimesToShowPopup(3);
                return 'max times = 3';
              },
            ),
            ActionButton(
              label: 'setDaysBeforeShowingPopupAgain(14)',
              icon: Icons.calendar_month,
              action: () async {
                await AppticsInAppRating.instance
                    .setDaysBeforeShowingPopupAgain(14);
                return 'reminder days = 14';
              },
            ),
            ActionButton(
              label: 'setShowStoreAlertOnFulFillingCriteria(true)',
              icon: Icons.notification_important,
              action: () async {
                await AppticsInAppRating.instance
                    .setShowStoreAlertOnFulFillingCriteria(true);
                return 'store alert on criteria = true';
              },
            ),
            ActionButton(
              label: 'setDisableAutoPromptOnFulFillingCriteria(true)',
              description: 'Disable the automatic prompt to drive it yourself.',
              icon: Icons.block,
              action: () async {
                await AppticsInAppRating.instance
                    .setDisableAutoPromptOnFulFillingCriteria(true);
                return 'auto-prompt disabled';
              },
            ),
          ],
        ),
      ],
    );
  }
}
