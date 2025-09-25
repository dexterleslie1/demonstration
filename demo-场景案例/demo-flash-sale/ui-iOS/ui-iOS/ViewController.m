//
//  ViewController.m
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/21.
//

#import "ViewController.h"
#import "MainViewController.h"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UITextField *textFieldUserId;
@property (weak, nonatomic) IBOutlet UITextField *textFieldMerchantId;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    // 显示上次保存的 userId 和 merchantId
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *userIdStr = [userDefaults stringForKey:@"userId"];
    NSString *merchantIdStr = [userDefaults stringForKey:@"merchantId"];
    self.textFieldUserId.text = userIdStr;
    self.textFieldMerchantId.text = merchantIdStr;
}

- (IBAction)onClickedOk:(id)sender {
    // 保存 userId 和 merchantId
    NSString *userIdStr = self.textFieldUserId.text;
    NSString *merchantIdStr = self.textFieldMerchantId.text;
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:userIdStr forKey:@"userId"];
    [userDefaults setObject:merchantIdStr forKey:@"merchantId"];
    
    UIWindow *window = self.view.window;
    MainViewController *mainViewController = [[MainViewController alloc] init];
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:mainViewController];
    window.rootViewController = navigationController;
}

@end
