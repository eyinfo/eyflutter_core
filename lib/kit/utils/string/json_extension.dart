import 'dart:collection';
import 'dart:convert';

import 'package:eyflutter_core/kit/utils/string/string_extension.dart';

extension JsonUtilsExtension on String {
  /// json中是否包含指定的键
  /// [keyName]键名称
  bool containerKey(String keyName) {
    if (keyName.isEmptyString) {
      return false;
    }
    if (this.isEmptyJson) {
      return false;
    }
    String regex = "(((\"" + keyName + "\")|('" + keyName + "')):(.*?)((\\,|\\})(\\s\\S)*))";
    var hasMatch = new RegExp(regex).hasMatch(this);
    return hasMatch;
  }

  /// json to map
  Map<String, dynamic> get jsonToMap {
    if (this.isEmptyJson) {
      return new HashMap<String, dynamic>();
    }
    try {
      return json.decode(this);
    } catch (e) {
      return new HashMap<String, dynamic>();
    }
  }

  ///检测json字符串是否为空
  bool get isEmptyJson {
    if (this.isEmptyString) {
      return true;
    }
    //这里用this.replaceAll('\\r|\\n|\\s|\\t','')可能会失败
    String json = this;
    ['\r', '\n', '\t', '\s', '\S'].forEach((element) {
      json = json.replaceAll(element, "");
    });
    //判断是否为json格式{...}或[...]
    String regex = "^(\\{(.+)*\\})\$|^(\\[(.+)*\\])\$";
    if (new RegExp(regex).hasMatch(json)) {
      //如果对象直接包含数组如{["id",3,"name":"名称"]}
      regex = "^(\\{\\[)(.+)(\\]\\})\$";
      if (new RegExp(regex).hasMatch(json)) {
        return false;
      } else {
        //如果数组中包含对象
        regex = "^(\\[\\{)(.+)(\\}\\])\$";
        if (new RegExp(regex).hasMatch(json)) {
          return false;
        } else {
          int length = json.replaceAll(" ", "").trim().length;
          if (length > 2) {
            return false;
          } else {
            return true;
          }
        }
      }
    } else {
      return true;
    }
  }
}
