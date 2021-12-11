import 'package:eyflutter_core/log/beans/log_event.dart';
import 'package:eyflutter_core/log/events/i_log_filter.dart';

class LogFilter implements ILogFilter {
  @override
  bool filterLog(LogEvent logEvent) {
    assert(() {
      if (logEvent == null || logEvent.level == null) {
        return false;
      }
      return true;
    }());
    return true;
  }
}
