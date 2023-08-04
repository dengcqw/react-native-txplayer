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
} & ViewProps;

const ComponentName = 'TxplayerView';

export const Commands = {
  startPlay: UIManager.getViewManagerConfig(ComponentName).Commands.startPlay as number,
  stopPlay: UIManager.getViewManagerConfig(ComponentName).Commands.stopPlay as number,
  addDanmaku: UIManager.getViewManagerConfig(ComponentName).Commands.addDanmaku as number,
}

export const TxplayerViewNative =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<TxplayerProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
