package com.txplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.liteav.demo.superplayer.SuperPlayerCode;
import com.tencent.liteav.demo.superplayer.SuperPlayerDef;
import com.tencent.liteav.demo.superplayer.SuperPlayerView;


public class TxplayerView extends FrameLayout {

  private SuperPlayerView superPlayerView        = null;
  private FeedPlayerCallBack feedPlayerCallBack     = null;

  private int                position               = -1;
  private boolean            playWithModelIsSuccess = false;

  private String videoURL;
  private String appId;
  private String fileId;
  private String psign;
  private Boolean enableSlider;
  private Boolean enableMorePanel;
  private Boolean enableDownload;
  private Boolean enableDanmaku;
  private Boolean enableFullScreen;
  private Integer playType;
  private Float playStartTime;
  private String language;
  public void setVideoURL(String videoURL) {
    this.videoURL = videoURL;
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
    superPlayerView = new SuperPlayerView(getContext());
    superPlayerView.showOrHideBackBtn(false);
    superPlayerView.setPlayerViewCallback(new SuperPlayerView.OnSuperPlayerViewCallback() {
      @Override
      public void onStartFullScreenPlay() {
        if (feedPlayerCallBack != null) {
          feedPlayerCallBack.onStartFullScreenPlay();
        }
      }

      @Override
      public void onStopFullScreenPlay() {
        if (feedPlayerCallBack != null) {
          feedPlayerCallBack.onStopFullScreenPlay();
        }
      }

      @Override
      public void onClickFloatCloseBtn() {

      }

      @Override
      public void onClickSmallReturnBtn() {
        if (feedPlayerCallBack != null) {
          feedPlayerCallBack.onClickSmallReturnBtn();
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
        superPlayerView.setStartTime(0);
      }

      @Override
      public void onPlayEnd() {

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
    });
    addView(superPlayerView);
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
  public void setFeedPlayerCallBack(FeedPlayerCallBack callBack) {
    feedPlayerCallBack = callBack;
  }

  public FeedPlayerCallBack getFeedPlayerCallBack() {
    return feedPlayerCallBack;
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

  public interface FeedPlayerCallBack {
    void onStartFullScreenPlay();

    void onStopFullScreenPlay();

    void onClickSmallReturnBtn();

  }

  public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
    superPlayerView.onRequestPermissionsResult(requestCode,grantResults);
  }

  public void setStartTime(int progress) {
    superPlayerView.setStartTime(progress);
  }

  public long getProgress() {
    return superPlayerView.getProgress();
  }
}
