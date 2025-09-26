//
//  TestingTableView.m
//  ios-ui-uitableview
//
//  Created by john on 19/7/9.
//  Copyright © 2019年 future. All rights reserved.
//

#import "TestingTableView.h"
#import "TestingTableViewCell.h"

@implementation TestingTableView

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (instancetype)init{
    self = [super init];
    if(self){
        self.data = [[NSMutableArray alloc] init];
        self.dataSource = self;
        self.delegate = self;
        
        // 注册 TableViewCell
        UINib *nib = [UINib nibWithNibName:@"TestingTableViewCell" bundle:nil];
        [self registerNib:nib forCellReuseIdentifier:@"myCell"];
    }
    return self;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.data.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    TestingTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"myCell" forIndexPath:indexPath];
    if(!cell){
        cell = [[TestingTableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"myCell"];
    }
    
    // 修改 TableViewCell 视图
    NSNumber *data = self.data[indexPath.row];
    [cell configureWithData:[NSString stringWithFormat:@"Row %@", data]];
    return cell;
}

@end
