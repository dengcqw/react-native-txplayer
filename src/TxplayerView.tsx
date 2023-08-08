import * as React from 'react';
import { findNodeHandle, UIManager, requireNativeComponent, type NativeSyntheticEvent } from 'react-native';

const ComponentName = 'TxplayerView';
const Commands = Platform.OS === 'ios' ? {
  startPlay: UIManager.getViewManagerConfig(ComponentName).Commands.startPlay,
  stopPlay: UIManager.getViewManagerConfig(ComponentName).Commands.stopPlay,
  addDanmaku: UIManager.getViewManagerConfig(ComponentName).Commands.addDanmaku,
}: {
  startPlay: UIManager.getViewManagerConfig(ComponentName).Commands.startPlay!.toString(),
  stopPlay: UIManager.getViewManagerConfig(ComponentName).Commands.stopPlay!.toString(),
  addDanmaku: UIManager.getViewManagerConfig(ComponentName).Commands.addDanmaku!.toString(),
}

export enum SuperPlayerState {
  StateFailed, // 播放失败
  StateBuffering, // 缓冲中
  StatePrepare, // 准备就绪
  StatePlaying, // 播放中
  StateStopped, // 停止播放
  StatePause, // 暂停播放
  StateFirstFrame, // 第一帧画面
}

export enum SuperPlayType {
  autoPlay,
  preload,
  manualPlay,
}

export type TxplayerViewApi = {
  startPlay: () => void;
  stopPlay: () => void;
  addDanmaku: (contents: string[]) => void;
};

export type PlayTimeType = {
  totalTime: number;
  progressTime: number;
  remainTime: number;
  isFinish: boolean;
};

export type TxplayerViewProps = {
  videoURL: string;

  appId: string;
  fileId: string;
  psign: string;

  videoName: string;
  videoCoverURL: string;
  enableSlider: boolean;
  enableMorePanel: boolean;
  enableDownload: boolean;
  enableDanmaku: boolean;
  enableFullScreen: boolean;
  playStartTime: number;
  language: string;

  playType: SuperPlayType;

  onPlayStateChange: (stateEvent: NativeSyntheticEvent<number>) => void;
  onPlayTimeChange: (stateEvent: NativeSyntheticEvent<PlayTimeType>) => void;
  onDownload: () => void;
};

const TxplayerViewNative = requireNativeComponent<TxplayerViewProps>(ComponentName);

const TxplayerView = React.forwardRef<TxplayerViewProps, TxplayerViewApi>((props, ref) => {
  const nativeRef = React.useRef<typeof TxplayerViewNative>();

  React.useImperativeHandle<any, TxplayerViewApi>(
    ref,
    () => ({
      startPlay: () => {
        UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current!), Commands.startPlay, []);
      },
      stopPlay: () => {
        UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current!), Commands.stopPlay, []);
      },
      addDanmaku: (contents: string[]) => {
        UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current!), Commands.addDanmaku, [contents]);
      },
    }),
    []
  );

  // @ts-ignore
  return <TxplayerViewNative {...props} ref={nativeRef} />;
});

export default TxplayerView;
