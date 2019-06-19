import 'dart:async';

import 'package:flutter/services.dart';

class Flupp {
  static const MethodChannel _channel =
      const MethodChannel('flupp');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
