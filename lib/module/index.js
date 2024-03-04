import TxplayerView, { SuperPlayerState, SuperPlayType, stopAllPlay } from './TxplayerView';
import { startDownload, deleteDownload, getDownloadList, stopDownload, subscribeEvent } from './TxDownloadManager';
export const TxDownloadManager = {
  startDownload,
  deleteDownload,
  getDownloadList,
  stopDownload,
  subscribeEvent
};
export { SuperPlayerState, SuperPlayType, stopAllPlay };
export default TxplayerView;
//# sourceMappingURL=index.js.map