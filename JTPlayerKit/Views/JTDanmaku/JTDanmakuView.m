//
//  JTDanmakuView.m
//  31- JTDanmakuDemo
//
//  Created by 于 传峰 on 15/7/9.
//  Copyright (c) 2015年 于 传峰. All rights reserved.
//

#import "JTDanmakuView.h"

#import "SuperPlayerLocalized.h"
#import "JTDanmakuInfo.h" 

#define X(view)       view.frame.origin.x
#define Y(view)       view.frame.origin.y
#define Width(view)   view.frame.size.width
#define Height(view)  view.frame.size.height
#define Left(view)    X(view)
#define Right(view)   (X(view) + Width(view))
#define Top(view)     Y(view)
#define Bottom(view)  (Y(view) + Height(view))
#define CenterX(view) (Left(view) + Right(view)) / 2
#define CenterY(view) (Top(view) + Bottom(view)) / 2

@interface JTDanmakuView () {
    NSTimer* _timer;
}
@property(nonatomic, strong) NSMutableArray* danmakus;
@property(nonatomic, strong) NSMutableArray* currentDanmakus;
@property(nonatomic, strong) NSMutableArray* subDanmakuInfos;

@property(nonatomic, strong) NSMutableDictionary* linesDict;
@property(nonatomic, strong) NSMutableDictionary* centerTopLinesDict;
@property(nonatomic, strong) NSMutableDictionary* centerBottomLinesDict;

//@property(nonatomic, assign) BOOL centerPause;

@end

static NSTimeInterval const timeMargin = 0.5;
@implementation             JTDanmakuView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.userInteractionEnabled = NO;
        self.backgroundColor        = [UIColor clearColor];
    }
    return self;
}

#pragma mark - lazy
- (NSMutableArray*)subDanmakuInfos {
    if (!_subDanmakuInfos) {
        _subDanmakuInfos = [[NSMutableArray alloc] init];
    }
    return _subDanmakuInfos;
}

- (NSMutableDictionary*)linesDict {
    if (!_linesDict) {
        _linesDict = [[NSMutableDictionary alloc] init];
    }
    return _linesDict;
}

- (NSMutableDictionary*)centerBottomLinesDict {
    if (!_centerBottomLinesDict) {
        _centerBottomLinesDict = [[NSMutableDictionary alloc] init];
    }
    return _centerBottomLinesDict;
}

- (NSMutableDictionary*)centerTopLinesDict {
    if (!_centerTopLinesDict) {
        _centerTopLinesDict = [[NSMutableDictionary alloc] init];
    }
    return _centerTopLinesDict;
}

- (NSMutableArray*)currentDanmakus {
    if (!_currentDanmakus) {
        _currentDanmakus = [NSMutableArray array];
    }
    return _currentDanmakus;
}

#pragma mark - perpare
- (void)prepareDanmakus:(NSArray*)danmakus {
    self.danmakus = [[danmakus sortedArrayUsingComparator:^NSComparisonResult(JTDanmaku* obj1, JTDanmaku* obj2) {
        if (obj1.timePoint > obj2.timePoint) {
            return NSOrderedDescending;
        }
        return NSOrderedAscending;
    }] mutableCopy];
}

- (void)getCurrentTime {
    //    NSLog(@"getCurrentTime---------");

    if ([self.delegate danmakuViewIsBuffering:self]) return;

    [self.subDanmakuInfos enumerateObjectsUsingBlock:^(JTDanmakuInfo* obj, NSUInteger idx, BOOL* stop) {
        NSTimeInterval leftTime = obj.leftTime;
        leftTime -= timeMargin;
        obj.leftTime = leftTime;
    }];

    [self.currentDanmakus removeAllObjects];
    NSTimeInterval timeInterval = [self.delegate danmakuViewGetPlayTime:self];
    NSString*      timeStr      = [NSString stringWithFormat:@"%0.1f", timeInterval];
    timeInterval                = timeStr.floatValue;

    [self.danmakus enumerateObjectsUsingBlock:^(JTDanmaku* obj, NSUInteger idx, BOOL* stop) {
        if (obj.timePoint >= timeInterval && obj.timePoint < timeInterval + timeMargin) {
            [self.currentDanmakus addObject:obj];
            //            NSLog(@"%f----%f--%zd", timeInterval, obj.timePoint, idx);
        } else if (obj.timePoint > timeInterval) {
            *stop = YES;
        }
    }];

    if (self.currentDanmakus.count > 0) {
        for (JTDanmaku* danmaku in self.currentDanmakus) {
            [self playDanmaku:danmaku];
        }
    }
}

- (void)playDanmaku:(JTDanmaku*)danmaku {
    UILabel* playerLabel       = [[UILabel alloc] init];
    playerLabel.attributedText = danmaku.contentStr;
    [playerLabel sizeToFit];
    [self addSubview:playerLabel];
    playerLabel.backgroundColor = [UIColor clearColor];
    //    self.playingLabel = playerLabel;

    switch (danmaku.position) {
        case JTDanmakuPositionNone:
            [self playFromRightDanmaku:danmaku playerLabel:playerLabel];
            break;

        case JTDanmakuPositionCenterTop:
        case JTDanmakuPositionCenterBottom:
            [self playCenterDanmaku:danmaku playerLabel:playerLabel];
            break;

        default:
            break;
    }
}

#pragma mark - center top \ bottom
- (void)playCenterDanmaku:(JTDanmaku*)danmaku playerLabel:(UILabel*)playerLabel {
    NSAssert(self.centerDuration && self.maxCenterLineCount, superPlayerLocalized(@"SuperPlayer.usebarrage"));

    JTDanmakuInfo* newInfo = [[JTDanmakuInfo alloc] init];
    newInfo.playLabel      = playerLabel;
    newInfo.leftTime       = self.centerDuration;
    newInfo.danmaku        = danmaku;

    NSMutableDictionary* centerDict = nil;

    if (danmaku.position == JTDanmakuPositionCenterTop) {
        centerDict = self.centerTopLinesDict;
    } else {
        centerDict = self.centerBottomLinesDict;
    }

    NSInteger valueCount = centerDict.allKeys.count;
    if (valueCount == 0) {
        newInfo.lineCount = 0;
        [self addCenterAnimation:newInfo centerDict:centerDict];
        return;
    }
    for (int i = 0; i < valueCount; i++) {
        JTDanmakuInfo* oldInfo = centerDict[@(i)];
        if (!oldInfo) break;
        if (![oldInfo isKindOfClass:[JTDanmakuInfo class]]) {
            newInfo.lineCount = i;
            [self addCenterAnimation:newInfo centerDict:centerDict];
            break;
        } else if (i == valueCount - 1) {
            if (valueCount < self.maxCenterLineCount) {
                newInfo.lineCount = i + 1;
                [self addCenterAnimation:newInfo centerDict:centerDict];
            } else {
                [self.danmakus removeObject:danmaku];
                [playerLabel removeFromSuperview];
                NSLog(@"%@", superPlayerLocalized(@"SuperPlayer.toomanycomments"));
            }
        }
    }
}

- (void)addCenterAnimation:(JTDanmakuInfo*)info centerDict:(NSMutableDictionary*)centerDict {
    UILabel*  label     = info.playLabel;
    NSInteger lineCount = info.lineCount;

    if (info.danmaku.position == JTDanmakuPositionCenterTop) {
        label.frame = CGRectMake((Width(self) - Width(label)) * 0.5, (self.lineHeight + self.lineMargin) * lineCount, Width(label), Height(label));
    } else {
        label.frame = CGRectMake((Width(self) - Width(label)) * 0.5, Height(self) - Height(label) - (self.lineHeight + self.lineMargin) * lineCount, Width(label), Height(label));
    }

    centerDict[@(lineCount)] = info;
    [self.subDanmakuInfos addObject:info];

    [self performCenterAnimationWithDuration:info.leftTime danmakuInfo:info];
}

- (void)performCenterAnimationWithDuration:(NSTimeInterval)duration danmakuInfo:(JTDanmakuInfo*)info {
    UILabel* label = info.playLabel;

    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(duration * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        if (self->_isPauseing) return;

        if (info.danmaku.position == JTDanmakuPositionCenterBottom) {
            self.centerBottomLinesDict[@(info.lineCount)] = @(0);
        } else {
            self.centerTopLinesDict[@(info.lineCount)] = @(0);
        }

        [label removeFromSuperview];
        [self.subDanmakuInfos removeObject:info];
    });
}

#pragma mark - from right
- (void)playFromRightDanmaku:(JTDanmaku*)danmaku playerLabel:(UILabel*)playerLabel {
    JTDanmakuInfo* newInfo = [[JTDanmakuInfo alloc] init];
    newInfo.playLabel      = playerLabel;
    newInfo.leftTime       = self.duration;
    newInfo.danmaku        = danmaku;

    playerLabel.frame = CGRectMake(Width(self), 0, Width(playerLabel), Height(playerLabel));

    NSInteger valueCount = self.linesDict.allKeys.count;
    if (valueCount == 0) {
        newInfo.lineCount = 0;
        [self addAnimationToViewWithInfo:newInfo];
        return;
    }

    for (int i = 0; i < valueCount; i++) {
        JTDanmakuInfo* oldInfo = self.linesDict[@(i)];
        if (!oldInfo) break;
        if (![self judgeIsRunintoWithFirstDanmakuInfo:oldInfo behindLabel:playerLabel]) {
            newInfo.lineCount = i;
            [self addAnimationToViewWithInfo:newInfo];
            break;
        } else if (i == valueCount - 1) {
            if (valueCount < self.maxShowLineCount) {
                newInfo.lineCount = i + 1;
                [self addAnimationToViewWithInfo:newInfo];
            } else {
                [self.danmakus removeObject:danmaku];
                [playerLabel removeFromSuperview];
                NSLog(@"%@", superPlayerLocalized(@"SuperPlayer.toomanycomments"));
            }
        }
    }
}

- (void)addAnimationToViewWithInfo:(JTDanmakuInfo*)info {
    UILabel*  label     = info.playLabel;
    NSInteger lineCount = info.lineCount;

    label.frame = CGRectMake(Width(self), (self.lineHeight + self.lineMargin) * lineCount, Width(label), Height(label));

    [self.subDanmakuInfos addObject:info];
    self.linesDict[@(lineCount)] = info;

    [self performAnimationWithDuration:info.leftTime danmakuInfo:info];
}

- (void)performAnimationWithDuration:(NSTimeInterval)duration danmakuInfo:(JTDanmakuInfo*)info {
    _isPlaying  = YES;
    _isPauseing = NO;

    UILabel* label    = info.playLabel;
    CGRect   endFrame = CGRectMake(-Width(label), Y(label), Width(label), Height(label));

    [UIView animateWithDuration:duration
        delay:0
        options:UIViewAnimationOptionCurveLinear
        animations:^{
            label.frame = endFrame;
        }
        completion:^(BOOL finished) {
            if (finished) {
                [label removeFromSuperview];
                [self.subDanmakuInfos removeObject:info];
            }
        }];
}

// 检测碰撞 -- 默认从右到左
- (BOOL)judgeIsRunintoWithFirstDanmakuInfo:(JTDanmakuInfo*)info behindLabel:(UILabel*)last {
    UILabel* firstLabel = info.playLabel;
    CGFloat  firstSpeed = [self getSpeedFromLabel:firstLabel];
    CGFloat  lastSpeed  = [self getSpeedFromLabel:last];

    //    CGRect firstFrame = info.labelFrame;
    CGFloat firstFrameRight = info.leftTime * firstSpeed;

    if (info.leftTime <= 1) return NO;

    if (Left(last) - firstFrameRight > 10) {
        if (lastSpeed <= firstSpeed) {
            return NO;
        } else {
            CGFloat lastEndLeft = Left(last) - lastSpeed * info.leftTime;
            if (lastEndLeft > 10) {
                return NO;
            }
        }
    }

    return YES;
}

// 计算速度
- (CGFloat)getSpeedFromLabel:(UILabel*)label {
    return (self.bounds.size.width + label.bounds.size.width) / self.duration;
}

#pragma mark - 公共方法

- (BOOL)isPrepared {
    NSAssert(self.duration && self.maxShowLineCount && self.lineHeight, superPlayerLocalized(@"SuperPlayer.mustsettingbarrage"));
    if (self.danmakus.count && self.lineHeight && self.duration && self.maxShowLineCount) {
        return YES;
    }
    return NO;
}

- (void)start {
    if (_isPauseing) [self resume];

    if ([self isPrepared]) {
        if (!_timer) {
            _timer = [NSTimer timerWithTimeInterval:timeMargin target:self selector:@selector(getCurrentTime) userInfo:nil repeats:YES];
            [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
            [_timer fire];
        }
    }
}
- (void)pause {
    if (!_timer || !_timer.isValid) return;

    _isPauseing = YES;
    _isPlaying  = NO;

    [_timer invalidate];
    _timer = nil;

    for (UILabel* label in self.subviews) {
        CALayer* layer = label.layer;
        CGRect   rect  = label.frame;
        if (layer.presentationLayer) {
            rect = ((CALayer*)layer.presentationLayer).frame;
        }
        label.frame = rect;
        [label.layer removeAllAnimations];
    }
}
- (void)resume {
    if (![self isPrepared] || _isPlaying || !_isPauseing) return;
    for (JTDanmakuInfo* info in self.subDanmakuInfos) {
        if (info.danmaku.position == JTDanmakuPositionNone) {
            [self performAnimationWithDuration:info.leftTime danmakuInfo:info];
        } else {
            _isPauseing = NO;
            [self performCenterAnimationWithDuration:info.leftTime danmakuInfo:info];
        }
    }

    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(timeMargin * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self start];
    });
}
- (void)stop {
    _isPauseing = NO;
    _isPlaying  = NO;

    [_timer invalidate];
    _timer = nil;
    [self.danmakus removeAllObjects];
    self.linesDict = nil;
}

- (void)clear {
    [_timer invalidate];
    _timer         = nil;
    self.linesDict = nil;
    _isPauseing    = YES;
    _isPlaying     = NO;
    [self.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
}

- (void)sendDanmakuSource:(JTDanmaku*)danmaku {
    [self playDanmaku:danmaku];
}

@end
