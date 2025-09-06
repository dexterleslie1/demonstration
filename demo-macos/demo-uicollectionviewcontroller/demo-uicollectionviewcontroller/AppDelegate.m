//
//  AppDelegate.m
//  demo-uicollectionviewcontroller
//
//  Created by dexterleslie on 2025/9/5.
//

#import "AppDelegate.h"
#import "TestUICollectionViewController.h"

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
    // 创建流式布局（网格）
    UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
    // Item 的宽和高自动计算尺寸
    // layout.estimatedItemSize = UICollectionViewFlowLayoutAutomaticSize;
    // 行间距
    layout.minimumLineSpacing = 10;
    // 列间距
    layout.minimumInteritemSpacing = 10;
    // 控制整个 section 的边距（上、左、下、右），左/右间距为 10
    layout.sectionInset = UIEdgeInsetsMake(0, 10, 0, 10);
    
    // 强制两列布局
    CGFloat totalWidth = [UIScreen mainScreen].bounds.size.width;
    CGFloat spacing = layout.sectionInset.left + layout.sectionInset.right + layout.minimumInteritemSpacing;
    // 固定高度 150
    layout.itemSize = CGSizeMake((totalWidth - spacing) / 2, 150);

    // 初始化控制器
    TestUICollectionViewController *collectionVC = [[TestUICollectionViewController alloc] initWithCollectionViewLayout:layout];
    self.window.rootViewController = collectionVC;
    
    // 将 UIWindow 设置成为应用程序的主窗口（Key Window）
    // 将 UIWindow 显示在屏幕上
    [self.window makeKeyAndVisible];
    
    return YES;
}

@end
