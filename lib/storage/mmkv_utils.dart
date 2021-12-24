import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/mq/cloud_channel_manager.dart';

enum MMkvPart {
  //常用缓存区
  normal,
  //状态缓存区
  state,
  //用户数据缓存区
  user
}

class MmkvUtils {
  factory MmkvUtils() => _getInstance();

  static MmkvUtils get instance => _getInstance();
  static MmkvUtils? _instance;

  static MmkvUtils _getInstance() {
    return _instance ??= new MmkvUtils._internal();
  }

  MmkvUtils._internal();

  String _mmkvMethodName = "8064e9487a0c42799d18e8978ead8d69";
  String _mmkvTakeMethodName = "a0caee30d73f4f499fc34870b2a39303";
  String _mmkvDeleteMethodName = "91aa77c467c44eeeb60e3bb5b5dfe2c2";

  String _userWithId = "70746218ab58dc96";
  String _ordinaryWithId = "c694f6da00968d67";
  String _stateWithId = "fa7463fbf7bc5a65";

  Future<T?> _cacheData<T>(String key, dynamic value, String type, MMkvPart part) {
    var withId = "";
    if (part == MMkvPart.user) {
      withId = _userWithId;
    } else if (part == MMkvPart.state) {
      withId = _stateWithId;
    } else {
      withId = _ordinaryWithId;
    }
    return CloudChannelManager.instance.send<T>(_mmkvMethodName, arguments: {
      "withId": withId,
      "key": key,
      "type": type,
      "value": value,
    });
  }

  Future<T?> _takeData<T>(String key, String type, MMkvPart part) {
    var withId = "";
    if (part == MMkvPart.user) {
      withId = _userWithId;
    } else if (part == MMkvPart.state) {
      withId = _stateWithId;
    } else {
      withId = _ordinaryWithId;
    }
    return CloudChannelManager.instance.send<T>(_mmkvTakeMethodName, arguments: {
      "withId": withId,
      "key": key,
      "type": type,
    });
  }

  /// 保存int数据
  /// [key] 存储key
  /// [value] int value
  /// [part] 缓存区类型
  Future<int?> putInt(String key, {int value = 0, MMkvPart part = MMkvPart.normal}) {
    if (key.isEmptyString) {
      return Future.value(0);
    }
    return _cacheData<int>(key, value, "int", part);
  }

  /// 保存double数据
  /// [key] 存储key
  /// [value] double value
  /// [part] 缓存区类型
  Future<double?> putDouble(String key, {double value = 0.0, MMkvPart part = MMkvPart.normal}) {
    if (key.isEmptyString) {
      return Future.value(0);
    }
    return _cacheData<double>(key, value, "double", part);
  }

  /// 保存string数据
  /// [key] 存储key
  /// [value] string value
  /// [part] 缓存区类型
  Future<String?> putString(String key, {String value = "", MMkvPart part = MMkvPart.normal}) {
    if (key.isEmptyString) {
      return Future.value("");
    }
    return _cacheData<String>(key, value, "string", part);
  }

  /// 保存bool数据
  /// [key] 存储key
  /// [value] bool value
  /// [part] 缓存区类型
  Future<bool?> putBool(String key, {bool value = false, MMkvPart part = MMkvPart.normal}) {
    if (key.isEmptyString) {
      return Future.value(false);
    }
    return _cacheData<bool>(key, value, "bool", part);
  }

  /// 获取int值
  /// [key] 存储key
  /// [part] 缓存区类型
  Future<int?> getInt(String key, {MMkvPart part = MMkvPart.normal}) {
    if (key.isEmptyString) {
      return Future.value(0);
    }
    return _takeData<int>(key, "int", part);
  }

  /// 获取double值
  /// [key] 存储key
  /// [part] 缓存区类型
  Future<double?> getDouble(String key, {MMkvPart part = MMkvPart.normal}) {
    if (key.isEmptyString) {
      return Future.value(0.0);
    }
    return _takeData<double>(key, "double", part);
  }

  /// 获取bool值
  /// [key] 存储key
  /// [part] 缓存区类型
  Future<bool?> getBool(String key, {MMkvPart part = MMkvPart.normal}) {
    if (key.isEmptyString) {
      return Future.value(false);
    }
    return _takeData<bool>(key, "bool", part);
  }

  /// 获取string值
  /// [key] 存储key
  /// [part] 缓存区类型
  Future<String?> getString(String key, {MMkvPart part = MMkvPart.normal}) {
    if (key.isEmptyString) {
      return Future.value("");
    }
    return _takeData<String>(key, "string", part);
  }

  /// 清除key对应缓存数据
  /// [key] 存储key
  /// [part] 缓存区类型
  Future<dynamic> remove(String key, {MMkvPart part = MMkvPart.normal}) {
    if (key.isEmptyString) {
      return Future.value();
    }
    var withId = "";
    if (part == MMkvPart.user) {
      withId = _userWithId;
    } else if (part == MMkvPart.state) {
      withId = _stateWithId;
    } else {
      withId = _ordinaryWithId;
    }
    return CloudChannelManager.instance.send<dynamic>(_mmkvDeleteMethodName, arguments: {
      "withId": withId,
      "key": key,
    });
  }
}
