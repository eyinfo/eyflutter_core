import 'package:eyflutter_core/kit/parts/config_manager.dart';
import 'package:eyflutter_core/kit/utils/json_utils.dart';
import 'package:eyflutter_core/kit/utils/map/map_extension.dart';
import 'package:eyflutter_core/kit/utils/set/list_extention.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/lang/event/on_lang_config.dart';
import 'package:eyflutter_core/mq/ebus/cloud_ebus.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show rootBundle;

class LangEntry {
  String code;
  Map<String, dynamic> map;

  LangEntry({this.code, this.map});
}

class LangManager {
  factory LangManager() => _getInstance();

  static LangManager get instance => _getInstance();
  static LangManager _instance;

  LangManager._internal();

  static LangManager _getInstance() {
    if (_instance == null) {
      _instance = new LangManager._internal();
    }
    return _instance;
  }

  /// 语言状态通知action
  String get stateAction => "9dd73c84f424446f94d6503e629174d8";

  /// lang type config key
  String get langTypeConfigKey => "2d94b1ab596a34e7";

  //当前语言
  Locale _currentLocale;
  LangEntry _langEntry;
  OnLangConfig _langConfig;

  //被加载的语言临时数据
  Map<String, String> _temMap = {};

  //托底默认语言
  Map<String, String> _defaultLang = {"code": "en", "country_code": "US"};

  /// 切换本地化语言
  void notifyAll(Locale locale) async {
    if (locale == null) {
      return;
    }
    _temMap.clear();
    _langEntry = await getLangMap(locale);
    CloudEBus.instance.dispatch(stateAction, params: locale);
  }

  void setLangEntry(LangEntry entry) {
    this._langEntry = entry;
  }

  Map<String, String> get enLang => _defaultLang;

  /// 获取语言配置对象
  OnLangConfig langConfig() {
    if (_langConfig == null) {
      var config = ConfigManager.instance.getConfig(langTypeConfigKey);
      if (config is OnLangConfig) {
        _langConfig = config;
      }
    }
    return _langConfig;
  }

  Locale _getCurrentLocale(OnLangConfig config, Locale locale) {
    if (locale?.languageCode?.isEmptyString ?? true) {
      _temMap.clear();
      if (config == null) {
        _currentLocale = Locale(_defaultLang["code"], _defaultLang["country_code"]);
      } else {
        var lang = config.defaultLang() ?? _defaultLang;
        _currentLocale = Locale(lang["code"], lang["country_code"]);
      }
    } else if (_currentLocale?.languageCode != locale?.languageCode) {
      //如果未找到则取配置中的默认语言
      if (config != null) {
        var supportLang = config.supportLang() ?? {};
        if (supportLang.containsKey(locale?.languageCode ?? "")) {
          _temMap.clear();
          _currentLocale = locale;
        }
      }
    }
    return _currentLocale;
  }

  void setCurrentLocale(Locale locale) {
    this._currentLocale = locale;
  }

  Future<LangEntry> getLangMap(Locale _locale) async {
    var config = langConfig();
    var locale = _getCurrentLocale(config, _locale);
    if (_langEntry != null && _langEntry.code == locale.languageCode && !_langEntry.map.isEmptyMap()) {
      return _langEntry;
    }
    var path = config?.langPackage(locale.languageCode) ?? "";
    if (path.isEmptyString) {
      _temMap.clear();
      return LangEntry();
    }
    try {
      //语言文件不存在则表示不支持语言加载默认语言
      return _loadAssetsLang(path, locale);
    } catch (e) {
      _temMap.clear();
      return _getDefaultLangEntry(config);
    }
  }

  Future<LangEntry> _loadAssetsLang(String path, Locale locale) async {
    String jsonLang = await rootBundle.loadString(path);
    var map = JsonUtils.fromJson(jsonLang);
    _langEntry = LangEntry(code: locale.languageCode, map: map);
    return _langEntry;
  }

  Future<LangEntry> _getDefaultLangEntry(OnLangConfig config) async {
    try {
      var lang = config.defaultLang() ?? _defaultLang;
      _currentLocale = Locale(lang["code"], lang["country_code"]);
      var path = config?.langPackage(_currentLocale.languageCode) ?? "";
      if (path.isEmptyString) {
        return LangEntry();
      }
      return _loadAssetsLang(path, _currentLocale);
    } catch (e) {
      return LangEntry();
    }
  }

  /// 获取对应语言内容
  /// [key] 语言关键信息key
  String value(String key) {
    if (key.isEmptyString) {
      return "";
    }
    if (_temMap.containsKey(key)) {
      return _temMap.getValue(key, "");
    }
    var _array = key.split(".") ?? [];
    var _dict = _langEntry?.map ?? {};
    var retValue = "";
    try {
      _array.forEach((item) {
        if (item == null || _dict[item].runtimeType == Null) {
          return;
        }
        if (_dict[item].runtimeType != String) {
          _dict = _dict[item];
        } else {
          retValue = _dict[item];
        }
      });
      if (retValue.isNotEmptyString) {
        _temMap[key] = retValue ?? "";
      }
      return retValue ?? "";
    } catch (e) {
      return "";
    }
  }

  /// 获取支持本地化语言
  List<Locale> get supportedLocale => langConfig()?.supportedLocale() ?? [];

  /// 判断本地语言是否支持
  /// [deviceLocale] 当前设备语言
  /// [supportedLocals] 设置的语言
  bool isSupported(Locale deviceLocale, List<Locale> supportedLocals) {
    if (deviceLocale == null || supportedLocale.isEmptyList) {
      return false;
    }
    bool flag = false;
    supportedLocale?.forEach((element) {
      if (element.languageCode == deviceLocale.languageCode) {
        flag = true;
        return;
      }
    });
    return flag;
  }

  /// 获取默认语言
  Locale defaultLang() {
    var config = langConfig();
    if (config == null) {
      return Locale(_defaultLang["code"], _defaultLang["country_code"]);
    }
    var defaultLang = config.defaultLang() ?? {};
    if (defaultLang["code"].isEmptyString) {
      return Locale(_defaultLang["code"], _defaultLang["country_code"]);
    }
    return Locale(defaultLang["code"], defaultLang["country_code"]);
  }
}
