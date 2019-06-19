import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class Flupp {
  static const MethodChannel _channel = const MethodChannel('com.xzkj/flupp');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future register({
    @required String sandbox,
    @required String production,
  }) async {
    Map args = {
      'sandbox': sandbox,
      'production': production,
    };
    final result = await _channel.invokeMethod('registerPayPal', args);
    return result;
  }

  static Future payment({
    @required String moneys,
    @required String shortDesc,
    String currency,
  }) async {
    Map args = {
      'moneys': moneys,
      'currency': currency ?? 'USD',
      'short': shortDesc,
    };
    final result = await _channel.invokeMethod('sendPayPal', args);
    return result;
  }
}
