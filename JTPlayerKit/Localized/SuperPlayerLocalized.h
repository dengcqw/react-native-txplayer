//
//  SuperPlayerLocalized.h
//  Pods
//
//  Copyright © 2022年 Tencent. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

extern NSString *superPlayerLocalizeFromTable(NSString *key, NSString *table);

extern NSString *const SuperPlayer_Localize_TableName;
extern NSString *      superPlayerLocalized(NSString *key);

// sandstalk
extern NSString * kSuperPlayerAppleLanguage;
extern void superPlayerUpdateLanguage(NSString *lang);

NS_ASSUME_NONNULL_END
