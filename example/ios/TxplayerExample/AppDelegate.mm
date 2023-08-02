#import "AppDelegate.h"

#import <React/RCTBundleURLProvider.h>
#import <react-native-orientation-locker/Orientation.h>
#import <TXLiteAVSDK_Professional/TXLiveBase.h>
#import "FeedPlayViewController.h"
#import <react-native-config/ReactNativeConfig.h>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  self.moduleName = @"TxplayerExample";
  // You can add your custom initial props in the dictionary below.
  // They will be passed down to the ViewController used by React Native.
  self.initialProps = @{};
  
  [TXLiveBase setLicenceURL:[ReactNativeConfig envFor:@"VodLicenseURL"] key:[ReactNativeConfig envFor:@"VodLicenseKey"]];
  
//  self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
//  self.window.backgroundColor = [UIColor whiteColor];
//  UINavigationController *navi = [[UINavigationController alloc]initWithRootViewController: [FeedPlayViewController new]];
//  self.window.rootViewController  = navi;
//  [self.window makeKeyAndVisible];
//  return YES;
  
  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
#if DEBUG
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index"];
#else
  return [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];
#endif
}

- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
  return [Orientation getOrientation];
}

@end
