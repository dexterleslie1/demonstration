//
//  ListByUserIdTableViewCell.h
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/26.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ListByUserIdTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *labelOrderId;
@property (weak, nonatomic) IBOutlet UILabel *labelCreateTime;
@property (weak, nonatomic) IBOutlet UILabel *labelMerchantId;
@property (weak, nonatomic) IBOutlet UILabel *labelProductList;

- (void) configureWithData:(NSDictionary *)data;

@end

NS_ASSUME_NONNULL_END
