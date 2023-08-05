package com.txplayer;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

public class TxplayerViewManager extends SimpleViewManager<TxplayerView> {
  public static final String REACT_CLASS = "TxplayerView";

  public final int COMMAND_STARTPLAY = 1;
  public final int COMMAND_STOPPLAY = 2;
  public final int COMMAND_ADDDanmuk = 3;

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  @NonNull
  public TxplayerView createViewInstance(ThemedReactContext reactContext) {
    return new TxplayerView(reactContext);
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
    int reactNativeViewId = args.getInt(0);
    int commandIdInt = Integer.parseInt(commandId);

    switch (commandIdInt) {
      case COMMAND_STARTPLAY:
        break;
      case COMMAND_STOPPLAY:
        break;
      case COMMAND_ADDDanmuk:
        break;
      default: {}
    }
  }

  @ReactProp(name = "videoURL")
  public void setVideoURL(TxplayerView view, String videoURL) {
    view.setVideoURL(videoURL);
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
}
