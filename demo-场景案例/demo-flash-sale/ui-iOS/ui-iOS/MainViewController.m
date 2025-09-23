//
//  MainViewController.m
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/22.
//

#import "MainViewController.h"
#import "MeViewController.h"
#import "HomeViewController.h"

@interface MainViewController ()

@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // 首页
    UIViewController *viewControllerHome = [[HomeViewController alloc] init];
    viewControllerHome.view.backgroundColor = [UIColor whiteColor];
    viewControllerHome.tabBarItem.title = @"首页";
    [self addChildViewController:viewControllerHome];
    
    // 我的
    UIViewController *viewControllerMe = [[MeViewController alloc] init];
    viewControllerMe.view.backgroundColor = [UIColor whiteColor];
    viewControllerMe.tabBarItem.title = @"我";
    [self addChildViewController:viewControllerMe];
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
