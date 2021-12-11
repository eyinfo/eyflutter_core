import 'package:eyflutter_core/log/events/i_log_filter.dart';
import 'package:eyflutter_core/log/events/i_log_listener.dart';

/// logger parameter config
class LoggerParameter {
  /// 被过滤掉的日志不再输出和监听拦截[ILogListener]
  ILogFilter _iLogFilter;

  /// 在日志打印之前将回调设置的监听
  ILogListener _iLogListener;

  /// 是否打印日志,设置为false后有效日志不再进行打印仍会回调监听
  bool _isPrintLog = true;

  ILogFilter get logFilter {
    return _iLogFilter;
  }

  LoggerParameter setLogFilter(ILogFilter logFilter) {
    _iLogFilter = logFilter;
    return this;
  }

  ILogListener get logListener {
    return _iLogListener;
  }

  LoggerParameter setLogListener(ILogListener logListener) {
    _iLogListener = logListener;
    return this;
  }

  bool get isPrintLog {
    return _isPrintLog;
  }

  LoggerParameter setPrintLog(bool isPrintLog) {
    _isPrintLog = isPrintLog;
    return this;
  }
}
