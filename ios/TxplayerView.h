//
//  TxplayerView.h
//  react-native-txplayer
//
//  Created by 邓锦龙 on 2023/8/2.
//

#import <UIKit/UIKit.h>
#import <React/RCTComponent.h>

NS_ASSUME_NONNULL_BEGIN

@interface TxplayerView : UIView

// 播放时间
@property (nonatomic, copy) RCTBubblingEventBlock onPlayTimeChange;
// 播放状态
@property (nonatomic, copy) RCTBubblingEventBlock onPlayStateChange;
// 下载点击事件
@property (nonatomic, copy) RCTBubblingEventBlock onDownload;
@property (nonatomic, copy) RCTBubblingEventBlock onFullscreen;
@property (nonatomic, copy) void (^onStartPlay)(TxplayerView *mgr);

// 播放内容
@property(copy, nonatomic) NSString *videoURL;

@property(copy, nonatomic) NSString *appId;
@property(copy, nonatomic) NSString *fileId;
@property(copy, nonatomic) NSString *psign;

// 播放器UI配置
@property(copy, nonatomic) NSString *videoName;
@property(copy, nonatomic) NSString *videoCoverURL;
@property(copy, nonatomic) NSNumber *playStartTime;
@property(copy, nonatomic) NSNumber *playType;
@property(copy, nonatomic) NSString *language;


@property(nonatomic, copy) NSNumber *enableLoop; // 进度条
@property(nonatomic, copy) NSNumber *hidePlayerControl;
@property(nonatomic, copy) NSNumber *enableSlider; // 进度条
@property(nonatomic, copy) NSNumber *enableMorePanel; // 全屏更多按键
@property(nonatomic, copy) NSNumber *enableDownload; // 可以下载
@property(nonatomic, copy) NSNumber *enableDanmaku; //
@property(nonatomic, copy) NSNumber *enableFullScreen;
@property(nonatomic, copy) NSNumber *timeEventDuration;
@property(nonatomic, copy) NSNumber *enableRotate; // 屏幕旋转

@property(assign, nonatomic) BOOL isDirty;


- (void)startPlay;
- (void)stopPlay;
- (void)togglePlay;
- (void)prepareDanmaku:(NSDictionary *)danmakus;
- (void)switchToOrientation:(NSString *)orientation;
- (void)seekTo:(NSNumber *)second;

@end


@class SuperPlayerView;
///全屏窗口
@interface FeedBaseFullScreenViewController : UIViewController
///视频窗口
@property (nonatomic, strong) SuperPlayerView *playerView;
@property (nonatomic,assign) NSUInteger orientation;
@property (nonatomic,assign) BOOL enableRotate;

- (UIView *)screenView;
@end

NS_ASSUME_NONNULL_END
