import 'package:eyflutter_core/kit/parts/keep_manager.dart';
import 'package:eyflutter_core/kit/utils/json_utils.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/storage/memory_utils.dart';
import 'package:eyflutter_core/storage/mmkv_utils.dart';
import 'package:flutter/material.dart';

class LangStorage {
  factory LangStorage() => _getInstance();

  static LangStorage get instance => _getInstance();
  static LangStorage? _instance;

  LangStorage._internal();

  static LangStorage _getInstance() {
    return _instance ??= new LangStorage._internal();
  }

  String _currLangCacheKey = "13e476583b25fe72";

  /// 缓存当前语言
  void cacheCurrentLang(Locale? locale) async {
    Map<String, String> map = {};
    map["code"] = locale?.languageCode ?? "";
    map["country_code"] = locale?.countryCode ?? "";
    String langJson = JsonUtils.toJson(map);
    KeepManager.instance.perform(
        params: langJson,
        function: (params) async {
          var response = await MmkvUtils.instance.putString(_currLangCacheKey, value: params);
          if (response.isNotEmptyString) {
            MemoryUtils.instance.set(_currLangCacheKey, response);
            return true;
          }
          return false;
        });
  }

  /// 初始化加载缓存语言
  void loadLang() {
    KeepManager.instance.perform(
        params: "",
        maxCount: 3,
        function: (params) async {
          var response = await MmkvUtils.instance.getString(_currLangCacheKey);
          if (response.isNotEmptyString) {
            MemoryUtils.instance.set(_currLangCacheKey, response);
            return true;
          }
          return false;
        });
  }

  /// 获取当前语言
  Locale? getCurrentLangCode() {
    var langJson = MemoryUtils.instance.getString(_currLangCacheKey);
    if (langJson.isEmptyString) {
      return null;
    }
    var map = JsonUtils.fromJson(langJson);
    String code = map["code"];
    String countryCode = map["country_code"];
    if (code.isEmptyString || countryCode.isEmptyString) {
      return null;
    }
    return Locale(code, countryCode);
  }

  /// 临时语言缓存(启动时默认语言缓存)
  void cacheMemoryLang(String langJson) {
    MemoryUtils.instance.set(_currLangCacheKey, langJson);
  }
}
