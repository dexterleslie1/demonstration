//
//  ViewController.m
//  demo-uialertcontroller
//
//  Created by dexterleslie on 2025/9/24.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (IBAction)onClicked1:(id)sender {
    UIAlertController *alert = [UIAlertController
        alertControllerWithTitle:@"提示"
        message:@"确定要删除吗？"
        preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
        NSLog(@"点击取消");
    }]];
    [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
        NSLog(@"点击确定");
    }]];
    [self presentViewController:alert animated:YES completion:nil];
}

@end
