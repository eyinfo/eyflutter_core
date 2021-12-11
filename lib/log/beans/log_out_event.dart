import 'package:eyflutter_core/log/enums/level.dart';

class LogOutEvent {
  /// 日志类别
  final Level level;

  /// 本条日志输出行集合
  final List<String> lines;

  LogOutEvent({this.level = Level.info, this.lines = const []});
}
