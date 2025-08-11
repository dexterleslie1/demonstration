//
//  ViewController.m
//  iOS-group-folder
//
//  Created by MacOS on 2020/1/9.
//  Copyright © 2020 MacOS. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    // 会对应在文件系统中的目录结构，`build` 后的文件会被复制到项目根目录
    NSString *path = [[NSBundle mainBundle] pathForResource:@"image1" ofType:@"png"];
    NSLog(@"Image path for group %@", path);
    
    // 会对应文件系统中的目录结构，build后的文件会对应在相应的目录中
    path = [[NSBundle mainBundle] pathForResource:@"image1" ofType:@"png" inDirectory:@"FolderReferences"];
    NSLog(@"Image path for folder references %@", path);
}


@end
