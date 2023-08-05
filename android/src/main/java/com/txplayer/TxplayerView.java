package com.txplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.tencent.liteav.demo.superplayer.SuperPlayerCode;
import com.tencent.liteav.demo.superplayer.SuperPlayerDef;
import com.tencent.liteav.demo.superplayer.SuperPlayerModel;
import com.tencent.liteav.demo.superplayer.SuperPlayerVideoId;
import com.tencent.liteav.demo.superplayer.SuperPlayerView;


public class TxplayerView extends FrameLayout {

  private SuperPlayerView superPlayerView        = null;
  private TxPlayerViewCallBack playerViewCallback     = null;

  private long lastTime = 0;

  private int                position               = -1;
  private boolean            playWithModelIsSuccess = false;

  private String videoURL;
  private String videoName;
  private String videoCoverURL;
  private String appId;
  private String fileId;
  private String psign;
  private Boolean enableSlider = true;
  private Boolean enableMorePanel = true;
  private Boolean enableDownload = false;
  private Boolean enableDanmaku = false;
  private Boolean enableFullScreen = true;
  private Integer playType = 0;
  private Float playStartTime = .0F;
  private String language;
  public void setVideoURL(String videoURL) {
    this.videoURL = videoURL;
  }
  public void setVideoName(String videoName) {
    this.videoName = videoName;
  }
  public void setVideoCoverURL(String videoCoverURL) {
    this.videoCoverURL = videoCoverURL;
  }
  public void setAppId(String appId) {
    this.appId = appId;
  }
  public void setFileId(String fileId) {
    this.fileId = fileId;
  }
  public void setPsign(String psign) {
    this.psign = psign;
  }
  public void setEnableSlider(Boolean enableSlider) {
    this.enableSlider = enableSlider;
  }
  public void setEnableMorePanel(Boolean enableMorePanel) {
    this.enableMorePanel = enableMorePanel;
  }
  public void setEnableDownload(Boolean enableDownload) {
    this.enableDownload = enableDownload;
  }
  public void setEnableDanmaku(Boolean enableDanmaku) {
    this.enableDanmaku = enableDanmaku;
  }
  public void setEnableFullScreen(Boolean enableFullScreen) {
    this.enableFullScreen = enableFullScreen;
  }
  public void setPlayType(Integer playType) {
    this.playType = playType;
  }
  public void setPlayStartTime(Float playStartTime) {
    this.playStartTime = playStartTime;
  }
  public void setLanguage(String language) {
    this.language = language;
  }

  public TxplayerView(@NonNull Context context) {
    super(context);
    this.initViews();
  }
  public TxplayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.initViews();
  }

  public TxplayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.initViews();
  }

  private void initViews() {
    this.setLayoutParams(new LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    superPlayerView = new SuperPlayerView(getContext());
    superPlayerView.showOrHideBackBtn(false);
    superPlayerView.setPlayerViewCallback(new SuperPlayerView.OnSuperPlayerViewCallback() {
      @Override
      public void onStartFullScreenPlay() {
        if (playerViewCallback != null) {
          playerViewCallback.onStartFullScreenPlay();
        }
      }

      @Override
      public void onStopFullScreenPlay() {
        if (playerViewCallback != null) {
          playerViewCallback.onStopFullScreenPlay();
        }
      }

      @Override
      public void onClickFloatCloseBtn() {

      }

      @Override
      public void onClickSmallReturnBtn() {
        if (playerViewCallback != null) {
          playerViewCallback.onClickSmallReturnBtn();
        }
      }

      @Override
      public void onStartFloatWindowPlay() {

      }

      @Override
      public void onPlaying() {
//                if (feedPlayerManager != null) {
//                    feedPlayerManager.setPlayingFeedPlayerView(FeedPlayerView.this, position);
//                }
        // 开始播放后，重置播放开始时间
//        superPlayerView.setStartTime(0);
        if (playerViewCallback != null) {
          playerViewCallback.onPlayStateChange(getId(), SuperPlayerDef.PlayerState.PLAYING.ordinal());
        }
      }

      @Override
      public void onPlayEnd() {
        if (playerViewCallback != null) {
          playerViewCallback.onPlayStateChange(getId(), SuperPlayerDef.PlayerState.END.ordinal());
          playTimeDidChange(true);
        }
      }

      @Override
      public void onError(int code) {
//                if (SuperPlayerCode.VOD_REQUEST_FILE_ID_FAIL == code) {
//                }
//                if (feedPlayerManager != null) {
//                    feedPlayerManager.removePlayingFeedPlayerView(position);
//                }
      }

      @Override
      public void onShowCacheListClick() {

      }

      @Override
      public void onPlayProgress(long current, long duration, long playable) {
        // 5秒更新一次
        if (lastTime > current) {
          lastTime = current;
          return;
        }
        if (lastTime < current + 5) {
          lastTime = current;
        }
        if (playerViewCallback != null) {
          WritableMap event = Arguments.createMap();
          event.putInt("totalTime", (int) duration);
          event.putInt("progressTime", (int) current);
          event.putInt("remainTime", (int) playable);
          event.putBoolean("isFinish",  isEnd());
          playerViewCallback.onPlayTimeChange(getId(), event);
        }
      }

      @Override
      public void superPlayerDidChangeState(Integer state) {
        if (playerViewCallback != null) {
          playerViewCallback.onPlayStateChange(getId(), state);
        }
      }
    });
    addView(superPlayerView);
  }

  private void  playTimeDidChange(Boolean isFinish) {
    if (playerViewCallback != null) {
      WritableMap event = Arguments.createMap();
      event.putInt("totalTime", 0);
      event.putInt("progressTime", 0);
      event.putInt("remainTime", 0);
      event.putBoolean("isFinish",  isFinish);
      playerViewCallback.onPlayTimeChange(getId(), event);
    }
  }

  public void startPlay() {
    if (superPlayerView == null) return;
    SuperPlayerModel model = new SuperPlayerModel();
    if (videoURL != null) {
      model.url = videoURL;
    } else {
      model.appId = Integer.valueOf(appId);
      model.videoId = new SuperPlayerVideoId();
      model.videoId.fileId = fileId;
      model.videoId.pSign = psign;
    }

    if (playType == 0) {
      model.playAction = SuperPlayerModel.PLAY_ACTION_AUTO_PLAY;
    } else if (playType == 1) {
      model.playAction = SuperPlayerModel.PLAY_ACTION_PRELOAD;
    } else if (playType == 2) {
      model.playAction = SuperPlayerModel.PLAY_ACTION_MANUAL_PLAY;
    }

    model.title = videoName;
    model.coverPictureUrl = videoCoverURL;
    superPlayerView.setStartTime(playStartTime);
    superPlayerView.playWithModelNeedLicence(model);
  }

  public void stopPlay() {
    if (superPlayerView == null) return;
    superPlayerView.stopPlay();
  }

  public void addDanmukInfo() {
    if (superPlayerView == null) return;
  }

  /**
   * 设置播放器管理类
   *
   * @param manager
   */
//    public void setFeedPlayerManager(FeedPlayerManager manager) {
//        feedPlayerManager = manager;
//    }

  /**
   * 设置回调接口
   *
   * @param callBack
   */
  public void setFeedPlayerCallBack(TxPlayerViewCallBack callBack) {
    playerViewCallback = callBack;
  }

  public TxPlayerViewCallBack getFeedPlayerCallBack() {
    return playerViewCallback;
  }

//    public void preparePlayVideo(int position, VideoModel videoModel) {
//        this.position = position;
//        this.videoModel = videoModel;
//        SuperPlayerModel playerModel = FeedVodListLoader.conversionModel(videoModel);
//        if (playerModel != null && superPlayerView != null) {
//            playerModel.playAction = SuperPlayerModel.PLAY_ACTION_MANUAL_PLAY;
//            playWithModelIsSuccess = false;
//            superPlayerView.playWithModelNeedLicence(playerModel);
//            superPlayerView.showOrHideBackBtn(false);
//        }
//    }

//    public void preLoad() {
//        if (null != videoModel && position > 0 && !playWithModelIsSuccess) {
//            SuperPlayerModel playerModel = FeedVodListLoader.conversionModel(videoModel);
//            playerModel.playAction = SuperPlayerModel.PLAY_ACTION_PRELOAD;
//            playWithModelIsSuccess = true;
//            superPlayerView.playWithModelNeedLicence(playerModel);
//            superPlayerView.showOrHideBackBtn(false);
//        }
//    }

//    public void play(SuperPlayerModel videoModel) {
//        if (playerModel != null && superPlayerView != null) {
//            playerModel.playAction = SuperPlayerModel.PLAY_ACTION_PRELOAD;
//            playWithModelIsSuccess = true;
//            superPlayerView.playWithModelNeedLicence(playerModel);
//            superPlayerView.showOrHideBackBtn(false);
//            superPlayerView.onResume();
//        }
//    }

  public void resume() {
    if (superPlayerView != null) {
      if (playWithModelIsSuccess) {
        superPlayerView.onResume();
      } else {
//                play(videoModel);
      }
    }
  }

  public void pause() {
    if (superPlayerView != null) {
      superPlayerView.onPause();
    }
  }

  public void stop() {
    if (superPlayerView != null) {
      position = -1;
      superPlayerView.stopPlay();
    }
  }

  public void reset() {
    position = -1;
    if (superPlayerView != null) {
      superPlayerView.revertUI();
    }
  }

  public void destroy() {
    reset();
    if (superPlayerView != null) {
      superPlayerView.setPlayerViewCallback(null);
      superPlayerView.resetPlayer();
      superPlayerView.release();
    }
    superPlayerView = null;
  }

  public boolean isPlaying() {
    return superPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.PLAYING;
  }

  public boolean isFullScreenPlay() {
    return superPlayerView.getPlayerMode() == SuperPlayerDef.PlayerMode.FULLSCREEN;
  }

  public void setWindowPlayMode() {
    superPlayerView.switchPlayMode(SuperPlayerDef.PlayerMode.WINDOW);
  }

  public boolean isEnd() {
    return superPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.END;
  }

  public int getPosition() {
    return position;
  }

  public interface TxPlayerViewCallBack {
    void onStartFullScreenPlay();

    void onStopFullScreenPlay();

    void onClickSmallReturnBtn();

    void onPlayStateChange(int viewId, Integer state);
    void onPlayTimeChange(int videwId, WritableMap map);
    void onDownload(int viewId);
  }

//  public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
//    superPlayerView.onRequestPermissionsResult(requestCode,grantResults);
//  }
//
//  public void setStartTime(int progress) {
//    superPlayerView.setStartTime(progress);
//  }
//
  public long getProgress() {
    return superPlayerView.getProgress();
  }
}
