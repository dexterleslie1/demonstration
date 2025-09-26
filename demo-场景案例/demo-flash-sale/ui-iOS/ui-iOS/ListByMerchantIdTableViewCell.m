//
//  ListByMerchantIdTableViewCell.m
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/26.
//

#import "ListByMerchantIdTableViewCell.h"

@implementation ListByMerchantIdTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)configureWithData:(NSDictionary *)data {
    NSString *orderId = [data objectForKey:@"id"];
    NSString *createTime = [data objectForKey:@"createTime"];
    long userId = [[data objectForKey:@"userId"] longValue];
    NSArray *orderDetailList = [data objectForKey:@"orderDetailList"];
    NSString *productList = @"";
    for(int i=0;i<orderDetailList.count;i++) {
        NSDictionary *productData = orderDetailList[i];
        NSString *productName = [productData objectForKey:@"productName"];
        productList = [NSString stringWithFormat:@"%@%@，", productList, productName];
    }
    [self.labelOrderId setText:[NSString stringWithFormat:@"%@", orderId]];
    [self.labelCreateTime setText:createTime];
    [self.labelUserId setText:[NSString stringWithFormat:@"%ld", userId]];
    [self.labelProductList setText:productList];
}

@end
