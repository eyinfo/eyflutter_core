extension ObjectUtilsDynamicExtension on Object {
  /// 转double数据
  double get toDouble {
    if (this == null) {
      return 0;
    }
    try {
      if (this is double) {
        return this as double;
      } else {
        return double.tryParse("$this");
      }
    } catch (e) {
      return 0;
    }
  }
}
