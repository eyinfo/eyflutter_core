import 'package:eyflutter_core/lang/event/on_lang_config.dart';
import 'package:eyflutter_core/lang/lang_manager.dart';
import 'package:flutter/material.dart';

class LangLocalizationsDelegate extends LocalizationsDelegate<LangManager> {
  OnLangConfig _config;

  bool _isInitialization = false;

  LangLocalizationsDelegate() {
    _config = LangManager.instance.langConfig();
  }

  @override
  bool isSupported(Locale locale) {
    if (_config == null) {
      return false;
    }
    var langKeys = _config.supportLang()?.keys?.toList() ?? [];
    return langKeys.contains(locale.languageCode);
  }

  @override
  Future<LangManager> load(Locale locale) async {
    if (!_isInitialization) {
      LangManager.instance.setCurrentLocale(locale);
      var entry = await LangManager.instance.getLangMap(locale);
      LangManager.instance.setLangEntry(entry);

      _isInitialization = true;
    }
    return Future.value(LangManager.instance);
  }

  @override
  bool shouldReload(covariant LocalizationsDelegate<LangManager> old) {
    return false;
  }
}
