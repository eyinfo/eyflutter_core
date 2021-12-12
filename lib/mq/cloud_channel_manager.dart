import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:flutter/services.dart';

class CloudChannelManager {
  factory CloudChannelManager() => _getInstance();

  static CloudChannelManager get instance => _getInstance();
  static CloudChannelManager _instance;

  static CloudChannelManager _getInstance() {
    _instance ??= CloudChannelManager._internal();
    return _instance;
  }

  String _methodName = "0eff8bd070f64d1890193686196f5a31";

  OptionalMethodChannel _methodChannel;

  CloudChannelManager._internal() {
    _methodChannel = OptionalMethodChannel(_methodName);
  }

  /// 听筒
  OptionalMethodChannel get channel => _methodChannel;

  /// 向native端发送并接收future回调
  /// [action] 此次通信的标识符
  /// [arguments] 此次通信数据
  Future<R> send<R>(String action, {arguments}) {
    if (action.isEmptyString) {
      return Future.value();
    }
    var future = _methodChannel.invokeMethod<R>(action, arguments);
    if (future == null) {
      return Future.value();
    }
    return future;
  }
}
