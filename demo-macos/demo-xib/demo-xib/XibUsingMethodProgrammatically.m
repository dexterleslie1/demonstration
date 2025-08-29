//
//  XibUsingMethodProgrammatically.m
//  demo-xib
//
//  Created by dexterleslie on 2025/8/30.
//

#import "XibUsingMethodProgrammatically.h"

@interface XibUsingMethodProgrammatically ()

@end

@implementation XibUsingMethodProgrammatically

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    UIButton *buttonClickme = [[UIButton alloc] init];
    [buttonClickme setTitle: @"Click me(XIB programmatically)" forState: UIControlStateNormal];
    // 设置颜色
    [buttonClickme setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
    [self.view addSubview: buttonClickme];
    
    [buttonClickme setTranslatesAutoresizingMaskIntoConstraints: NO];
    
    [self.view addConstraint: [NSLayoutConstraint
                               constraintWithItem:buttonClickme
                               attribute:NSLayoutAttributeCenterX
                               relatedBy:NSLayoutRelationEqual
                               toItem:self.view
                               attribute:NSLayoutAttributeCenterX
                               multiplier:1.0f
                               constant:0]];
    
    [self.view addConstraint: [NSLayoutConstraint
                               constraintWithItem:buttonClickme
                               attribute:NSLayoutAttributeCenterY
                               relatedBy:NSLayoutRelationEqual
                               toItem:self.view
                               attribute:NSLayoutAttributeCenterY
                               multiplier:1.0f
                               constant:0]];
    
    // 最小宽度约束（可选）
    NSLayoutConstraint *minWidth = [NSLayoutConstraint
                                   constraintWithItem:buttonClickme
                                   attribute:NSLayoutAttributeWidth
                                   relatedBy:NSLayoutRelationGreaterThanOrEqual
                                   toItem:nil
                                   attribute:NSLayoutAttributeNotAnAttribute
                                   multiplier:1.0f
                                   constant:100]; // 宽度至少 100 点
    [self.view addConstraint:minWidth];

    // 高度约束（根据字体自动计算）
    NSLayoutConstraint *height = [NSLayoutConstraint
                                  constraintWithItem:buttonClickme
                                  attribute:NSLayoutAttributeHeight
                                  relatedBy:NSLayoutRelationEqual
                                  toItem:nil
                                  attribute:NSLayoutAttributeNotAnAttribute
                                  multiplier:1.0f
                                  constant:44]; // 固定高度 44 点（系统按钮常用高度）
    [self.view addConstraint:height];
    
    [buttonClickme
     addTarget:self
     action:@selector(onClickedClickme:)
     forControlEvents:UIControlEventTouchUpInside];

}

- (void) onClickedClickme: (UIButton *) button {
    NSLog(@"已经点击我了(XIB programmatically)");
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
