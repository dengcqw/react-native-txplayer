/**
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * @flow strict-local
 * @format
 */

import type { ViewProps, HostComponent } from 'react-native';
import type { BubblingEventHandler, Int32, WithDefault } from 'react-native/Libraries/Types/CodegenTypes';

import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import * as React from 'react';

type PlayTimeType = Readonly<{
  totalTime: Int32;
  progressTime: Int32;
  remainTime: Int32;
  isFinish: boolean;
}>;

type StateEvent = Readonly<{state: Int32}>
type FullscreenEvent = Readonly<{fullscreen: Int32}>
type OnDownloadEvent = Readonly<{}>

interface NativeProps extends ViewProps {
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
  playStartTime: Int32;
  language: string;
  enableLoop: boolean;
  timeEventDuration: Int32; // 时间时间发送间隔

  playType: Int32;

  onPlayStateChange: BubblingEventHandler<StateEvent>;
  onPlayTimeChange: BubblingEventHandler<PlayTimeType>;
  onDownload: BubblingEventHandler<OnDownloadEvent>;
  onFullscreen: BubblingEventHandler<FullscreenEvent>;
}

type ComponentType = HostComponent<NativeProps>;

interface NativeCommands {
  startPlay: (viewRef: React.ElementRef<ComponentType>) => void;
  stopPlay: (viewRef: React.ElementRef<ComponentType>) => void;
  togglePlay: (viewRef: React.ElementRef<ComponentType>) => void;
  addDanmaku: (viewRef: React.ElementRef<ComponentType>, contents: string[]) => void;
  switchToOrientation: (viewRef: React.ElementRef<ComponentType>, oriention: string, force: WithDefault<string, '0'>) => void;
  seekTo: (viewRef: React.ElementRef<ComponentType>, second: Int32) => void;
}

export const Commands: NativeCommands = codegenNativeCommands<NativeCommands>({
  supportedCommands: ['startPlay', 'stopPlay', 'togglePlay', 'addDanmaku', 'switchToOrientation', 'seekTo'],
});

export default codegenNativeComponent<NativeProps>('TxplayerView');
