package com.xzkj.flupp.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;


import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Map;

import io.flutter.Log;
import io.flutter.app.FlutterActivity;
import io.flutter.app.FlutterApplication;
import io.flutter.plugin.common.MethodCall;

/**
 * https://www.jianshu.com/p/8b96c8d8819b
 * Android集成PayPal支付
 */
public class PayPalHelper {
    private static final String TAG = "PayPalHelper";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

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
     */
    public void startPayPalService(Context context, MethodCall params) {

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
     *
     * @param context
     */
    public void stopPayPalService(Context context) {
        context.stopService(new Intent(context, PayPalService.class));
    }

    /**
     * 开始执行支付操作
     *
     * @param context
     */
    public void doPayPalPay(Context context,String moneys, String currentcy, String shotDesc) {

        PayPalPayment thingToBuy = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE,moneys,currentcy,shotDesc);

        Intent intent = new Intent(context, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        ((FlutterActivity) context).startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    /*
     * This method shows use of optional payment details and item list.
     * TODO 这里修改支付参数
     * 直接给PP创建支付的信息，支付对象实体信息
     */
    private PayPalPayment getStuffToBuy(String paymentIntent,String moneys, String currentcy, String shotDesc) {
        //--- include an item list, payment amount details
        //具体的产品信息列表
//        PayPalItem[] items =
//                {
//                        new PayPalItem("sample item #1", 2, new BigDecimal("0.01"), "USD",
//                                "sku-12345678"),
//                        new PayPalItem("free sample item #2", 1, new BigDecimal("0.00"),
//                                "USD", "sku-zero-price"),
//                        new PayPalItem("sample item #3 with a longer name", 6, new BigDecimal("0.99"),
//                                "USD", "sku-33333")
//                };
//        BigDecimal subtotal = PayPalItem.getItemTotal(items);
//        BigDecimal shipping = new BigDecimal("0.21");
//        BigDecimal tax = new BigDecimal("0.67");
//        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
//        BigDecimal amount = subtotal.add(shipping).add(tax);
//        PayPalPayment payment = new PayPalPayment(amount, "USD", "sample item", paymentIntent);
//        payment.items(items).paymentDetails(paymentDetails);
//        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
//        payment.custom("This is text that will be associated with the payment that the app can use.");
        PayPalPayment payment = new PayPalPayment(new BigDecimal(moneys), currentcy, shotDesc,
                paymentIntent)
        return payment;
    }

    /**
     * 处理支付之后的结果
     *
     * @param context
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void confirmPayResult(final Context context, int requestCode, int resultCode, Intent data, final DoResult doResult) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {

                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        //displayResultText("PaymentConfirmation info received from PayPal");
                        // 这里直接跟服务器确认支付结果，支付结果确认后回调处理结果
                        JSONObject jsonObject = confirm.toJSONObject();
                        if (jsonObject != null) {
                            JSONObject response = jsonObject.optJSONObject("response");
                            if (response != null) {
                                String id = response.optString("id");
                                //根据Id从自己的服务器判断相应的查询逻辑
                                doResult.confirmSuccess(id);
                            }
                        }
                    } catch (Exception e) {
                        doResult.confirmNetWorkError();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                doResult.customerCanceled();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                doResult.invalidPaymentConfiguration();
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        doResult.confirmFuturePayment();

                        String authorization_code = auth.getAuthorizationCode();

                        //sendAuthorizationToServer(auth);
                        //displayResultText("Future Payment code received from PayPal");

                    } catch (Exception e) {
                        doResult.confirmNetWorkError();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                doResult.customerCanceled();
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                doResult.invalidPaymentConfiguration();

            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {

                        String authorization_code = auth.getAuthorizationCode();

                        //sendAuthorizationToServer(auth);
                        //displayResultText("Profile Sharing code received from PayPal");

                    } catch (Exception e) {
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
            }
        }
    }

    /**
     * c处理完结果之后回调
     */
    public interface DoResult {
        //与服务确认支付成功
        void confirmSuccess(String id);

        //网络异常或者json返回有问题
        void confirmNetWorkError();

        //用户取消支付
        void customerCanceled();

        //授权支付
        void confirmFuturePayment();

        //订单支付验证无效
        void invalidPaymentConfiguration();
    }


}

