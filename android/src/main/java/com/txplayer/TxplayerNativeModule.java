package com.txplayer;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import java.lang.ref.WeakReference;

@ReactModule(name = TxplayerNativeModule.NAME)
public class TxplayerNativeModule extends ReactContextBaseJavaModule {
    public static final String NAME = "TxplayerNativeModule";

    private WeakReference<TxplayerView> currentPlayer;

    public TxplayerNativeModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    public void setCurrentPlayer(WeakReference<TxplayerView> currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @ReactMethod
    public void stopAllPlay() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (currentPlayer != null) {
                    TxplayerView player = currentPlayer.get();
                    if (player != null) {
                        player.stopPlay();
                    }
                }
            }
        });
    }
}

