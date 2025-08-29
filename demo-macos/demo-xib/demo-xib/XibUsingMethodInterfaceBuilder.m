//
//  XibUsingMethodInterfaceBuilder.m
//  demo-xib
//
//  Created by dexterleslie on 2025/8/30.
//

#import "XibUsingMethodInterfaceBuilder.h"

@interface XibUsingMethodInterfaceBuilder ()

@end

@implementation XibUsingMethodInterfaceBuilder

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

- (IBAction)onClickedClickMe:(id)sender {
    NSLog(@"已经点击我了(XIB IB)");
}
@end
