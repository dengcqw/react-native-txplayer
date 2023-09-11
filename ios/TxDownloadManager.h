#ifdef RCT_NEW_ARCH_ENABLED
#import "RNJtjsiSpec.h"


@interface TxDownloadManager : NSObject <NativeJtjsiSpec>
#else
#import <React/RCTBridgeModule.h>

@interface TxDownloadManager : NSObject <RCTBridgeModule>
#endif

- (void)startDownload:(NSString *)videoInfo;
- (void)stopDownload:(NSString *)videoFileId;
- (void)deleteDownload:(NSString *)videoFileId;
- (NSString *)getDownloadList;
@end

