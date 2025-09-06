//
//  StackLayoutRatioViewController.m
//  iOS-layout
//
//  Created by dexterleslie on 2025/9/6.
//  Copyright © 2025 YingShun International Network Co., Limited. All rights reserved.
//

#import "StackLayoutRatioViewController.h"

@interface StackLayoutRatioViewController ()

@end

@implementation StackLayoutRatioViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // 图片和按钮的高比例为 3:1
    self.button.translatesAutoresizingMaskIntoConstraints = NO;
    [NSLayoutConstraint activateConstraints:@[
        [self.imageView.heightAnchor constraintEqualToAnchor:self.button.heightAnchor multiplier:3],
    ]];
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
