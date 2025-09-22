//
//  ViewController.m
//  demo-storyboard
//
//  Created by dexterleslie on 2025/9/21.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

// Unwind Segue 时回调函数
- (IBAction) myUnwindSegue:(UIStoryboardSegue *)unwindSegue {
    // unwindSegue.identifier 是 unwind segue 的 identifier
    // unwindSegue.sourceViewController 是返回前 View Controller 的实例以判断 unwind segue 从哪里返回
    NSLog(@"unwindIdentifier=%@,sourceViewController=%@", unwindSegue.identifier, [unwindSegue.sourceViewController class]);
}

// 显示 second view controller
- (IBAction)onClickedShowSecond:(id)sender {
    // 调用 segue 显示 second view controller
    [self performSegueWithIdentifier:@"show-second" sender:nil];
}

// 显示 third view controller
- (IBAction)onClickedShowThird:(id)sender {
    //  调用 segue 显示 third view controller
    [self performSegueWithIdentifier:@"show-third" sender:nil];
}


@end
