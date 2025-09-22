//
//  ThirdViewController.m
//  demo-storyboard
//
//  Created by dexterleslie on 2025/9/22.
//

#import "ThirdViewController.h"

@interface ThirdViewController ()

@end

@implementation ThirdViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (IBAction)onClickedBack:(id)sender {
    // 调用 unwind segue 以销毁当前 View Controller
    [self performSegueWithIdentifier:@"unwind" sender:self];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
