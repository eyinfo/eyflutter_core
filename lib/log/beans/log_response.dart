import 'package:eyflutter_core/log/enums/level.dart';

class LogResponse {
  final Level level;

  final List<String> logFuture;

  LogResponse(this.level, this.logFuture);
}
