package com.xzkj.flupp_example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONObject;

import io.flutter.app.FlutterActivity;
import io.flutter.app.FlutterApplication;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {

  private String TAG = "Pay";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    getIntent().putExtra("enable-software-rendering", true);
    super.onCreate(savedInstanceState);
    ((FlutterApplication)getApplication()).setCurrentActivity(this);
    GeneratedPluginRegistrant.registerWith(this);
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1) {
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
                Toast.makeText(this, "与服务确认支付成功", Toast.LENGTH_SHORT).show();
              }
            }
          } catch (Exception e) {
            Toast.makeText(this, "网络异常或者json返回有问题", Toast.LENGTH_SHORT).show();
          }
        }
      } else if (resultCode == Activity.RESULT_CANCELED) {
        Toast.makeText(this, "用户取消支付", Toast.LENGTH_SHORT).show();
      } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
        Toast.makeText(this, "订单支付验证无效", Toast.LENGTH_SHORT).show();
      }
    } else if (requestCode == 2) {
      if (resultCode == Activity.RESULT_OK) {
        PayPalAuthorization auth =
                data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
        if (auth != null) {
          try {
            Toast.makeText(this, "授权支付", Toast.LENGTH_SHORT).show();

            String authorization_code = auth.getAuthorizationCode();

            //sendAuthorizationToServer(auth);
            //displayResultText("Future Payment code received from PayPal");

          } catch (Exception e) {
            Toast.makeText(this, "网络异常或者json返回有问题", Toast.LENGTH_SHORT).show();

          }
        }
      } else if (resultCode == Activity.RESULT_CANCELED) {
        Toast.makeText(this, "用户取消支付", Toast.LENGTH_SHORT).show();
      } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
        Toast.makeText(this, "订单支付验证无效", Toast.LENGTH_SHORT).show();
      }
    } else if (requestCode == 3) {
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

  @Override
  protected void onDestroy() {
    stopService(new Intent(this, PayPalService.class));
    super.onDestroy();
  }


}
