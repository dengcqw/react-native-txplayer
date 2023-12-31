package com.tencent.liteav.demo.superplayer.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Created by liyuejiao on 2018/1/29.
 * <p>
 * 全功能播放器中的弹幕View
 * <p>
 * 1、随机发送弹幕{@link #addDanmaku(String, boolean)}
 * <p>
 * 2、弹幕操作所在线程的Handler{@link DanmuHandler}
 */
public class DanmuView extends DanmakuView {
    private Context        mContext;
    private DanmakuContext mDanmakuContext;
    private boolean        mShowDanma;         // 弹幕是否开启
    private HandlerThread  mHandlerThread;     // 发送弹幕的线程
    private DanmuHandler   mDanmuHandler;      // 弹幕线程handler

    private int currIndex;

    private List<String> mDanmuDataList = new ArrayList<>();

    // prepare完成后, 是否直接run
    private boolean mReadyToRun;

    // 是否已被结束(视频播放完了)
    private boolean terminated;

    private boolean hide = true;

    public DanmuView(Context context) {
        super(context);
        init(context);
    }

    public DanmuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DanmuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        enableDanmakuDrawingCache(true);
        setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                mShowDanma = true;
                start();
                initDanmuHandler();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        mDanmakuContext = DanmakuContext.create();
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        mDanmakuContext.preventOverlapping(overlappingEnablePair);
        prepare(mParser, mDanmakuContext);
    }

    public void setDanmuDataList(List<String> danmuDataList) {
        mDanmuDataList.addAll(danmuDataList);
    }

    public List<String> getDanmuDataList() {
        return mDanmuDataList;
    }

    public void run() {
        // 如果当前有正在执行的任务, 不要重复发起
        if ((mDanmuHandler != null && mDanmuHandler.isRunning) ||
          mDanmuDataList.size() == 0 ||
          hide
//          terminated
        ) {
            return;
        }
        // 第一次run时, danmuview会处于初始化状态
        if (!isPrepared()) {
            mReadyToRun = true;
            return;
        }
        mDanmuHandler.sendEmptyMessageAtTime(DanmuHandler.MSG_SEND_DANMU, 100);
    }

    // 停止danmu播放; 不会再被唤起
    public void stopDanmu() {
        Log.i("TxplayerView_TAG", "stopDanmu");
//        terminated = true;
        clearHandler();
    }

    private void clearHandler() {
        if (mDanmuHandler != null) {
            mDanmuHandler.isRunning = false;
            mDanmuHandler.removeMessages(DanmuHandler.MSG_SEND_DANMU);
        }
    }

    @Override
    public void resume() {
        super.resume();
        run();
        // TODO: 2023/8/7 启动的时候是否会执行
        Log.i("TAG_danmuview", "resume");
    }

    @Override
    public void pause() {
        super.pause();
        clearHandler();
    }

    @Override
    public void release() {
        super.release();
        mShowDanma = false;
        if (mDanmuHandler != null) {
            mDanmuHandler.removeCallbacksAndMessages(null);
            mDanmuHandler = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    private BaseDanmakuParser mParser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    private void initDanmuHandler() {
        mHandlerThread = new HandlerThread("Danmu");
        mHandlerThread.start();
        mDanmuHandler = new DanmuHandler(mHandlerThread.getLooper());
        if (mReadyToRun) {
            run();
        }
    }

    /**
     * 向弹幕View中添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku != null) {
            danmaku.text = content;
            danmaku.padding = 5;
            danmaku.textSize = sp2px(mContext, 20.0f);
            danmaku.textColor = Color.WHITE;
            danmaku.setTime(getCurrentTime());
            if (withBorder) {
                danmaku.borderColor = Color.GREEN;
            }
            addDanmaku(danmaku);
        }
    }

    public void toggle(boolean on) {
        Log.i(TAG, "onToggleControllerView on:" + on);
        if (mDanmuHandler == null) {
            return;
        }
        hide = !on;
        if (on) {
            run();
        } else {
            clearHandler();
        }
    }

    public class DanmuHandler extends Handler {
        public static final int MSG_SEND_DANMU = 1001;

        public DanmuHandler(Looper looper) {
            super(looper);
        }

        // 当前是否正在执行任务
        public boolean isRunning;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEND_DANMU:
                    if (currIndex >= mDanmuDataList.size()) {
                        isRunning = false;
                        return;
                    }
                    sendDanmu();
                    isRunning = true;
                    int time = new Random().nextInt(4000);
                    mDanmuHandler.sendEmptyMessageDelayed(MSG_SEND_DANMU, time);
                    break;
            }
        }

        private void sendDanmu() {
            /*int time = new Random().nextInt(300);
            String content = getContext().getResources().getString(R.string.superplayer_danmu) + time + time;
            addDanmaku(content, false);*/
            // TODO: 2023/8/7 发送弹幕
            String content = mDanmuDataList.get(currIndex);
            addDanmaku(content, false);
            currIndex++;
        }
    }

    private int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }
}
