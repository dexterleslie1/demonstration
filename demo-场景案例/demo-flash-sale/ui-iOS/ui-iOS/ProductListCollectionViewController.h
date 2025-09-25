//
//  ProductListCollectionViewController.h
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/25.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface ProductListCollectionViewController : UICollectionViewController

@property (strong, nonatomic) NSArray<NSDictionary *>* productList;

@end

NS_ASSUME_NONNULL_END
