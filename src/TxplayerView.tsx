import * as React from 'react';
import { findNodeHandle, UIManager, type NativeSyntheticEvent } from 'react-native';
import { TxplayerViewNative, Commands } from './TxplayerViewManager';

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

const TxplayerView = React.forwardRef<TxplayerViewApi, TxplayerViewProps>((props, ref) => {
  const nativeRef = React.useRef();

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

  return (
    <TxplayerViewNative
      ref={(ref) => {
        nativeRef.current = ref;
      }}
      {...props}
    />
  );
});

export default TxplayerView
