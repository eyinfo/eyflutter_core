import 'package:eyflutter_core/kit/timer/system.dart';
import 'package:eyflutter_core/kit/utils/int/int_extension.dart';
import 'package:eyflutter_core/kit/utils/json_utils.dart';
import 'package:eyflutter_core/kit/utils/string/json_extension.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/storage/mmkv_utils.dart';
import 'package:flutter/material.dart';

/// 特定时间内数据缓存
/// 超过缓存时间后下次获取数据自动删除
class EffectiveMMkvUtils {
  factory EffectiveMMkvUtils() => _getInstance();

  static EffectiveMMkvUtils get instance => _getInstance();
  static EffectiveMMkvUtils _instance;

  static EffectiveMMkvUtils _getInstance() {
    if (_instance == null) {
      _instance = new EffectiveMMkvUtils._internal();
    }
    return _instance;
  }

  EffectiveMMkvUtils._internal();

  /// 缓存有效数据
  /// [cacheKey] 缓存key
  /// [value] 缓存数据
  /// [duration] 缓存时间
  void putString({@required String cacheKey, String value, Duration duration = const Duration()}) {
    if (value.isEmptyString || duration == null) {
      return;
    }
    var map = {"startTime": System.currentTimeMillis, "duration": duration.inMilliseconds, "value": value};
    var json = JsonUtils.toJson(map);
    MmkvUtils.instance.putString(cacheKey, value: json);
  }

  /// 清除缓存
  /// [cacheKey] 缓存key
  void clean({@required String cacheKey}) {
    MmkvUtils.instance.putString(cacheKey, value: "");
  }

  /// 获取有效数据
  /// [cacheKey] 缓存key
  Future<String> getString({@required String cacheKey}) async {
    var value = await MmkvUtils.instance.getString(cacheKey);
    if (value.isEmptyString || value.isEmptyJson) {
      return "";
    }
    var map = value.jsonToMap ?? {};
    if (!map.containsKey("startTime") || !map.containsKey("duration") || !map.containsKey("value")) {
      return "";
    }
    if (map["value"] is! String) {
      return "";
    }
    var content = map["value"] as String;
    if ((map["startTime"] is! int) || (map["duration"] is! int)) {
      return content;
    }
    var startTime = map["startTime"] as int;
    var duration = map["duration"] as int;
    if (startTime.isNullOrZero || duration.isNullOrZero) {
      return content;
    }
    var diff = System.currentTimeMillis - startTime;
    if (diff > duration) {
      MmkvUtils.instance.remove(cacheKey);
      return "";
    }
    return content;
  }
}
