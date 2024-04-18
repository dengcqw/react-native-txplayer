//
//  SuperPlayerSubtitlesView.h
//  SuperPlayer-Player
//
//  Created by 路鹏 on 2022/10/11.
//  Copyright © 2022 Tencent. All rights reserved.

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class TXTrackInfo;
@protocol SuperPlayerSubtitlesViewDelegate <NSObject>
    
- (void)chooseSubtitlesInfo:(TXTrackInfo *)info preSubtitlesInfo:(TXTrackInfo *)preInfo;

- (void)onSettingViewDoneClickWithDic:(NSMutableDictionary *)dic;

@end

@interface SuperPlayerSubtitlesView : UIView

@property (nonatomic, weak) id<SuperPlayerSubtitlesViewDelegate> delegate;

- (void)initSubtitlesViewWithTrackArray:(NSMutableArray<TXTrackInfo *> *)subtitlesArray
                  currentSubtitlesIndex:(NSInteger)currentSubtitlesIndex;

@end

NS_ASSUME_NONNULL_END
