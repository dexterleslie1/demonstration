//
//  SecondViewController.m
//  demo-uinavigationcontroller
//
//  Created by dexterleslie on 2025/9/4.
//

#import "SecondViewController.h"

@interface SecondViewController ()

@end

@implementation SecondViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // UINavigationController 会使用此标题显示
    self.title = @"Second View Controller";
    
    UIBarButtonItem *buttonItemBack = [[UIBarButtonItem alloc]
                                          initWithTitle:@"< Back"
                                          style:UIBarButtonItemStylePlain
                                          target:self
                                          action:@selector(onClickedButtonItemBack:)];
    // 设置 UIViewController 的左边按钮
    self.navigationItem.leftBarButtonItem = buttonItemBack;
}

- (void)onClickedButtonItemBack:(id) sender{
    // 返回上一个视图
    [self.navigationController popViewControllerAnimated:YES];
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
