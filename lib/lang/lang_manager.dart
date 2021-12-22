import 'package:eyflutter_core/eyflutter_core.dart';
import 'package:eyflutter_core/kit/parts/config_manager.dart';
import 'package:eyflutter_core/kit/utils/json_utils.dart';
import 'package:eyflutter_core/kit/utils/map/map_extension.dart';
import 'package:eyflutter_core/kit/utils/set/list_extention.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/lang/beans/lang_config_entry.dart';
import 'package:eyflutter_core/lang/beans/lang_entry.dart';
import 'package:eyflutter_core/lang/event/on_lang_change_state.dart';
import 'package:eyflutter_core/lang/event/on_lang_config.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show rootBundle;

class LangManager {
  factory LangManager() => _getInstance();

  static LangManager get instance => _getInstance();
  static LangManager _instance;

  static LangManager _getInstance() {
    if (_instance == null) {
      _instance = new LangManager._internal();
    }
    return _instance;
  }

  /// lang type config key
  String get _langTypeConfigKey => "2d94b1ab596a34e7";

  //当前语言
  Locale _currentLocale;
  Map<String, dynamic> _textMap = {};
  OnLangConfig _langConfig;
  Map<String, LangEntry> _supportLangMap = {};
  bool _isUpdateSupportLang = false;
  LangEntry _defaultLang;
  LangEntry _tempDefaultLang;
  Map<int, OnLangChangeState> _langStateDispatchs = {};
  List<Locale> _supportLocales = [];
  LangEntry _currentLang;
  List<LangConfigEntry> _configEntries;
  LangConfigEntry _cacheConfigEntry;
  String _langData;

  LangManager._internal() {
    _defaultLang = LangEntry(langCode: "zh", countryCode: "CN");
    _reset();
  }

  void addChangeState(int hashCode, OnLangChangeState langState) {
    _langStateDispatchs[hashCode] = langState;
  }

  void removeChangeState(int hashCode) {
    _langStateDispatchs.remove(hashCode);
  }

  /// region 切换本地化语言
  void notifyAll(Locale locale) async {
    if (locale == null) {
      return;
    }
    _reset();
    await getLangData(locale);
    _langStateDispatchs.forEach((key, value) {
      value.dispatchLangState(locale);
    });
  }

  void _reset() {
    _textMap.clear();
    _currentLocale = null;
    _currentLang = null;
    _cacheConfigEntry = null;
    _langData = "";
  }

  /// endregion

  /// region 注册配置项

  List<LangConfigEntry> _getLangEntries() {
    if (_configEntries == null) {
      var config = langConfig();
      _configEntries = config.langEntries() ?? [];
    }
    return _configEntries;
  }

  /// [config] OnLangConfig实现类
  void registerConfig<T extends OnLangConfig>(T config) {
    if (config == null) {
      return;
    }
    ConfigManager.instance.addConfig(_langTypeConfigKey, config);
    var langEntries = _getLangEntries();
    if (langEntries?.length ?? 0 == _supportLangMap.length) {
      _isUpdateSupportLang = false;
    } else {
      _isUpdateSupportLang = true;
    }
  }

  /// endregion

  /// region 获取语言配置对象
  OnLangConfig langConfig() {
    if (_langConfig == null) {
      var config = ConfigManager.instance.getConfig(_langTypeConfigKey);
      if (config is OnLangConfig) {
        _langConfig = config;
      }
    }
    return _langConfig;
  }

  /// endregion

  /// region 获取支持的语言
  bool _checkSupportLang() {
    if (_isUpdateSupportLang || _supportLangMap.isEmptyMap()) {
      _isUpdateSupportLang = true;
    }
    return _isUpdateSupportLang;
  }

  Map<String, LangEntry> _supportLang() {
    if (!_checkSupportLang()) {
      _isUpdateSupportLang = false;
      return _supportLangMap;
    }
    var langEntries = _getLangEntries();
    if (langEntries.isEmptyList) {
      _isUpdateSupportLang = true;
      return {};
    }
    _supportLangMap.clear();
    langEntries.forEach((element) {
      _supportLangMap[element.langCode] = LangEntry(langCode: element.langCode, countryCode: element.countryCode);
    });
    _isUpdateSupportLang = false;
    return _supportLangMap;
  }

  Map<String, LangEntry> get supportLang => _supportLang();

  /// endregion

  /// region 获取默认语言

  LangEntry _getDefaultLang() {
    if (_tempDefaultLang != null) {
      return _tempDefaultLang;
    }
    var langEntries = _getLangEntries();
    if (langEntries.isEmptyList) {
      return _defaultLang;
    }
    langEntries.forEach((element) {
      if (element.isDefault ?? false) {
        _tempDefaultLang = LangEntry(langCode: element.langCode, countryCode: element.countryCode);
        return;
      }
    });
    return _tempDefaultLang;
  }

  LangEntry get defaultLang => _getDefaultLang();

  /// endregion

  /// region 获取当前被使用的语言

  Locale _getCurrentLocale(OnLangConfig config, Locale locale) {
    if ((locale?.languageCode?.isEmptyString ?? true) || config == null) {
      var defaultLang = _getDefaultLang();
      _currentLocale = Locale(defaultLang.langCode, defaultLang.countryCode);
    } else if (_currentLocale?.languageCode != locale?.languageCode) {
      var langEntries = _getLangEntries();
      langEntries?.forEach((element) {
        if (element.langCode == locale?.languageCode) {
          _currentLocale = locale;
          return;
        }
      });
      if (_currentLocale == null) {
        var defaultLang = _getDefaultLang();
        _currentLocale = Locale(defaultLang.langCode, defaultLang.countryCode);
      }
    }
    return _currentLocale;
  }

  Locale currentLocale(Locale locale) {
    return _getCurrentLocale(langConfig(), locale);
  }

  LangEntry _getCurrentLang() {
    if (_currentLang == null) {
      if (_currentLocale != null) {
        _currentLang = LangEntry(langCode: _currentLocale.languageCode, countryCode: _currentLocale.countryCode);
      } else {
        var lang = defaultLang;
        _currentLang = LangEntry(langCode: lang.langCode, countryCode: lang.countryCode);
      }
    }
    return _currentLang;
  }

  LangEntry get currentLang => _getCurrentLang();

  /// endregion

  /// region 绑定本地语言
  void bindCurrentLocale(Locale locale) async {
    await getLangData(locale);
    var config = langConfig();
    _getCurrentLocale(config, locale);
  }

  /// endregion

  /// region 本地化语言
  /// 获取支持本地化语言
  List<Locale> _getSupportLocales() {
    if (_supportLocales.isNotEmptyList) {
      return _supportLocales;
    }
    var entries = supportLang?.entries;
    entries?.forEach((element) {
      var entry = element.value;
      _supportLocales.add(Locale(entry.langCode, entry.countryCode));
    });
    return _supportLocales;
  }

  List<Locale> get supportedLocale => _getSupportLocales();

  /// 判断本地语言是否支持
  /// [deviceLocale] 当前设备语言
  /// [supportedLocals] 设置的语言
  bool isSupported(Locale deviceLocale) {
    var locales = supportedLocale;
    if (deviceLocale == null || locales.isEmptyList) {
      return false;
    }
    bool flag = false;
    locales?.forEach((element) {
      if (element.languageCode == deviceLocale.languageCode) {
        flag = true;
        return;
      }
    });
    return flag;
  }

  /// endregion

  /// 获取语言数据
  Future<String> getLangData(Locale _locale) async {
    if (_langData.isNotEmptyString) {
      return _langData;
    }
    var configLang = _getCacheConfigLang();
    if (configLang?.unique?.isEmptyString ?? true) {
      return "";
    }
    _langData = await MmkvUtils.instance.getString(configLang.unique);
    return _langData;
  }

  /// region 获取对应语言内容
  LangConfigEntry _getCacheConfigLang() {
    if (_cacheConfigEntry == null) {
      var lang = currentLang;
      var entries = _getLangEntries();
      _cacheConfigEntry = entries?.firstWhere((element) => element.langCode == lang.langCode);
    }
    return _cacheConfigEntry;
  }

  void _parsingData() {
    if (_langData.isEmptyJson) {
      return;
    }
    var map = JsonUtils.fromJson(_langData);
    if (map.isEmptyMap()) {
      return;
    }
    _textMap.addAll(map);
  }

  /// [key] 语言关键信息key
  String value(String key) {
    if (key.isEmptyString) {
      return "";
    }
    if (_textMap.isEmptyMap()) {
      _parsingData();
    }
    if (_textMap.containsKey(key)) {
      var text = _textMap.getValue<String>(key);
      if (text.isNotEmptyString) {
        return text;
      }
    }
    var _array = key.split(".") ?? [];
    var _dict = _textMap ?? {};
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
      return retValue ?? "";
    } catch (e) {
      return "";
    }
  }

  /// endregion
}
