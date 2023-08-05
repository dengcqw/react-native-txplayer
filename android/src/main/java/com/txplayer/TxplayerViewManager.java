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
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.Map;

public class TxplayerViewManager extends SimpleViewManager<TxplayerView> implements TxplayerView.TxPlayerViewCallBack {
  public static final String REACT_CLASS = "TxplayerView";
  private final ReactApplicationContext context;
  private TxplayerView currentPlayerView;

  public final int COMMAND_STARTPLAY = 1;
  public final int COMMAND_STOPPLAY = 2;
  public final int COMMAND_ADDDanmuk = 3;

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
    return new TxplayerView(reactContext.getCurrentActivity());
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
      "addDanmaku", COMMAND_ADDDanmuk
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
        if (currentPlayerView != root) {
          root.startPlay();
          if (currentPlayerView != null) {
            currentPlayerView.stopPlay();
          }
        }
        currentPlayerView = root;
        break;
      case COMMAND_STOPPLAY:
        root.stopPlay();
        break;
      case COMMAND_ADDDanmuk:
        root.addDanmukInfo();
        break;
      default: {}
    }
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
  public void setEnableSlider(TxplayerView view, String enableSlider) {
    view.setEnableSlider(Boolean.valueOf(enableSlider));
  }
  @ReactProp(name = "enableMorePanel")
  public void setEnableMorePanel(TxplayerView view, String enableMorePanel) {
    view.setEnableMorePanel(Boolean.valueOf(enableMorePanel));
  }
  @ReactProp(name = "enableDownload")
  public void setEnableDownload(TxplayerView view, String enableDownload) {
    view.setEnableDownload(Boolean.valueOf(enableDownload));
  }
  @ReactProp(name = "enableDanmaku")
  public void setEnableDanmaku(TxplayerView view, String enableDanmaku) {
    view.setEnableDanmaku(Boolean.valueOf(enableDanmaku));
  }
  @ReactProp(name = "enableFullScreen")
  public void setEnableFullScreen(TxplayerView view, String enableFullScreen) {
    view.setEnableFullScreen(Boolean.valueOf(enableFullScreen));
  }
  @ReactProp(name = "playType")
  public void setPlayType(TxplayerView view, String playType) {
    view.setPlayType(Integer.valueOf(playType));
  }
  @ReactProp(name = "playStartTime")
  public void setPlayStartTime(TxplayerView view, String playStartTime) {
    view.setPlayStartTime(Float.valueOf(playStartTime));
  }
  @ReactProp(name = "language")
  public void setLanguage(TxplayerView view, String language) {
    view.setLanguage(language);
  }

  @Override
  public void onStartFullScreenPlay() {

  }

  @Override
  public void onStopFullScreenPlay() {

  }

  @Override
  public void onClickSmallReturnBtn() {

  }

  @Override
  public void onPlayStateChange(int viewId, Integer state) {
    WritableMap event = Arguments.createMap();
    event.putInt("state", state);
    context
      .getJSModule(RCTEventEmitter.class)
      .receiveEvent(viewId, "onPlayStateChange", event);
  }

  @Override
  public void onPlayTimeChange(int viewId, WritableMap map) {
    context
      .getJSModule(RCTEventEmitter.class)
      .receiveEvent(viewId, "onPlayTimeChange", map);
  }

  @Override
  public void onDownload(int viewId) {
    WritableMap event = Arguments.createMap();
    context
      .getJSModule(RCTEventEmitter.class)
      .receiveEvent(viewId, "onDownload", event);
  }
}
