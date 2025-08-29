//
//  AppDelegate.m
//  demo-xib
//
//  Created by dexterleslie on 2025/8/29.
//

#import "AppDelegate.h"
#import "XibViewController.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    self.window = [[UIWindow alloc]initWithFrame:[UIScreen mainScreen].bounds];
    self.window.backgroundColor = [UIColor whiteColor];
    UIViewController *viewController = [[XibViewController alloc] initWithNibName:@"XibViewController"
                                                                           bundle:[NSBundle mainBundle]];
    self.window.rootViewController = viewController;
    [self.window makeKeyAndVisible];
    
    return YES;
}


@end
