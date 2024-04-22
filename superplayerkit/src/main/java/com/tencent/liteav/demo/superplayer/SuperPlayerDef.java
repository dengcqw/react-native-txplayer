package com.tencent.liteav.demo.superplayer;

public class SuperPlayerDef {

    public enum PlayerMode {
        WINDOW,
        FULLSCREEN,
        FLOAT
    }

    // sandstalk
    public enum PlayerState {
      INIT,       //初始状态
      PAUSE,      // 暂停中
      LOADING,    // 缓冲中
      PLAYING,    // 播放中
      END         // 结束播放
    }

    public enum PlayerType {
        VOD,
        LIVE,
        LIVE_SHIFT  // Live replay
    }

    public enum Orientation {
        LANDSCAPE,
        PORTRAIT
    }
}
