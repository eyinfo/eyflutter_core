import 'package:eyflutter_core/log/enums/level.dart';

class LogEvent<E> {
  /// 日志级别
  final Level level;

  /// 描述性日志信息
  final dynamic message;

  /// 日志标记值(可用作控制台或日志定位过滤条件)
  final String tag;

  /// 堆栈信息(在日志调用时传入)
  final StackTrace stackTrace;

  /// 异常日志
  final E exception;

  /// 日志记录时间
  final DateTime dateTime;

  /// 对[Level.fatal]或[Level.wtf]日志，errorMarkedMessage消息区别于其它内容显示;
  final dynamic errorMarkedMessage;

  LogEvent(this.level, this.message,
      {this.tag = '', this.stackTrace, this.exception, this.dateTime, this.errorMarkedMessage});
}
