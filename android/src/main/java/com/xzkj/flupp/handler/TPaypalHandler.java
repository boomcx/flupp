package com.xzkj.flupp.handler;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.MethodChannel.Result;

public class TPaypalHandler {

    private PluginRegistry.Registrar mActivity;

    private static TPaypalHandler payPalHelper;

    private TPaypalHandler(PluginRegistry.Registrar context) {
        mActivity = context;
    }

    public static TPaypalHandler instance(PluginRegistry.Registrar context) {
        if (payPalHelper == null) {
            payPalHelper = new TPaypalHandler(context);
        }
        return payPalHelper;
    }

    public void register(MethodCall params) {
        PayPalHelper.getInstance().startPayPalService(mActivity, params);
    }

    public void pay(MethodCall call, Result result) {
        //支付
        PayPalHelper.getInstance().doPayPalPay(call, result);
    }
}
