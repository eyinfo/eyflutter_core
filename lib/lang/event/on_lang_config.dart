import 'package:flutter/material.dart';

mixin OnLangConfig {
  //支持的语言
  // Map<String, dynamic> supportLang = {
  //     "en": {"code": "en", "country_code": "US"},
  //   };
  Map<String, dynamic> supportLang();

  //默认语言
  // dynamic defaultLang = {"code": "en", "country_code": "US"};
  Map<String, String> defaultLang();

  //支持本地化语言
  List<Locale> supportedLocale();

  //语言包路径
  //code:语言编码
  String langPackage(String code);
}
