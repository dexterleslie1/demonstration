//
//  demo_afnetworkingTests.m
//  demo-afnetworkingTests
//
//  Created by dexterleslie on 2025/9/24.
//

#import <XCTest/XCTest.h>
#import <AFNetworking/AFNetworking.h>

@interface demo_afnetworkingTests : XCTestCase

@end

@implementation demo_afnetworkingTests

- (void)setUp {
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
}

- (void)testExample {
    // This is an example of a functional test case.
    // Use XCTAssert and related functions to verify your tests produce the correct results.
    
    NSString *host = @"192.168.235.128";
    int port = 8080;
    NSString *uriPrefix = [NSString stringWithFormat:@"http://%@:%d", host, port];

    AFHTTPSessionManager *manager = nil;
    XCTestExpectation *expectation = nil;

    __block NSNumber *errorCode = nil;
    __block NSString *errorMessage = nil;
    __block NSError *errorResult = nil;

    /* 测试失败的 get 请求，业务异常处理 */
    expectation = [[XCTestExpectation alloc] init];
    manager = [AFHTTPSessionManager manager];
    [manager GET: [NSString stringWithFormat:@"%@/api/v1/test1", uriPrefix] parameters: nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 业务异常处理
        errorCode = [responseObject objectForKey: @"errorCode"];
        if([errorCode intValue]>0) {
            errorMessage = [responseObject objectForKey: @"errorMessage"];
        }

        [expectation fulfill];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        errorResult = error;
        [expectation fulfill];
    }];

    [self waitForExpectations:@[expectation] timeout:10];

    XCTAssertNil(errorResult);
    XCTAssertEqual([errorCode intValue], 90000);
    XCTAssertEqualObjects(@"Missing required parameter \"name\"!", errorMessage);

    /* 测试成功的 get 请求 */
    errorCode = nil;
    errorMessage = nil;
    errorResult = nil;

    expectation = [[XCTestExpectation alloc] init];
    manager = [AFHTTPSessionManager manager];
    NSDictionary *params = @{
        @"name": @"Dexter"
    };
    [manager GET: [NSString stringWithFormat:@"%@/api/v1/test1", uriPrefix] parameters: params success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        [expectation fulfill];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        errorResult = error;
        [expectation fulfill];
    }];

    [self waitForExpectations:@[expectation] timeout:10];

    XCTAssertNil(errorResult);
    XCTAssertEqual([errorCode intValue], 0);
    XCTAssertNil(errorMessage);

    /* 测试 http 400 返回 */
    errorCode = nil;
    errorMessage = nil;
    errorResult = nil;

    expectation = [[XCTestExpectation alloc] init];
    manager = [AFHTTPSessionManager manager];
    [manager GET: [NSString stringWithFormat:@"%@/api/v1/testHttp400", uriPrefix] parameters: nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        [expectation fulfill];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        errorResult = error;

        // 错误域
        XCTAssertEqualObjects(@"com.alamofire.error.serialization.response", errorResult.domain);
        // 错误码
        XCTAssertEqual(-1011, errorResult.code);
        // 描述
        XCTAssertEqualObjects(@"Request failed: bad request (400)", errorResult.localizedDescription);
        // 失败原因
        XCTAssertNil(errorResult.localizedFailureReason);
        // AFNetworking 特定的错误信息
        NSHTTPURLResponse *response = errorResult.userInfo[AFNetworkingOperationFailingURLResponseErrorKey];
        // HTTP 状态码
        XCTAssertEqual(400, response.statusCode);
        // URL
        XCTAssertEqualObjects(@"http://192.168.235.128:8080/api/v1/testHttp400", response.URL.absoluteString);
        // 头信息
        XCTAssertEqual(4, response.allHeaderFields.count);
        XCTAssertEqualObjects(@"application/json", response.allHeaderFields[@"Content-Type"]);
        XCTAssertEqualObjects(@"close", response.allHeaderFields[@"Connection"]);

        // 响应体
        NSData *responseData = errorResult.userInfo[AFNetworkingOperationFailingURLResponseDataErrorKey];
        NSString *json = json = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
        // json 转换为 NSDictionary
        NSData *jsonData = [json dataUsingEncoding:NSUTF8StringEncoding];
        NSError *jsonError;
        NSDictionary *dictionary = [NSJSONSerialization JSONObjectWithData:jsonData
                                                     options:NSJSONReadingMutableContainers
                                                       error:&jsonError];
        XCTAssertNil(jsonError);
        XCTAssertEqual(90000, [dictionary[@"errorCode"] intValue]);
        XCTAssertEqualObjects(@"测试业务异常", dictionary[@"errorMessage"]);

        [expectation fulfill];
    }];

    [self waitForExpectations:@[expectation] timeout:10];

    XCTAssertEqualObjects(@"Request failed: bad request (400)", errorResult.localizedDescription);
    XCTAssertEqual([errorCode intValue], 0);
    XCTAssertNil(errorMessage);
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
