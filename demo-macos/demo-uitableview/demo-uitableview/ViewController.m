//
//  ViewController.m
//  demo-uitableview
//
//  Created by dexterleslie on 2025/9/4.
//

#import "ViewController.h"
#import "TestingTableView.h"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UIButton *button;

@property (strong, nonatomic) TestingTableView *tableView;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.tableView = [[TestingTableView alloc] init];
    for(int i=1; i<=100; i++){
        // 向 TableView 数据源添加数据
        [self.tableView.data addObject:[NSNumber numberWithInt:i]];
    }
    [self.view addSubview:self.tableView];
    self.tableView.translatesAutoresizingMaskIntoConstraints = NO;
    [NSLayoutConstraint activateConstraints:@[
        [self.tableView.topAnchor constraintEqualToAnchor:self.button.bottomAnchor],
        [self.tableView.leadingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.leadingAnchor],
        [self.tableView.trailingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.trailingAnchor],
        [self.tableView.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor]
    ]];
}

- (IBAction)onClicked:(id)sender {
    // 向 TableView 数据源添加数据
    [self.tableView.data removeAllObjects];
    for(int i=1; i<=5; i++){
        [self.tableView.data addObject:[NSNumber numberWithInt:i]];
    }
    // 数据源更新后通知 TableView 视图更新
    [self.tableView reloadData];
}


@end
