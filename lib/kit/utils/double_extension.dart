import "package:intl/intl.dart";
extension DoubleUtilsExtension on double {
  int get doubleInt {
    return this != null ? this.toInt() : 0;
  }

  /// 是否为空或0
  bool get isNullOrZero => this == null || this == 0;

  /// 减数
  double reduce(double reduction) {
    if (this == null) {
      return -reduction;
    }
    return this - reduction;
  }

  /// 加数
  double add(double number) {
    if (this == null) {
      return number;
    }
    return this + number;
  }

  String toFormatString({String format = "#.##"}){
    final oCcy = new NumberFormat(format);
    return oCcy.format(this);
  }
}
