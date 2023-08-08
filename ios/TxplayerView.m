//
//  TxplayerView.m
//  react-native-txplayer
//
//  Created by 邓锦龙 on 2023/8/2.
//

#import "TxplayerView.h"
#import <Masonry/Masonry.h>
#import <react-native-orientation-locker/Orientation.h>

#import "SuperPlayer.h"
#import "UIView+MMLayout.h"
#import "JTDanmakuView.h"
#import "SuperPlayerLocalized.h"
#import "TXLiveSDKTypeDef.h"
#import "TXLiveSDKEventDef.h"

//50 : 5秒
const NSInteger kProgressUpdateTime = 250;

@interface UIViewController(TxplayerView)
- (BOOL)movRotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation;
- (void)movSetNeedsUpdateOfSupportedInterfaceOrientations;
@end

@implementation UIViewController (TxplayerView)

- (BOOL)movRotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    
#ifdef __IPHONE_16_0
    if (@available(iOS 16.0, *)) {
        [self setNeedsUpdateOfSupportedInterfaceOrientations];
        __block BOOL result = YES;
        UIInterfaceOrientationMask mask = 1 << interfaceOrientation;
        UIWindow *window = self.view.window ?: UIApplication.sharedApplication.delegate.window;
        [window.windowScene requestGeometryUpdateWithPreferences:
         [[UIWindowSceneGeometryPreferencesIOS alloc] initWithInterfaceOrientations:mask] errorHandler:^(NSError * _Nonnull error) {
            if (error) {
                result = NO;
            }
        }];
        return result;
    }  else {
        if ([[UIDevice currentDevice] respondsToSelector:@selector(setOrientation:)]) {
            NSNumber *orientationUnknown = @(UIInterfaceOrientationUnknown);
            [[UIDevice currentDevice] setValue:orientationUnknown forKey:@"orientation"];
            [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger:interfaceOrientation] forKey:@"orientation"];
        }
        /// 延时一下调用，否则无法横屏
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [UIViewController attemptRotationToDeviceOrientation];
        });
        
        return YES;
    }
#else
    if ([[UIDevice currentDevice] respondsToSelector:@selector(setOrientation:)]) {
        NSNumber *orientationUnknown = @(UIInterfaceOrientationUnknown);
        [[UIDevice currentDevice] setValue:orientationUnknown forKey:@"orientation"];
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger:interfaceOrientation] forKey:@"orientation"];
    }
    [UIViewController attemptRotationToDeviceOrientation];
    return YES;
#endif
}
- (void)movSetNeedsUpdateOfSupportedInterfaceOrientations {
    
    if (@available(iOS 16.0, *)) {
        
        dispatch_async(dispatch_get_main_queue(), ^{
            
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= 160000
            [self setNeedsUpdateOfSupportedInterfaceOrientations];
#else
            
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
            SEL supportedInterfaceSelector = NSSelectorFromString(@"setNeedsUpdateOfSupportedInterfaceOrientations");
            [self performSelector:supportedInterfaceSelector];
#pragma clang diagnostic pop
            
#endif
        });
    }
}

@end

@interface TxplayerView () <SuperPlayerDelegate,SuperPlayerPlayListener,JTDanmakuDelegate>
@property(strong, nonatomic) SuperPlayerView *playerView;

@property(nonatomic, strong) JTDanmakuView *danmakuView;
@property (nonatomic,assign) NSInteger updateCount;
@property (nonatomic,strong) NSMutableArray *danmakus;
@end

@implementation TxplayerView

- (void)dealloc {
    [_playerView resetPlayer];
}

- (void)startPlay {
    SuperPlayerModel *model = [[SuperPlayerModel alloc] init];
    if(self.videoURL != nil && self.videoURL.length > 0) {
        model.videoURL          = self.videoURL;
        [self.playerView.controlView setResolutionViewState: NO];
    } else {
        model.appId             = [self.appId longLongValue];
        model.videoId           = [[SuperPlayerVideoId alloc] init];
        model.videoId.fileId    = self.fileId;
        model.videoId.psign     = self.psign;
        [self.playerView.controlView setResolutionViewState: YES];
    }
    
    if (self.playType.intValue == 0) {
        model.action = PLAY_ACTION_AUTO_PLAY;
    } else if(self.playType.intValue == 1) {
        model.action = PLAY_ACTION_PRELOAD;
    } else if(self.playType.intValue == 2) {
        model.action = PLAY_ACTION_MANUAL_PLAY;
    }
    
    model.name = self.videoName;
    model.customCoverImageUrl = self.videoCoverURL;
    
    self.playerView.startTime = self.playStartTime.floatValue;
    
    [self.playerView.controlView setSubtitlesBtnState: NO];
    [self.playerView.controlView setTrackBtnState: NO];
    [self.playerView.controlView showOrHideBackBtn: NO];
    
    if ([self.playerView.controlView isKindOfClass:[SPDefaultControlView class]]){
        SPDefaultControlView *controlView = (SPDefaultControlView *)self.playerView.controlView;
        controlView.disableMoreBtn = !self.enableMorePanel.boolValue;
        controlView.disableDownloadBtn = !self.enableDownload.boolValue;
        controlView.enableFullscreen = self.enableFullScreen.boolValue;
        
        if(self.enableDanmaku.boolValue) {
            controlView.disableDanmakuBtn = NO;
            controlView.danmakuBtn.selected = YES;
            [controlView.danmakuBtn addTarget:self action:@selector(danmakuShowAction:) forControlEvents:UIControlEventTouchUpInside];
            [self.playerView addSubview:self.danmakuView];
            
            [_danmakuView mas_makeConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.playerView);
                make.bottom.equalTo(self.playerView);
                make.left.equalTo(self.playerView);
                make.right.equalTo(self.playerView);
            }];
        }
    }
    
    // 滑动手势调节音量
    self.playerView.disableSliderToTime = !self.enableSlider.boolValue;
    // 禁用竖屏全部调节音量和声音
    self.playerView.disableGesture = YES;
    // 屏幕保持
    [UIApplication sharedApplication].idleTimerDisabled = YES;
    
    [self.playerView playWithModelNeedLicence:model];
}

- (void)stopPlay {
    [self.playerView pause];
    
    if(self.onPlayStateChange != nil){
        self.onPlayStateChange(@{
            @"state": [NSNumber numberWithInteger:StateStopped]
        });
    }
}

- (SuperPlayerView *)playerView {
    if (!_playerView) {
        _playerView            = [[SuperPlayerView alloc] init];
        _playerView.disableGesture = YES;
        _playerView.fatherView = self;
        _playerView.delegate = self;
        _playerView.playListener = self;
        _playerView.disableVolumControl = YES;
        _playerView.loop = NO;
        [_playerView showOrHideBackBtn: NO];
        // demo的时移域名，请根据您项目实际情况修改这里
        _playerView.playerConfig.playShiftDomain = @"liteavapp.timeshift.qcloud.com";
        if ([_playerView.controlView isKindOfClass:[SPDefaultControlView class]]) {
          SPDefaultControlView *controlView = (SPDefaultControlView *)_playerView.controlView;
          controlView.disablePipBtn = YES;
          controlView.disableMoreBtn = YES;
          controlView.disableCaptureBtn = YES;
          controlView.disableTrackBtn = YES;
          controlView.disableDanmakuBtn = YES;
          controlView.disableDownloadBtn = YES;
        }
    }
    return _playerView;
}

- (void)setLanguage:(NSString *)language {
    if (language && ![language isEqualToString:_language]) {
        _language = language;
        superPlayerUpdateLanguage(language);
    }
}

#pragma mark - SuperPlayerDelegate


- (void)superPlayerBackAction:(SuperPlayerView *)player {
}

- (void)superPlayerDidStart:(SuperPlayerView *)player {
    [self playTimeDidChange:NO];
    // 开播前会被重置
    [self.playerView.controlView setSliderState: self.enableSlider.boolValue];
}

- (void)superPlayerDidEnd:(SuperPlayerView *)player {
    [self playTimeDidChange:YES];
    if(self.onPlayStateChange != nil){
        self.onPlayStateChange(@{
            @"state": [NSNumber numberWithInteger:StateStopped]
        });
    }
}

- (void)superPlayerFullScreenChanged:(SuperPlayerView *)player {
    [[UIApplication sharedApplication] setStatusBarHidden:false];
}

- (void)superPlayerDidSelectDownload:(SuperPlayerView *)player{
    if(self.onDownload){
        self.onDownload(@{});
    }
}

-(void)fullScreenHookAction {
    self.playerView.disableGesture = NO;
    [self.playerView showOrHideBackBtn:YES];

    FeedBaseFullScreenViewController *vc = [FeedBaseFullScreenViewController new];
    vc.playerView = self.playerView;
    // fix 覆盖 tab bar
    UINavigationController *navi = (UINavigationController *)[UIApplication sharedApplication].keyWindow.rootViewController;
    if ([navi isKindOfClass:[UINavigationController class]]) {
        [navi pushViewController:vc animated:NO];
    } else {
        [self.viewController.navigationController pushViewController:vc animated:NO];
    }
}

- (void)backHookAction {
    self.playerView.disableGesture = YES;
    // 竖屏隐藏返回按键
    [self.playerView showOrHideBackBtn:NO];
    
    UINavigationController *navi = (UINavigationController *)[UIApplication sharedApplication].keyWindow.rootViewController;
    if ([navi isKindOfClass:[UINavigationController class]]) {
        [navi popViewControllerAnimated:NO];
    } else {
        [self.viewController.navigationController popViewControllerAnimated:NO];
    }
    self.playerView.fatherView = self;
}

- (void)singleTapClick {
}
- (void)lockScreen:(BOOL)lock {
}
//- (void)screenRotation:(BOOL)fullScreen {
//    if (fullScreen) {
//        [Orientation setOrientation:UIInterfaceOrientationMaskLandscapeRight];
//    } else {
//        [Orientation setOrientation:UIInterfaceOrientationMaskPortrait];
//    }
//    [self.viewController movSetNeedsUpdateOfSupportedInterfaceOrientations];
//}

/// 播放状态变更通知
- (void)superPlayerDidChangeState:(SuperPlayerState)state{
    if (state == StateStopped) return;
    if(self.onPlayStateChange != nil){
        self.onPlayStateChange(@{
            @"state": [NSNumber numberWithInteger:state]
        });
    }
}

#pragma mark - SuperPlayerPlayListener

- (void)onVodPlayEvent:(TXVodPlayer *)player event:(int)evtID withParam:(NSDictionary *)param {
    
    if (evtID == PLAY_EVT_PLAY_PROGRESS && self.playerView.state == StatePlaying) {
        [self progressEvent];
    }
}

- (void)progressEvent{
    // NSLog(@"VodPlayEvent");
    self.updateCount++;
    if (self.updateCount == kProgressUpdateTime){
        self.updateCount = 0;
        [self playTimeDidChange: NO];
    }
}

- (void)playTimeDidChange:(BOOL)isFinish{
    if (self.onPlayTimeChange != nil) {
        unsigned long totalTime = self.playerView.playDuration * 1000;
        unsigned long progressTime = self.playerView.playCurrentTime * 1000;
        unsigned long remainTime = totalTime - progressTime;
        BOOL finish = isFinish || remainTime == 0;
        self.onPlayTimeChange(@{
            @"totalTime": [NSNumber numberWithUnsignedLong:totalTime],
            @"progressTime": [NSNumber numberWithUnsignedLong:progressTime],
            @"remainTime": [NSNumber numberWithUnsignedLong:remainTime],
            @"isFinish": [NSNumber numberWithBool: finish]
        });
    }
}

#pragma mark - JTDanmakuDelegate

#define kRandomColor [UIColor colorWithRed:arc4random_uniform(256) / 255.0 green:arc4random_uniform(256) / 255.0 blue:arc4random_uniform(256) / 255.0 alpha:1]

- (void)prepareDanmaku:(NSDictionary *)danmakuInfo{
    
    if (![danmakuInfo isKindOfClass: [NSDictionary class]]) {
        return;
    }
    
    NSArray *danmakus = danmakuInfo[@"records"];
    NSNumber *total = danmakuInfo[@"total"];
    NSNumber *current = danmakuInfo[@"current"];
    NSNumber *size = danmakuInfo[@"size"];
    
    
    if (danmakus == nil) return;
    if (![danmakus isKindOfClass:[NSArray class]]) return;
    if (danmakus.count == 0) return;
    
    if (total == nil) return;
    if (![total isKindOfClass:[NSNumber class]]) return;
    if (total.integerValue <= 0) return;
    
    
    if(_danmakus == nil){
        _danmakus = [NSMutableArray array];
    }
    [_danmakus removeAllObjects];
    
    UIFont *font = [UIFont systemFontOfSize:15];
    CGFloat playDuration = _playerView.playDuration;
    if (playDuration == 0) playDuration = 60;
    CGFloat timeSpace = playDuration / total.floatValue;
    
    int index = (current.intValue - 1) * size.intValue;
    for (NSDictionary *dict in danmakus) {
        JTDanmaku * danmaku    = [[JTDanmaku alloc] init];
        [_danmakus addObject:danmaku];
        NSMutableAttributedString *contentStr = [[NSMutableAttributedString alloc] initWithString:dict[@"content"] attributes:@{NSFontAttributeName : font, NSForegroundColorAttributeName : [UIColor whiteColor]}];
        danmaku.contentStr = contentStr;
        danmaku.timePoint  = timeSpace * index;
        index++;
    }
    
    [_danmakuView prepareDanmakus:_danmakus];
}

- (void)danmakuShowAction:(UIButton *)btn {
    if (btn.selected) {
        [self danmakuShow];
    } else {
        [self danmakuHidden];
    }
}

- (void)danmakuShow{
    if (!_danmakuView.hidden) return;
    [_danmakuView start];
    _danmakuView.hidden = NO;
}

- (void)danmakuHidden{
    if (_danmakuView.hidden) return;
    [_danmakuView pause];
    _danmakuView.hidden = YES;
    
}

- (NSTimeInterval)danmakuViewGetPlayTime:(JTDanmakuView *)danmakuView {
    return self.playerView.playCurrentTime;
}

- (BOOL)danmakuViewIsBuffering:(JTDanmakuView *)danmakuView {
    return self.playerView.state != StatePlaying;
}


@end


///全屏窗口
@interface FeedBaseFullScreenViewController()<SuperPlayerDelegate>
///视频窗口父view
@property (nonatomic, strong) UIView *faterView;
@end

@implementation FeedBaseFullScreenViewController

- (UIView *)screenView {
    return nil;
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    // 竖视频可以不旋转
    [Orientation setOrientation:UIInterfaceOrientationMaskLandscapeRight];
    [self movRotateToInterfaceOrientation:UIInterfaceOrientationLandscapeRight];
    [self movSetNeedsUpdateOfSupportedInterfaceOrientations];
    // React Navigation 不需要隐藏
    //[self.navigationController setNavigationBarHidden:YES animated:NO];
}

-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [Orientation setOrientation:UIInterfaceOrientationMaskPortrait];
    [self movRotateToInterfaceOrientation:UIInterfaceOrientationPortrait];
    [self movSetNeedsUpdateOfSupportedInterfaceOrientations];
    //[self.navigationController setNavigationBarHidden:NO animated:NO];
}

-(void)viewDidLoad{
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor blackColor];
    [self.view addSubview:self.faterView];
    [self.faterView mas_makeConstraints:^(MASConstraintMaker *make) {
        if (@available(iOS 11.0, *)) {
            make.left.equalTo(self.view.mas_safeAreaLayoutGuideLeft);
            make.right.equalTo(self.view.mas_safeAreaLayoutGuideRight);
        } else {
            make.left.equalTo(self.view.mas_left);
            make.right.equalTo(self.view.mas_right);
        }
        make.top.equalTo(self.view.mas_top);
        make.bottom.equalTo(self.view.mas_bottom);
    }];
    
}
- (UIView *)faterView {
    if(!_faterView){
        _faterView = [UIView new];
        _faterView.backgroundColor = UIColor.blackColor;
    }
    return _faterView;
}

- (void)setPlayerView:(SuperPlayerView *)playerView {
    _playerView = playerView;
    _playerView.fatherView = self.faterView;
}

@end
