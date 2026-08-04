import {useFocusEffect} from '@react-navigation/native';
import {Apptics} from '@zoho_apptics/apptics-react-native';
import {useCallback} from 'react';

/**
 * Pairs `Apptics.screenAttached` with `Apptics.screenDetached` so screen views
 * and dwell time are reported for as long as the screen is focused.
 *
 * Automatic native screen tracking is switched off via `Apptics.init(false)`,
 * so this pairing is the only source of screen stats.
 */
export function useScreenTracking(screenName: string) {
  useFocusEffect(
    useCallback(() => {
      Apptics.screenAttached(screenName);
      return () => Apptics.screenDetached(screenName);
    }, [screenName]),
  );
}
