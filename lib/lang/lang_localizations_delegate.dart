import 'package:eyflutter_core/lang/lang_manager.dart';
import 'package:flutter/material.dart';

class LangLocalizationsDelegate extends LocalizationsDelegate<LangManager> {
  bool _isInitialization = false;

  @override
  bool isSupported(Locale locale) {
    return LangManager.instance.isSupported(locale);
  }

  @override
  Future<LangManager> load(Locale locale) async {
    if (!_isInitialization) {
      LangManager.instance.bindCurrentLocale(locale);
      _isInitialization = true;
    }
    return Future.value(LangManager.instance);
  }

  @override
  bool shouldReload(covariant LocalizationsDelegate<LangManager> old) {
    return false;
  }
}
