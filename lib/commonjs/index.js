"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
Object.defineProperty(exports, "SuperPlayType", {
  enumerable: true,
  get: function () {
    return _TxplayerView.SuperPlayType;
  }
});
Object.defineProperty(exports, "SuperPlayerState", {
  enumerable: true,
  get: function () {
    return _TxplayerView.SuperPlayerState;
  }
});
exports.default = exports.TxDownloadManager = void 0;
Object.defineProperty(exports, "stopAllPlay", {
  enumerable: true,
  get: function () {
    return _TxplayerView.stopAllPlay;
  }
});
var _TxplayerView = _interopRequireWildcard(require("./TxplayerView"));
var _TxDownloadManager = require("./TxDownloadManager");
function _getRequireWildcardCache(nodeInterop) { if (typeof WeakMap !== "function") return null; var cacheBabelInterop = new WeakMap(); var cacheNodeInterop = new WeakMap(); return (_getRequireWildcardCache = function (nodeInterop) { return nodeInterop ? cacheNodeInterop : cacheBabelInterop; })(nodeInterop); }
function _interopRequireWildcard(obj, nodeInterop) { if (!nodeInterop && obj && obj.__esModule) { return obj; } if (obj === null || typeof obj !== "object" && typeof obj !== "function") { return { default: obj }; } var cache = _getRequireWildcardCache(nodeInterop); if (cache && cache.has(obj)) { return cache.get(obj); } var newObj = {}; var hasPropertyDescriptor = Object.defineProperty && Object.getOwnPropertyDescriptor; for (var key in obj) { if (key !== "default" && Object.prototype.hasOwnProperty.call(obj, key)) { var desc = hasPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : null; if (desc && (desc.get || desc.set)) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } newObj.default = obj; if (cache) { cache.set(obj, newObj); } return newObj; }
const TxDownloadManager = {
  startDownload: _TxDownloadManager.startDownload,
  deleteDownload: _TxDownloadManager.deleteDownload,
  getDownloadList: _TxDownloadManager.getDownloadList,
  stopDownload: _TxDownloadManager.stopDownload,
  subscribeEvent: _TxDownloadManager.subscribeEvent
};
exports.TxDownloadManager = TxDownloadManager;
var _default = _TxplayerView.default;
exports.default = _default;
//# sourceMappingURL=index.js.map