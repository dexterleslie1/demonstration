//
//  TestingTableView.h
//  ios-ui-uitableview
//
//  Created by john on 19/7/9.
//  Copyright © 2019年 future. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TestingTableView : UITableView <UITableViewDelegate, UITableViewDataSource>

// TableView 的数据源
@property (nonatomic, strong) NSMutableArray<NSNumber *> *data;

@end
