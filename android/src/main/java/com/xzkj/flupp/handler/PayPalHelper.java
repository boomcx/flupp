package com.xzkj.flupp.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.widget.Toast;

import io.flutter.plugin.common.JSONUtil;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;


import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.flutter.Log;
import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * Android集成PayPal支付
 * @author Apple
 */

public class PayPalHelper implements ActivityResultListener {
    private static final String TAG = "PayPalHelper";

    private Activity activity;
    private Context context;
    private Result activeResult;


    private static final int REQUEST_CODE_PAYMENT = 1;

    private static PayPalConfiguration config;
    private static PayPalHelper payPalHelper;


    private PayPalHelper() {
    }

    public boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static PayPalHelper getInstance() {
        if (payPalHelper == null) {
            synchronized (PayPalHelper.class) {
                payPalHelper = new PayPalHelper();
            }
        }
        return payPalHelper;
    }

    /**
     * 启动PayPal服务
     *
     * @param context
     * @param params
     */
    public void startPayPalService(PluginRegistry.Registrar registrar, MethodCall params) {
        this.context = registrar.context();
        this.activity = registrar.activity();
        registrar.addActivityResultListener(this);


        //配置何种支付环境，一般沙盒，正式
        if(isApkInDebug(context)) {

            String clientId = params.argument("sandbox");
            config = new PayPalConfiguration()
                    .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                    .clientId(clientId);

            Log.i("isApkInDebug ", "true");
            Log.i("isApkInDebug ", clientId);

        }else {

            String clientId = params.argument("production");
            config = new PayPalConfiguration()
                    .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                    .clientId(clientId);

            Log.i("'isApkInDebug '","false ");
            Log.i("isApkInDebug ", clientId);
        }

        //
        Intent intent = new Intent(context, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        context.startService(intent);
    }

    /**
     * 停止PayPal服务
     */
    public void stopPayPalService() {
        context.stopService(new Intent(context, PayPalService.class));
    }

    /**
     * 开始执行支付操作
     *
     * @param call
     * @param result
     */
    public void doPayPalPay(MethodCall call, Result result) {
        this.activeResult = result;

        //
        PayPalPayment thingToBuy = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE, call);

        Intent intent = new Intent(context, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        this.activity.startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }


    /**
     * 直接给PP创建支付的信息，支付对象实体信息
     *
     * @param paymentIntent
     * @param call
     */
    private PayPalPayment getStuffToBuy(String paymentIntent, MethodCall call) {
        String moneys = call.argument("moneys");
        String currentcy = call.argument("currency");
        String shotDesc = call.argument("short");
        PayPalPayment payment = new PayPalPayment(new BigDecimal(moneys), currentcy, shotDesc,
                paymentIntent);
        return payment;
    }


    /**
     * 处理支付之后的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult onActivityResult onActivityResult");
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        JSONObject jsonObject = confirm.toJSONObject();
                        if (jsonObject != null) {
                            JSONObject response = jsonObject.optJSONObject("response");
                            if (response != null) {
                                  String id = response.optString("id");
                                //  与服务确认支付成功
                                Log.i(TAG, jsonObject.toString());
                                this.paySuccess(id);
                            }
                        }
                    } catch (Exception e) {
                        // 网络异常或者json返回有问题
                        this.payError("网络异常或者json返回有问题");
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // 用户取消支付
                this.userCancel();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                this.payError("订单支付验证无效");
            }
            return  true;
        }

        stopPayPalService();

        return  false;
    }

    private void payError(String msg) {
        Map res = new HashMap();
        res.put("code", -1);
        res.put("result", msg);
        this.activeResult.success(res);
    }

    private void userCancel() {
        Map res = new HashMap();
        res.put("code", -2);
        res.put("result",  "用户取消");
        this.activeResult.success(res);
    }

    private void paySuccess(String pid) {
        Map res = new HashMap();
        res.put("code", 0);
        res.put("result", pid);
        this.activeResult.success(res);
    }

}

