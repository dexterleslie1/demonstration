//
//  MyCollectionViewCell.m
//  demo-uicollectionviewcontroller
//
//  Created by dexterleslie on 2025/9/5.
//

#import "MyCollectionViewCell.h"

@implementation MyCollectionViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
    
    // 图片和按钮的比例为 5:1
    self.button.translatesAutoresizingMaskIntoConstraints = NO;
    [NSLayoutConstraint activateConstraints:@[
        [self.imageView.heightAnchor constraintEqualToAnchor:self.button.heightAnchor multiplier:5]
    ]];
}

- (void)configureWithData:(id)title {
    [self.button setTitle:title forState:UIControlStateNormal];
}

@end
