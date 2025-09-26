//
//  ListByMerchantIdViewController.m
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/24.
//

#import "ListByMerchantIdViewController.h"
#import "ListByMerchantIdTableViewCell.h"

#import "Config.h"
#import <AFNetworking/AFNetworking.h>
#import "MBProgressHUD.h"

@interface ListByMerchantIdViewController ()
@property (weak, nonatomic) IBOutlet UIPickerView *pickerView;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end

@implementation ListByMerchantIdViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // 初始化状态 PickerView
    self.pickerViewData = [NSMutableArray array];
    [self.pickerViewData addObject:@{
        @"display":@"全部",
        @"value":@"",
    }];
    [self.pickerViewData addObject:@{
        @"display":@"未支付",
        @"value":@"Unpay",
    }];
    [self.pickerViewData addObject:@{
        @"display":@"未发货",
        @"value":@"Undelivery",
    }];
    [self.pickerViewData addObject:@{
        @"display":@"未收货",
        @"value":@"Unreceive",
    }];
    [self.pickerViewData addObject:@{
        @"display":@"已签收",
        @"value":@"Received",
    }];
    [self.pickerViewData addObject:@{
        @"display":@"买家取消",
        @"value":@"Canceled",
    }];
    self.pickerView.dataSource = self;
    self.pickerView.delegate = self;
    
    // 初始化订单列表 TableView
    // 注册 TableViewCell
    UINib *nib = [UINib nibWithNibName:@"ListByMerchantIdTableViewCell" bundle:nil];
    [self.tableView registerNib:nib forCellReuseIdentifier:@"cell"];
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/


#pragma mark <UIPickerViewDataSource>

// 返回选择器中的列数
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView {
    return 1; // 例如1列
}

// 返回每列中的行数
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
    return self.pickerViewData.count; // 数据源数组中的元素数量
}

#pragma mark <UIPickerViewDelegate>

// 返回每行的标题
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
    return [self.pickerViewData[row] objectForKey:@"display"];
}

# pragma mark <UITableViewDataSource>
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.tableViewData.count;
}

# pragma mark <UITableViewDelegate>
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    ListByMerchantIdTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
    if(!cell){
        cell = [[ListByMerchantIdTableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"cell"];
    }
    
    // 修改 TableViewCell 视图
    NSDictionary *data = self.tableViewData[indexPath.row];
    [cell configureWithData:data];
    return cell;
}

// 查询订单
- (IBAction)onClickedQuery:(id)sender {
    // 获取当前选中的行
    // 0 表示第一列
    NSInteger selectedRow = [self.pickerView selectedRowInComponent:0];
    // 根据行号获取对应的值
    NSString *status = [self.pickerViewData[selectedRow] objectForKey:@"value"];
    
    NSString *url;
    if([status length] <= 0) {
        url = @"/api/v1/order/listByMerchantIdAndWithoutStatus";
    } else {
        url = @"/api/v1/order/listByMerchantIdAndStatus";
    }
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    NSDictionary *params = @{
        @"merchantId":[[NSUserDefaults standardUserDefaults] objectForKey:@"merchantId"],
        @"latestMonth":@"true",
        @"status":status
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
                NSArray *data = [responseObject objectForKey:@"data"];
                self.tableViewData = data;
                [self.tableView reloadData];
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
