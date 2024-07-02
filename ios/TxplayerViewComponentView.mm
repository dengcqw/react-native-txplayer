//
//  TxplayerViewComponentView.m
//  react-native-txplayer
//
//  Created by 邓锦龙 on 2024/6/28.
//

#ifdef RCT_NEW_ARCH_ENABLED

#import "TxplayerViewComponentView.h"
#import "TxplayerView.h"

#import <React/RCTConversions.h>
#import <React/RCTFabricComponentsPlugins.h>
#import <react/renderer/components/rntxplayer/ComponentDescriptors.h>
#import <react/renderer/components/rntxplayer/Props.h>
#import <react/renderer/components/rntxplayer/EventEmitters.h>
#import <react/renderer/components/rntxplayer/RCTComponentViewHelpers.h>

using namespace facebook::react;

@interface TxplayerViewComponentView() <RCTTxplayerViewViewProtocol>

@end

@implementation TxplayerViewComponentView
{
    TxplayerView *txplayerView;
    BOOL _shouldPostponeUpdate;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<TxplayerViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        _props = TxplayerViewShadowNode::defaultSharedProps();
        txplayerView = [[TxplayerView alloc] initWithFrame:self.bounds];
        self.contentView = txplayerView;
    }
    return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &_oldProps = static_cast<const TxplayerViewProps &>(*_props);
    const auto &newProps = static_cast<const TxplayerViewProps &>(*props);
    
    NSMutableArray *changedProps = @[].mutableCopy;
    if (_oldProps.videoURL != newProps.videoURL) {
        txplayerView.videoURL = RCTNSStringFromString(newProps.videoURL);
        [changedProps addObject: @"videoURL"];
    }
    if (_oldProps.appId != newProps.appId) {
        txplayerView.appId = RCTNSStringFromString(newProps.appId);
    }
    if (_oldProps.fileId != newProps.fileId) {
        txplayerView.fileId = RCTNSStringFromString(newProps.fileId);
        [changedProps addObject: @"fileId"];
    }
    if (_oldProps.psign != newProps.psign) {
        txplayerView.psign = RCTNSStringFromString(newProps.psign);
    }
    if (_oldProps.videoName != newProps.videoName) {
        txplayerView.videoName = RCTNSStringFromString(newProps.videoName);
    }
    if (_oldProps.videoCoverURL != newProps.videoCoverURL) {
        txplayerView.videoCoverURL = RCTNSStringFromString(newProps.videoCoverURL);
    }
    if (_oldProps.playStartTime != newProps.playStartTime) {
        txplayerView.playStartTime = @(newProps.playStartTime);
    }
    if (_oldProps.playType != newProps.playType) {
        txplayerView.playType = @(newProps.playType);
    }
    if (_oldProps.language != newProps.language) {
        txplayerView.language = RCTNSStringFromString(newProps.language);
    }
    if (_oldProps.enableLoop != newProps.enableLoop) {
        txplayerView.enableLoop = @(newProps.enableLoop);
    }
    if (_oldProps.hidePlayerControl != newProps.hidePlayerControl) {
        txplayerView.hidePlayerControl = @(newProps.hidePlayerControl);
    }
    if (_oldProps.enableSlider != newProps.enableSlider) {
        txplayerView.enableSlider = @(newProps.enableSlider);
    }
    if (_oldProps.enableMorePanel != newProps.enableMorePanel) {
        txplayerView.enableMorePanel = @(newProps.enableMorePanel);
    }
    if (_oldProps.enableDownload != newProps.enableDownload) {
        txplayerView.enableDownload = @(newProps.enableDownload);
    }
    if (_oldProps.enableDanmaku != newProps.enableDanmaku) {
        txplayerView.enableDanmaku = @(newProps.enableDanmaku);
    }
    if (_oldProps.enableFullScreen != newProps.enableFullScreen) {
        txplayerView.enableFullScreen = @(newProps.enableFullScreen);
    }
    if (_oldProps.timeEventDuration != newProps.timeEventDuration) {
        txplayerView.timeEventDuration = @(newProps.timeEventDuration);
    }
    if (_oldProps.enableRotate != newProps.enableRotate) {
        txplayerView.enableRotate = @(newProps.enableRotate);
    }
    if (_oldProps.enablePIP != newProps.enablePIP) {
        txplayerView.enablePIP = @(newProps.enablePIP);
    }
    [txplayerView didSetProps:changedProps];
    
    [super updateProps:props oldProps:oldProps];
}

#pragma mark - Native Commands

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args
{
    if ([commandName isEqualToString:@"startPlay"]) {
        [self startPlay];
        return;
    }
    if ([commandName isEqualToString:@"stopPlay"]) {
        [self stopPlay];
        return;
    }
    if ([commandName isEqualToString:@"togglePlay"]) {
        [self togglePlay];
        return;
    }
    if ([commandName isEqualToString:@"addDanmaku"]) {
        NSObject *arg0 = args[0];
        NSArray* value = (NSArray *)arg0;
        [self addDanmaku:value];
        return;
    }
    if ([commandName isEqualToString:@"switchToOrientation"]) {
        NSObject *arg0 = args[0];
        NSObject *arg1 = args[1];
        [txplayerView switchToOrientation:(NSString *)arg0 force:(NSString *)arg1];
        return;
    }
    if ([commandName isEqualToString:@"seekTo"]) {
        NSObject *arg0 = args[0];
        NSInteger value = [(NSNumber *)arg0 integerValue];
        [self seekTo:value];
        return;
    }
}

- (void)updateEventEmitter:(const facebook::react::EventEmitter::Shared &)eventEmitter
{
    [super updateEventEmitter:eventEmitter];
    assert(std::dynamic_pointer_cast<TxplayerViewEventEmitter const>(eventEmitter));
    [txplayerView setEventEmitter:std::static_pointer_cast<TxplayerViewEventEmitter const>(eventEmitter)];
}

- (void)prepareForRecycle
{
    [super prepareForRecycle];
    if (txplayerView) {
        [txplayerView stopPlay];
    }
    _props = TxplayerViewShadowNode::defaultSharedProps();
    txplayerView = [[TxplayerView alloc] initWithFrame:self.bounds];
    self.contentView = txplayerView;
}


- (void)startPlay {
    [txplayerView startPlay];
}

- (void)stopPlay {
    [txplayerView stopPlay];
}

- (void)togglePlay {
    [txplayerView togglePlay];
}

- (void)addDanmaku:(const NSArray *)contents {
    //    [txplayerView prepareDanmaku:contents];
}

- (void)switchToOrientation:(NSString *)oriention force:(NSString *)force {
    [txplayerView switchToOrientation:oriention force:force];
}

- (void)seekTo:(NSInteger)second {
    [txplayerView seekTo:@(second)];
}

@end

Class<RCTComponentViewProtocol> TxplayerViewCls(void)
{
    return TxplayerViewComponentView.class;
}

#endif

