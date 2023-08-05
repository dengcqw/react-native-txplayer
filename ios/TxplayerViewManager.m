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
    
    view.playType = @0;
    view.playStartTime = @0;
    
    return view;
}

RCT_EXPORT_VIEW_PROPERTY(videoURL, NSString)
RCT_EXPORT_VIEW_PROPERTY(appId, NSString)
RCT_EXPORT_VIEW_PROPERTY(fileId, NSString)
RCT_EXPORT_VIEW_PROPERTY(psign, NSString)

RCT_EXPORT_VIEW_PROPERTY(onPlayStateChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onPlayTimeChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onDownload, RCTBubblingEventBlock)

RCT_EXPORT_VIEW_PROPERTY(enableSlider, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableMorePanel, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableDownload, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableDanmaku, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableFullScreen, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(playType, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(playStartTime, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(language, NSString)


RCT_EXPORT_METHOD(startPlay:(nonnull NSNumber *) reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        TxplayerView * player  = (TxplayerView *) viewRegistry[reactTag];
        if (self.currentPlayerView != player) {
            [player startPlay];
            if (self.currentPlayerView) {
                [self.currentPlayerView stopPlay];
            }
        }
        self.currentPlayerView = player;
    }];
}

RCT_EXPORT_METHOD(stopPlay:(nonnull NSNumber *) reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        TxplayerView * player  = (TxplayerView *) viewRegistry[reactTag];
        [player stopPlay];
    }];
}

RCT_EXPORT_METHOD(addDanmaku:(nonnull NSNumber *) reactTag danmakuInfo:(NSDictionary *)danmakuInfo ){
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        TxplayerView * player  = (TxplayerView *) viewRegistry[reactTag];
        [player prepareDanmaku:danmakuInfo];
    }];
}

@end
