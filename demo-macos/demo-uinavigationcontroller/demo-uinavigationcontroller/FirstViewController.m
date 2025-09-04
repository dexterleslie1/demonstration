//
//  FirstViewController.m
//  demo-uinavigationcontroller
//
//  Created by dexterleslie on 2025/9/4.
//

#import "FirstViewController.h"
#import "SecondViewController.h"

@interface FirstViewController ()

@end

@implementation FirstViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // UINavigationController 会使用此标题显示
    self.title = @"First View Controller";
    
    // 添加 button
    UIButton *button = [[UIButton alloc] init];
    [button setTitle:@"Click me" forState:UIControlStateNormal];
    [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [button addTarget:self action:@selector(onClickedButtonClickMe:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:button];
    
    // 使用 AutoLayout 布局
    button.translatesAutoresizingMaskIntoConstraints = NO;
    [button.leadingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.leadingAnchor constant:0].active = YES;
    [button.trailingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.trailingAnchor constant:0].active = YES;
    [button.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor constant:0].active = YES;
}

- (void)onClickedButtonClickMe:(id) sender{
    // 向 UINavigationController 添加视图
    SecondViewController *viewController = [[SecondViewController alloc] init];
    [self.navigationController pushViewController:viewController animated:YES];
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
