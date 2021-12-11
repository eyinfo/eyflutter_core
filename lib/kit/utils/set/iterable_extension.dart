import 'package:path/path.dart' as path;

extension IterableUtilsExtension on Iterable {
  /// 组合地址，相对地址用/组合
  String get combineToPath {
    if (this == null || this.isEmpty) {
      return "";
    }
    return path.joinAll(this);
  }
}
