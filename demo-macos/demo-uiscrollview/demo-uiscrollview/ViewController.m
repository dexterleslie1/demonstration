//
//  ViewController.m
//  demo-uiscrollview
//
//  Created by dexterleslie on 2025/9/23.
//

#import "ViewController.h"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    // 不显示滚动条
    self.scrollView.showsHorizontalScrollIndicator = NO;
    self.scrollView.showsVerticalScrollIndicator = NO;
    
    UILabel *previousLabel = nil;
    for(int i = 0; i < 15; i++) {
        UILabel *label = [[UILabel alloc] init];
        label.translatesAutoresizingMaskIntoConstraints = NO;
        label.text = [NSString stringWithFormat:@"t%d", i];
        [self.scrollView addSubview:label];
        
        // 添加文本字段的约束
        [NSLayoutConstraint activateConstraints:@[
            [label.centerYAnchor constraintEqualToAnchor:self.scrollView.centerYAnchor]
        ]];
        
        if (previousLabel) {
            // 如果不是第一个文本字段，设置与前一个文本字段的间距
            [label.leadingAnchor constraintEqualToAnchor:previousLabel.trailingAnchor constant:20].active = YES;
        } else {
            // 第一个文本字段与内容视图左对齐
            [label.leadingAnchor constraintEqualToAnchor:self.scrollView.leadingAnchor].active = YES;
        }
        
        previousLabel = label;
    }
    
    if (previousLabel) {
        // 最后一个文本字段与内容视图右对齐，确定 contentSize 的宽度
        [previousLabel.trailingAnchor constraintEqualToAnchor:self.scrollView.trailingAnchor].active = YES;
        // 最后一个文本字段与 scrollView 高度对齐以确定 contentSize 的高度
        [previousLabel.heightAnchor constraintEqualToAnchor:self.scrollView.heightAnchor].active = YES;
    }
}


@end
