//
//  SuperPlayerLocalized.m
//  Pods
//
//  Copyright © 2022年 Tencent. All rights reserved.
//

#import "SuperPlayerLocalized.h"

NSBundle * superPlayerBundle;

void superPlayerUpdateLanguage(NSString *lang) {
    NSString *resourceDict = [[NSBundle mainBundle] pathForResource:@"SuperPlayerKitBundle" ofType:@"bundle"];
    if (resourceDict.length == 0) { // 资源Bundle文件目录
        return;
    }
    
    NSString *bundleOfPath = [NSBundle pathForResource:lang ofType:@"lproj" inDirectory:resourceDict];
    NSBundle *bundle = [NSBundle bundleWithPath:bundleOfPath];
    if (!bundle) { // 语言lproj的判断
        bundleOfPath = [NSBundle pathForResource:@"en" ofType:@"lproj" inDirectory:resourceDict];
        bundle = [NSBundle bundleWithPath:bundleOfPath];
    }
    superPlayerBundle = bundle;
}

NSString *superPlayerLocalizeFromTable(NSString *key, NSString *table) {
    if (superPlayerBundle == nil) {
        superPlayerUpdateLanguage(@"zh-cn");
    }
    return [superPlayerBundle localizedStringForKey:key value:@"" table:table];
}

NSString *const SuperPlayer_Localize_TableName = @"SuperPlayerLocalized";
NSString *      superPlayerLocalized(NSString *key) {
    return superPlayerLocalizeFromTable(key, SuperPlayer_Localize_TableName);
}
