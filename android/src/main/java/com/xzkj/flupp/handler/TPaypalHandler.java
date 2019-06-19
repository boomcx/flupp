package com.xzkj.flupp.handler;

import android.text.TextUtils;

import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;

public class TPaypalHandler{

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
        PayPalHelper.getInstance().startPayPalService(mActivity.context(), params);
    }

    public void pay(String moneys, String currentcy, String shotDesc) {
        //支付
        PayPalHelper.getInstance().doPayPalPay(mActivity.activity(), TextUtils.isEmpty(moneys) ? "0.01" : moneys,currentcy,shotDesc);
    }
}
