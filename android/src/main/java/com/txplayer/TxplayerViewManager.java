package com.txplayer;

import android.os.Handler;
import android.os.Looper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.viewmanagers.TxplayerViewManagerDelegate;
import com.facebook.react.viewmanagers.TxplayerViewManagerInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ReactModule(name = TxplayerViewManager.REACT_CLASS)
public class TxplayerViewManager extends SimpleViewManager<TxplayerView> implements TxplayerView.TxPlayerViewCallBack, TxplayerViewManagerInterface<TxplayerView> {
  public static final String REACT_CLASS = "TxplayerView";
  private final ReactApplicationContext context;


  public final int COMMAND_STARTPLAY = 1;
  public final int COMMAND_STOPPLAY = 2;
  public final int COMMAND_ADDDanmuk = 3;
  public final int COMMAND_SWITCH_TO_LANDSCAPE = 4;
  public final int COMMAND_TOGGLE_PLAY = 5;
  public final int COMMAND_SEEKTO = 6;

  private WeakReference<TxplayerView> currentPlayer;


  private final ViewManagerDelegate<TxplayerView> mDelegate;
  public TxplayerViewManager(ReactApplicationContext reactContext) {
    context = reactContext;
    mDelegate = new TxplayerViewManagerDelegate<>(this);
  }

  @Nullable
  @Override
  protected ViewManagerDelegate<TxplayerView> getDelegate() {
    return mDelegate;
  }

  @Override
  @NonNull
  public String getName() {
    return TxplayerViewManager.REACT_CLASS;
  }

  @Override
  @NonNull
  protected TxplayerView createViewInstance(@NonNull ThemedReactContext context) {
    TxplayerView txplayerView = new TxplayerView(this.context.getCurrentActivity());
    txplayerView.setFeedPlayerCallBack(this);
    return txplayerView;
  }

  @Override
  public void onDropViewInstance(TxplayerView view) {
    super.onDropViewInstance(view);
    view.stopPlay();
    view.destroy();
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
    try {
      HashMap danmu = (HashMap) args.toArrayList().get(0);
      ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) danmu.get("records");
      List<String> danmuContentList = new ArrayList<>();
      for (HashMap<String, String> strObj : list) {
        danmuContentList.add(strObj.get("content"));
      }
      root.addDanmukInfo(danmuContentList);
    } catch (Exception e) {
    }
  }

  private void switchToOrientation(TxplayerView root, ReadableArray args) {
    if (args == null) {
      return;
    }
    String oriention  = args.getString(0);
    String force  = args.getString(1);
    root.switchToOrientation(oriention, "1".equals(force));
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
    builder.put(
      "onFullscreen",
      MapBuilder.of(
        "phasedRegistrationNames",
        MapBuilder.of("bubbled", "onFullscreen")
      ));

    return builder.build();
  }


  @Override
  public void seekTo(TxplayerView view, int second) {
    view.seekTo(second);
  }

  @Override
  public void switchToOrientation(TxplayerView view, String oriention, String force) {
    view.switchToOrientation(oriention, force.equals("1"));
  }

  @Override
  public void addDanmaku(TxplayerView view, ReadableArray contents) {
    if (contents == null) {
      return;
    }
    try {
      HashMap danmu = (HashMap) contents.toArrayList().get(0);
      ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) danmu.get("records");
      List<String> danmuContentList = new ArrayList<>();
      for (HashMap<String, String> strObj : list) {
        danmuContentList.add(strObj.get("content"));
      }
      view.addDanmukInfo(danmuContentList);
    } catch (Exception e) {
    }
  }

  @Override
  public void togglePlay(TxplayerView view) {
    view.togglePlay();
  }

  @Override
  public void stopPlay(TxplayerView view) {
    view.stopPlay();
  }

  @Override
  public void startPlay(TxplayerView view) {
    view.startPlay();
  }

  @Override
  public void setLanguage(TxplayerView view, @Nullable String value) {
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
  public void setEnableSlider(TxplayerView view, boolean enableSlider) {
    view.setEnableSlider(enableSlider);
  }
  @ReactProp(name = "enableMorePanel")
  public void setEnableMorePanel(TxplayerView view, boolean enableMorePanel) {
    view.setEnableMorePanel(enableMorePanel);
  }
  @ReactProp(name = "enableDownload")
  public void setEnableDownload(TxplayerView view, boolean enableDownload) {
      view.setEnableDownload(enableDownload);
  }
  @ReactProp(name = "enableDanmaku")
  public void setEnableDanmaku(TxplayerView view, boolean enableDanmaku) {
      view.setEnableDanmaku(enableDanmaku);
  }
  @ReactProp(name = "enableFullScreen")
  public void setEnableFullScreen(TxplayerView view, boolean enableFullScreen) {
      view.setEnableFullScreen(enableFullScreen);
  }
  @ReactProp(name = "playType")
  public void setPlayType(TxplayerView view, int playType) {
    view.setPlayType(playType);
  }
  @ReactProp(name = "playStartTime")
  public void setPlayStartTime(TxplayerView view, int playStartTime) {
    view.setPlayStartTime(playStartTime);
  }

  @ReactProp(name = "hidePlayerControl")
  public void setHidePlayerControl(TxplayerView view, boolean hidePlayerControl) {
    view.setHidePlayerControl(hidePlayerControl);
  }
  @ReactProp(name = "enableLoop")
  public void setEnableLoop(TxplayerView view, boolean enableLoop) {
      view.setEnableLoop(enableLoop);
  }
  @ReactProp(name = "enableRotate")
  public void setEnableRotate(TxplayerView view, boolean enableRotate) {
      view.setEnableRotate(enableRotate);
  }
  @ReactProp(name = "enablePIP")
  public void setEnablePIP(TxplayerView view, boolean enablePIP) {
      view.setEnablePIP(enablePIP);
  }

  @ReactProp(name = "timeEventDuration")
  public void setTimeEventDuration(TxplayerView view, int timeEventDuration) {
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
  public void onFullscreen(int viewId, boolean fullscreen) {
    WritableMap event = Arguments.createMap();
    event.putInt("fullscreen", fullscreen ? 1 : 0);
    sendEvent(viewId,"onFullscreen", event);
  }

  @Override
  public void onStartPlay(TxplayerView view) {
    // update currentplayer, when user tap player to start play
    if (currentPlayer != null) {
      TxplayerView player = currentPlayer.get();
      if (player != null && player != view) {
        player.stopPlay();
      }
    }
    currentPlayer = new WeakReference<>(view);
    context.getNativeModule(TxplayerNativeModule.class).setCurrentPlayer(currentPlayer);
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

  @Override
  protected void onAfterUpdateTransaction(@NonNull TxplayerView view) {
    super.onAfterUpdateTransaction(view);
    Log.d("Txplayer", "onAfterUpdateTransaction: " + view.getId());
    if (view.needAutoStart()) {
      new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
          view.startPlay();
        }
      }, 200);
    }
  }
}
