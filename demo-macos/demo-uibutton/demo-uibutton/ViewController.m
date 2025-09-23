//
//  ViewController.m
//  demo-uibutton
//
//  Created by dexterleslie on 2025/9/23.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
  
    // UIButton initWithFrame
    CGRect rect = CGRectMake(0, 30, 200, 60);
    UIButton *button1 = [[UIButton alloc] initWithFrame:rect];
    [button1 setTitle:@"Button initWithFrame" forState:UIControlStateNormal];
    [button1 setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [button1 setBackgroundColor:[UIColor redColor]];
    [self.view addSubview:button1];
  
    button1.translatesAutoresizingMaskIntoConstraints=NO;
    [self.view addConstraint: [NSLayoutConstraint
                               constraintWithItem:button1
                               attribute:NSLayoutAttributeCenterX
                               relatedBy:NSLayoutRelationEqual
                               toItem:self.view
                               attribute:NSLayoutAttributeCenterX
                               multiplier:1.0f
                               constant:0]];
    [self.view addConstraint: [NSLayoutConstraint
                               constraintWithItem:button1
                               attribute:NSLayoutAttributeTop
                               relatedBy:NSLayoutRelationEqual
                               toItem:self.view
                               attribute:NSLayoutAttributeTop
                               multiplier:1.0f
                               constant:80]];
  
    // UIButton buttonWithType
    UIButton *button2 = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    rect = CGRectMake(0, 100, 200, 60);
    button2.frame = rect;
    [button2 setTitle:@"Button withType" forState:UIControlStateNormal];
    [button2 setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [button2 setBackgroundColor:[UIColor redColor]];
    [self.view addSubview:button2];
  
    button2.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addConstraint: [NSLayoutConstraint
                               constraintWithItem:button2
                               attribute:NSLayoutAttributeCenterX
                               relatedBy:NSLayoutRelationEqual
                               toItem:self.view
                               attribute:NSLayoutAttributeCenterX
                               multiplier:1.0f
                               constant:0]];
    [self.view addConstraint: [NSLayoutConstraint
                               constraintWithItem:button2
                               attribute:NSLayoutAttributeTop
                               relatedBy:NSLayoutRelationEqual
                               toItem:button1
                               attribute:NSLayoutAttributeBottom
                               multiplier:1.0f
                               constant:10]];
  
    // UIButton 约束布局
    UIButton *button3 = [[UIButton alloc] initWithFrame:CGRectMake(0, 170, 200, 60)];
    [button3 setTitle:@"Click me" forState:UIControlStateNormal];
    [button3 setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [button3 addTarget:self action:@selector(buttonClick:) forControlEvents:UIControlEventTouchDown];
    [self.view addSubview:button3];
  
    button3.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addConstraint: [NSLayoutConstraint
                               constraintWithItem:button3
                               attribute:NSLayoutAttributeCenterX
                               relatedBy:NSLayoutRelationEqual
                               toItem:self.view
                               attribute:NSLayoutAttributeCenterX
                               multiplier:1.0f
                               constant:0]];
    [self.view addConstraint: [NSLayoutConstraint
                               constraintWithItem:button3
                               attribute:NSLayoutAttributeTop
                               relatedBy:NSLayoutRelationEqual
                               toItem:button2
                               attribute:NSLayoutAttributeBottom
                               multiplier:1.0f
                               constant:10]];
    
    // 在代码中触发按钮点击事件
    [button3 sendActionsForControlEvents:UIControlEventTouchDown];
}

- (void)buttonClick:(id) sender{
    NSLog(@"Button click!!");
}

@end
