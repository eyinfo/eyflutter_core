import 'package:eyflutter_core/lang/beans/lang_config_entry.dart';
import 'package:flutter/material.dart';

mixin OnLangConfig {
  /// 语言配置列表
  List<LangConfigEntry> langEntries();

  /// 支持本地化语言
  List<Locale> supportedLocale();
}
