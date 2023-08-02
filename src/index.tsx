import {
  requireNativeComponent,
  UIManager,
  Platform,
  type ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-txplayer' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type TxplayerProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'TxplayerView';

export const TxplayerView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<TxplayerProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
