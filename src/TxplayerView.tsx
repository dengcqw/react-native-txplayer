import * as React from 'react';
import { findNodeHandle, UIManager, requireNativeComponent, type NativeSyntheticEvent, Platform, NativeModules } from 'react-native';

const TxplayerViewMgr = Platform.OS === 'ios' ? NativeModules.TxplayerView : NativeModules.TxplayerNativeModule

const ComponentName = 'TxplayerView';
const Commands = Platform.OS === 'ios' ? {
  startPlay: UIManager.getViewManagerConfig(ComponentName).Commands.startPlay!,
  stopPlay: UIManager.getViewManagerConfig(ComponentName).Commands.stopPlay!,
  addDanmaku: UIManager.getViewManagerConfig(ComponentName).Commands.addDanmaku!,
  switchToOrientation: UIManager.getViewManagerConfig(ComponentName).Commands.switchToOrientation!,
  seekTo: UIManager.getViewManagerConfig(ComponentName).Commands.seekTo!,
  togglePlay: UIManager.getViewManagerConfig(ComponentName).Commands.togglePlay!
}: {
  startPlay: UIManager.getViewManagerConfig(ComponentName).Commands.startPlay!.toString(),
  stopPlay: UIManager.getViewManagerConfig(ComponentName).Commands.stopPlay!.toString(),
  addDanmaku: UIManager.getViewManagerConfig(ComponentName).Commands.addDanmaku!.toString(),
  switchToOrientation: UIManager.getViewManagerConfig(ComponentName).Commands.switchToOrientation!.toString(),
  seekTo: UIManager.getViewManagerConfig(ComponentName).Commands.seekTo!.toString(),
  togglePlay: UIManager.getViewManagerConfig(ComponentName).Commands.togglePlay!.toString()
}

export enum SuperPlayerState {
  StateFailed = 0, // 播放失败
  StateBuffering = 1, // 缓冲中
  StatePrepare = 2, // 准备就绪
  StatePlaying = 3, // 播放中
  StateStopped = 4, // 停止播放
  StatePause = 5, // 暂停播放
  StateFirstFrame = 6, // 第一帧画面
}

export enum SuperPlayType {
  autoPlay,
  preload,
  manualPlay,
}

export type TxplayerViewApi = {
  startPlay: () => void;
  stopPlay: () => void;
  togglePlay: () => void;
  addDanmaku: (contents: string[]) => void;
  switchToOrientation: (oriention: string) => void;
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
  hidePlayerControl: boolean;
  enableMorePanel: boolean;
  enableDownload: boolean;
  enableDanmaku: boolean;
  enableFullScreen: boolean;
  enableRotate: boolean;
  enablePIP: boolean;
  playStartTime: number;
  language: string;
  enableLoop: boolean;
  timeEventDuration: number; // 时间时间发送间隔

  playType: SuperPlayType;

  onPlayStateChange: (stateEvent: NativeSyntheticEvent<number>) => void;
  onPlayTimeChange: (stateEvent: NativeSyntheticEvent<PlayTimeType>) => void;
  onDownload: () => void;
  onFullscreen: (fullscreen: number) => void; // 1 yes 0 no
};

const TxplayerViewNative = requireNativeComponent<TxplayerViewProps>(ComponentName);

const TxplayerView = React.forwardRef<TxplayerViewProps, TxplayerViewApi>((props, ref) => {
  const nativeRef = React.useRef<typeof TxplayerViewNative>();

  React.useImperativeHandle<any, TxplayerViewApi>(
    ref,
    () => ({
      startPlay: () => {
        try {
          UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current!), Commands.startPlay, []);
        } catch (e) {
        }
      },
      stopPlay: () => {
        UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current!), Commands.stopPlay, []);
      },
      addDanmaku: (contents: string[]) => {
        UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current!), Commands.addDanmaku, [contents]);
      },
      switchToOrientation: (oriention: string) => {
        UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current!), Commands.switchToOrientation, [oriention]);
      },
      togglePlay: () => {
        UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current!), Commands.togglePlay, []);
      },
      seekTo: (second: number) => {
        UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current!), Commands.seekTo, [second]);
      }
    }),
    []
  );

  // @ts-ignore
  return <TxplayerViewNative {...props} ref={nativeRef} />;
});

// @ts-ignore
export const stopAllPlay = () => {
 TxplayerViewMgr && TxplayerViewMgr.stopAllPlay && TxplayerViewMgr.stopAllPlay()
}


export const startPip = () => {
 TxplayerViewMgr && TxplayerViewMgr.startPip && TxplayerViewMgr.startPip()
}

export const stopPip = () => {
 TxplayerViewMgr && TxplayerViewMgr.stopPip && TxplayerViewMgr.stopPip()
}


export default TxplayerView;

