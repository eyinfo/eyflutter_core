extension SetUtilsSetExtension<T> on Set<T> {
  /// 判断是否空集合
  bool get isEmptySet {
    return this == null || this.isEmpty;
  }
}
