//
//  HomeViewController.m
//  ui-iOS
//
//  Created by dexterleslie on 2025/9/23.
//

#import "HomeViewController.h"
#import "ViewController.h"
#import "HomePageViewController.h"
#import "ProductListViewController.h"
#import "ListByUserIdViewController.h"
#import "ListByMerchantIdViewController.h"
#import "CreateProductViewController.h"

@interface HomeViewController ()
@property (weak, nonatomic) IBOutlet UILabel *textFieldUserId;
@property (weak, nonatomic) IBOutlet UILabel *textFieldMerchantId;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

@property NSMutableArray *navItemList;
// NavItem 的原始字体
@property UIFont *originFont;

@property (strong, nonatomic) NSMutableArray<UIViewController *> *pageList;
@property (strong, nonatomic) HomePageViewController *pageViewController;

@end

@implementation HomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // 显示 userId 和 merchantId
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *userIdStr = [userDefaults stringForKey:@"userId"];
    NSString *merchantIdStr = [userDefaults stringForKey:@"merchantId"];
    self.textFieldUserId.text = userIdStr;
    self.textFieldMerchantId.text = merchantIdStr;
    
    [self setupScrollView];
    [self setupPageViewController];
    
    // 默认选中商品列表
     [((UIButton*)self.navItemList[1]) sendActionsForControlEvents: UIControlEventTouchDown];
}

- (void) setupScrollView {
    // 不显示滚动条
    self.scrollView.showsHorizontalScrollIndicator = NO;
    self.scrollView.showsVerticalScrollIndicator = NO;
    
    NSArray *navItemTextList = @[@"首页", @"商品列表", @"用户订单", @"商家订单", @"新增商品"];
    
    self.navItemList = [NSMutableArray array];
    UIButton *previouNavItem = nil;
    for(int i = 0; i < navItemTextList.count; i++) {
        UIButton *navItem = [[UIButton alloc] init];
        navItem.translatesAutoresizingMaskIntoConstraints = NO;
        [navItem setTitle:navItemTextList[i] forState:UIControlStateNormal];
        [navItem setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        [navItem addTarget:self action:@selector(onClickedNavItem:) forControlEvents:UIControlEventTouchDown];
        [self.scrollView addSubview:navItem];
        
        // 保存原始字体
        self.originFont = navItem.titleLabel.font;
        
        [self.navItemList addObject:navItem];
        
        // 添加文本字段的约束
        [NSLayoutConstraint activateConstraints:@[
            [navItem.centerYAnchor constraintEqualToAnchor:self.scrollView.centerYAnchor]
        ]];
        
        if (previouNavItem) {
            // 如果不是第一个文本字段，设置与前一个文本字段的间距
            [navItem.leadingAnchor constraintEqualToAnchor:previouNavItem.trailingAnchor constant:20].active = YES;
        } else {
            // 第一个文本字段与内容视图左对齐
            [navItem.leadingAnchor constraintEqualToAnchor:self.scrollView.leadingAnchor].active = YES;
        }
        
        previouNavItem = navItem;
    }
    
    if (previouNavItem) {
        // 最后一个文本字段与内容视图右对齐，确定 contentSize 的宽度
        [previouNavItem.trailingAnchor constraintEqualToAnchor:self.scrollView.trailingAnchor].active = YES;
        // 最后一个文本字段与 scrollView 高度对齐以确定 contentSize 的高度
        [previouNavItem.heightAnchor constraintEqualToAnchor:self.scrollView.heightAnchor].active = YES;
    }
}

// navItem 点击
- (void)onClickedNavItem:(id) sender{
    [self setNavItemState:self.navItemList withClicked:sender];
    
    if(self.navItemList[0] == sender) {
        // 首页
        
        UIWindow *window = self.view.window;
        // 获取 main bundle 中的 storyboard
        UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
        // 获取初始视图控制器（箭头指向的控制器）
        UIViewController *viewController = [storyboard instantiateInitialViewController];
        window.rootViewController = viewController;
    } else if(self.navItemList[1] == sender) {
        // 商品列表
        
        [self.pageViewController setViewControllers:@[self.pageList[0]]
                                          direction:UIPageViewControllerNavigationDirectionReverse
                                           animated:YES
                                         completion:nil];
    } else if(self.navItemList[2] == sender) {
        // 用户订单
        
        int index = [self.pageList indexOfObject:self.pageViewController.viewControllers.firstObject];
        [self.pageViewController setViewControllers:@[self.pageList[1]]
                                          direction:index<1?UIPageViewControllerNavigationDirectionForward:UIPageViewControllerNavigationDirectionReverse
                                           animated:YES
                                         completion:nil];
    } else if(self.navItemList[3] == sender) {
        // 商家订单
        
        [self.pageViewController setViewControllers:@[self.pageList[2]]
                                          direction:index<2?UIPageViewControllerNavigationDirectionForward:UIPageViewControllerNavigationDirectionReverse
                                           animated:YES
                                         completion:nil];
    } else if(self.navItemList[4] == sender) {
        // 新增商品
        
        [self.pageViewController setViewControllers:@[self.pageList[3]]
                                          direction:UIPageViewControllerNavigationDirectionForward
                                           animated:YES
                                         completion:nil];
    }
}

// 设置 navItem 状态
- (void) setNavItemState: (NSArray *)navItemList withClicked:(UIButton *)navItemOnClicked {
    for(int i=0;i<navItemList.count;i++) {
        UIButton *navItem = (UIButton *)navItemList[i];
        if(navItem == navItemOnClicked) {
            // 选中的 NavItem 显示粗体
            navItem.titleLabel.font = [UIFont boldSystemFontOfSize:self.originFont.pointSize];
        } else {
            // 未选中的 NavItem 显示原始字体
            navItem.titleLabel.font = self.originFont;
        }
    }
}

- (void) setupPageViewController {
    // 初始化页面列表
    self.pageList = [NSMutableArray array];
    [self.pageList addObject:[[ProductListViewController alloc] init]];
    [self.pageList addObject:[[ListByUserIdViewController alloc] init]];
    [self.pageList addObject:[[ListByMerchantIdViewController alloc] init]];
    [self.pageList addObject:[[CreateProductViewController alloc] init]];
    
    // 添加 PageViewController
    self.pageViewController = [[HomePageViewController alloc] initWithTransitionStyle:UIPageViewControllerTransitionStyleScroll
                                                                navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal
                                                                options:nil];
    [self addChildViewController:self.pageViewController];
    [self.view addSubview:self.pageViewController.view];
    self.pageViewController.view.translatesAutoresizingMaskIntoConstraints = NO;
    [self.pageViewController.view.topAnchor constraintEqualToAnchor:self.scrollView.bottomAnchor].active = YES;
    [self.pageViewController.view.leadingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.leadingAnchor].active = YES;
    [self.pageViewController.view.trailingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.trailingAnchor].active = YES;
    [self.pageViewController.view.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor].active = YES;
    [self.pageViewController didMoveToParentViewController:self];
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
