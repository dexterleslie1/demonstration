//
//  ViewController.m
//  demo-uipageviewcontroller
//
//  Created by dexterleslie on 2025/9/23.
//

#import "ViewController.h"
#import "FirstViewController.h"
#import "SecondViewController.h"
#import "ThirdViewController.h"

@interface ViewController ()

@property (strong, nonatomic) UIPageViewController *pageViewController;
@property (strong, nonatomic) NSMutableArray<UIViewController *> *pageList;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.pageList = [NSMutableArray array];
    FirstViewController *firstViewController = [[FirstViewController alloc] init];
    SecondViewController *secondViewController = [[SecondViewController alloc] init];
    ThirdViewController *thirdViewController = [[ThirdViewController alloc] init];
    [self.pageList addObject:firstViewController];
    [self.pageList addObject:secondViewController];
    [self.pageList addObject:thirdViewController];
    
    // 设置初始化视图
    [self.pageViewController setViewControllers:@[self.pageList[0]]
                                      direction:UIPageViewControllerNavigationDirectionForward
                                       animated:NO
                                     completion:nil];
}

// 通过 Segue 获取 ContainerView 中的 UIPageViewController 实例
// 需要先设置 Storyboard 中 Embed Segue 的 Identifier 为 EmbedPageViewControllerSegue
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:@"EmbedPageViewControllerSegue"]) {
        self.pageViewController = (UIPageViewController *)segue.destinationViewController;
        self.pageViewController.dataSource = self;
    }
}

- (IBAction)onClickedPrev:(id)sender {
    int index = [self indexOfCurrentViewController];
    index = MAX(0, index-1);
    // 编程设置当前显示的 UIViewController
    [self.pageViewController setViewControllers:@[self.pageList[index]]
                                      direction:UIPageViewControllerNavigationDirectionReverse
                                       animated:YES
                                     completion:nil];
}

- (IBAction)onClickedNext:(id)sender {
    int index = [self indexOfCurrentViewController];
    index = MIN(self.pageList.count-1, index+1);
    // 编程设置当前显示的 UIViewController
    [self.pageViewController setViewControllers:@[self.pageList[index]]
                                      direction:UIPageViewControllerNavigationDirectionForward
                                       animated:YES
                                     completion:nil];
}

// 获取 UIPageViewController 当前显示的 UIViewController 索引
- (int) indexOfCurrentViewController {
    return [self.pageList indexOfObject:self.pageViewController.viewControllers.firstObject];
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController
    viewControllerBeforeViewController:(UIViewController *)viewController {
    // 返回前一个视图控制器
    NSUInteger index = [self.pageList indexOfObject:viewController];
    if (index == 0 || index == NSNotFound) {
        return nil;
    }
    return self.pageList[--index];
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController
    viewControllerAfterViewController:(UIViewController *)viewController {
    // 返回后一个视图控制器
    NSUInteger index = [self.pageList indexOfObject:viewController];
    if (index == NSNotFound || index == self.pageList.count - 1) {
        return nil;
    }
    return self.pageList[++index];
}

@end
