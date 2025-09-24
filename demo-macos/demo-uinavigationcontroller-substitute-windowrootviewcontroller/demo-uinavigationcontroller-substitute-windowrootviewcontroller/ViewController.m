//
//  ViewController.m
//  demo-uinavigationcontroller-substitute-windowrootviewcontroller
//
//  Created by dexterleslie on 2025/9/24.
//

#import "ViewController.h"
#import "MyViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (IBAction)onClickedSwitchToMyView:(id)sender {
    UIViewController *viewController = [[MyViewController alloc] init];
    [self.navigationController pushViewController:viewController animated:YES];
}

@end
