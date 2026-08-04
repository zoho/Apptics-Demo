import {useFocusEffect} from '@react-navigation/native';
import {Apptics} from '@zoho_apptics/apptics-react-native';
import {useCallback} from 'react';

/**
 * Reports a screen view to Apptics for as long as the screen is focused.
 *
 * This is the recommended screen-tracking pattern for React Navigation:
 * `screenAttached` when the screen gains focus and `screenDetached` when it
 * loses it — so a screen you navigate *away* from stops accruing dwell time
 * even though its component stays mounted in the stack.
 *
 * Automatic native screen tracking is switched off in `appticsBootstrap.ts`
 * (`Apptics.init(false)`) precisely so this manual pairing is the single source
 * of truth.
 */
export function useScreenTracking(screenName: string) {
  useFocusEffect(
    useCallback(() => {
      Apptics.screenAttached(screenName);
      return () => Apptics.screenDetached(screenName);
    }, [screenName]),
  );
}
