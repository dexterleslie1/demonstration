//
//  ViewController.m
//  demo-subviewcontroller
//
//  Created by dexterleslie on 2025/9/23.
//

#import "ViewController.h"
#import "XibProgramaticallySubViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

}

// Storyboard 子控制器
- (IBAction)onClickedStoryboardSubViewController:(id)sender {
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"MyStoryboard" bundle:nil];
    UIViewController *viewController = [storyboard instantiateInitialViewController];
    [self.navigationController pushViewController:viewController animated:YES];
}

// Xib 编程式子控制器
- (IBAction)onClickedXibProgramaticallySubViewController:(id)sender {
    UIViewController *viewController = [[XibProgramaticallySubViewController alloc] init];
    [self.navigationController pushViewController:viewController animated:YES];
}

@end
