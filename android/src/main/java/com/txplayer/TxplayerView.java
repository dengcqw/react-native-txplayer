package com.txplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.LifecycleEventListener;
import com.tencent.liteav.demo.superplayer.SuperPlayerDef;
import com.tencent.liteav.demo.superplayer.SuperPlayerModel;
import com.tencent.liteav.demo.superplayer.SuperPlayerVideoId;
import com.tencent.liteav.demo.superplayer.SuperPlayerView;
import com.tencent.liteav.demo.superplayer.ui.player.FullScreenPlayer;
import com.tencent.liteav.demo.superplayer.ui.player.WindowPlayer;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class TxplayerView extends FrameLayout implements LifecycleEventListener {

  // 播放器计数
  static int playingCount = 0;
  private boolean isPlaying = false; // 播放中，不熄屏

  private SuperPlayerView superPlayerView        = null;
  private TxPlayerViewCallBack playerViewCallback     = null;

  private long lastTime = 0;
  private long playDuration = 0;

  private String videoURL;
  private String videoName;
  private String videoCoverURL;
  private String appId;
  private String fileId;
  private String psign;
  private boolean enableSlider = true;
  private boolean enableMorePanel = true;
  private boolean enableDownload = false;
  private boolean enableDanmaku = false;
  private boolean enableFullScreen = true;
  private Integer playType = 0;
  private double playStartTime = .0;

  private Integer timeEventDuration = 5;

  private boolean enableLoop = false;

  private boolean enableRotate = true;

  private boolean enablePIP = false;

  private boolean hidePlayerControl = false;

  private boolean isDirty = true;

  // 是否需要预加载
  public boolean needAutoStart() {
    return isDirty && playType != 2;
  }

  public void setVideoURL(String videoURL) {
    if (this.videoURL != null && this.videoURL.equals(videoURL)) {
      return;
    }
    this.videoURL = videoURL;
    this.isDirty = true;
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
    if (this.fileId != null && this.fileId.equals(fileId)) return;
    this.fileId = fileId;
    this.isDirty = true;
  }
  public void setPsign(String psign) {
    this.psign = psign;
  }
  public void setEnableSlider(boolean enableSlider) {
    this.enableSlider = enableSlider;
  }
  public void setEnableMorePanel(boolean enableMorePanel) {
    this.enableMorePanel = enableMorePanel;
  }
  public void setEnableDownload(boolean enableDownload) {
    this.enableDownload = enableDownload;
  }
  public void setEnableDanmaku(boolean enableDanmaku) {
    this.enableDanmaku = enableDanmaku;
  }
  public void setEnableFullScreen(boolean enableFullScreen) {
    this.enableFullScreen = enableFullScreen;
  }
  public void setPlayType(Integer playType) {
    this.playType = playType;
  }
  public void setPlayStartTime(double playStartTime) {
    this.playStartTime = playStartTime;
  }
  public void setTimeEventDuration(Integer timeEventDuration) {
    this.timeEventDuration = timeEventDuration;
  }
  public void setEnableLoop(boolean enableLoop) {
    this.enableLoop = enableLoop;
  }
  public void setEnableRotate(boolean enableRotate) {
    this.enableRotate = enableRotate;
  }
  public void setEnablePIP(boolean enablePIP) {
    this.enablePIP = enablePIP;
  }
  public void setHidePlayerControl(boolean hidePlayerControl) {
    this.hidePlayerControl = hidePlayerControl;
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
    setupLayoutHack();
    superPlayerView = new SuperPlayerView(getContext());
    ColorDrawable colorDrawable = new ColorDrawable();
    colorDrawable.setColor(Color.BLACK);
    superPlayerView.setBackground(colorDrawable);

    addView(superPlayerView);
    superPlayerView.showOrHideBackBtn(false);

    Activity act = (Activity)this.getContext();
    superPlayerView.setPlayerViewCallback(new SuperPlayerView.OnSuperPlayerViewCallback() {
      @Override
      public void onStartFullScreenPlay() {
        removeFeedPlayFromItem();
        rootView().addView(superPlayerView , new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT));
        playerViewCallback.onFullscreen(getId(), true);
        act.getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        FullScreenPlayer fullscreenPlayer = superPlayerView.getFullscreenPlayer();
        superPlayerView.toggleDanmu(fullscreenPlayer.mBarrageOn);
      }

      @Override
      public void onStopFullScreenPlay() {
        addFeedPlayToItem();
        playerViewCallback.onFullscreen(getId(), false);
        act.getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        superPlayerView.toggleDanmu(false);
      }

      @Override
      public void onClickFloatCloseBtn() {

      }

      @Override
      public void onClickSmallReturnBtn() {
      }

      @Override
      public void onStartFloatWindowPlay() {

      }

      @Override
      public void onPlaying() {
        Log.d("djl", "onPlaying: ");
        if (isPlaying == false) {
          isPlaying = true;
          playerViewCallback.onStartPlay(TxplayerView.this);
          Log.d("Txplayer", "onPlaying: " + videoName);
          keepScreen(true);
        }
      }

      @Override
      public void onPlayEnd() {
        Log.d("djl", "onPlayEnd: ");
        if (isPlaying) {
          isPlaying = false;
          keepScreen(false);
        }
        if (playerViewCallback != null) {
          playerViewCallback.onPlayStateChange(getId(), SuperPlayerDef.PlayerState.END.ordinal());
          playTimeDidChange(true);
        }
      }

      @Override
      public void onError(int code) {
      }

      @Override
      public void onShowCacheListClick() {

      }

      @Override
      public void onPlayProgress(long current, long duration, long playable) {
        // 5秒更新一次
        if (current == lastTime) {
          return;
        }
        if (current > lastTime &&  current - lastTime < timeEventDuration) {
          return;
        }
        lastTime = current;
        if (playerViewCallback != null) {
          WritableMap event = Arguments.createMap();
          event.putInt("totalTime", (int) duration);
          event.putInt("progressTime", (int) current);
          event.putInt("remainTime", (int) (duration - current));
          event.putBoolean("isFinish",  isEnd());
          playerViewCallback.onPlayTimeChange(getId(), event);
        }

        playDuration = duration;
      }

      @Override
      public void superPlayerDidChangeState(Integer state) {
        if (isPlaying && state == SuperPlayerDef.PlayerState.PAUSE.ordinal()) {
          isPlaying = false;
          keepScreen(false);
        }
        if (state == 4) return;
        if (playerViewCallback != null) {
          playerViewCallback.onPlayStateChange(getId(), state);
        }
      }

      @Override
      public void onClickDownload() {
        if (playerViewCallback != null) {
          playerViewCallback.onDownload(getId());
        }
      }
    });
  }

  private void  playTimeDidChange(boolean isFinish) {
    if (playerViewCallback != null) {
      WritableMap event = Arguments.createMap();
      event.putInt("totalTime", (int) playDuration);
      event.putInt("progressTime", (int) playDuration);
      event.putInt("remainTime", 0);
      event.putBoolean("isFinish",  isFinish);
      playerViewCallback.onPlayTimeChange(getId(), event);
    }
  }

  public void startPlay() {
    Log.d("Txplayer", "will startPlay: " + videoName + getId());
    if (superPlayerView == null) return;
    Log.d("Txplayer", "did startPlay: " + videoName);

    this.isDirty = false;

    SuperPlayerModel model = new SuperPlayerModel();
    if (videoURL != null) {
      model.url = videoURL;
      superPlayerView.setQualityVisible(false);
    } else {
      model.appId = Integer.valueOf(appId);
      model.videoId = new SuperPlayerVideoId();
      model.videoId.fileId = fileId;
      model.videoId.pSign = psign;
      superPlayerView.setQualityVisible(true);
    }

    if (playType == 0) {
      model.playAction = SuperPlayerModel.PLAY_ACTION_AUTO_PLAY;
    } else if (playType == 1) {
      model.playAction = SuperPlayerModel.PLAY_ACTION_PRELOAD;
    } else if (playType == 2) {
      model.playAction = SuperPlayerModel.PLAY_ACTION_MANUAL_PLAY;
    }

    FullScreenPlayer fullscreenPlayer = superPlayerView.getFullscreenPlayer();
    fullscreenPlayer.setEnableSlider(enableSlider);
    View mIvSnapshot = fullscreenPlayer.findViewById(com.tencent.liteav.demo.superplayer.R.id.superplayer_iv_snapshot);
    mIvSnapshot.setVisibility(View.GONE);
    View mDanmu = fullscreenPlayer.findViewById(com.tencent.liteav.demo.superplayer.R.id.superplayer_iv_danmuku);
    mDanmu.setVisibility(enableDanmaku ? View.VISIBLE : View.GONE);

    WindowPlayer windowPlayer = superPlayerView.getWindowPlayer();
    windowPlayer.setEnableSlider(enableSlider);
//    View mDownloadBtn = windowPlayer.findViewById(com.tencent.liteav.demo.superplayer.R.id.superplayer_iv_download);
//    mDownloadBtn.setVisibility(enableDownload ? View.VISIBLE : View.GONE);
    if (hidePlayerControl) {
      View mImageStartAndResume = (View) windowPlayer.findViewById(com.tencent.liteav.demo.superplayer.R.id.superplayer_resume);
      if (mImageStartAndResume != null && mImageStartAndResume.getParent() != null) {
        ((ViewGroup)mImageStartAndResume.getParent()).removeView(mImageStartAndResume);
      }
      if (windowPlayer.getParent() != null) {
        ((ViewGroup)windowPlayer.getParent()).removeView(windowPlayer);
      }
    } else {
      View mIvFullScreen = (View) windowPlayer.findViewById(com.tencent.liteav.demo.superplayer.R.id.superplayer_iv_fullscreen);
      mIvFullScreen.setVisibility(enableFullScreen ? View.VISIBLE : View.GONE);
      View mTvTitle = (View) windowPlayer.findViewById(com.tencent.liteav.demo.superplayer.R.id.superplayer_tv_title);
      mTvTitle.setVisibility(View.GONE);
    }

    model.title = videoName;
    model.coverPictureUrl = videoCoverURL;
    if (enableDownload) {
      model.isEnableCache = true;
    }

    superPlayerView.setLoop(enableLoop);
    superPlayerView.setEnableRotate(enableRotate);
    superPlayerView.showPIPIV(enablePIP);
    superPlayerView.setStartTime(playStartTime);
    superPlayerView.playWithModelNeedLicence(model);
  }

  public void stopPlay() {
    if (superPlayerView == null) return;
    superPlayerView.pause();
  }

  public void startPip() {
    if (superPlayerView == null) return;
    superPlayerView.setEnableRotate(false);
    superPlayerView.switchToLandscape();
  }

  public void stopPip() {
    if (superPlayerView == null) return;
    superPlayerView.setEnableRotate(true);
    superPlayerView.switchToPortrait();
  }

  public void togglePlay() {
    Log.d("Txplayer", "togglePlay: " + videoName);
    if (superPlayerView == null) return;
    if (isPlaying()) {
      superPlayerView.pause();
    } else {
      superPlayerView.togglePlay();
    }
  }

  public void seekTo(int seconds) {
    if (superPlayerView == null) return;
    superPlayerView.seekTo(seconds);
  }

  public void addDanmukInfo(List<String> danmuContentList) {
    if (danmuContentList == null ||
      danmuContentList.size() == 0 ||
      superPlayerView == null) {
      return;
    }
    superPlayerView.setDanmuData(danmuContentList);
  }

  public void switchToOrientation(String oriention, boolean force) {
    if (!enableFullScreen) {
      return;
    }

    Activity act = (Activity)getContext();
    if (act.isInPictureInPictureMode()) {
      return;
    }


    if ("portrait".equals(oriention)) {
      if(!isFullScreenPlay()) return;
      if (!force) {
        if (superPlayerView.getFullscreenPlayer().isLockScreen()) return;
      }
      superPlayerView.switchToPortrait();
    }

    if(isFullScreenPlay()) return;
    if ("left".equals(oriention)) {
      superPlayerView.switchToLandscape();
    } else if ("right".equals(oriention)) {
      superPlayerView.switchToLandscape();
    }
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

  public void stop() {
    if (superPlayerView != null) {
      superPlayerView.pause();
    }
  }

  public void destroy() {
    Log.d("Txplayer", "destroy: " + getId());
    if (superPlayerView != null) {
      ((ViewGroup) superPlayerView.getParent()).removeView(superPlayerView);
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

  public boolean isEnd() {
    return superPlayerView.getPlayerState() == SuperPlayerDef.PlayerState.END;
  }

  @Override
  public void onHostResume() {
    Log.d("Txplayer", "onHostResume: " + getId());
  }

  @Override
  public void onHostPause() {
    Log.d("Txplayer", "onHostPause: " + getId());
  }

  @Override
  public void onHostDestroy() {
    Log.d("Txplayer", "onHostDestroy: " + getId());
  }

  public interface TxPlayerViewCallBack {
    void onPlayStateChange(int viewId, Integer state);
    void onPlayTimeChange(int videwId, WritableMap map);
    void onDownload(int viewId);
    void onFullscreen(int viewId, boolean fullscreen);
    void onStartPlay(TxplayerView view);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

  }

  @Override
  protected void onDetachedFromWindow() {
    if (isPlaying) {
      isPlaying = false;
      keepScreen(false);
    }
    Log.d("Txplayer", "onDetachedFromWindow: " + getId());
    super.onDetachedFromWindow();
  }

  // 定时计算child layout，RN只计算自己管理的view？
  private void setupLayoutHack() {
    Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
      @Override
      public void doFrame(long frameTimeNanos) {
        manuallyLayoutChildren();
        getViewTreeObserver().dispatchOnGlobalLayout();
        Choreographer.getInstance().postFrameCallbackDelayed(this, 200);
      }
    });
  }

  private void manuallyLayoutChildren() {
    // Log.i("djl", "manuallyLayoutChildren");
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      child.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
      child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
    }
  }

  // 退出全屏
  public void addFeedPlayToItem() {
    ((ViewGroup) superPlayerView.getParent()).removeView(superPlayerView);
    addView(superPlayerView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT));
  }

  public void removeFeedPlayFromItem() {
    if (superPlayerView.getParent() != null) {
      ((ViewGroup)superPlayerView.getParent()).removeView(superPlayerView);
    }
  }

  private ViewGroup rootView() {
    Activity act = (Activity)getContext();
    return (ViewGroup)act.findViewById(android.R.id.content);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (this.hidePlayerControl) {
      return true;
    }
    return super.onInterceptTouchEvent(ev);
  }

  void keepScreen(boolean keep) {
    if (keep) {
      TxplayerView.playingCount ++;
      Activity act = (Activity) getContext();
      act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    } else {
      TxplayerView.playingCount --;
      if (TxplayerView.playingCount <= 0) {
        TxplayerView.playingCount  = 0;
        Activity act = (Activity) getContext();
        act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      }
    }
    Log.d("djl", "Txplay keepScreen: " + TxplayerView.playingCount);
  }
}

