import 'package:eyflutter_core/log/beans/log_event.dart';
import 'package:eyflutter_core/log/log_function_nested.dart';

mixin ILogPrinter {
  LogFunctionNested log(LogEvent logEvent);
}
