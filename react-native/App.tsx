/**
 * Apptics React Native — Sample App
 *
 * @format
 */

import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import React from 'react';
import {StatusBar} from 'react-native';
import {SafeAreaProvider} from 'react-native-safe-area-context';

import {initApptics} from './src/core/appticsBootstrap';
import {FeatureRoute, FEATURES, RootStackParamList} from './src/navigation';
import {AnalyticsScreen} from './src/screens/AnalyticsScreen';
import {ApiTrackingScreen} from './src/screens/ApiTrackingScreen';
import {CrashScreen} from './src/screens/CrashScreen';
import {FeedbackScreen} from './src/screens/FeedbackScreen';
import {HomeScreen} from './src/screens/HomeScreen';
import {InAppUpdateScreen} from './src/screens/InAppUpdateScreen';
import {PrivacyScreen} from './src/screens/PrivacyScreen';
import {PushScreen} from './src/screens/PushScreen';
import {RatingScreen} from './src/screens/RatingScreen';
import {RemoteConfigScreen} from './src/screens/RemoteConfigScreen';
import {RemoteLoggerScreen} from './src/screens/RemoteLoggerScreen';
import {UserScreen} from './src/screens/UserScreen';
import {theme} from './src/theme';

// Initialize Apptics before the first render. Credentials come from the native
// config files (see the README) — this only starts the SDK and wires up the
// runtime behaviours this app opts into.
initApptics();

const Stack = createNativeStackNavigator<RootStackParamList>();

const FEATURE_SCREENS: Record<FeatureRoute, React.ComponentType> = {
  Analytics: AnalyticsScreen,
  User: UserScreen,
  Privacy: PrivacyScreen,
  Crash: CrashScreen,
  Feedback: FeedbackScreen,
  RemoteLogger: RemoteLoggerScreen,
  ApiTracking: ApiTrackingScreen,
  InAppUpdate: InAppUpdateScreen,
  Rating: RatingScreen,
  RemoteConfig: RemoteConfigScreen,
  Push: PushScreen,
};

function App() {
  return (
    <SafeAreaProvider>
      <StatusBar barStyle="light-content" />
      <NavigationContainer>
        <Stack.Navigator
          screenOptions={{
            headerStyle: {backgroundColor: theme.colors.primary},
            headerTintColor: '#FFFFFF',
            headerTitleStyle: {fontWeight: '700'},
            contentStyle: {backgroundColor: theme.colors.background},
          }}>
          <Stack.Screen
            name="Home"
            component={HomeScreen}
            options={{title: 'Apptics React Native'}}
          />
          {/* Every feature screen is registered from the same data that builds
              the home grid, so adding a demo means touching one list. */}
          {FEATURES.map(feature => (
            <Stack.Screen
              key={feature.route}
              name={feature.route}
              component={FEATURE_SCREENS[feature.route]}
              options={{title: feature.title}}
            />
          ))}
        </Stack.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  );
}

export default App;
