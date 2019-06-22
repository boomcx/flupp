//
//  TPaypalHandler.m
//  flutter_paypal
//
//  Created by Apple on 2019/5/30.
//

#import "TPaypalHandler.h"
#import "PayPalMobile.h"

@interface TPaypalHandler ()<PayPalPaymentDelegate>

@property (nonatomic, strong) PayPalConfiguration  *paypalConfig;
@property (nonatomic, strong) UIViewController *rootVC;
@property (nonatomic, copy) FlutterResult payResult;

@end
@implementation TPaypalHandler

#pragma mark - Register
- (void)registerWithCall:(FlutterMethodCall *)call  result:(FlutterResult)result {
    // SandboxEnvir:(NSString *)sandboxEnvir productionEnvir:(NSString *)productionEnvir
    NSDictionary *clients = call.arguments;
    if (![clients.allKeys containsObject: @"sandbox"]) {
        result(@"沙盒环境 client id 不存在");
        return;
    } if (![clients.allKeys containsObject: @"production"]) {
        result(@"生产环境 client id 不存在");
        return;
    }
    NSDictionary *envirs  = @{
                              PayPalEnvironmentProduction:clients[@"production"],
                              PayPalEnvironmentSandbox: clients[@"sandbox"],
                              };
    [PayPalMobile initializeWithClientIdsForEnvironments:envirs];
}


#pragma mark - Payment
- (void)preconnectEnvirAndPayConfigurarion {
    
#ifdef DEBUG
    [PayPalMobile preconnectWithEnvironment:PayPalEnvironmentSandbox];
#else
    [PayPalMobile preconnectWithEnvironment:PayPalEnvironmentProduction];
#endif
    
    // 初始化当前 flutter 视图控制器
    _rootVC =  [UIApplication sharedApplication].keyWindow.rootViewController;
    
    // 支付配置
    _paypalConfig = [[PayPalConfiguration alloc] init];
    // 是否支持信用卡
    //    _paypalConfig.acceptCreditCards = NO;
    // 关联收货地址
    _paypalConfig.payPalShippingAddressOption = PayPalShippingAddressOptionPayPal;
    // 默认语言
    _paypalConfig.languageOrLocale =  [[NSLocale currentLocale] objectForKey:NSLocaleLanguageCode];
    // 其他参数自行添加
    // ...
}

- (void)paymentWithCall:(FlutterMethodCall *)call  result:(FlutterResult)result {
    self.payResult = result;
    [self preconnectEnvirAndPayConfigurarion];
    
    PayPalPayment *payment = [[PayPalPayment alloc] init];
    
    // 单笔支付
    payment.amount = [NSDecimalNumber decimalNumberWithString:call.arguments[@"moneys"]];
    // 不支持人民币（CNY）
    payment.currencyCode = @"USD";
    payment.shortDescription = call.arguments[@"short"];

    if (!payment.processable) {
        // 支付参数体错误
        result(@"支付信息错误");
    }
    
    // 发起支付
    PayPalPaymentViewController *payVC = [[PayPalPaymentViewController alloc] initWithPayment:payment configuration:_paypalConfig delegate:self];
    [_rootVC presentViewController:payVC animated:YES completion:nil];
    
}


#pragma mark - PayPalPaymentDelegate
- (void)payPalPaymentDidCancel:(PayPalPaymentViewController *)paymentViewController {
    self.payResult(@{@"code": @(-2), @"result": @"用户取消"});
    [_rootVC dismissViewControllerAnimated:YES completion:nil];
}

- (void)payPalPaymentViewController:(PayPalPaymentViewController *)paymentViewController didCompletePayment:(PayPalPayment *)completedPayment {
    
    if ([completedPayment.confirmation[@"response"][@"state"] isEqualToString:@"approved"]) {
        // 支付成功
        self.payResult(@{@"code": @0, @"result": completedPayment.confirmation[@"response"][@"id"]});
    }else {
        // 其他异常，未测试
        self.payResult(@{@"code": @(-1), @"result": @"支付异常"});
    }
    // 退出支付
    [_rootVC dismissViewControllerAnimated:YES completion:nil];
    /*
     completedPayment.confirmation
     {
     client =     {
     environment = sandbox;
     "paypal_sdk_version" = "2.18.1";
     platform = iOS;
     "product_name" = "PayPal iOS SDK";
     };
     response =     {
     "create_time" = "2019-05-29T11:18:55Z";
     id = "PAYID-123456";
     intent = sale;
     state = approved;
     };
     "response_type" = payment;
     }
     */
}


@end
