import 'package:eyflutter_core/kit/sprintf/sprintf_impl.dart';
import 'package:eyflutter_core/kit/utils/set/list_extention.dart';

extension StringUtilsExtension on String? {
  bool get isEmptyString {
    return this?.isEmpty ?? true;
  }

  bool get isNotEmptyString {
    return this?.isNotEmpty ?? false;
  }

  ///字符串是否包含特定字符
  ///[params]待比较字符数组
  bool isContainsSub(List<String> params) {
    if (this.isEmptyString || params.isEmptyList) {
      return false;
    }
    for (var param in params) {
      if (this?.contains(param) ?? false) {
        return true;
      }
    }
    return false;
  }

  ///字符串开始是否包含特定字符
  ///[params]待比较字符
  bool isContainsStartsWith(List<String> params) {
    if (this.isEmptyString || params.isEmptyList) {
      return false;
    }
    for (var param in params) {
      if (this?.startsWith(param) ?? false) {
        return true;
      }
    }
    return false;
  }

  /// 判断对应的值是否与其中被比较的值相等
  /// [compareValues] 被比较值
  bool isInEquals(List<String> compareValues) {
    if (compareValues.isEmptyList) {
      return false;
    }
    return compareValues.contains(this);
  }

  ///截断字符
  ///text处理文体
  ///[length]最大长度(以字符计)
  ///[endChars]末尾显示字符
  ///[isAccurate]是否精确统计
  String ellipsize(int length, String endChars, bool isAccurate) {
    String text = this?.trim() ?? "";
    int len = text.length;
    int count = 0;
    StringBuffer builder = new StringBuffer();
    for (int i = 0; i < len; i++) {
      var c = text.substring(i, i + 1);
      if (isAccurate && RegExp("[\u4e00-\u9fa5]").hasMatch(c)) {
        count += 2;
      } else {
        count++;
      }
      if (count <= length) {
        builder.write(c);
      } else {
        break;
      }
    }
    if (len >= length) {
      builder.write(endChars);
    }
    return builder.toString().trim();
  }

  /// 截取2个字符串之间的字符串
  String substringByStr(String start, String end) {
    if (this.isEmptyString) {
      return '';
    }
    int startIndex = this?.indexOf(start) ?? 0;
    int endIndex = this?.indexOf(end, startIndex + start.length) ?? 0;
    return this?.substring(startIndex + start.length, endIndex) ?? "";
  }

  // 格式化字符串
  String format(List<String> args) {
    if (this.isEmptyString) {
      return '';
    }
    if (args.isEmptyList) {
      return this ?? "";
    }
    var printFormat = new PrintFormat();
    return printFormat(this ?? "", args);
  }

  /// 返回排除后缀名称
  String get withoutExtension {
    if (this == null) {
      return "";
    }
    int endIndex = this?.lastIndexOf(".") ?? 0;
    if (endIndex < 0) {
      return this ?? "";
    }
    return this?.substring(0, endIndex) ?? "";
  }

  /// 验证是否email格式
  bool get isEmail {
    String regex = "^[a-z0-9A-Z_.-]+[a-z0-9A-Z]@([a-z0-9A-Z]+([a-z0-9A-Z_-]+)?\\.)+[a-zA-Z]{2,}\$";
    if (this.isEmptyString) {
      return false;
    }
    return RegExp(regex).hasMatch(this ?? "");
  }

  double get toNumDouble {
    if (this.isEmptyString) {
      return 0;
    }
    try {
      return double.tryParse("$this") ?? 0;
    } catch (e) {
      return 0;
    }
  }

  int get toInt {
    if (this.isEmptyString) {
      return 0;
    }
    try {
      return int.tryParse("$this") ?? 0;
    } catch (e) {
      return 0;
    }
  }

  /// 转成当地时间
  DateTime toLocalTime() {
    if (this.isEmptyString) {
      return DateTime.now();
    }
    DateTime time = DateTime.parse(this ?? "").toLocal();
    return time;
  }
}
