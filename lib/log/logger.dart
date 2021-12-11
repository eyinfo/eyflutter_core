import 'package:eyflutter_core/log/enums/level.dart';
import 'package:eyflutter_core/log/events/i_logger.dart';
import 'package:eyflutter_core/log/log_factory.dart';
import 'package:eyflutter_core/log/logger_parameter.dart';
import 'package:eyflutter_core/log/printers/console_output.dart';
import 'package:eyflutter_core/log/printers/pretty_printer.dart';

/// Use instances of logger to send log messages to the [LogPrinter].
class Logger {
  Logger._() {
    _parameter = LoggerParameter();
    _iLogger = LogFactory(parameter: _parameter, logPrinter: PrettyPrinter(), logOutput: ConsoleOutput());
  }

  ILogger _iLogger;
  LoggerParameter _parameter;

  static Logger _instance;

  static Logger get instance {
    if (_instance == null) {
      _instance = Logger._();
    }
    return _instance;
  }

  LoggerParameter get builder {
    return _parameter;
  }

  /// 详细日志
  void verbose<E>(dynamic message, {String tag, E exception}) {
    _iLogger.printLog(Level.verbose, message, tag: tag, stackTrace: StackTrace.current, exception: exception);
  }

  /// 调试日志
  void debug<E>(dynamic message, {String tag, E exception}) {
    _iLogger.printLog(Level.debug, message, tag: tag, stackTrace: StackTrace.current, exception: exception);
  }

  /// 普通信息类日志
  void info(dynamic message, {String tag}) {
    _iLogger.printLog(Level.info, message, tag: tag, stackTrace: StackTrace.current);
  }

  /// 警告日志
  void warning(dynamic message, {String tag}) {
    _iLogger.printLog(Level.warning, message, tag: tag, stackTrace: StackTrace.current);
  }

  /// 错误日志
  void error<E>(dynamic message, {String tag, E exception}) {
    _iLogger.printLog(Level.error, message, tag: tag, stackTrace: StackTrace.current, exception: exception);
  }

  /// 致命错误
  void fatal<E>(dynamic message, {String tag, E exception, dynamic errorMarkedMessage}) {
    _iLogger.printLog(Level.fatal, message,
        tag: tag, stackTrace: StackTrace.current, exception: exception, errorMarkedMessage: errorMarkedMessage);
  }

  /// 正常情况下永远不会发生bug
  void wtf(dynamic message, {String tag, dynamic errorMarkedMessage}) {
    _iLogger.printLog(Level.wtf, message,
        tag: tag, stackTrace: StackTrace.current, errorMarkedMessage: errorMarkedMessage);
  }

  /// 堆栈日志
  void trace(dynamic message, {String tag}) {
    _iLogger.printLog(Level.trace, message, tag: tag, stackTrace: StackTrace.current);
  }

  void test2() {}
}
