import 'package:eyflutter_core/kit/timer/system.dart';

class CycleUtils {
  /// 执行过程中时间间隔超过特定时间后才做回调
  /// [millisecond] 间隔时间
  /// [startTime] 执行开始时间
  /// [call] 回调函数
  void perform({int millisecond = 0, int startTime = 0, Function? call, Function? otherwise}) {
    if (startTime < 0 || millisecond < 0 || call == null) {
      if (otherwise != null) {
        otherwise();
      }
      return;
    }
    if (millisecond >= startTime) {
      if (otherwise != null) {
        otherwise();
      }
      return;
    }
    int diff = System.currentTimeMillis - startTime;
    if (diff >= millisecond) {
      call();
    } else {
      if (otherwise != null) {
        otherwise();
      }
    }
  }
}
