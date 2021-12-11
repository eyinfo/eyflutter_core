import 'dart:convert';

import 'package:crypto/crypto.dart';

import 'string_extension.dart';

/// https://pub.flutter-io.cn/packages/crypto
extension CryptoExtension on String {
  String get toSha256 {
    if (this.isEmptyString) {
      return "";
    }
    var bytes = utf8.encode(this);
    var digest = sha256.convert(bytes);
    return digest.toString();
  }

  // 对字符串进行md5加密
  String generateMd5() {
    if (this.isEmptyString) {
      return '';
    }
    return md5.convert(utf8.encode(this)).toString();
  }

  String get toSha1 {
    if (this.isEmptyString) {
      return "";
    }
    var bytes = utf8.encode(this);
    var digest = sha1.convert(bytes);
    return digest.toString();
  }
}
