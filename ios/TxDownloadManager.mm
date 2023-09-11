#import "TxDownloadManager.h"
#import <React/RCTBridge+Private.h>
#import <React/RCTUtils.h>
#import <jsi/jsi.h>
#import <sys/utsname.h>
#import "YeetJSIUtils.h"
#import "TXVodDownloadManager.h"

using namespace facebook::jsi;


static void install(Runtime &jsiRuntime, TxDownloadManager *manager);

@interface TxDownloadManager () <TXVodDownloadDelegate>
@end

@implementation TxDownloadManager

@synthesize bridge = _bridge;
@synthesize methodQueue = _methodQueue;

RCT_EXPORT_MODULE(TxDownloadManager)

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
    dataSource.appId = 1304755944;
    dataSource.fileId = dict[@"fileId"];
    dataSource.pSign = dict[@"pSign"];
    [[self getDownloadMgr] startDownload:dataSource];
}
- (void)stopDownload:(NSString *)videoFileId {
    NSLog(@"TxPlayer: stopDownload %@", videoFileId);
    id mediaInfo =[[self getDownloadMgr] getDownloadMediaInfo:0 fileId:videoFileId qualityId:0 userName:@""];
    [[self getDownloadMgr] stopDownload:mediaInfo];
}
- (void)deleteDownload:(NSString *)videoFileId {
    NSLog(@"TxPlayer: deleteDownload %@", videoFileId);
    id mediaInfo =[[self getDownloadMgr] getDownloadMediaInfo:0 fileId:videoFileId qualityId:0 userName:@""];
    [[self getDownloadMgr] deleteDownloadMediaInfo:mediaInfo];
}
- (NSString *)getDownloadList {
    NSArray<TXVodDownloadMediaInfo *> * array = [[self getDownloadMgr] getDownloadMediaInfoList];
    if (array == nil) array = @[];
    NSError* error;
    NSData *str = [NSJSONSerialization dataWithJSONObject:array options:kNilOptions error:&error];
    return [[NSString alloc]initWithData:str encoding:NSUTF8StringEncoding];
}

- (void)onDownloadError:(TXVodDownloadMediaInfo *)mediaInfo errorCode:(TXDownloadError)code errorMsg:(NSString *)msg {
    NSLog(@"TxPlayer: onDownloadError %@", mediaInfo.dataSource.fileId);
}

- (void)onDownloadFinish:(TXVodDownloadMediaInfo *)mediaInfo {
    NSLog(@"TxPlayer: onDownloadFinish %@", mediaInfo.dataSource.fileId);
}

- (void)onDownloadProgress:(TXVodDownloadMediaInfo *)mediaInfo {
    NSLog(@"TxPlayer: onDownloadProgress %@ %@", mediaInfo.dataSource.fileId, @(mediaInfo.progress));
}

- (void)onDownloadStart:(TXVodDownloadMediaInfo *)mediaInfo {
    NSLog(@"TxPlayer: onDownloadStart %@", mediaInfo.dataSource.fileId);
}

- (void)onDownloadStop:(TXVodDownloadMediaInfo *)mediaInfo {
    NSLog(@"TxPlayer: onDownloadStop %@", mediaInfo.dataSource.fileId);
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
    
    jsiRuntime.global().setProperty(jsiRuntime, "getDownloadList", std::move(getDownloadList));
    
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
    
    jsiRuntime.global().setProperty(jsiRuntime, "startDownload", std::move(startDownload));
    
    
    auto stopDownload = Function::createFromHostFunction(jsiRuntime,
                                                         PropNameID::forAscii(jsiRuntime,
                                                                              "stopDownload"),
                                                         1,
                                                         [manager](Runtime &runtime,
                                                                   const Value &thisValue,
                                                                   const Value *arguments,
                                                                   size_t count) -> Value {
        NSString *fileId = convertJSIStringToNSString(runtime, arguments[0].getString(runtime));
        
        [manager stopDownload: fileId];
        
        return Value(true);
    });
    
    jsiRuntime.global().setProperty(jsiRuntime, "stopDownload", std::move(stopDownload));
    
    auto deleteDownload = Function::createFromHostFunction(jsiRuntime,
                                                           PropNameID::forAscii(jsiRuntime,
                                                                                "deleteDownload"),
                                                           1,
                                                           [manager](Runtime &runtime,
                                                                     const Value &thisValue,
                                                                     const Value *arguments,
                                                                     size_t count) -> Value {
        
        NSString *fileId = convertJSIStringToNSString(runtime, arguments[0].getString(runtime));
        
        [manager stopDownload: fileId];
        
        return Value(true);
    });
    
    jsiRuntime.global().setProperty(jsiRuntime, "deleteDownload", std::move(deleteDownload));
}
