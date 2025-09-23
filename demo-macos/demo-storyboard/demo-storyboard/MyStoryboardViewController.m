//
//  MyStoryboardViewController.m
//  demo-storyboard
//
//  Created by dexterleslie on 2025/9/23.
//

#import "MyStoryboardViewController.h"

@interface MyStoryboardViewController ()
@property (weak, nonatomic) IBOutlet UILabel *label;

@end

@implementation MyStoryboardViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    // 用于协助测试动态加载 storyboard 时是否会回调 ViewController 的 viewDidLoad 方法
    self.label.textColor = [UIColor greenColor];
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
