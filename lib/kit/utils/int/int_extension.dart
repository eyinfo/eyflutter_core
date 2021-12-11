import "package:intl/intl.dart";

extension IntUtilsExtension on int {
  /// 是否为空或0
  bool get isNullOrZero => this == null || this == 0;

  /// 减数
  int reduce(int reduction) {
    if (this == null) {
      return -reduction;
    }
    return this - reduction;
  }

  /// 加数
  int add(int number) {
    if (this == null) {
      return number;
    }
    return this + number;
  }

  /// 转成当地时间
  DateTime toLocalTime({bool isutc = false}) {
    if (this.isNullOrZero) {
      return null;
    }
    int mstime = this;
    if ("$this".length == 10) {
      mstime = this * 1000;
    }
    DateTime time = DateTime.fromMillisecondsSinceEpoch(mstime, isUtc: isutc).toLocal();
    return time;
  }

  String toFormatString({String format = "#.##"}) {
    final oCcy = new NumberFormat(format);
    return oCcy.format(this);
  }
}
