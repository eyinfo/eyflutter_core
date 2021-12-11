import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:path/path.dart' as path;

import '../map/map_extension.dart';

extension UrlUtilsExtension on String {
  String getUrl(String baseUrl) {
    if (this.isEmptyString) {
      return baseUrl;
    }
    if (this.isContainsStartsWith(["http://", "https://"])) {
      return this;
    }
    var join = path.join(baseUrl, this);
    return join;
  }

  /// 验证是否url地址
  bool get isUrl {
    var regex =
        RegExp(r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\(\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+', caseSensitive: false);
    return regex.hasMatch(this);
  }

  /// 追加url参数
  /// [params] 将添加的url参数
  String appendUrlParams(Map<String, dynamic> params) {
    if (params.isEmptyMap()) {
      return this;
    }
    var index = this.indexOf("?");
    if (index == 0) {
      return this;
    }
    var paramsSb = StringBuffer();
    var pos = 0;
    params.forEach((key, value) {
      paramsSb.write("$key=$value");
      if ((pos + 1) < params.length) {
        paramsSb.write("&");
      }
      pos++;
    });
    if (index > 0) {
      return "${this}&${paramsSb.toString()}";
    } else {
      return "${this}?${paramsSb.toString()}";
    }
  }
}
