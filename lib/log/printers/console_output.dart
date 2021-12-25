import 'package:eyflutter_core/log/beans/log_out_event.dart';
import 'package:eyflutter_core/log/events/i_log_output.dart';
import 'dart:core';

/// Default implementation of [LogOutput].
///
/// It sends everything to the system console.
class ConsoleOutput implements ILogOutput {
  @override
  void output(LogOutEvent logOutEvent) {
    logOutEvent.lines.forEach(print);
  }
}
