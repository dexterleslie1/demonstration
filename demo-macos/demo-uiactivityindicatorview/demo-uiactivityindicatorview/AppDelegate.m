//
//  AppDelegate.m
//  demo-uiactivityindicatorview
//
//  Created by dexterleslie on 2025/9/7.
//

#import "AppDelegate.h"
#import "ActivityIndicatorViewBasicUsageViewController.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    // 初始化 UIWindow
    self.window = [[UIWindow alloc]initWithFrame:[UIScreen mainScreen].bounds];
    // 设置 UIWindow 背景颜色
    self.window.backgroundColor = [UIColor grayColor];
  
    // 设置 UIWindow 的根视图控制器
    UIViewController *viewController = [[ActivityIndicatorViewBasicUsageViewController alloc] init];
    self.window.rootViewController = viewController;
  
    // 将 UIWindow 设置成为应用程序的主窗口（Key Window）
    // 将 UIWindow 显示在屏幕上
    [self.window makeKeyAndVisible];

    return YES;
}

@end
