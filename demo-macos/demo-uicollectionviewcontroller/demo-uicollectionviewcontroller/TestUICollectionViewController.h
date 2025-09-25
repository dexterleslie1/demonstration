//
//  TestUICollectionViewController.h
//  demo-uicollectionviewcontroller
//
//  Created by dexterleslie on 2025/9/5.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TestUICollectionViewController : UICollectionViewController

// CollectionViewController 数据源属性
@property (nonatomic, strong) NSArray<NSString *> *itemList;

@end

NS_ASSUME_NONNULL_END
