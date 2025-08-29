//
//  ViewController.m
//  demo-appdelegate
//
//  Created by dexterleslie on 2025/8/29.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}


- (IBAction)onClickedGetAppDelegate:(id)sender {
    // 使用静态方法获取AppDelegate
    AppDelegate *appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    // [appDelegate.window.rootViewController dismissViewControllerAnimated:YES completion:nil];
}
@end
