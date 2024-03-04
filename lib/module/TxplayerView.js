function _extends() { _extends = Object.assign ? Object.assign.bind() : function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }
import * as React from 'react';
import { findNodeHandle, UIManager, requireNativeComponent, Platform, NativeModules } from 'react-native';
const TxplayerViewMgr = Platform.OS === 'ios' ? NativeModules.TxplayerView : NativeModules.TxplayerNativeModule;
const ComponentName = 'TxplayerView';
const Commands = Platform.OS === 'ios' ? {
  startPlay: UIManager.getViewManagerConfig(ComponentName).Commands.startPlay,
  stopPlay: UIManager.getViewManagerConfig(ComponentName).Commands.stopPlay,
  addDanmaku: UIManager.getViewManagerConfig(ComponentName).Commands.addDanmaku,
  switchToOrientation: UIManager.getViewManagerConfig(ComponentName).Commands.switchToOrientation,
  seekTo: UIManager.getViewManagerConfig(ComponentName).Commands.seekTo,
  togglePlay: UIManager.getViewManagerConfig(ComponentName).Commands.togglePlay
} : {
  startPlay: UIManager.getViewManagerConfig(ComponentName).Commands.startPlay.toString(),
  stopPlay: UIManager.getViewManagerConfig(ComponentName).Commands.stopPlay.toString(),
  addDanmaku: UIManager.getViewManagerConfig(ComponentName).Commands.addDanmaku.toString(),
  switchToOrientation: UIManager.getViewManagerConfig(ComponentName).Commands.switchToOrientation.toString(),
  seekTo: UIManager.getViewManagerConfig(ComponentName).Commands.seekTo.toString(),
  togglePlay: UIManager.getViewManagerConfig(ComponentName).Commands.togglePlay.toString()
};
export let SuperPlayerState = /*#__PURE__*/function (SuperPlayerState) {
  SuperPlayerState[SuperPlayerState["StateFailed"] = 0] = "StateFailed";
  SuperPlayerState[SuperPlayerState["StateBuffering"] = 1] = "StateBuffering";
  SuperPlayerState[SuperPlayerState["StatePrepare"] = 2] = "StatePrepare";
  SuperPlayerState[SuperPlayerState["StatePlaying"] = 3] = "StatePlaying";
  SuperPlayerState[SuperPlayerState["StateStopped"] = 4] = "StateStopped";
  SuperPlayerState[SuperPlayerState["StatePause"] = 5] = "StatePause";
  SuperPlayerState[SuperPlayerState["StateFirstFrame"] = 6] = "StateFirstFrame";
  return SuperPlayerState;
}({}); // 第一帧画面
export let SuperPlayType = /*#__PURE__*/function (SuperPlayType) {
  SuperPlayType[SuperPlayType["autoPlay"] = 0] = "autoPlay";
  SuperPlayType[SuperPlayType["preload"] = 1] = "preload";
  SuperPlayType[SuperPlayType["manualPlay"] = 2] = "manualPlay";
  return SuperPlayType;
}({});
const TxplayerViewNative = requireNativeComponent(ComponentName);
const TxplayerView = /*#__PURE__*/React.forwardRef((props, ref) => {
  const nativeRef = React.useRef();
  React.useImperativeHandle(ref, () => ({
    startPlay: () => {
      try {
        UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), Commands.startPlay, []);
      } catch (e) {}
    },
    stopPlay: () => {
      UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), Commands.stopPlay, []);
    },
    addDanmaku: contents => {
      UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), Commands.addDanmaku, [contents]);
    },
    switchToOrientation: oriention => {
      UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), Commands.switchToOrientation, [oriention]);
    },
    togglePlay: () => {
      UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), Commands.togglePlay, []);
    },
    seekTo: second => {
      UIManager.dispatchViewManagerCommand(findNodeHandle(nativeRef.current), Commands.seekTo, [second]);
    }
  }), []);

  // @ts-ignore
  return /*#__PURE__*/React.createElement(TxplayerViewNative, _extends({}, props, {
    ref: nativeRef
  }));
});

// @ts-ignore
export const stopAllPlay = () => {
  TxplayerViewMgr && TxplayerViewMgr.stopAllPlay && TxplayerViewMgr.stopAllPlay();
};
export default TxplayerView;
//# sourceMappingURL=TxplayerView.js.map