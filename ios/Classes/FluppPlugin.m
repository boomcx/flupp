#import "FluppPlugin.h"
#import "TPaypalHandler.h"


NSString *const registerPaypal = @"registerPayPal";
NSString *const sendPaypal = @"sendPayPal";

@interface FluppPlugin ()

@property (nonatomic, strong) TPaypalHandler *paypalHandler;


@end

@implementation FluppPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"com.xzkj/flupp"
            binaryMessenger:[registrar messenger]];
  FluppPlugin* instance = [[FluppPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    
    if ([registerPaypal isEqualToString:call.method]) {
        // 注册贝宝
        [_paypalHandler registerWithCall:call result:result];
    }
    else  if([sendPaypal isEqualToString:call.method]) {
        // 发起支付
        [_paypalHandler paymentWithCall:call result:result];
    }
    else  if ([@"getPlatformVersion" isEqualToString:call.method]) {
        result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
    }else {
        result(FlutterMethodNotImplemented);
    }
    
}

@end
