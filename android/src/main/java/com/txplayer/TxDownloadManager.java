package com.txplayer;

import static com.tencent.rtmp.downloader.TXVodDownloadDataSource.QUALITY_720P;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaScriptContextHolder;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tencent.rtmp.downloader.ITXVodDownloadListener;
import com.tencent.rtmp.downloader.TXVodDownloadDataSource;
import com.tencent.rtmp.downloader.TXVodDownloadManager;
import com.tencent.rtmp.downloader.TXVodDownloadMediaInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@ReactModule(name = TxDownloadManager.NAME)
public class TxDownloadManager extends ReactContextBaseJavaModule implements ITXVodDownloadListener {
  public static final String NAME = "TxDownloadManager";
  public static final String TxDownloadEvent = "TxDownloadEvent";

  public TxDownloadManager(ReactApplicationContext reactContext) {
    super(reactContext);
  }
  private TXVodDownloadManager  mDownloadManager;
  private int listeners;

  public static final int quality = QUALITY_720P;
  public static final String UserName = "default";


  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private native void nativeInstall(long jsi);

  @ReactMethod(isBlockingSynchronousMethod = true)
  public boolean install() {
    try {
      Log.i(NAME, "Loading C++ library...");
      System.loadLibrary("txdownloadcpp");

      JavaScriptContextHolder jsContext = getReactApplicationContext().getJavaScriptContextHolder();
      nativeInstall(jsContext.get());
      Log.i(NAME, "Successfully installed TxDownloadManager JSI Bindings!");
      return true;
    } catch (Exception exception) {
      Log.e(NAME, "Failed to install TxDownloadManager JSI Bindings!", exception);
      return false;
    }
  }

  TXVodDownloadManager getDownloadMgr() {
    if (mDownloadManager == null) {
      mDownloadManager = TXVodDownloadManager.getInstance();
      mDownloadManager.setListener(this);
    }
    return mDownloadManager;
  }

  public void startDownload(String videoInfo) {
    JsonObject convertedObject = new Gson().fromJson(videoInfo, JsonObject.class);

    TXVodDownloadDataSource downloadDataSource = new TXVodDownloadDataSource(
      convertedObject.get("appId").getAsInt(),
      convertedObject.get("fileId").getAsString(),
      quality,
      convertedObject.get("sign").getAsString(),
      UserName);
    getDownloadMgr().startDownload(downloadDataSource);
    Log.d(NAME, "startDownload ");
  }

  public void stopDownload(String videoFileId, String appId) {
    TXVodDownloadMediaInfo info = getDownloadMgr().getDownloadMediaInfo(Integer.valueOf(appId), videoFileId, quality, UserName);
    getDownloadMgr().stopDownload(info);
    Log.d(NAME, "stopDownload " + videoFileId);
  }

  public void deleteDownload(String videoFileId, String appId) {
    TXVodDownloadMediaInfo info = getDownloadMgr().getDownloadMediaInfo(Integer.valueOf(appId), videoFileId, quality, UserName);
    getDownloadMgr().stopDownload(info);
    boolean ret = getDownloadMgr().deleteDownloadMediaInfo(info);
    Log.d(NAME, "deleteDownload: " + " vid=" + videoFileId + " ret=" + ret );
  }

  public String getDownloadList() {
    List<TXVodDownloadMediaInfo>  list = getDownloadMgr().getDownloadMediaInfoList();
    LinkedList newList = new LinkedList();
    for (int i = 0; i < list.size(); i++) {
      HashMap<String, Object> map = new HashMap<>();
      map.put("duration", list.get(i).getDuration());
      map.put("size", list.get(i).getSize());
      map.put("downloadSize", list.get(i).getDownloadSize());
      map.put("progress", list.get(i).getProgress());
      map.put("playPath", list.get(i).getPlayPath());
      map.put("speed", list.get(i).getSpeed());
      map.put("downloadState", list.get(i).getDownloadState());
//      map.put("isResourceBroken", list.get(i).is());
      map.put("appId", list.get(i).getDataSource().getAppId());
      map.put("fileId", list.get(i).getDataSource().getFileId());
      map.put("sign", list.get(i).getDataSource().getPSign());

      newList.push(map);
    }
    Gson gson = new Gson();

    return gson.toJson(newList);
  }

  private void sendEvent(String eventName, @Nullable WritableMap params) {
    getReactApplicationContext()
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }
  @ReactMethod
  public void addListener(String eventName) {
    if (eventName.equals(TxDownloadEvent)) {
      listeners ++;
    }
  }
  @ReactMethod
  public void removeListeners(Integer count) {
    listeners = listeners - count;
  }

  @Override
  public void onDownloadStart(TXVodDownloadMediaInfo txVodDownloadMediaInfo) {
    if (listeners <= 0) return;
    WritableMap params = Arguments.createMap();
    params.putString("name", "start");
    params.putString("fileId", txVodDownloadMediaInfo.getDataSource().getFileId());
    sendEvent(TxDownloadEvent, params);
  }

  @Override
  public void onDownloadProgress(TXVodDownloadMediaInfo txVodDownloadMediaInfo) {
    if (listeners <= 0) return;
    WritableMap params = Arguments.createMap();
    params.putString("name", "progress");
    params.putString("fileId", txVodDownloadMediaInfo.getDataSource().getFileId());
    params.putDouble("progress", txVodDownloadMediaInfo.getProgress());
    params.putDouble("downloaded", txVodDownloadMediaInfo.getDownloadSize());
    sendEvent(TxDownloadEvent, params);
  }

  @Override
  public void onDownloadStop(TXVodDownloadMediaInfo txVodDownloadMediaInfo) {
    if (listeners <= 0) return;
    WritableMap params = Arguments.createMap();
    params.putString("name", "stop");
    params.putString("fileId", txVodDownloadMediaInfo.getDataSource().getFileId());
    sendEvent(TxDownloadEvent, params);
  }

  @Override
  public void onDownloadFinish(TXVodDownloadMediaInfo txVodDownloadMediaInfo) {
    if (listeners <= 0) return;
    WritableMap params = Arguments.createMap();
    params.putString("name", "finish");
    params.putString("fileId", txVodDownloadMediaInfo.getDataSource().getFileId());
    sendEvent(TxDownloadEvent, params);
  }

  @Override
  public void onDownloadError(TXVodDownloadMediaInfo txVodDownloadMediaInfo, int i, String s) {
    if (listeners <= 0) return;
    WritableMap params = Arguments.createMap();
    params.putString("name", "error");
    params.putString("fileId", txVodDownloadMediaInfo.getDataSource().getFileId());
    sendEvent(TxDownloadEvent, params);
  }

  @Override
  public int hlsKeyVerify(TXVodDownloadMediaInfo txVodDownloadMediaInfo, String s, byte[] bytes) {
    return 0;
  }
}
