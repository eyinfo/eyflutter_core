import 'package:eyflutter_core/log/beans/log_response.dart';

/// 解决并发情况下日志输出堵塞问题

typedef Future<LogResponse> LogFunctionNestedCall();

class LogFunctionNested {
  final LogFunctionNestedCall _nestedCall;

  LogFunctionNested(this._nestedCall);

  Future<LogResponse> perform() {
    return _nestedCall();
  }
}
