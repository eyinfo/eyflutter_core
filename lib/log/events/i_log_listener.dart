
import 'package:eyflutter_core/log/beans/log_event.dart';

/// 日志监听
mixin ILogListener {
  void onLogListener(Future<LogEvent> logFuture);
}
