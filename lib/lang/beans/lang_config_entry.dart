import 'package:eyflutter_core/eyflutter_core.dart';

/// Author lijinghuan
/// Email:ljh0576123@163.com
/// CreateTime:2021-12-24
/// Description:Generated file. Do not edit.
/// Modifier:
/// ModifyContent:

class LangConfigEntry {
  /// 语言编码
  String langCode;

  /// 国家编码
  String countryCode;

  /// 是否默认语言
  bool isDefault;

  /// 语言包标识
  String unique;

  LangConfigEntry({this.langCode = '',this.countryCode = '',this.isDefault = false,this.unique = '', Map<String, dynamic>? json});

  factory LangConfigEntry.fromJson(Map<String,dynamic> json) => _$LangConfigEntryFromJson(json);

  Map<String,dynamic> toJson() => _$LangConfigEntryToJson(this);
}

LangConfigEntry _$LangConfigEntryFromJson(Map<String, dynamic> json) {
  return LangConfigEntry(
      langCode:(json['langCode'] is String)?((json['langCode'] ?? '') as String):'',
      countryCode:(json['countryCode'] is String)?((json['countryCode'] ?? '') as String):'',
      isDefault:(json['isDefault'] is String)?("${json['isDefault']}").isTrue:(json['isDefault'] ?? false) as bool,
      unique:(json['unique'] is String)?((json['unique'] ?? '') as String):'', json: json);
}

Map<String, dynamic> _$LangConfigEntryToJson(LangConfigEntry instance) {
  final val = <String, dynamic>{};
  val['langCode'] = instance.langCode;
  val['countryCode'] = instance.countryCode;
  val['isDefault'] = instance.isDefault;
  val['unique'] = instance.unique;
  return val;
}

//define_fields:{"LangConfigEntry": {"dart_file_name": {"name": "lang_config_entry", "type": "", "subType": "object", "childClassName": "", "defaultValue": "", "describe": ""}, "langCode": {"name": "langCode", "type": "string", "subType": "", "childClassName": "", "defaultValue": "", "describe": "\u8bed\u8a00\u7f16\u7801"}, "countryCode": {"name": "countryCode", "type": "string", "subType": "", "childClassName": "", "defaultValue": "", "describe": "\u56fd\u5bb6\u7f16\u7801"}, "isDefault": {"name": "isDefault", "type": "bool", "subType": "", "childClassName": "", "defaultValue": "", "describe": "\u662f\u5426\u9ed8\u8ba4\u8bed\u8a00"}, "unique": {"name": "unique", "type": "string", "subType": "", "childClassName": "", "defaultValue": "", "describe": "\u8bed\u8a00\u5305\u6807\u8bc6"}}}