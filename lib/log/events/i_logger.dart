import 'package:eyflutter_core/log/enums/level.dart';

mixin ILogger {
  void printLog<E>(Level level, dynamic message,
      {String tag, StackTrace stackTrace, E exception, dynamic errorMarkedMessage});
}
