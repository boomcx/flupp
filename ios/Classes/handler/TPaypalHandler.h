//
//  TPaypalHandler.h
//  flutter_paypal
//
//  Created by Apple on 2019/5/30.
//

#import <Foundation/Foundation.h>
#import "FluppPlugin.h"

NS_ASSUME_NONNULL_BEGIN

@interface TPaypalHandler : NSObject


/**
 环境注册

 @param call 接收桥接参数
 @param result 信息回调
 */
- (void)registerWithCall:(FlutterMethodCall *)call  result:(FlutterResult)result;


/**
 发起支付

 @param call 接收桥接参数
 @param result 信息回调
 */
- (void)paymentWithCall:(FlutterMethodCall *)call  result:(FlutterResult)result;

@end

NS_ASSUME_NONNULL_END
