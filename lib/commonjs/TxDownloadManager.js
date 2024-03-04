"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.subscribeEvent = exports.stopDownload = exports.startDownload = exports.getDownloadList = exports.deleteDownload = void 0;
var _reactNative = require("react-native");
const emitter = new _reactNative.NativeEventEmitter(_reactNative.NativeModules.TxDownloadManager);
function isLoaded() {
  // @ts-ignore
  return global.TXD_getDownloadList != null;
}
if (!isLoaded()) {
  var _NativeModules$TxDown;
  const result = (_NativeModules$TxDown = _reactNative.NativeModules.TxDownloadManager) === null || _NativeModules$TxDown === void 0 ? void 0 : _NativeModules$TxDown.install();
  if (!result && !isLoaded()) {
    throw new Error('JSI bindings were not installed for: TxDownloadManager Module');
  }
  if (!isLoaded()) {
    throw new Error('JSI bindings were not installed for: TxDownloadManager Module');
  }
}
const startDownload = videoInfo => {
  try {
    // @ts-ignore
    global.TXD_startDownload(JSON.stringify(videoInfo));
  } catch (e) {
    console.log('----> TxDownloadManager startDownload err', e);
  }
};
exports.startDownload = startDownload;
const stopDownload = (fileId, appId) => {
  // @ts-ignore
  global.TXD_stopDownload(fileId, appId);
};
exports.stopDownload = stopDownload;
const deleteDownload = (fileId, appId) => {
  // @ts-ignore
  global.TXD_deleteDownload(fileId, appId);
};
exports.deleteDownload = deleteDownload;
const getDownloadList = () => {
  try {
    // @ts-ignore
    return JSON.parse(global.TXD_getDownloadList());
  } catch (e) {
    console.log('----> TxDownloadManager getDownloadList err', e);
    return [];
  }
};
exports.getDownloadList = getDownloadList;
const subscribeEvent = (fileId, cb) => {
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
exports.subscribeEvent = subscribeEvent;
//# sourceMappingURL=TxDownloadManager.js.map