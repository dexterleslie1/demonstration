//
//  ViewController.m
//  demo-logintomain
//
//  Created by dexterleslie on 2025/9/22.
//

#import "ViewController.h"
#import "MainViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (IBAction)onClickedLogin:(id)sender {
    // 跳转到主界面
    UIWindow *window = self.view.window;
    MainViewController *mainViewController = [[MainViewController alloc] init];
    window.rootViewController = mainViewController;
}

@end
