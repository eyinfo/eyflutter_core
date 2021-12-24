import 'dart:convert';

import 'package:eyflutter_core/kit/utils/string/json_extension.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/log/logger.dart';

class JsonUtils {
  JsonUtils._();

  /// 对象转json
  /// [obj]需要转换的对象
  static String toJson(Object? obj) {
    if (obj == null) {
      return "";
    }
    try {
      return json.encode(obj);
    } catch (e) {
      Logger.instance.error(e);
      return "";
    }
  }

  static Map<String, dynamic> fromJson(String data) {
    if (data.isEmptyJson) {
      return {};
    }
    try {
      return json.decode(data);
    } catch (e) {
      Logger.instance.error(e);
      return {};
    }
  }

  static List fromJsonList(String data) {
    if (data.isEmptyJson) {
      return [];
    }
    try {
      return json.decode(data);
    } catch (e) {
      Logger.instance.error(e);
      return [];
    }
  }

  static String getValue({String keyName = "", String json = "", bool isFillWhitespace = false}) {
    if (keyName.isEmptyString || json.isEmptyString) {
      return "";
    }
    if (isFillWhitespace == true) {
      json = json.replaceAll("\\r|\\n|\\s|\\t", "");
    }
    String regex = "((\"" +
        keyName +
        "\")|('" +
        keyName +
        "')):(((\\[(.+)\\](\\,|\\}))|(\\{(.+)\\}(\\,|\\})))|((.*?)((\\,|\\})(\\s\\S)*)))";
    var matcher = RegExp(regex, multiLine: true);
    var matches = matcher.allMatches(json);
    String value = "";
    matches.forEach((element) {
      //避免null出错
      value = element.groupCount > 0 ? (element.group(0) ?? "").trim() : "";
      //根据:分隔
      int index = value.indexOf(":");
      if (index >= 0 && (index + 1) < value.length) {
        value = value.substring(index + 1).trim();
        int start = 0;
        if (value.startsWith("\"") || value.startsWith("'")) {
          start = 1;
        }
        //去掉前面引号
        //去掉最后一个字符包含的,或}
        value = value.substring(start, value.length - 1);
        value = value.trim();
        //去掉后面引号
        int end = value.length;
        if (value.endsWith("\"") || value.endsWith("'")) {
          end -= 1;
        }
        value = value.substring(0, end);
        return;
      }
    });
    return value;
  }
}
