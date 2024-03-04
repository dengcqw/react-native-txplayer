import TxplayerView, { type PlayTimeType, SuperPlayerState, SuperPlayType, type TxplayerViewApi, type TxplayerViewProps, stopAllPlay } from './TxplayerView';
import { type VideoInfo, type DownloadInfo, type DownloadEventType } from './TxDownloadManager';
export declare const TxDownloadManager: {
    startDownload: (videoInfo: VideoInfo) => void;
    deleteDownload: (fileId: string, appId: string) => void;
    getDownloadList: () => DownloadInfo[];
    stopDownload: (fileId: string, appId: string) => void;
    subscribeEvent: (fileId: string, cb: (value: DownloadEventType) => void) => () => void;
};
export type { PlayTimeType, TxplayerViewApi, TxplayerViewProps, VideoInfo, DownloadInfo, DownloadEventType };
export { SuperPlayerState, SuperPlayType, stopAllPlay };
export default TxplayerView;
//# sourceMappingURL=index.d.ts.map