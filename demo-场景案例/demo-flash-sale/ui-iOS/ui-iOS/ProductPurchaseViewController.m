//
//  ProductPurchaseViewController.m
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/25.
//

#import "ProductPurchaseViewController.h"

#import "Config.h"
#import <AFNetworking/AFNetworking.h>
#import "MBProgressHUD.h"

@interface ProductPurchaseViewController ()
@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UILabel *labelProductName;
@property (weak, nonatomic) IBOutlet UILabel *labelProductStockAmount;
@property (weak, nonatomic) IBOutlet UILabel *labelProductStatus;

@property (nonatomic) BOOL flashSale;

@end

@implementation ProductPurchaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // 设置 ImageView 的边框
    self.imageView.layer.borderColor = [UIColor grayColor].CGColor;
    self.imageView.layer.borderWidth = 1;
    self.imageView.layer.cornerRadius = 10;
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    NSDictionary *params = @{
        @"id": self.productId
    };
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:window animated:YES];
    [manager GET: [NSString stringWithFormat:@"%@/api/v1/order/getProductById", ApiUrlPrefix]
        parameters: params
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
                NSDictionary *productData = [responseObject objectForKey:@"data"];
                NSString *name = [productData objectForKey:@"name"];
                NSNumber *stockAmount = [productData objectForKey:@"stock"];
                self.flashSale = [[productData objectForKey:@"flashSale"] boolValue];
                NSNumber *toFlashSaleStartTimeRemainingSeconds = [productData objectForKey:@"toFlashSaleStartTimeRemainingSeconds"];
                NSNumber *toFlashSaleEndTimeRemainingSeconds = [productData objectForKey:@"toFlashSaleEndTimeRemainingSeconds"];
                
                [self.labelProductName setText:name];
                [self.labelProductStockAmount setText:[NSString stringWithFormat:@"%@",stockAmount]];
                if(!self.flashSale) {
                    // 隐藏但占据布局空间
                    [self.labelProductStatus setAlpha:0];
                } else {
                    [self.labelProductStatus setAlpha:1];
                    
                    if([toFlashSaleStartTimeRemainingSeconds intValue] > 0) {
                        [self.labelProductStatus setText: [NSString stringWithFormat:@"距离秒杀开始时间还有 %d 秒", [toFlashSaleStartTimeRemainingSeconds intValue]]];
                        [self.labelProductStatus setTextColor:UIColorFromRGB(0xFFA500)];
                    } else if([toFlashSaleStartTimeRemainingSeconds intValue] <= 0 && [toFlashSaleEndTimeRemainingSeconds intValue] > 0) {
                        [self.labelProductStatus setText:[NSString stringWithFormat:@"距离秒杀结束还有 %d 秒", [toFlashSaleEndTimeRemainingSeconds intValue]]];
                        [self.labelProductStatus setTextColor:UIColorFromRGB(0x9ACD32)];
                    } else if([toFlashSaleStartTimeRemainingSeconds intValue] <=0 && [toFlashSaleEndTimeRemainingSeconds intValue] <= 0) {
                        [self.labelProductStatus setText:@"秒杀已结束"];
                        [self.labelProductStatus setTextColor:UIColorFromRGB(0xFF0000)];
                    }
                }
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

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

// 购买商品
- (IBAction)onClickedPurchase:(id)sender {
    NSString *url;
    if(!self.flashSale) {
        url = @"/api/v1/order/create";
    } else {
        url = @"/api/v1/order/createFlashSale";
    }
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    NSDictionary *params = @{
        @"productId":self.productId,
        @"userId":[[NSUserDefaults standardUserDefaults] objectForKey:@"userId"],
        @"randomCreateTime": @"true"
    };
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:window animated:YES];
    [manager GET: [NSString stringWithFormat:@"%@%@", ApiUrlPrefix, url]
        parameters: params
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
                UIAlertController *alert = [UIAlertController
                        alertControllerWithTitle:@"提示"
                        message:data
                        preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil]];
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
