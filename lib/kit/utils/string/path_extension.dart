import 'package:eyflutter_core/kit/utils/set/list_extention.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:path/path.dart' as p;

extension PathUtilsExtension on String {
  /// 获取地址扩展名
  String get suffixName {
    if (this.isEmptyString) {
      return "";
    }
    var extension = p.extension(this) ?? "";
    var index = extension.indexOf("?");
    if (index >= 0) {
      extension = extension.substring(0, index);
    }
    return extension.startsWith(".") ? extension.substring(1) : extension;
  }

  /// 获取路径文件名(test.dart)
  String get getName {
    if (this.isEmptyString) {
      return "";
    }
    var basename = p.basename(this);
    return basename ?? "";
  }

  /// 包含前后斜线(输入示例:/path0/path1)
  /// eg. /path0/path1/
  String get appendSlash {
    if (this.isEmptyString) {
      return "";
    }
    String path = this.replaceAll("\\r|\\n|\\t", this);
    bool start = false, end = false;
    if (!path.startsWith("/")) {
      start = true;
    }
    if (!path.endsWith("/")) {
      end = true;
    }
    if (start || end) {
      path = "${start ? "/" : ""}$path${end ? "/" : ""}";
    }
    return path;
  }

  /// 替换最后路径名
  /// (示例:http://xxxx/xx/[name?...|name2 -> newName[?...]])
  /// [replacePathName]替换路径名
  String replaceLastPathName(String replacePathName) {
    if ([this, replacePathName].hasEmptyElement) {
      return "";
    }
    Uri uri = Uri.parse(this);
    List<String> segments = List.from(uri.pathSegments);
    segments[segments.length - 1] = replacePathName;
    return uri.replace(pathSegments: segments).toString();
  }

  /// 追加参数
  String appendParams({String key, String value}) {
    if (key.isEmptyString) {
      return this;
    }
    if (this.isEmptyString) {
      return "$key=$value";
    }
    Uri uri = Uri.parse(this);
    Map<String, String> parameters = {};
    parameters.addAll(uri.queryParameters);
    parameters[key] = value;
    return uri.replace(queryParameters: parameters).toString();
  }

  /// 追加路径
  String appendPath(String path) {
    if (path.isEmptyString) {
      return this;
    }
    List<String> paths = [];
    Uri uri = Uri.parse(this);
    var segments = uri.pathSegments;
    if (segments.isNotEmptyList) {
      segments.forEach((element) {
        if (element.isNotEmptyString) {
          paths.add(element);
        }
      });
    }
    var lst = path.split('/');
    lst.forEach((element) {
      if (element.isNotEmptyString) {
        paths.add(element);
      }
    });
    return uri.replace(pathSegments: paths).toString();
  }
}
