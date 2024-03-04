"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.stopAllPlay = exports.default = exports.SuperPlayerState = exports.SuperPlayType = void 0;
var React = _interopRequireWildcard(require("react"));
var _reactNative = require("react-native");
function _getRequireWildcardCache(nodeInterop) { if (typeof WeakMap !== "function") return null; var cacheBabelInterop = new WeakMap(); var cacheNodeInterop = new WeakMap(); return (_getRequireWildcardCache = function (nodeInterop) { return nodeInterop ? cacheNodeInterop : cacheBabelInterop; })(nodeInterop); }
function _interopRequireWildcard(obj, nodeInterop) { if (!nodeInterop && obj && obj.__esModule) { return obj; } if (obj === null || typeof obj !== "object" && typeof obj !== "function") { return { default: obj }; } var cache = _getRequireWildcardCache(nodeInterop); if (cache && cache.has(obj)) { return cache.get(obj); } var newObj = {}; var hasPropertyDescriptor = Object.defineProperty && Object.getOwnPropertyDescriptor; for (var key in obj) { if (key !== "default" && Object.prototype.hasOwnProperty.call(obj, key)) { var desc = hasPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : null; if (desc && (desc.get || desc.set)) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } newObj.default = obj; if (cache) { cache.set(obj, newObj); } return newObj; }
function _extends() { _extends = Object.assign ? Object.assign.bind() : function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }
const TxplayerViewMgr = _reactNative.Platform.OS === 'ios' ? _reactNative.NativeModules.TxplayerView : _reactNative.NativeModules.TxplayerNativeModule;
const ComponentName = 'TxplayerView';
const Commands = _reactNative.Platform.OS === 'ios' ? {
  startPlay: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.startPlay,
  stopPlay: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.stopPlay,
  addDanmaku: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.addDanmaku,
  switchToOrientation: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.switchToOrientation,
  seekTo: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.seekTo,
  togglePlay: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.togglePlay
} : {
  startPlay: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.startPlay.toString(),
  stopPlay: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.stopPlay.toString(),
  addDanmaku: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.addDanmaku.toString(),
  switchToOrientation: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.switchToOrientation.toString(),
  seekTo: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.seekTo.toString(),
  togglePlay: _reactNative.UIManager.getViewManagerConfig(ComponentName).Commands.togglePlay.toString()
};
let SuperPlayerState = /*#__PURE__*/function (SuperPlayerState) {
  SuperPlayerState[SuperPlayerState["StateFailed"] = 0] = "StateFailed";
  SuperPlayerState[SuperPlayerState["StateBuffering"] = 1] = "StateBuffering";
  SuperPlayerState[SuperPlayerState["StatePrepare"] = 2] = "StatePrepare";
  SuperPlayerState[SuperPlayerState["StatePlaying"] = 3] = "StatePlaying";
  SuperPlayerState[SuperPlayerState["StateStopped"] = 4] = "StateStopped";
  SuperPlayerState[SuperPlayerState["StatePause"] = 5] = "StatePause";
  SuperPlayerState[SuperPlayerState["StateFirstFrame"] = 6] = "StateFirstFrame";
  return SuperPlayerState;
}({}); // 第一帧画面
exports.SuperPlayerState = SuperPlayerState;
let SuperPlayType = /*#__PURE__*/function (SuperPlayType) {
  SuperPlayType[SuperPlayType["autoPlay"] = 0] = "autoPlay";
  SuperPlayType[SuperPlayType["preload"] = 1] = "preload";
  SuperPlayType[SuperPlayType["manualPlay"] = 2] = "manualPlay";
  return SuperPlayType;
}({});
exports.SuperPlayType = SuperPlayType;
const TxplayerViewNative = (0, _reactNative.requireNativeComponent)(ComponentName);
const TxplayerView = /*#__PURE__*/React.forwardRef((props, ref) => {
  const nativeRef = React.useRef();
  React.useImperativeHandle(ref, () => ({
    startPlay: () => {
      try {
        _reactNative.UIManager.dispatchViewManagerCommand((0, _reactNative.findNodeHandle)(nativeRef.current), Commands.startPlay, []);
      } catch (e) {}
    },
    stopPlay: () => {
      _reactNative.UIManager.dispatchViewManagerCommand((0, _reactNative.findNodeHandle)(nativeRef.current), Commands.stopPlay, []);
    },
    addDanmaku: contents => {
      _reactNative.UIManager.dispatchViewManagerCommand((0, _reactNative.findNodeHandle)(nativeRef.current), Commands.addDanmaku, [contents]);
    },
    switchToOrientation: oriention => {
      _reactNative.UIManager.dispatchViewManagerCommand((0, _reactNative.findNodeHandle)(nativeRef.current), Commands.switchToOrientation, [oriention]);
    },
    togglePlay: () => {
      _reactNative.UIManager.dispatchViewManagerCommand((0, _reactNative.findNodeHandle)(nativeRef.current), Commands.togglePlay, []);
    },
    seekTo: second => {
      _reactNative.UIManager.dispatchViewManagerCommand((0, _reactNative.findNodeHandle)(nativeRef.current), Commands.seekTo, [second]);
    }
  }), []);

  // @ts-ignore
  return /*#__PURE__*/React.createElement(TxplayerViewNative, _extends({}, props, {
    ref: nativeRef
  }));
});

// @ts-ignore
const stopAllPlay = () => {
  TxplayerViewMgr && TxplayerViewMgr.stopAllPlay && TxplayerViewMgr.stopAllPlay();
};
exports.stopAllPlay = stopAllPlay;
var _default = TxplayerView;
exports.default = _default;
//# sourceMappingURL=TxplayerView.js.map