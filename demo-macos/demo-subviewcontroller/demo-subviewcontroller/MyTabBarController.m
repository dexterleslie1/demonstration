//
//  MyTabBarController.m
//  demo-subviewcontroller
//
//  Created by dexterleslie on 2025/9/23.
//

#import "MyTabBarController.h"

@interface MyTabBarController ()

@end

@implementation MyTabBarController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    // 首页
    UIViewController *viewControllerHome = [[UIViewController alloc] init];
    viewControllerHome.view.backgroundColor = [UIColor whiteColor];
    viewControllerHome.title = @"首页";
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:viewControllerHome];
    navigationController.tabBarItem.title = @"首页";
    [self addChildViewController:navigationController];
    
    // 我的
    UIViewController *viewControllerMe = [[UIViewController alloc] init];
    viewControllerMe.view.backgroundColor = [UIColor whiteColor];
    viewControllerMe.title = @"我";
    navigationController = [[UINavigationController alloc] initWithRootViewController:viewControllerMe];
    navigationController.tabBarItem.title = @"我";
    [self addChildViewController:navigationController];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
