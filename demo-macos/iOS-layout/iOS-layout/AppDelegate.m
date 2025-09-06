//
//  AppDelegate.m
//  iOS-layout
//
//  Created by MacOS on 2020/11/28.
//  Copyright © 2020 YingShun International Network Co., Limited. All rights reserved.
//

#import "AppDelegate.h"
#import "AutolayoutXibViewController.h"
#import "MasonryViewController.h"
#import "StackLayoutViewController.h"
#import "StackLayoutRatioViewController.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    self.window = [[UIWindow alloc]initWithFrame:[UIScreen mainScreen].bounds];
    self.window.backgroundColor = [UIColor whiteColor];
    
//    UIViewController *viewController = [[AutolayoutXibViewController alloc]
//                                        initWithNibName:@"AutolayoutXibViewController"
//                                        bundle:[NSBundle mainBundle]];
//    UIViewController *viewController = [[MasonryViewController alloc] init];
//    UIViewController *viewController = [[StackLayoutViewController alloc] init];
    UIViewController *viewController = [[StackLayoutRatioViewController alloc] init];
    
    self.window.rootViewController = viewController;
    [self.window makeKeyAndVisible];
    
    return YES;
}

@end
