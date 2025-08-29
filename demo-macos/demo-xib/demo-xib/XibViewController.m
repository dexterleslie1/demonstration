//
//  XibViewController.m
//  demo-xib
//
//  Created by dexterleslie on 2025/8/29.
//

#import "XibViewController.h"

@interface XibViewController ()

@end

@implementation XibViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self.buttonClickme addTarget:self action:@selector(onClickedButtonClickme:) forControlEvents:UIControlEventTouchUpInside];
}

- (void) onClickedButtonClickme:(UIButton *) button {
    NSLog(@"已经点击我了");
}

@end

