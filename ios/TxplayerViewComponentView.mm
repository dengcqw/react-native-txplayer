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
        static const auto defaultProps = std::make_shared<const TxplayerViewProps>();
        _props = defaultProps;
        txplayerView = [[TxplayerView alloc] initWithFrame:self.bounds];
        self.contentView = txplayerView;
    }
    return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{

    [super updateProps:props oldProps:oldProps];
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

