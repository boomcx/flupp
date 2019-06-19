import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flupp/flupp.dart';

void main() {
  const MethodChannel channel = MethodChannel('flupp');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Flupp.platformVersion, '42');
  });
}
