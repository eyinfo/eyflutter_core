import 'package:flutter/services.dart';

class ClipboardUtils {
  ClipboardUtils._();

  /// 获取剪贴板数据
  static Future<String> getClipboardData() async {
    var clipboardData = await Clipboard.getData(Clipboard.kTextPlain);
    if (clipboardData == null) {
      return Future.value("");
    }
    return Future.value(clipboardData.text);
  }

  /// 设置剪贴板数据
  /// [text]复制到剪贴板的内容
  static void setClipboardData(String text) {
    ClipboardData data = new ClipboardData(text: text);
    Clipboard.setData(data);
  }
}
