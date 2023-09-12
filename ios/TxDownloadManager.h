#ifdef RCT_NEW_ARCH_ENABLED
#import "RNJtjsiSpec.h"


@interface TxDownloadManager : RCTEventEmitter <NativeJtjsiSpec>
#else
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface TxDownloadManager : RCTEventEmitter <RCTBridgeModule>
#endif

- (void)startDownload:(NSString *)videoInfo;
- (void)stopDownload:(NSString *)videoFileId;
- (void)deleteDownload:(NSString *)videoFileId;
- (NSString *)getDownloadList;
@end

