package com.txplayer;

import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
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
  public final int COMMAND_SHOW_HIGHLIGHT_AREA = 7;
  public final int COMMAND_SHOW_INTERACTION = 8;
  public final int COMMAND_UPDATE_ANSWER = 9;

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
    TxplayerView txplayerView = new TxplayerView(context);
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
    HashMap<String, Integer> map = new HashMap();
    map.put("startPlay", COMMAND_STARTPLAY);
    map.put("stopPlay", COMMAND_STOPPLAY);
    map.put("addDanmaku", COMMAND_ADDDanmuk);
    map.put("switchToOrientation", COMMAND_SWITCH_TO_LANDSCAPE);
    map.put("togglePlay", COMMAND_TOGGLE_PLAY);
    map.put("seekTo", COMMAND_SEEKTO);
    map.put("showHighlightArea", COMMAND_SHOW_HIGHLIGHT_AREA);
    map.put("showInteraction", COMMAND_SHOW_INTERACTION);
    map.put("updateAnswer", COMMAND_UPDATE_ANSWER);

    return map;
  }

  @Override
  public void receiveCommand(
    @NonNull TxplayerView root,
    String commandId,
    @Nullable ReadableArray args
  ) {
    super.receiveCommand(root, commandId, args);

    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
      return;
    }
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
      case COMMAND_SHOW_HIGHLIGHT_AREA:
//        showHighlightArea(root, args);
      case COMMAND_SHOW_INTERACTION:
        showInteraction(root, args.getString(0));
      case COMMAND_UPDATE_ANSWER:
        updateAnswer(root, args.getString(0));
        break;
      default: {}
    }
  }

  @Deprecated
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
            "onPlayTimeTrigger",
            MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onPlayTimeTrigger")
            ));
    builder.put(
            "onInteractionEvent",
            MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onInteractionEvent")
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
  public void togglePlay(TxplayerView view) {
    view.togglePlay();
  }

  @Override
  public void addDanmaku(TxplayerView view, ReadableArray records, int size, int total, int current) {
    if (records == null) {
      return;
    }
    try {
      ArrayList<Object> list = records.toArrayList();
      List<String> danmuContentList = new ArrayList<>();
      for (Object content : list) {
        danmuContentList.add((String) content);
      }
      view.addDanmukInfo(danmuContentList);
    } catch (Exception e) {
    }
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

//  @Override
//  public void showHighlightArea(TxplayerView view, ReadableArray value) {
//    List<HashMap<String, Float>> list = new ArrayList<>();
//    for (int i = 0; i < value.size(); i++) {
//      ReadableMap map = value.getMap(i);
//      HashMap<String, Float> newMap = new HashMap<>();
//      newMap.put("x", (float) map.getDouble("x"));
//      newMap.put("y", (float) map.getDouble("y"));
//      newMap.put("w", (float) map.getDouble("w"));
//      newMap.put("h", (float) map.getDouble("h"));
//      list.add(newMap);
//    }
//    view.showHighlightArea(list);
//  }

  @Override
  public void showInteraction(TxplayerView view, String value) {
    view.post(new Runnable() {
      @Override
      public void run() {
        view.showInteraction(value);
      }
    });
  }

  @Override
  public void updateAnswer(TxplayerView view, String value) {
    view.post(new Runnable() {
      @Override
      public void run() {
        view.updateAnswer(value);
      }
    });
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
  @ReactProp(name = "subtitles")
  public void setSubtitles(TxplayerView view, @Nullable ReadableArray value) {
    view.setSubtitles(value);
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
  @ReactProp(name = "setVideoEventPositions")
  public void setVideoEventPositions(TxplayerView view, @Nullable ReadableArray value) {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < value.size(); i++) {
      list.add(value.getInt(i));
    }
    view.setVideoEventPositions(list);
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

  // View send event to JS
  @Override
  public void onPlayStateChange(View view, Integer state) {
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
    sendEvent(view,"onPlayStateChange", event);
  }

  @Override
  public void onPlayTimeChange(View view, WritableMap map) {
    sendEvent(view,"onPlayTimeChange", map);
  }

  @Override
  public void onPlayTimeTrigger(View view, WritableMap map) {
    sendEvent(view,"onPlayTimeTrigger", map);
  }

  @Override
  public void onFullscreen(View view, boolean fullscreen) {
    WritableMap event = Arguments.createMap();
    event.putInt("fullscreen", fullscreen ? 1 : 0);
    sendEvent(view,"onFullscreen", event);
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
  public void onInteractionSubmit(TxplayerView view, String jsonStr) {
    WritableMap params = Arguments.createMap();
    params.putString("data", jsonStr);
    params.putString("type", "submit");
    sendEvent(view,"onInteractionEvent", params);
  }

  @Override
  public void onDownload(View view) {
    sendEvent(view,"onDownload", Arguments.createMap());
  }

  private void sendEvent(View view, String eventName, @Nullable WritableMap params) {
    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
      EventDispatcher dispatcher =
              UIManagerHelper.getEventDispatcherForReactTag(context, view.getId());
      if (dispatcher == null) return;
      int surfaceId = UIManagerHelper.getSurfaceId(view.getContext());
      if (surfaceId == -1)  return;

      dispatcher.dispatchEvent(
              new Event(surfaceId, view.getId()) {
                @Override
                public String getEventName() {
                  return eventName;
                }

                @Override
                public WritableMap getEventData() {
                  return params;
                }
              }
      );
    } else {
      context.getJSModule(RCTEventEmitter.class).receiveEvent(
              view.getId(),
              eventName,
              params);
    }
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
