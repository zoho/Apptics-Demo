import {Platform} from 'react-native';

/**
 * A single, tiny design token set shared by every screen. Keeping it here means
 * the demo screens stay focused on Apptics APIs instead of styling.
 */
export const theme = {
  colors: {
    primary: '#1565C0',
    primaryContainer: '#DCE7F8',
    onPrimaryContainer: '#0B3A72',
    danger: '#B3261E',
    dangerContainer: '#F9DEDC',
    onDangerContainer: '#601410',
    background: '#F4F6FA',
    surface: '#FFFFFF',
    border: '#E1E5EC',
    text: '#141821',
    hint: '#5F6675',
    // Console (always dark, like a terminal).
    consoleBg: '#1E1E1E',
    consoleHeaderBg: '#2D2D2D',
    consoleInfo: '#5BC8FF',
    consoleSuccess: '#5CE08A',
    consoleError: '#FF6B6B',
    consoleEvent: '#FFD466',
    consoleMuted: '#7A7A7A',
  },
  radius: 12,
  monospace: Platform.select({ios: 'Menlo', default: 'monospace'}),
} as const;
