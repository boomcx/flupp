import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flupp/flupp.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();

    //
    Flupp.register(
      sandbox:
      "AU4zQhs9G_nyYAvnNh64quK8UpUrrFZVbEom7ygmr2FwzmjEkLflcDV0Yso2cXhSjZRxfpKp4D6Lt53c",
      production:
      "AS_cHzhWtzdQE1GFPjix2c_l8Ga7Jp_8BDhc0g5IsO8qvWobZkT_RXdtEmenZpN0PrXOwR0oJE5oSYh7",
    );
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await Flupp.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: InkWell(
            child: Text('Running on: $_platformVersion\n'),
            onTap: () {
              //
              print('123123');


              Flupp.payment(moneys: "100", shortDesc: '充值').then((data) {
                print(data);
              });

            },
          )
        ),
      ),
    );
  }
}
