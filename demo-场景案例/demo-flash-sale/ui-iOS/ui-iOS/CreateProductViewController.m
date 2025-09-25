//
//  CreateProductViewController.m
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/25.
//

#import "CreateProductViewController.h"
#import "ProductPurchaseViewController.h"

#import "Config.h"
#import <AFNetworking/AFNetworking.h>
#import "MBProgressHUD.h"

@interface CreateProductViewController ()
@property (weak, nonatomic) IBOutlet UILabel *labelNotice;
@property (weak, nonatomic) IBOutlet UIStackView *stackViewFlashSaleStartTime;
@property (weak, nonatomic) IBOutlet UIStackView *stackViewFlashSaleEndTime;
@property (weak, nonatomic) IBOutlet UISwitch *switchFlashSale;

@end

@implementation CreateProductViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    [self.labelNotice setTextColor:UIColorFromRGB(0x9ACD32)];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

// 勾选或者取消勾选秒杀开关
- (IBAction)onValueChangedFlashSale:(id)sender {
    UISwitch *switchFlashSale = (UISwitch *)sender;
    BOOL isOn = switchFlashSale.isOn;
    [self.stackViewFlashSaleStartTime setHidden:isOn?NO:YES];
    [self.stackViewFlashSaleEndTime setHidden:isOn?NO:YES];
}

// 点击新增商品按钮
- (IBAction)onClickedCreateProduct:(id)sender {
    NSString *url;
    if(!self.switchFlashSale.isOn) {
        url = @"/api/v1/order/addOrdinaryProduct";
    } else {
        url = @"/api/v1/order/addFlashSaleProduct";
    }
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:window animated:YES];
    [manager GET: [NSString stringWithFormat:@"%@%@", ApiUrlPrefix, url]
        parameters: nil
         success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            // 业务异常处理
            NSNumber *errorCode = [responseObject objectForKey: @"errorCode"];
            if([errorCode intValue]>0) {
                NSString *errorMessage = [responseObject objectForKey: @"errorMessage"];
                UIAlertController *alert = [UIAlertController
                        alertControllerWithTitle:@"提示"
                        message:errorMessage
                        preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            } else {
                NSString *data = [responseObject objectForKey:@"data"];
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"提示" message:@"成功新增商品，是否跳转到商品详情功能并下单呢？" preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                    // 跳转到商品购买或者秒杀页面
                    ProductPurchaseViewController *viewController = [[ProductPurchaseViewController alloc] init];
                    viewController.productId = data;
                    [self.navigationController pushViewController:viewController animated:YES];
                }]];
                [alert addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            }

            [hud hideAnimated:YES];
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            NSString *description = error.localizedDescription;
            // AFNetworking 特定的错误信息
            NSHTTPURLResponse *response = error.userInfo[AFNetworkingOperationFailingURLResponseErrorKey];
            int statusCode = (int)response.statusCode;
            NSString *url = response.URL.absoluteString;
            NSString *errorMessage = [NSString stringWithFormat:@"HTTP 错误，状态码：%d，信息：%@，URL：%@", statusCode, description, url];
            UIAlertController *alert = [UIAlertController
                    alertControllerWithTitle:@"提示"
                    message:errorMessage
                    preferredStyle:UIAlertControllerStyleAlert];
            [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil]];
            [self presentViewController:alert animated:YES completion:nil];
            
            [hud hideAnimated:YES];
        }];
}

@end
