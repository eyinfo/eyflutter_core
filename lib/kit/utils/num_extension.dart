extension NumUtilsExtension on num? {
  /// 判断字符标识是否true(eg. true or 1)
  bool get isNumTrue {
    if (this == null) {
      return false;
    }
    if (this is num) {
      return this == 1;
    }
    return false;
  }

  /// 判断是否为数字类型
  bool get isNumNumeric {
    if (this == null) {
      return false;
    }
    if (this is num) {
      return true;
    }
    return false;
  }

  /// 转为double类型
  double get toNumDouble {
    if (this == null) {
      return 0;
    }
    try {
      if (this is double) {
        return this as double;
      } else {
        return double.tryParse("$this") ?? 0;
      }
    } catch (e) {
      return 0;
    }
  }

  /// 转为int类型
  int get toNumInt {
    if (this == null) {
      return 0;
    }
    try {
      if (this is num) {
        return (this ?? 0) ~/ 1;
      } else {
        return (num.tryParse("$this") ?? 0) ~/ 1;
      }
    } catch (e) {
      return 0;
    }
  }

  /// 是否为空或0
  bool get isNullOrZero => this == null || this == 0;

  /// 减数
  num reduce(num reduction) {
    if (this == null) {
      return -reduction;
    }
    return this ?? 0 - reduction;
  }

  /// 加数
  num add(num number) {
    if (this == null) {
      return number;
    }
    return this ?? 0 + number;
  }
}
