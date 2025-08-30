//
//  AppDelegate.m
//  demo-uiviewcontroller
//
//  Created by dexterleslie on 2025/8/30.
//

#import "AppDelegate.h"
#import "RootViewController.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    // 初始化 UIWindow
    self.window = [[UIWindow alloc]initWithFrame:[UIScreen mainScreen].bounds];
    // 设置 UIWindow 背景颜色
    self.window.backgroundColor = [UIColor grayColor];
    RootViewController *rootViewController = [[RootViewController alloc] init];
    self.window.rootViewController = rootViewController;
    [self.window makeKeyAndVisible];
    
    return YES;
}


@end
