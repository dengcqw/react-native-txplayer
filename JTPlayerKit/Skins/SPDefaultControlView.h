//
//  SPDefaultControlView.h
//  SuperPlayer
//
//  Created by annidyfeng on 2018/9/30.
//

#import "SuperPlayerControlView.h"

@interface SPDefaultControlView : SuperPlayerControlView

/** 标题 */
@property(nonatomic, strong) UILabel *titleLabel;
/** 开始播放按钮 */
@property(nonatomic, strong) UIButton *startBtn;
/** 当前播放时长label */
@property(nonatomic, strong) UILabel *currentTimeLabel;
/** 视频总时长label */
@property(nonatomic, strong) UILabel *totalTimeLabel;
/** 全屏按钮 */
@property(nonatomic, strong) UIButton *fullScreenBtn;
/** 锁定屏幕方向按钮 */
@property(nonatomic, strong) UIButton *lockBtn;

/** 返回按钮*/
@property(nonatomic, strong) UIButton *backBtn;
/// 是否禁用返回
@property(nonatomic, assign) BOOL disableBackBtn;
/** bottomView*/
@property(nonatomic, strong) UIImageView *bottomImageView;
/** topView */
@property(nonatomic, strong) UIImageView *topImageView;
/** 弹幕按钮 */
@property(nonatomic, strong) UIButton *danmakuBtn;
/// 是否禁用弹幕
@property(nonatomic, assign) BOOL disableDanmakuBtn;
/** 离线缓存按钮 */
@property(nonatomic, strong) UIButton *offlineBtn;
/// 是否禁用离线缓存
@property(nonatomic, assign) BOOL disableOfflineBtn;
/** 下载按钮 */
@property(nonatomic, strong) UIButton *downloadBtn;
/// 是否禁用下载
@property(nonatomic, assign) BOOL disableDownloadBtn;
/** 音轨切换按钮 */
@property(nonatomic, strong) UIButton *trackBtn;
/// 是否禁用音轨切换
@property(nonatomic, assign) BOOL disableTrackBtn;
/** 字幕切换按钮 */
@property(nonatomic, strong) UIButton *subtitlesBtn;
/// 是否禁用字幕切换
@property(nonatomic, assign) BOOL disableSubtitlesBtn;
/** 截图按钮 */
@property(nonatomic, strong) UIButton *captureBtn;
/// 是否禁用截图
@property(nonatomic, assign) BOOL disableCaptureBtn;
/** 画中画按钮 */
@property(nonatomic, strong) UIButton *pipBtn;
/// 是否禁用画中画
@property(nonatomic, assign) BOOL disablePipBtn;
/** 更多按钮 */
@property(nonatomic, strong) UIButton *moreBtn;
/// 是否禁用更多
@property(nonatomic, assign) BOOL disableMoreBtn;
/** 切换分辨率按钮 */
@property(nonatomic, strong) UIButton *resolutionBtn;
/** 分辨率的View */
@property(nonatomic, strong) UIView *resolutionView;
/// 是否禁用分辨率按钮
@property(nonatomic, assign) BOOL disableResolutionBtn;
/** 播放按钮 */
@property(nonatomic, strong) UIButton *playeBtn;
/** 加载失败按钮 */
@property(nonatomic, strong) UIButton *middleBtn;

/** 当前选中的分辨率btn按钮 */
@property(nonatomic, weak) UIButton *resoultionCurrentBtn;

/** 分辨率的名称 */
@property(nonatomic, strong) NSArray<NSString *> *resolutionArray;
/** 更多设置View */
@property(nonatomic, strong) SuperPlayerSettingsView *moreContentView;
/** 音轨View*/
@property (nonatomic, strong) SuperPlayerTrackView *trackView;
/** 字幕View*/
@property (nonatomic, strong) SuperPlayerSubtitlesView *subtitlesView;
/** 返回直播 */
@property(nonatomic, strong) UIButton *backLiveBtn;

/// 画面比例
@property CGFloat videoRatio;

/** 滑杆 */
@property(nonatomic, strong) PlayerSlider *videoSlider;

@property(nonatomic, strong) UIButton *nextBtn;

/** 重播按钮 */
@property(nonatomic, strong) UIButton *repeatBtn;

/** 是否全屏播放 */
@property(nonatomic, assign, getter=isFullScreen) BOOL fullScreen;
@property(nonatomic, assign, getter=isLockScreen) BOOL isLockScreen;
@property(nonatomic, strong) UIButton *                pointJumpBtn;

//反向设置横屏按钮的选中状态
- (void)fullScreenButtonSelectState:(BOOL)state;

// sandstalk fix iOS 全屏 安全距离
- (void)layoutLandscape:(Boolean)landscapeFullscreen;
@end
