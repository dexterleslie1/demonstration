//
//  AppDelegate.m
//  demo-uitabbarcontroller
//
//  Created by dexterleslie on 2025/8/30.
//

#import "AppDelegate.h"
#import "MainTabBarViewController.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    // 初始化 UIWindow
    self.window = [[UIWindow alloc]initWithFrame:[UIScreen mainScreen].bounds];
    // 设置 UIWindow 背景颜色
    self.window.backgroundColor = [UIColor grayColor];
    MainTabBarViewController *viewController = [[MainTabBarViewController alloc] init];
    self.window.rootViewController = viewController;
    [self.window makeKeyAndVisible];
    
    return YES;
}


@end
