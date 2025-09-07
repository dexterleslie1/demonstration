//
//  ActivityIndicatorViewBasicUsageViewController.h
//  demo-uiactivityindicatorview
//
//  Created by dexterleslie on 2025/9/7.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

// UIActivityIndicatorView 基本用法
@interface ActivityIndicatorViewBasicUsageViewController : UIViewController
- (IBAction)onClickedStart:(id)sender;
- (IBAction)onClickedStop:(id)sender;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicatorView;

@end

NS_ASSUME_NONNULL_END
