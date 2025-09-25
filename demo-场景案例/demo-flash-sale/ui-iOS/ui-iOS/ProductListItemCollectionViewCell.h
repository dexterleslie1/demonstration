//
//  ProductListItemCollectionViewCell.h
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/25.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProductListItemCollectionViewCell : UICollectionViewCell
@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UILabel *labelProductName;
@property (weak, nonatomic) IBOutlet UILabel *labelProductStockAmount;
@property (weak, nonatomic) IBOutlet UILabel *labelProductStatus;

- (void) configureWithData:(NSDictionary *) productData;

@end

NS_ASSUME_NONNULL_END
