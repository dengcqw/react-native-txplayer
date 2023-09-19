#import "TxDownloadManager.h"
#import <React/RCTBridge+Private.h>
#import <React/RCTUtils.h>
#import <jsi/jsi.h>
#import <sys/utsname.h>
#import "YeetJSIUtils.h"
#import "TXVodDownloadManager.h"

using namespace facebook::jsi;

static NSString * TXDownloadEvent = @"TxDownloadEvent";

NSDictionary *getDownloanInfo(TXVodDownloadMediaInfo *mediaInfo) {
    return @{
        @"appId": @(mediaInfo.dataSource.appId),
        @"fileId": mediaInfo.dataSource.fileId,
        @"sign":mediaInfo.dataSource.pSign,
        @"duration":@(mediaInfo.duration),
        @"size":@(mediaInfo.size),
        @"downloadSize":@(mediaInfo.downloadSize),
        @"progress":@(mediaInfo.progress),
        @"playPath":mediaInfo.playPath,
        @"speed":@(mediaInfo.speed),
        @"downloadState":@(mediaInfo.downloadState),
        @"isResourceBroken":@(mediaInfo.isResourceBroken),
    };
}

NSArray<NSDictionary *> *getDownloanInfos(NSArray<TXVodDownloadMediaInfo *> *mediaInfos) {
    NSMutableArray *arr = [NSMutableArray new];
    for (TXVodDownloadMediaInfo *info in mediaInfos) {
        [arr addObject:getDownloanInfo(info)];
    }
    return arr;
}

static void install(Runtime &jsiRuntime, TxDownloadManager *manager);

@interface TxDownloadManager () <TXVodDownloadDelegate>
@property(assign, nonatomic) Boolean hasListeners;
@end

@implementation TxDownloadManager

@synthesize bridge = _bridge;
@synthesize methodQueue = _methodQueue;

RCT_EXPORT_MODULE(TxDownloadManager)

- (NSArray<NSString *> *)supportedEvents
{
  return @[TXDownloadEvent];
}

// 在添加第一个监听函数时触发
- (void)startObserving {
    self.hasListeners = YES;
}

- (void)stopObserving {
    self.hasListeners = NO;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

// Installing JSI Bindings as done by
// https://github.com/mrousavy/react-native-mmkv
RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(install)
{
    RCTBridge* bridge = [RCTBridge currentBridge];
    RCTCxxBridge* cxxBridge = (RCTCxxBridge*)bridge;
    if (cxxBridge == nil) {
        return @false;
    }
    
    auto jsiRuntime = (jsi::Runtime*) cxxBridge.runtime;
    if (jsiRuntime == nil) {
        return @false;
    }
    
    install(*(facebook::jsi::Runtime *)jsiRuntime, self);
    
    return @true;
}

- (TXVodDownloadManager *)getDownloadMgr {
    TXVodDownloadManager * mgr = [TXVodDownloadManager shareInstance];
    if (mgr.delegate != self) {
        mgr.delegate = self;
    }
    return mgr;
}
    
- (void)startDownload:(NSString *)videoInfo {
    NSError* error;
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:[videoInfo dataUsingEncoding:(NSUTF8StringEncoding)] options:(kNilOptions) error:&error];
    NSLog(@"TxPlayer: startDownload");
    
    TXVodDownloadDataSource *dataSource = [[TXVodDownloadDataSource alloc] init];
    dataSource.appId = [dict[@"appId"] integerValue];
    dataSource.fileId = dict[@"fileId"];
    dataSource.pSign = dict[@"sign"];
    dataSource.quality = TXVodQualityHD;
    dataSource.userName = @"default";
    [[self getDownloadMgr] startDownload:dataSource];

}
- (void)stopDownload:(NSString *)videoFileId appId:(NSString *)appId {
    NSLog(@"TxPlayer: stopDownload %@ %@", appId, videoFileId);
    id mediaInfo =[[self getDownloadMgr] getDownloadMediaInfo:[appId integerValue] fileId:videoFileId qualityId:TXVodQualityHD userName:@"default"];
    [[self getDownloadMgr] stopDownload:mediaInfo];
}
- (void)deleteDownload:(NSString *)videoFileId appId:(NSString *)appId {
    NSLog(@"TxPlayer: deleteDownload %@ %@", appId, videoFileId);
    id mediaInfo =[[self getDownloadMgr] getDownloadMediaInfo:[appId integerValue] fileId:videoFileId qualityId:TXVodQualityHD userName:@"default"];
    [[self getDownloadMgr] deleteDownloadMediaInfo:mediaInfo];
}
- (NSString *)getDownloadList {
    NSArray<TXVodDownloadMediaInfo *> * array = [[self getDownloadMgr] getDownloadMediaInfoList];
    
    if (array == nil) array = @[];
    NSError* error;
    NSData *str = [NSJSONSerialization dataWithJSONObject:getDownloanInfos(array) options:NSJSONWritingFragmentsAllowed error:&error];
    NSLog(@"TxPlayer: getDownloadList %@", [error localizedDescription]);
    return [[NSString alloc] initWithData:str encoding:NSUTF8StringEncoding];
}

- (void)onDownloadError:(TXVodDownloadMediaInfo *)mediaInfo errorCode:(TXDownloadError)code errorMsg:(NSString *)msg {
    NSLog(@"TxPlayer: onDownloadError %@ %@", mediaInfo.dataSource.fileId, msg);
    if (!self.hasListeners) return;
    [self sendEventWithName:TXDownloadEvent body:@{
        @"name": @"error",
        @"fileId": mediaInfo.dataSource.fileId,
    }];
}

- (void)onDownloadFinish:(TXVodDownloadMediaInfo *)mediaInfo {
    NSLog(@"TxPlayer: onDownloadFinish %@", mediaInfo.dataSource.fileId);
    if (!self.hasListeners) return;
    [self sendEventWithName:TXDownloadEvent body:@{
        @"name": @"finish",
        @"fileId": mediaInfo.dataSource.fileId
    }];
}

- (void)onDownloadProgress:(TXVodDownloadMediaInfo *)mediaInfo {
    NSLog(@"TxPlayer: onDownloadProgress %@ %@", mediaInfo.dataSource.fileId, @(mediaInfo.progress));
    if (!self.hasListeners) return;
    [self sendEventWithName:TXDownloadEvent body:@{
        @"name": @"progress",
        @"fileId": mediaInfo.dataSource.fileId,
        @"progress": @(mediaInfo.progress),
        @"downloaded": @(mediaInfo.downloadSize)
    }];
}

- (void)onDownloadStart:(TXVodDownloadMediaInfo *)mediaInfo {
    NSLog(@"TxPlayer: onDownloadStart %@", mediaInfo.dataSource.fileId);
    if (!self.hasListeners) return;
    [self sendEventWithName:TXDownloadEvent body:@{
        @"name": @"start",
        @"fileId": mediaInfo.dataSource.fileId
    }];
}

- (void)onDownloadStop:(TXVodDownloadMediaInfo *)mediaInfo {
    NSLog(@"TxPlayer: onDownloadStop %@", mediaInfo.dataSource.fileId);
    if (!self.hasListeners) return;
    [self sendEventWithName:TXDownloadEvent body:@{
        @"name": @"stop",
        @"fileId": mediaInfo.dataSource.fileId
    }];
}

- (int)hlsKeyVerify:(TXVodDownloadMediaInfo *)mediaInfo url:(NSString *)url data:(NSData *)data {
    return 0;
}


@end

static void install(jsi::Runtime &jsiRuntime, TxDownloadManager *manager) {
    auto getDownloadList = Function::createFromHostFunction(jsiRuntime,
                                                            PropNameID::forAscii(jsiRuntime,
                                                                                 "getDownloadList"),
                                                            0,
                                                            [manager](Runtime &runtime,
                                                                      const Value &thisValue,
                                                                      const Value *arguments,
                                                                      size_t count) -> Value {
        
        jsi::String downloadList = convertNSStringToJSIString(runtime, [manager getDownloadList]);
        
        return Value(runtime, downloadList);
    });
    
    jsiRuntime.global().setProperty(jsiRuntime, "TXD_getDownloadList", std::move(getDownloadList));
    
    auto startDownload = Function::createFromHostFunction(jsiRuntime,
                                                          PropNameID::forAscii(jsiRuntime,
                                                                               "startDownload"),
                                                          1,
                                                          [manager](Runtime &runtime,
                                                                    const Value &thisValue,
                                                                    const Value *arguments,
                                                                    size_t count) -> Value {
        
        NSString *videoInfo = convertJSIStringToNSString(runtime, arguments[0].getString(runtime));
        
        [manager startDownload:videoInfo];
        
        return Value(true);
    });
    
    jsiRuntime.global().setProperty(jsiRuntime, "TXD_startDownload", std::move(startDownload));
    
    
    auto stopDownload = Function::createFromHostFunction(jsiRuntime,
                                                         PropNameID::forAscii(jsiRuntime,
                                                                              "stopDownload"),
                                                         1,
                                                         [manager](Runtime &runtime,
                                                                   const Value &thisValue,
                                                                   const Value *arguments,
                                                                   size_t count) -> Value {
        NSString *fileId = convertJSIStringToNSString(runtime, arguments[0].getString(runtime));
        NSString *appId = convertJSIStringToNSString(runtime, arguments[1].getString(runtime));
        
        [manager stopDownload: fileId appId:appId];
        
        return Value(true);
    });
    
    jsiRuntime.global().setProperty(jsiRuntime, "TXD_stopDownload", std::move(stopDownload));
    
    auto deleteDownload = Function::createFromHostFunction(jsiRuntime,
                                                           PropNameID::forAscii(jsiRuntime,
                                                                                "deleteDownload"),
                                                           1,
                                                           [manager](Runtime &runtime,
                                                                     const Value &thisValue,
                                                                     const Value *arguments,
                                                                     size_t count) -> Value {
        
        NSString *fileId = convertJSIStringToNSString(runtime, arguments[0].getString(runtime));
        NSString *appId = convertJSIStringToNSString(runtime, arguments[1].getString(runtime));
        
        [manager stopDownload: fileId appId:appId];
        
        return Value(true);
    });
    
    jsiRuntime.global().setProperty(jsiRuntime, "TXD_deleteDownload", std::move(deleteDownload));
}
