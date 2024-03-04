import * as React from 'react';
import { type NativeSyntheticEvent } from 'react-native';
export declare enum SuperPlayerState {
    StateFailed = 0,
    StateBuffering = 1,
    StatePrepare = 2,
    StatePlaying = 3,
    StateStopped = 4,
    StatePause = 5,
    StateFirstFrame = 6
}
export declare enum SuperPlayType {
    autoPlay = 0,
    preload = 1,
    manualPlay = 2
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
    playStartTime: number;
    language: string;
    enableLoop: boolean;
    timeEventDuration: number;
    playType: SuperPlayType;
    onPlayStateChange: (stateEvent: NativeSyntheticEvent<number>) => void;
    onPlayTimeChange: (stateEvent: NativeSyntheticEvent<PlayTimeType>) => void;
    onDownload: () => void;
    onFullscreen: (fullscreen: number) => void;
};
declare const TxplayerView: React.ForwardRefExoticComponent<TxplayerViewApi & React.RefAttributes<TxplayerViewProps>>;
export declare const stopAllPlay: () => void;
export default TxplayerView;
//# sourceMappingURL=TxplayerView.d.ts.map