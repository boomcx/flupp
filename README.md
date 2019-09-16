# flupp



## 安装
在你的`pubspec.yaml`文件的dependencies节点下添加:
```
flupp: 
 git: https://github.com/boomcx/flupp.git
 
```

使用方法:

导入:
```
import 'package:flupp/flupp.dart';
```

注册沙盒环境与发布环境:
```
  Flupp.register(
    sandbox:
        "AU4zQhs9G_nyYAvnNh64quK8UpUrrFZVbEom7ygmr2FwzmjEkLflcDV0Yso2cXhSjZRxfpKp4D6Lt53c",
    production:
        "AS_cHzhWtzdQE1GFPjix2c_l8Ga7Jp_8BDhc0g5IsO8qvWobZkT_RXdtEmenZpN0PrXOwR0oJE5oSYh7",
  );
  
```

发起支付:
```
 Map resp = await Flupp.payment(moneys: "100.98", shortDesc: "充值");
```
