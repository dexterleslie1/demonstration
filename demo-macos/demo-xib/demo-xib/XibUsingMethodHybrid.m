//
//  XibUsingMethodHybrid.m
//  demo-xib
//
//  Created by dexterleslie on 2025/8/30.
//

#import "XibUsingMethodHybrid.h"

@interface XibUsingMethodHybrid ()

@end

@implementation XibUsingMethodHybrid

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    // 演示使用 Interface Builder+编程 混合方式 使用 xib
    [self.buttonClickme
     addTarget:self
     action:@selector(onClickedButtonClickme:)
     forControlEvents:UIControlEventTouchUpInside];
}

- (void) onClickedButtonClickme:(UIButton *) button {
    NSLog(@"已经点击我了");
}

@end
