import 'dart:collection';

import 'package:eyflutter_core/kit/utils/dynamic_utils.dart';

/// 内存存储
class MemoryUtils {
  factory MemoryUtils() => _getInstance();

  static MemoryUtils get instance => _getInstance();
  static MemoryUtils? _instance;

  static MemoryUtils _getInstance() {
    return _instance ??= new MemoryUtils._internal();
  }

  static Map<String, dynamic> _map = new HashMap<String, dynamic>();

  MemoryUtils._internal();

  /// 设置临时缓存
  /// [key]cache key
  /// [value]cache value
  void set(String key, dynamic value) {
    _map[key] = value;
  }

  /// 获取临时缓存
  /// [key]cache key
  dynamic get(String key) {
    return _map[key];
  }

  /// 获取缓存String
  /// [key] cache key
  String getString(String key) {
    dynamic value = get(key);
    if (value == null || value is! String) {
      return "";
    }
    return value;
  }

  /// 获取缓存num
  /// [key]cache key
  num getNum(String key) {
    dynamic value = get(key);
    if (value == null) {
      return 0;
    }
    return value.toNum();
  }

  /// 获取缓存int
  /// [key]cache key
  int getInt(String key) {
    num value = getNum(key);
    return value.toInt();
  }

  /// 获取缓存double
  /// [key]cache key
  double getDouble(String key) {
    num value = getNum(key);
    return value.toDouble();
  }

  /// 获取缓存bool
  /// [key]cache key
  bool getBool(String key) {
    Object value = get(key);
    return value.isTrue;
  }

  /// 移除包含containKey的缓存
  /// [containKey]包含缓存key
  void removeContainKey(String containKey) {
    Set<String> keys = new HashSet<String>();
    _map.keys.forEach((String key) {
      if (key.contains(containKey)) {
        keys.add(key);
      }
    });
    keys.forEach((String removeKey) {
      _map.remove(removeKey);
    });
  }

  void remove(String key) {
    _map.remove(key);
  }

  /// 移除所有缓存
  void clearAll() {
    _map.clear();
  }
}
