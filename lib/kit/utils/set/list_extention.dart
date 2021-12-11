extension ListUtilsExtension<T> on List<T> {
  /// 判断list是否为空
  bool get isEmptyList {
    return this == null || this.isEmpty;
  }

  bool get isNotEmptyList {
    return !this.isEmptyList;
  }

  /// 判断列表是否包含有空值
  bool get hasEmptyElement {
    if (this.isEmptyList) {
      return true;
    }
    return this.contains("") || this.contains(null);
  }

  List<T> get copy {
    List<T> copyList = [];
    this.forEach((T element) {
      copyList.add(element);
    });
    return copyList;
  }
}
