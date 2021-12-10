
import 'dart:async';

import 'package:flutter/services.dart';

class EyflutterCore {
  static const MethodChannel _channel = MethodChannel('eyflutter_core');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
