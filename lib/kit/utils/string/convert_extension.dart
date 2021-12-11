import 'dart:collection';

import 'package:eyflutter_core/kit/utils/set/list_extention.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import "package:intl/intl.dart";

extension ConvertUtilsStringExtension on String {
  /// 字符串参数转map对象
  MapEntry<String, dynamic> toMapEntry(String splitChar, bool urlDecode) {
    if (this.isEmptyString) {
      return null;
    }
    if (splitChar.isEmptyString) {
      return MapEntry(this, null);
    }
    List<String> array = this.split(splitChar);
    String value = "";
    if (array.length > 1) {
      value = urlDecode ? Uri.decodeComponent(array[1]) : array[1];
    }
    return MapEntry(array[0], value);
  }

  /// 根据连接字符拼接的字符串转换成map
  /// 示例:"siteId=2&positionValue=71"
  /// [firstSplit]一级分隔符
  /// [secondSplit]二级分隔符
  /// [isDecode]value是否需要Uri.decodeComponent解码
  Map<String, dynamic> toMap(String firstSplit, {String secondSplit = "", bool isDecode}) {
    if (this.isEmptyString || firstSplit.isEmptyString) {
      return new LinkedHashMap<String, dynamic>();
    }
    var items =
        this.split(firstSplit).map((e) => e.toMapEntry(secondSplit, isDecode)).where((element) => element != null);
    return LinkedHashMap.fromEntries(items);
  }

  /// 根据连接字符拼接的字符串转换成list
  /// 示例:"2&71&3&25"
  /// [connector]连接符
  /// [isDecode]value是否需要Uri.decodeComponent解码
  List<String> toList(String connector, {bool isDecode}) {
    if (connector.isEmptyString || this == null) {
      return this == null ? [] : [this];
    }
    return this.split(connector);
  }

  /// 根据标识位以连接符组成的字符串解析为map
  /// (可能存在非url参数的情况)
  /// [startChar] 解析该字符串之后的参数
  /// [firstSplit]一级分隔符
  /// [secondSplit]二级分隔符
  /// [isDecode]value是否需要Uri.decodeComponent解码
  Map<String, dynamic> toMapByIdentify(String startChar, String firstSplit, {String secondSplit = "", bool isDecode}) {
    if (this.isEmptyString) {
      return {};
    }
    String params = "";
    if (!startChar.isEmptyString) {
      int index = this.indexOf(startChar);
      if (index < 0) {
        return {};
      }
      params = this.substring(index + startChar.length);
    }
    return params.toMap(firstSplit, secondSplit: secondSplit, isDecode: isDecode);
  }

  /// url参数合并(referenceUrl参数向targetUrl合并)
  /// [targetUrl]合并参数后的url连接
  String toMergeUrl({String targetUrl = ""}) {
    if ([targetUrl, this].hasEmptyElement) {
      return targetUrl;
    }
    var targetUri = Uri.parse(targetUrl);
    var refUri = Uri.parse(this);
    var parameters = refUri.queryParameters;
    if (parameters == null || parameters.isEmpty) {
      return targetUrl;
    }
    //原uri->map不能修改
    Map<String, dynamic> paramsMap = {};
    paramsMap.addAll(targetUri.queryParameters);
    parameters.forEach((String key, dynamic value) {
      if (!paramsMap.containsKey(key)) {
        paramsMap[key] = value;
      }
    });
    var replace = targetUri.replace(queryParameters: paramsMap);
    return replace.toString();
  }

  /// 数字字符转换为num(eg. for long float)
  /// [defaultValue]默认值
  num toNum({num defaultValue = 0}) {
    if (this == null) {
      return defaultValue;
    }
    return num.tryParse(this) ?? defaultValue;
  }

  /// 金额格式化
  String toFormatString({String format = "#.##"}) {
    if (this.isEmptyString) {
      return this;
    }
    String regex = "^((([0-9]+|0).([0-9]{1,2}))\$)|^(([1-9]+).([0-9]{1,2})\$)";
    if (RegExp(regex).hasMatch(this)) {
      final oCcy = new NumberFormat(format);
      return oCcy.format(this.toNumDouble);
    } else {
      return this;
    }
  }
}
