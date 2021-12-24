import 'package:flutter/material.dart';

class ConstNavigator {
  ConstNavigator._();

  /// 静态路由跳转
  /// [context] 组件上下文
  /// [target] 目标页面
  static void go({required BuildContext context, required Widget target}) {
    Navigator.push(context, MaterialPageRoute(builder: (BuildContext context) {
      return target;
    }));
  }
}
