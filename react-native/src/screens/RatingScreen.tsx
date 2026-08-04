import {AppticsInAppRatings} from '@zoho_apptics/apptics-react-native';
import React from 'react';
import {Platform} from 'react-native';

import {ActionButton} from '../components/ActionButton';
import {FeatureScaffold} from '../components/FeatureScaffold';
import {SectionCard} from '../components/SectionCard';
import {useScreenTracking} from '../core/useScreenTracking';

/**
 * Demonstrates in-app ratings.
 *
 * The ratings module is initialized automatically by `Apptics.init()`. On
 * Android the SDK's own review prompt appears by itself once the criteria you
 * configured in the console are met; the methods here let you show the
 * *platform* review sheets — Play Core on Android, StoreKit on iOS — on demand.
 *
 * `AppticsRateUsModuleEmitter.willDisplayReviewPrompt` (wired up in
 * `core/appticsBootstrap.ts`) fires just before the iOS sheet appears, so you
 * can pause a video or a game loop first. Watch for the 🔔 line below.
 */
export function RatingScreen() {
  useScreenTracking('RatingScreen');

  return (
    <FeatureScaffold
      intro={
        'Rating prompts are driven by criteria you set in the Apptics console ' +
        '(Developer → Growth → In-app rating). Both OS review sheets are also ' +
        'rate-limited by the platform itself, so nothing may appear even when ' +
        'the call succeeds.'
      }>
      <SectionCard
        title="Android — Play Core review"
        subtitle="Shows Google Play's in-app review sheet when the criteria are met.">
        <ActionButton
          label="shouldShowPlayCoreAlertForAndroid()"
          icon="shop"
          disabled={Platform.OS !== 'android'}
          disabledNote="Android only"
          action={() => {
            AppticsInAppRatings.shouldShowPlayCoreAlertForAndroid();
            return 'Play Core review requested';
          }}
        />
      </SectionCard>

      <SectionCard
        title="iOS — App Store review"
        subtitle="Shows StoreKit's rating prompt. Apple limits it to a few times per year per user.">
        <ActionButton
          label="showAppStoreRatings()"
          icon="star"
          description="Fires willDisplayReviewPrompt just before the sheet appears."
          action={() => {
            AppticsInAppRatings.showAppStoreRatings();
            return 'App Store rating requested';
          }}
        />
      </SectionCard>

      <SectionCard
        title="How to test"
        subtitle={
          '1. Configure a rating criterion in the console for this app version.\n' +
          '2. Android: install from Play (internal testing) — the Play Core ' +
          'sheet is a no-op for sideloaded builds.\n' +
          '3. iOS: run on a real device; the prompt is suppressed in some ' +
          'simulator/TestFlight combinations.\n' +
          '4. Meet the criteria (sessions / events), then reopen this screen.'
        }
      />
    </FeatureScaffold>
  );
}
