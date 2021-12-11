import 'package:eyflutter_core/kit/timer/system.dart';

/// 检测事件是否快速点击
class ClickEvent {
  ClickEvent._();

  static int _intervalTime = 400;
  static int _lastClickTime = 0;

  static bool isFastClick() {
    var time = System.currentTimeMillis;
    var diff = time - _lastClickTime;
    if (diff > 0 && diff < _intervalTime) {
      return true;
    }
    _lastClickTime = time;
    return false;
  }
}
