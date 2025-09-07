//
//  ActivityIndicatorViewBasicUsageViewController.m
//  demo-uiactivityindicatorview
//
//  Created by dexterleslie on 2025/9/7.
//

#import "ActivityIndicatorViewBasicUsageViewController.h"

@interface ActivityIndicatorViewBasicUsageViewController ()

@end

@implementation ActivityIndicatorViewBasicUsageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)onClickedStop:(id)sender {
    // 停止 indicator
    [self.activityIndicatorView stopAnimating];
}

- (IBAction)onClickedStart:(id)sender {
    // 启动 indicator
    [self.activityIndicatorView startAnimating];
}
@end
