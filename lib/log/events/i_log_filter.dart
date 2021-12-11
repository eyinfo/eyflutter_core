
import 'package:eyflutter_core/log/beans/log_event.dart';

mixin ILogFilter {
  bool filterLog(LogEvent logEvent);
}
