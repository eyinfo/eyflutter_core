/// 不支持dynamic本身扩展
extension DynamicUtilsDynamicExtension on dynamic {
  /// 判断字符标识是否true(eg. true or 1)
  bool get isTrue {
    if (this == null) {
      return false;
    }
    if (this is bool) {
      return this == true;
    }
    if (this is String) {
      return this.toString().toLowerCase().trim() == 'true';
    }
    if (this is num) {
      return this == 1;
    }
    return false;
  }

  /// 获取【枚举】名称
  String get name {
    if (this == null) {
      return "";
    }
    var value = this.toString();
    var start = value.indexOf(".");
    if (start < 0) {
      return "";
    }
    return value.substring(start + 1);
  }

  /// 判断是否为数字类型
  bool get isNumeric {
    if (this == null) {
      return false;
    }
    if (this is String) {
      var value = this as String;
      return double.tryParse(value) != null;
    } else if (this is num) {
      return true;
    }
    return false;
  }

  /// dynamic类型转string
  String get string {
    if (this is! String) {
      return "";
    }
    return (this as String).trim();
  }
}
