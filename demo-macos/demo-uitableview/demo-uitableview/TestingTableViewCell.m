//
//  TestingTableViewCell.m
//  demo-uitableview
//
//  Created by dexterleslie on 2025/9/26.
//

#import "TestingTableViewCell.h"

@implementation TestingTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)configureWithData:(NSString *)data {
    [self.labelOrderId setText:data];
}

@end
