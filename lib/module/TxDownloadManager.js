import { NativeModules, NativeEventEmitter } from 'react-native';
const emitter = new NativeEventEmitter(NativeModules.TxDownloadManager);
function isLoaded() {
  // @ts-ignore
  return global.TXD_getDownloadList != null;
}
if (!isLoaded()) {
  var _NativeModules$TxDown;
  const result = (_NativeModules$TxDown = NativeModules.TxDownloadManager) === null || _NativeModules$TxDown === void 0 ? void 0 : _NativeModules$TxDown.install();
  if (!result && !isLoaded()) {
    throw new Error('JSI bindings were not installed for: TxDownloadManager Module');
  }
  if (!isLoaded()) {
    throw new Error('JSI bindings were not installed for: TxDownloadManager Module');
  }
}
export const startDownload = videoInfo => {
  try {
    // @ts-ignore
    global.TXD_startDownload(JSON.stringify(videoInfo));
  } catch (e) {
    console.log('----> TxDownloadManager startDownload err', e);
  }
};
export const stopDownload = (fileId, appId) => {
  // @ts-ignore
  global.TXD_stopDownload(fileId, appId);
};
export const deleteDownload = (fileId, appId) => {
  // @ts-ignore
  global.TXD_deleteDownload(fileId, appId);
};
export const getDownloadList = () => {
  try {
    // @ts-ignore
    return JSON.parse(global.TXD_getDownloadList());
  } catch (e) {
    console.log('----> TxDownloadManager getDownloadList err', e);
    return [];
  }
};
export const subscribeEvent = (fileId, cb) => {
  const subscription = emitter.addListener('TxDownloadEvent', value => {
    console.log('----> download event', value);
    if (value.fileId === fileId) {
      cb && cb(value);
    }
  });
  return () => {
    subscription.remove();
  };
};
//# sourceMappingURL=TxDownloadManager.js.map