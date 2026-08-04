export type RootStackParamList = {
  Home: undefined;
  Analytics: undefined;
  User: undefined;
  Privacy: undefined;
  Crash: undefined;
  Feedback: undefined;
  RemoteLogger: undefined;
  ApiTracking: undefined;
  InAppUpdate: undefined;
  Rating: undefined;
  RemoteConfig: undefined;
  Push: undefined;
};

export type FeatureRoute = Exclude<keyof RootStackParamList, 'Home'>;

export interface Feature {
  route: FeatureRoute;
  title: string;
  subtitle: string;
  icon: string;
}

export const FEATURES: Feature[] = [
  {
    route: 'Analytics',
    title: 'Analytics',
    subtitle: 'Events, screens, sessions, flush',
    icon: 'bar-chart',
  },
  {
    route: 'User',
    title: 'User',
    subtitle: 'Identify user, properties',
    icon: 'person',
  },
  {
    route: 'Privacy',
    title: 'Privacy',
    subtitle: 'Tracking state & consent',
    icon: 'privacy-tip',
  },
  {
    route: 'Crash',
    title: 'Crash',
    subtitle: 'Fatal & non-fatal reporting',
    icon: 'bug-report',
  },
  {
    route: 'Feedback',
    title: 'Feedback',
    subtitle: 'Forms, shake, logs',
    icon: 'feedback',
  },
  {
    route: 'RemoteLogger',
    title: 'Remote Logging',
    subtitle: 'Live logs in the console',
    icon: 'article',
  },
  {
    route: 'ApiTracking',
    title: 'API Tracking',
    subtitle: '3 integration strategies',
    icon: 'http',
  },
  {
    route: 'InAppUpdate',
    title: 'In-App Update',
    subtitle: 'Version alerts',
    icon: 'system-update',
  },
  {
    route: 'Rating',
    title: 'In-App Rating',
    subtitle: 'Rating prompts & store',
    icon: 'star-rate',
  },
  {
    route: 'RemoteConfig',
    title: 'Remote Config',
    subtitle: 'Server-driven values',
    icon: 'settings-remote',
  },
  {
    route: 'Push',
    title: 'Push',
    subtitle: 'Foreground/background',
    icon: 'notifications-active',
  },
];
