//
//  ViewController.m
//  demo-uipickerview
//
//  Created by dexterleslie on 2025/9/26.
//

#import "ViewController.h"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UIPickerView *pickerView;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.data = [NSMutableArray array];
    for(int i=0;i<5;i++) {
        [self.data addObject:@{
            @"value":[NSString stringWithFormat:@"value-%d", i],
            @"display":[NSString stringWithFormat:@"display-%d", i]
        }];
    }
    
    self.pickerView.dataSource = self;
    self.pickerView.delegate = self;
}

#pragma mark <UIPickerViewDataSource>

// 返回选择器中的列数
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView {
    return 1; // 例如1列
}

// 返回每列中的行数
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
    return self.data.count; // 数据源数组中的元素数量
}

#pragma mark <UIPickerViewDelegate>

// 返回每行的标题
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
    return [self.data[row] objectForKey:@"display"];
}

// 选中某行时调用
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    NSLog(@"Selected: %@", [self.data[row] objectForKey:@"value"]);
}

- (IBAction)onClickedGetPickerViewCurrentValue:(id)sender {
    // 获取当前选中的行
    // 0 表示第一列
    NSInteger selectedRow = [self.pickerView selectedRowInComponent:0];
    // 根据行号获取对应的值
    NSString *currentValue = [self.data[selectedRow] objectForKey:@"value"];
    NSLog(@"当前选中的值是: %@", currentValue);
}

@end
