package com.xzkj.flupp;

import android.util.Log;

import com.xzkj.flupp.handler.TPaypalHandler;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FluppPlugin */
public class FluppPlugin implements MethodCallHandler {


  private String Tag = "Pay";
  private Registrar mContext;

  private FluppPlugin(Registrar mContext) {
    this.mContext = mContext;
  }


  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "com.xzkj/flupp");
    channel.setMethodCallHandler(new FluppPlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("registerPayPal")) {
      TPaypalHandler.instance(mContext).register(call);
      Tag = "Pay";
      Log.e(Tag,"registerPayPal");
    } else if (call.method.equals("sendPayPal")) {
      TPaypalHandler.instance(mContext).pay(call, result);
      Log.e(Tag,"sendPayPal");
    } else if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
      Log.e(Tag,"getPlatformVersion");
    } else {
      result.notImplemented();
      Log.e(Tag,"notImplemented");
    }
  }
}
