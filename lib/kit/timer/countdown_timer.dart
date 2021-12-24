import 'dart:async';

typedef CountdownCallback = void Function(int days, int hours, int minutes, int seconds);

class CountdownTimer {
  Timer? _timer;

  //以下单位按毫秒计
  int _secondUnit = 1000;
  int _minuteUnit = 60000;
  int _hourUnit = 3600000;
  int _dayUnit = 86400000;

  bool _isFinish = true;

  /// 开始倒计时
  /// [startTime] 开始时间(单位毫秒)
  /// [endTime] 结束时间(单位毫秒)
  /// [callback] 回调
  void start(int startTime, int endTime, CountdownCallback callback) {
    if (startTime < 0 || endTime < 0 || startTime > endTime) {
      return;
    }
    startCountdown(endTime - startTime, callback);
  }

  /// 开始倒计时
  /// [restOfTime] 剩余时间(单位毫秒)
  /// [callback] 回调(null时不做计时处理)
  void startCountdown(int restOfTime, CountdownCallback? callback) {
    if (restOfTime <= 0 || callback == null) {
      return;
    }
    stop();
    _isFinish = false;
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (restOfTime > 0) {
        restOfTime -= 1000;
        int diff = restOfTime;
        int days = diff ~/ _dayUnit;
        diff = diff - days * _dayUnit;
        int hours = diff ~/ _hourUnit;
        diff = diff - hours * _hourUnit;
        int minute = diff ~/ _minuteUnit;
        diff = diff - minute * _minuteUnit;
        int second = diff ~/ _secondUnit;
        if (days == 0 && hours == 0 && minute == 0 && second == 0) {
          stop();
        }
        callback(days, hours, minute, second);
      } else {
        stop();
        if (!_isFinish) {
          callback(0, 0, 0, 0);
        }
      }
    });
  }

  /// 停止倒计时
  void stop() {
    if (_timer?.isActive ?? false) {
      _timer?.cancel();
    }
    _timer = null;
    _isFinish = true;
  }
}
