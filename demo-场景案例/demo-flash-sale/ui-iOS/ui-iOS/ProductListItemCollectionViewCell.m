//
//  ProductListItemCollectionViewCell.m
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/25.
//

#import "ProductListItemCollectionViewCell.h"
#import "Config.h"

@implementation ProductListItemCollectionViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
    
    // 设置 cell 的边框
    self.layer.borderColor = [UIColor blackColor].CGColor;
    self.layer.borderWidth = 1;
    self.layer.cornerRadius = 10;
    
    // 设置 ImageView 的边框
    self.imageView.layer.borderColor = [UIColor grayColor].CGColor;
    self.imageView.layer.borderWidth = 1;
    self.imageView.layer.cornerRadius = 10;
}

- (void)configureWithData:(NSDictionary *)productData {
    NSString *name = [productData objectForKey:@"name"];
    NSNumber *stockAmount = [productData objectForKey:@"stock"];
    BOOL flashSale = [[productData objectForKey:@"flashSale"] boolValue];
    NSNumber *toFlashSaleStartTimeRemainingSeconds = [productData objectForKey:@"toFlashSaleStartTimeRemainingSeconds"];
    NSNumber *toFlashSaleEndTimeRemainingSeconds = [productData objectForKey:@"toFlashSaleEndTimeRemainingSeconds"];
    
    [self.labelProductName setText:name];
    [self.labelProductStockAmount setText:[NSString stringWithFormat:@"%@",stockAmount]];
    if(!flashSale) {
        // 隐藏但占据布局空间
        [self.labelProductStatus setAlpha:0];
    } else {
        [self.labelProductStatus setAlpha:1];
        
        if([toFlashSaleStartTimeRemainingSeconds intValue] > 0) {
            [self.labelProductStatus setText: [NSString stringWithFormat:@"距离秒杀开始时间还有 %d 秒", [toFlashSaleStartTimeRemainingSeconds intValue]]];
            [self.labelProductStatus setTextColor:UIColorFromRGB(0xFFA500)];
        } else if([toFlashSaleStartTimeRemainingSeconds intValue] <= 0 && [toFlashSaleEndTimeRemainingSeconds intValue] > 0) {
            [self.labelProductStatus setText:[NSString stringWithFormat:@"距离秒杀结束还有 %d 秒", [toFlashSaleEndTimeRemainingSeconds intValue]]];
            [self.labelProductStatus setTextColor:UIColorFromRGB(0x9ACD32)];
        } else if([toFlashSaleStartTimeRemainingSeconds intValue] <=0 && [toFlashSaleEndTimeRemainingSeconds intValue] <= 0) {
            [self.labelProductStatus setText:@"秒杀已结束"];
            [self.labelProductStatus setTextColor:UIColorFromRGB(0xFF0000)];
        }
    }
}

@end
