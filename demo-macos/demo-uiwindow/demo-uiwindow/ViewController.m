//
//  ViewController.m
//  demo-uiwindow
//
//  Created by dexterleslie on 2025/9/22.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    // 在 viewDidLoad 不能获取 UIWindow 实例
    UIWindow *window = self.view.window;
    NSLog(@"viewDidLoad UIWindow=%@", window);
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
    UIWindow *window = self.view.window;
    NSLog(@"viewDidAppear UIWindow=%@", window);
}

@end
