//
//  ViewController.m
//  demo-uiimageview
//
//  Created by dexterleslie on 2025/9/6.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    // 获取 Assets 中的图片
    UIImage *uiImage = [UIImage imageNamed:@"icon_mok"];
    UIImageView *uiImageView = [[UIImageView alloc] initWithImage:uiImage];
    [self.view addSubview:uiImageView];
    
    uiImageView.translatesAutoresizingMaskIntoConstraints = NO;
    [NSLayoutConstraint activateConstraints:@[
        [uiImageView.leadingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.leadingAnchor constant:0],
        [uiImageView.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor constant:0],
        [uiImageView.widthAnchor constraintEqualToConstant:100],
        [uiImageView.heightAnchor constraintEqualToConstant:100]
    ]];
    UIImageView *imageViewPrev = uiImageView;
    
    // 搜索图片资源路径
    NSString *imagePath = [[NSBundle mainBundle] pathForResource:@"icon_t2" ofType:@"png"];
    // 创建图片
    uiImage = [UIImage imageWithContentsOfFile:imagePath];
    // 创建图片视图
    uiImageView = [[UIImageView alloc] initWithImage: uiImage];
    [self.view addSubview: uiImageView];
    
    uiImageView.translatesAutoresizingMaskIntoConstraints = NO;
    [NSLayoutConstraint activateConstraints:@[
        [uiImageView.leadingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.leadingAnchor constant:0],
        [uiImageView.topAnchor constraintEqualToAnchor:imageViewPrev.bottomAnchor constant:0],
        [uiImageView.widthAnchor constraintEqualToConstant:100],
        [uiImageView.heightAnchor constraintEqualToConstant:100]
    ]];
}


@end
