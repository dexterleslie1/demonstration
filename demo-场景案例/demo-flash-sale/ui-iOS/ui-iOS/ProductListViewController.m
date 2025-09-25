//
//  ProductListViewController.m
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/24.
//

#import "ProductListViewController.h"

#import "Config.h"
#import <AFNetworking/AFNetworking.h>
#import "MBProgressHUD.h"

#import "ProductListCollectionViewController.h"

@interface ProductListViewController ()
@property (weak, nonatomic) IBOutlet UIButton *buttonRefresh;
@property (strong, nonatomic) ProductListCollectionViewController *collectionViewController;

@end

@implementation ProductListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    [self setupProductListCollectionViewController];
    
    [self reloadProductList];
}

- (void) reloadProductList {
    // 加载商品列表
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:window animated:YES];
    [manager GET: [NSString stringWithFormat:@"%@/api/v1/order/listProductByIds", ApiUrlPrefix]
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
                NSArray *data = [responseObject objectForKey:@"data"];
                self.collectionViewController.productList = data;
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

// 设置商品列表 CollectionViewController
- (void) setupProductListCollectionViewController {
    // 添加 ProductListCollectionViewController 子控制器
    // 创建流式布局（网格）
    UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
    // Item 的宽和高自动计算尺寸
    // layout.estimatedItemSize = UICollectionViewFlowLayoutAutomaticSize;
    // 行间距
    layout.minimumLineSpacing = 10;
    // 列间距
    layout.minimumInteritemSpacing = 10;
    // 控制整个 section 的边距（上、左、下、右），左/右间距为 10
    layout.sectionInset = UIEdgeInsetsMake(0, 10, 0, 10);
    
    // 强制两列布局
    CGFloat totalWidth = [UIScreen mainScreen].bounds.size.width;
    CGFloat spacing = layout.sectionInset.left + layout.sectionInset.right + layout.minimumInteritemSpacing;
    // 固定高度 150
    layout.itemSize = CGSizeMake((totalWidth - spacing) / 2, 250);
    
    self.collectionViewController = [[ProductListCollectionViewController alloc] initWithCollectionViewLayout:layout];
    [self addChildViewController:self.collectionViewController];
    [self.view addSubview:self.collectionViewController.view];
    self.collectionViewController.view.translatesAutoresizingMaskIntoConstraints = NO;
    [NSLayoutConstraint activateConstraints:@[
        [self.collectionViewController.view.leadingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.leadingAnchor],
        [self.collectionViewController.view.trailingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.trailingAnchor],
        [self.collectionViewController.view.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor],
        [self.collectionViewController.view.topAnchor constraintEqualToAnchor:self.buttonRefresh.bottomAnchor constant:10]
    ]];
    [self.collectionViewController.view setBackgroundColor:[UIColor greenColor]];
    [self.collectionViewController didMoveToParentViewController:self];
}

- (IBAction)onClickedRefresh:(id)sender {
    [self reloadProductList];
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
