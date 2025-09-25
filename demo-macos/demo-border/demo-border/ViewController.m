//
//  ViewController.m
//  demo-border
//
//  Created by dexterleslie on 2025/9/25.
//

#import "ViewController.h"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UIView *myView;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    // 设置边框为黑色
    self.myView.layer.borderColor = [UIColor blackColor].CGColor;
    // 设置边框宽度为 1
    self.myView.layer.borderWidth = 1;
    // 设置边框圆角
    self.myView.layer.cornerRadius = 10;
}


@end
