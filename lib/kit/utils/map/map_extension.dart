import 'package:eyflutter_core/kit/utils/set/list_extention.dart';

extension MapUtilsMapKeyExtension<M, T> on Map<M, T>? {
  /// 移除map中对应key的数据
  /// [keys] map keys
  void removeKeys(List<M> keys) {
    if (this.isEmptyMap() || keys.isEmptyList) {
      return;
    }
    keys.forEach((key) {
      //remove key or value
      this?.remove(key);
    });
  }

  /// 添加元素到map中
  /// [key]元素key
  /// [value]元素value
  Map<M, T>? addItem(M key, T value) {
    if (key == null || value == null) {
      return this;
    }
    this?[key] = value;
    return this;
  }

  /// 合并两个map对象
  /// [items]要合并的map集合
  Map<M, T> merge(Map<M, T> items) {
    var map = this ?? {};
    if (items.isEmptyMap()) {
      return map;
    }
    items.forEach((k, v) {
      if (v != null) {
        map[k] = v;
      }
    });
    return map;
  }

  /// 获取map value
  /// [key]map->key
  /// [defaultValue]不在存在时返回默认值
  T? getValue<T>(M key, [T? defaultValue]) {
    if (this.isEmptyMap() || !(this?.containsKey(key) ?? false)) {
      return defaultValue;
    }
    Object? obj = this?[key];
    if (obj is! T) {
      return defaultValue;
    }
    T result = this?[key] as T;
    return result ?? defaultValue;
  }

  /// 判断是否空map
  /// key-value
  bool isEmptyMap() {
    return this?.isEmpty ?? true;
  }

  /// 判断map为非空
  bool isNotEmptyMap() {
    return !this.isEmptyMap();
  }
}
