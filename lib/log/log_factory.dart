import 'dart:collection';

import 'package:eyflutter_core/log/beans/log_event.dart';
import 'package:eyflutter_core/log/beans/log_out_event.dart';
import 'package:eyflutter_core/log/beans/log_response.dart';
import 'package:eyflutter_core/log/enums/level.dart';
import 'package:eyflutter_core/log/events/i_log_output.dart';
import 'package:eyflutter_core/log/events/i_log_printer.dart';
import 'package:eyflutter_core/log/events/i_logger.dart';
import 'package:eyflutter_core/log/log_filter.dart';
import 'package:eyflutter_core/log/log_function_nested.dart';
import 'package:eyflutter_core/log/logger_parameter.dart';

/// 日志实现
class LogFactory implements ILogger {
  LogFactory({this.parameter, this.logPrinter, this.logOutput});

  final LoggerParameter? parameter;
  final ILogPrinter? logPrinter;
  final ILogOutput? logOutput;

  ListQueue<LogFunctionNested> queue = new ListQueue<LogFunctionNested>();
  bool isPrinting = false;

  @override
  void printLog<E>(Level level, dynamic message,
      {String? tag, StackTrace? stackTrace, E? exception, dynamic errorMarkedMessage}) {
    try {
      assert(message != null);
      if (errorMarkedMessage != null && errorMarkedMessage is StackTrace) {
        throw ArgumentError('errorMarkedMessage parameter cannot take a StackTrace!');
      }
      if (parameter?.logFilter == null) {
        parameter?.setLogFilter(LogFilter());
      }
      var logEvent = new LogEvent(level, message,
          tag: tag ?? "",
          stackTrace: stackTrace,
          exception: exception,
          dateTime: DateTime.now(),
          errorMarkedMessage: errorMarkedMessage);
      //过滤掉不需要打印日志
      if (!(parameter?.logFilter?.filterLog(logEvent) ?? false)) {
        return;
      }
      parameter?.logListener?.onLogListener(Future.value(logEvent));
      if (parameter?.isPrintLog ?? false) {
        var nested = logPrinter?.log(logEvent);
        if (nested != null) {
          queue.addFirst(nested);
          if (!isPrinting) {
            _performLog();
          }
        }
      }
    } catch (e) {
      print(e);
    }
  }

  void _performLog() {
    if (queue.isEmpty) {
      isPrinting = false;
      return;
    }
    isPrinting = true;
    LogFunctionNested removeLast = queue.removeLast();
    Future<LogResponse> responseCall = removeLast.perform();
    responseCall.then((response) {
      var level = response.level;
      var lines = response.logFuture;
      logOutput?.output(LogOutEvent(level: level, lines: lines));
    });
    _performLog();
  }
}
