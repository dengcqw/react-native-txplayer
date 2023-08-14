package com.txplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TxplayerViewManager extends SimpleViewManager<TxplayerView> implements TxplayerView.TxPlayerViewCallBack {
  public static final String REACT_CLASS = "TxplayerView";
  private final ReactApplicationContext context;

  public final int COMMAND_STARTPLAY = 1;
  public final int COMMAND_STOPPLAY = 2;
  public final int COMMAND_ADDDanmuk = 3;
  public final int COMMAND_SWITCH_TO_LANDSCAPE = 4;
  public final int COMMAND_TOGGLE_PLAY = 5;
  public final int COMMAND_SEEKTO = 6;



  public TxplayerViewManager(ReactApplicationContext reactContext) {
    this.context = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  @NonNull
  public TxplayerView createViewInstance(ThemedReactContext reactContext) {
    TxplayerView txplayerView = new TxplayerView(reactContext.getCurrentActivity());
    txplayerView.setFeedPlayerCallBack(this);
    return txplayerView;
  }

  @Override
  public void onDropViewInstance(TxplayerView view) {
    super.onDropViewInstance(view);
    view.stopPlay();
  }

  @Nullable
  @Override
  public Map<String, Integer> getCommandsMap() {
    return MapBuilder.of(
      "startPlay", COMMAND_STARTPLAY,
      "stopPlay", COMMAND_STOPPLAY,
      "addDanmaku", COMMAND_ADDDanmuk,
      "switchToOrientation", COMMAND_SWITCH_TO_LANDSCAPE,
      "togglePlay", COMMAND_TOGGLE_PLAY,
      "seekTo", COMMAND_SEEKTO
    );
  }

  @Override
  public void receiveCommand(
    @NonNull TxplayerView root,
    String commandId,
    @Nullable ReadableArray args
  ) {
    super.receiveCommand(root, commandId, args);
    int commandIdInt = Integer.parseInt(commandId);

    switch (commandIdInt) {
      case COMMAND_STARTPLAY:
        root.startPlay();
        break;
      case COMMAND_STOPPLAY:
        root.stopPlay();
        break;
      case COMMAND_ADDDanmuk:
        handleAddDanmuList(root, args);
        break;
      case COMMAND_SWITCH_TO_LANDSCAPE:
        switchToOrientation(root, args);
        break;
      case COMMAND_TOGGLE_PLAY:
        root.togglePlay();
        break;
      case COMMAND_SEEKTO:
        int seconds = args.getInt(0);
        root.seekTo(seconds);
        break;
      default: {}
    }
  }

  private void handleAddDanmuList(TxplayerView root, ReadableArray args) {
    if (args == null) {
      return;
    }
    List<String> danmuContentList = new ArrayList<>();
    ReadableArray danmuList = args.getArray(0);
    for (Object strObj : danmuList.toArrayList()) {
      danmuContentList.add(String.valueOf(strObj));
    }
    root.addDanmukInfo(danmuContentList);
  }

  private void switchToOrientation(TxplayerView root, ReadableArray args) {
    if (args == null) {
      return;
    }
    String oriention  = args.getString(0);
    root.switchToOrientation(oriention);
  }

  public Map getExportedCustomBubblingEventTypeConstants() {
    MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
    builder.put(
      "onPlayStateChange",
      MapBuilder.of(
        "phasedRegistrationNames",
        MapBuilder.of("bubbled", "onPlayStateChange")
      ));
    builder.put(
      "onDownload",
      MapBuilder.of(
        "phasedRegistrationNames",
        MapBuilder.of("bubbled", "onDownload")
      ));
    builder.put(
      "onPlayTimeChange",
      MapBuilder.of(
        "phasedRegistrationNames",
        MapBuilder.of("bubbled", "onPlayTimeChange")
      ));

    return builder.build();
  }

  @ReactProp(name = "videoURL")
  public void setVideoURL(TxplayerView view, String videoURL) {
    view.setVideoURL(videoURL);
  }
  @ReactProp(name = "videoName")
  public void setVideoName(TxplayerView view, String videoName) {
    view.setVideoName(videoName);
  }
  @ReactProp(name = "videoCoverURL")
  public void setVideoCoverURL(TxplayerView view, String videoCoverURL) {
    view.setVideoCoverURL(videoCoverURL);
  }
  @ReactProp(name = "appId")
  public void setAppId(TxplayerView view, String appId) {
    view.setAppId(appId);
  }
  @ReactProp(name = "fileId")
  public void setFileId(TxplayerView view, String fileId) {
    view.setFileId(fileId);
  }
  @ReactProp(name = "psign")
  public void setPsign(TxplayerView view, String psign) {
    view.setPsign(psign);
  }
  @ReactProp(name = "enableSlider")
  public void setEnableSlider(TxplayerView view, Boolean enableSlider) {
    view.setEnableSlider(enableSlider);
  }
  @ReactProp(name = "enableMorePanel")
  public void setEnableMorePanel(TxplayerView view, Boolean enableMorePanel) {
    view.setEnableMorePanel(enableMorePanel);
  }
  @ReactProp(name = "enableDownload")
  public void setEnableDownload(TxplayerView view, Boolean enableDownload) {
    view.setEnableDownload(enableDownload);
  }
  @ReactProp(name = "enableDanmaku")
  public void setEnableDanmaku(TxplayerView view, Boolean enableDanmaku) {
    view.setEnableDanmaku(enableDanmaku);
  }
  @ReactProp(name = "enableFullScreen")
  public void setEnableFullScreen(TxplayerView view, Boolean enableFullScreen) {
    view.setEnableFullScreen(enableFullScreen);
  }
  @ReactProp(name = "playType")
  public void setPlayType(TxplayerView view, Integer playType) {
    view.setPlayType(playType);
  }
  @ReactProp(name = "playStartTime")
  public void setPlayStartTime(TxplayerView view, String playStartTime) {
    view.setPlayStartTime(Float.valueOf(playStartTime));
  }
  @ReactProp(name = "language")
  public void setLanguage(TxplayerView view, String language) {
    view.setLanguage(language);
  }

  @ReactProp(name = "hidePlayerControl")
  public void setHidePlayerControl(TxplayerView view, Boolean hidePlayerControl) {
    view.setHidePlayerControl(hidePlayerControl);
  }
  @ReactProp(name = "enableLoop")
  public void setEnableLoop(TxplayerView view, Boolean enableLoop) {
    view.setEnableLoop(enableLoop);
  }
  @ReactProp(name = "timeEventDuration")
  public void setTimeEventDuration(TxplayerView view, Integer timeEventDuration) {
    view.setTimeEventDuration(timeEventDuration);
  }

  @Override
  public void onPlayStateChange(int viewId, Integer state) {
    WritableMap event = Arguments.createMap();
    // 转换成js定义
    if (state == 1) {
      state = 5;
    } else if (state == 2) {
      state = 1;
    } else if (state == 0) {
      state = 2;
    }
    event.putInt("state", state);
    sendEvent(viewId,"onPlayStateChange", event);
  }

  @Override
  public void onPlayTimeChange(int viewId, WritableMap map) {
    sendEvent(viewId,"onPlayTimeChange", map);
  }

  @Override
  public void onDownload(int viewId) {
    sendEvent(viewId,"onDownload", Arguments.createMap());
  }

  private void sendEvent(int viewId, String eventName, @Nullable WritableMap params) {
    context.getJSModule(RCTEventEmitter.class).receiveEvent(
      viewId,
      eventName,
      params);
  }
}

