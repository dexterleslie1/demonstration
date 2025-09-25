//
//  MyCollectionViewCell.h
//  demo-uicollectionviewcontroller
//
//  Created by dexterleslie on 2025/9/5.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface MyCollectionViewCell : UICollectionViewCell
@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UIButton *button;

// 创建 Cell 时外部调用设置 Cell
- (void) configureWithData:(NSString *)title;

@end

NS_ASSUME_NONNULL_END
