//
// HYBAppDelegate.m
// [y] hybris Platform
//
// Copyright (c) 2000-2015 hybris AG
// All rights reserved.
//
// This software is the confidential and proprietary information of hybris
// ("Confidential Information"). You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the
// license agreement you entered into with hybris.
//

#import <hybrisMobileCommerceLibrary/NSObject+HYBAdditionalMethods.h>
#import "HYBAppDelegate.h"
#import "HYBLoginViewController.h"
#import "HYBMasterNavigationController.h"
#import "UIViewController+Navigation.h"

#import "DDTTYLogger.h"
#import "DDASLLogger.h"
#import "HYB2BService.h"
#import "HYBBackEndFacade.h"

#ifdef CALABASH
#import "CalabashBackdoor.h"
#endif

#import "MYEnvironmentConfig.h"
#import "MYEnvironmentConfig+HYBAdditionalMethods.h"

#import <HockeySDK/HockeySDK.h>
#define HOCKEYSDK_KEY   @"HOCKEYSDK_KEY"

@implementation HYBAppDelegate {
    UIImageView *snapshotSecurityBlocker;
}

#ifdef CALABASH
//calabash backdoor methods
- (NSString *)backdoorLogin:(NSString *)paramsAsString {
    NSString *result = [[CalabashBackdoor sharedInstance] backdoorLogin:paramsAsString];
    DDLogDebug(@"backdoorLogin result:%@", result);
    return result;
}

- (NSString *)backdoorNavigateTo:(NSString *)paramsAsString {
    NSString *result = [[CalabashBackdoor sharedInstance] backdoorNavigateTo:paramsAsString];
    DDLogDebug(@"backdoorNavigateTo result:%@", result);
    return result;
}

- (NSString *)backdoorGoProductdetail:(NSString *)paramsAsString {
    NSString *result = [[CalabashBackdoor sharedInstance] backdoorGoProductdetail:paramsAsString];
    DDLogDebug(@"backdoorGoProductdetail result:%@", result);
    return result;
}
#endif

//app life cycle

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    //initialize Logging
    [DDLog addLogger:[DDASLLogger sharedInstance]];
    [DDLog addLogger:[DDTTYLogger sharedInstance]];
    
    //initi env config
    [MYEnvironmentConfig initSharedConfigWithPList:@"Environments.plist"];
    DDLogInfo(@"Configured Back End: %@", [[[MYEnvironmentConfig sharedConfig] configValues] objectForKey:HOST_ATTRIBUTE_KEY]);
    DDLogDebug(@"Configured buildUserId: %@", [[MYEnvironmentConfig sharedConfig] buildUserId]);
    
    //initialize hockeyapp
    NSString *hockeySdk = [[[MYEnvironmentConfig sharedConfig] configValues] objectForKey:HOCKEYSDK_KEY];
    
    if (hockeySdk && hockeySdk.length > 0) {
        [[BITHockeyManager sharedHockeyManager] configureWithIdentifier:hockeySdk];
        [[BITHockeyManager sharedHockeyManager] startManager];
        [[BITHockeyManager sharedHockeyManager].authenticator authenticateInstallation];
    }
    
    //set b2b backend
    _backEndService = [[HYB2BService alloc] initWithDefaults];
    
    //prepare login screen
    HYBLoginViewController *login = [[HYBLoginViewController alloc] initWithBackEndService:_backEndService];

    //setup main window
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];

    //main navigation controller
    self.masterNavigationController = [[HYBMasterNavigationController alloc] initWithRootViewController:login];
    self.masterNavigationController.navigationBarHidden = YES;

    self.window.rootViewController = self.masterNavigationController;
    [self.window makeKeyAndVisible];

    return YES;
}

- (void)passLoginWindow {
    [self.window.rootViewController setupDrawers];
}

- (void)applicationWillResignActive:(UIApplication *)application {
    //good for her
}

- (UIImageView *)createLogoView {
    UIImageView *imageHolder = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Logo.png"]];
    imageHolder.contentMode = UIViewContentModeScaleAspectFit;
    return imageHolder;
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    snapshotSecurityBlocker = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Background.png"]];
    snapshotSecurityBlocker.frame = self.window.frame;
    snapshotSecurityBlocker.contentMode = UIViewContentModeCenter;

    UIView *logo = [self createLogoView];
    logo.center = snapshotSecurityBlocker.center;
    [snapshotSecurityBlocker addSubview:logo];

    [self.window addSubview:snapshotSecurityBlocker];
}

- (void)applicationDidBecomeActive:(UIApplication *)application{
    if(snapshotSecurityBlocker){
        [snapshotSecurityBlocker removeFromSuperview];
        snapshotSecurityBlocker = nil;
    }
}

@end
