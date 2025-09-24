//
//  ViewController.m
//  demo-mbprogresshub
//
//  Created by dexterleslie on 2025/9/24.
//

#import "ViewController.h"
#import "MBProgressHUD.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

// 简单的加载提示
- (IBAction)onClicked:(id)sender {
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    // 显示 HUD
    [MBProgressHUD showHUDAddedTo:window animated:YES];
    
    // 在后台执行耗时操作（例如网络请求）
    dispatch_async(dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0), ^{
        // 模拟耗时操作
        sleep(3);
        
        // 回到主线程隐藏 HUD
        dispatch_async(dispatch_get_main_queue(), ^{
            [MBProgressHUD hideHUDForView:window animated:YES];
        });
    });
}

// 带文字的加载提示
- (IBAction)onClicked2:(id)sender {
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:window animated:YES];
    hud.label.text = @"加载中..."; // 设置提示文字
    dispatch_async(dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0), ^{
        // 模拟耗时操作
        sleep(3);
        
        // 回到主线程隐藏 HUD
        dispatch_async(dispatch_get_main_queue(), ^{
            [hud hideAnimated:YES];
        });
    });
}

@end
