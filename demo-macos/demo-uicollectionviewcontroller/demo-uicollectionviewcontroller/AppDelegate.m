//
//  AppDelegate.m
//  demo-uicollectionviewcontroller
//
//  Created by dexterleslie on 2025/9/5.
//

#import "AppDelegate.h"
#import "TestUICollectionViewController.h"

@interface AppDelegate ()

@property (strong, nonatomic) TestUICollectionViewController *collectionViewController;

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    // 初始化 UIWindow
    self.window = [[UIWindow alloc]initWithFrame:[UIScreen mainScreen].bounds];
    // 设置 UIWindow 背景颜色
    self.window.backgroundColor = [UIColor whiteColor];
    
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
    self.collectionViewController = collectionVC;
    NSMutableArray<NSString *> *itemList = [NSMutableArray array];
    for(int i=0;i<20;i++) {
        [itemList addObject:[NSString stringWithFormat:@"%d", i]];
    }
    collectionVC.itemList = itemList;
    
    // 修改数据源按钮
    UIViewController *viewController = [[UIViewController alloc] init];
    UIButton *buttonRefresh = [UIButton buttonWithType:UIButtonTypeSystem];
    [buttonRefresh setTitle:@"动态设置CollectionViewController数据源" forState:UIControlStateNormal];
    [buttonRefresh addTarget:self action:@selector(onClicked:) forControlEvents:UIControlEventTouchDown];
    [viewController.view addSubview:buttonRefresh];
    buttonRefresh.translatesAutoresizingMaskIntoConstraints = NO;
    [NSLayoutConstraint activateConstraints:@[
        [buttonRefresh.centerXAnchor constraintEqualToAnchor:viewController.view.safeAreaLayoutGuide.centerXAnchor],
        [buttonRefresh.topAnchor constraintEqualToAnchor:viewController.view.safeAreaLayoutGuide.topAnchor]
    ]];
    
    [viewController addChildViewController:collectionVC];
    [viewController.view addSubview:collectionVC.view];
    collectionVC.view.translatesAutoresizingMaskIntoConstraints = NO;
    [NSLayoutConstraint activateConstraints:@[
        [collectionVC.view.leadingAnchor constraintEqualToAnchor:viewController.view.safeAreaLayoutGuide.leadingAnchor],
        [collectionVC.view.trailingAnchor constraintEqualToAnchor:viewController.view.safeAreaLayoutGuide.trailingAnchor],
        [collectionVC.view.bottomAnchor constraintEqualToAnchor:viewController.view.safeAreaLayoutGuide.bottomAnchor],
        [collectionVC.view.topAnchor constraintEqualToAnchor:buttonRefresh.bottomAnchor constant:10]
    ]];
    [collectionVC didMoveToParentViewController:viewController];
    
    self.window.rootViewController = viewController;
    
    // 将 UIWindow 设置成为应用程序的主窗口（Key Window）
    // 将 UIWindow 显示在屏幕上
    [self.window makeKeyAndVisible];
    
    return YES;
}

// 动态修改 CollectionViewController 数据源
- (void)onClicked:(id) sender {
    NSMutableArray<NSString *> *itemList = [NSMutableArray array];
    for(int i=0;i<5;i++) {
        [itemList addObject:[NSString stringWithFormat:@"%d", i]];
    }
    self.collectionViewController.itemList = itemList;
}

@end
