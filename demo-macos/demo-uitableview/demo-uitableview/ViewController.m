//
//  ViewController.m
//  demo-uitableview
//
//  Created by dexterleslie on 2025/9/4.
//

#import "ViewController.h"
#import "TestingTableView.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    TestingTableView *tableView = [[TestingTableView alloc] init];
    for(int i=1; i<=100; i++){
        [tableView.data addObject:[NSNumber numberWithInt:i]];
    }
    [self.view addSubview:tableView];
    
    tableView.translatesAutoresizingMaskIntoConstraints = NO;
    [tableView.centerXAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.centerXAnchor constant:0].active = YES;
    [tableView.centerYAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.centerYAnchor constant:0].active = YES;
    [tableView.widthAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.widthAnchor constant:0].active = YES;
    [tableView.heightAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.heightAnchor constant:0].active = YES;
}


@end
