#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import <React/RCTBridgeModule.h>

#import "TxplayerView.h"

// 1 横屏后view会重建，需要保存播放器view
// 2 多个 view 使用播放器
// 3 覆盖tabbar

@interface TxplayerViewManager : RCTViewManager
@property(weak, nonatomic) TxplayerView *currentPlayerView; // 退出页面能自动暂停, 其他开播时主动暂停
@end

@implementation TxplayerViewManager

RCT_EXPORT_MODULE(TxplayerView)

- (UIView *)view
{
    TxplayerView  *view = [TxplayerView new];
    view.enableSlider = @YES;
    view.enableMorePanel = @YES;
    view.enableDownload = @NO;
    view.enableDanmaku = @NO;
    view.enableFullScreen = @YES;
    view.hidePlayerControl = @NO;
    view.enableLoop = @YES;
    view.dirty = true;
    
    view.playType = @0;
    view.playStartTime = @0;
    view.timeEventDuration = @5;
    view.enableRotate = @YES;
    __weak TxplayerViewManager *weakSelf = self;
    view.onStartPlay = ^(TxplayerView *player) {
        __strong TxplayerViewManager *mgr = weakSelf;
        if (mgr.currentPlayerView == player) return;
        dispatch_async(dispatch_get_main_queue(), ^{
            if (mgr.currentPlayerView) {
                [mgr.currentPlayerView stopPlay];
            }
            mgr.currentPlayerView = player;
        });
    };
    
    return view;
}

RCT_EXPORT_VIEW_PROPERTY(videoURL, NSString)
RCT_EXPORT_VIEW_PROPERTY(appId, NSString)
RCT_EXPORT_VIEW_PROPERTY(fileId, NSString)
RCT_EXPORT_VIEW_PROPERTY(psign, NSString)
RCT_EXPORT_VIEW_PROPERTY(videoCoverURL, NSString)
RCT_EXPORT_VIEW_PROPERTY(videoName, NSString)

RCT_EXPORT_VIEW_PROPERTY(onPlayStateChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onPlayTimeChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onDownload, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onFullscreen, RCTBubblingEventBlock)

RCT_EXPORT_VIEW_PROPERTY(hidePlayerControl, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableSlider, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableMorePanel, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableDownload, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableDanmaku, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableFullScreen, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(playType, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(playStartTime, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(language, NSString)
RCT_EXPORT_VIEW_PROPERTY(enableLoop, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(timeEventDuration, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableRotate, NSNumber)

RCT_EXPORT_METHOD(startPlay:(nonnull NSNumber *) reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        TxplayerView * player  = (TxplayerView *) viewRegistry[reactTag];
        [player startPlay];
        NSLog(@"djl startPlay %@ %@", reactTag, player.videoName);
    }];
}

RCT_EXPORT_METHOD(stopPlay:(nonnull NSNumber *) reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        TxplayerView * player  = (TxplayerView *) viewRegistry[reactTag];
        [player stopPlay];
        NSLog(@"djl stopPlay %@ %@", reactTag, player.videoName);
    }];
}

RCT_EXPORT_METHOD(addDanmaku:(nonnull NSNumber *) reactTag danmakuInfo:(NSDictionary *)danmakuInfo ){
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        TxplayerView * player  = (TxplayerView *) viewRegistry[reactTag];
        [player prepareDanmaku:danmakuInfo];
    }];
}

RCT_EXPORT_METHOD(switchToOrientation:(nonnull NSNumber *) reactTag oriention: (NSString *)oriention) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        TxplayerView * player  = (TxplayerView *) viewRegistry[reactTag];
        [player switchToOrientation:oriention];
    }];
}

RCT_EXPORT_METHOD(seekTo:(nonnull NSNumber *) reactTag second: (nonnull NSNumber *)second) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        TxplayerView * player  = (TxplayerView *) viewRegistry[reactTag];
        [player seekTo:second];
    }];
}

RCT_EXPORT_METHOD(togglePlay:(nonnull NSNumber *) reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        TxplayerView * player  = (TxplayerView *) viewRegistry[reactTag];
        [player togglePlay];
        NSLog(@"djl togglePlay %@ %@", reactTag, player.videoName);
    }];
}

RCT_EXPORT_METHOD(stopAllPlay) {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSLog(@"djl stopAllPlay");
        if (self.currentPlayerView) {
            [self.currentPlayerView stopPlay];
        }
    });
}

@end
