//
//  XibProgramaticallySubViewController.m
//  demo-subviewcontroller
//
//  Created by dexterleslie on 2025/9/24.
//

#import "XibProgramaticallySubViewController.h"
#import "MyTabBarController.h"

@interface XibProgramaticallySubViewController ()

@end

@implementation XibProgramaticallySubViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // 编程式添加 UITabBarController 子控制器
    MyTabBarController *viewController = [[MyTabBarController alloc] init];
    [self addChildViewController:viewController];
    [self.view addSubview:viewController.view];
    viewController.view.translatesAutoresizingMaskIntoConstraints = NO;
    [viewController.view.topAnchor constraintEqualToAnchor:self.view.topAnchor].active = YES;
    [viewController.view.leadingAnchor constraintEqualToAnchor:self.view.leadingAnchor].active = YES;
    [viewController.view.trailingAnchor constraintEqualToAnchor:self.view.trailingAnchor].active = YES;
    [viewController.view.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor].active = YES;
    [viewController didMoveToParentViewController:self];
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
