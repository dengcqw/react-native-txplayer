import TxplayerView, {
  type PlayTimeType,
  SuperPlayerState,
  SuperPlayType,
  type TxplayerViewApi,
  type TxplayerViewProps,
} from './TxplayerView';
import {
  startDownload,
  deleteDownload,
  getDownloadList,
  stopDownload,
  type VideoInfo,
  type DownloadInfo,
  subscribeEvent,
  type DownloadEventType,
} from './TxDownloadManager';

export const TxDownloadManager = {
  startDownload,
  deleteDownload,
  getDownloadList,
  stopDownload,
  subscribeEvent,
};

export type { PlayTimeType, TxplayerViewApi, TxplayerViewProps, VideoInfo, DownloadInfo, DownloadEventType };
export { SuperPlayerState, SuperPlayType };
export default TxplayerView;
