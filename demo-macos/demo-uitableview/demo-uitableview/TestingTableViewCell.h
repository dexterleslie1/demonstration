//
//  TestingTableViewCell.h
//  demo-uitableview
//
//  Created by dexterleslie on 2025/9/26.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TestingTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *labelOrderId;

// 外部调用用于配置 Cell
- (void) configureWithData:(NSString *) data;

@end

NS_ASSUME_NONNULL_END
