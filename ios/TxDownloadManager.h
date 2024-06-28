
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <rntxplaper/rntxplaper.h>
#endif

@interface TxDownloadManager : RCTEventEmitter <
#ifdef RCT_NEW_ARCH_ENABLED
                                NativeTxDownloadManagerModuleSpec
#else
                                RCTBridgeModule
#endif
                                >

@end

