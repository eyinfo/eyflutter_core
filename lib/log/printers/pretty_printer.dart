import 'dart:convert';

import 'package:eyflutter_core/log/beans/log_event.dart';
import 'package:eyflutter_core/log/beans/log_response.dart';
import 'package:eyflutter_core/log/enums/level.dart';
import 'package:eyflutter_core/log/events/i_log_printer.dart';
import 'package:eyflutter_core/log/log_function_nested.dart';
import 'package:eyflutter_core/log/printers/ansi_color.dart';

class PrettyPrinter implements ILogPrinter {
  String _topLeftCorner = '┌';
  String _bottomLeftCorner = '└';
  String _middleCorner = '├';
  String _verticalLine = '│';
  String _doubleDivider = '─';
  String _singleDivider = '┄';

  var levelColors = {
    Level.verbose: AnsiColor.fg(AnsiColor.grey(0.5)),
    Level.debug: AnsiColor.none(),
    Level.info: AnsiColor.fg(12),
    Level.warning: AnsiColor.fg(208),
    Level.error: AnsiColor.fg(196),
    Level.wtf: AnsiColor.fg(199),
    Level.fatal: AnsiColor.bg(190),
    Level.trace: AnsiColor.bg(12)
  };

  var emojis = {
    Level.verbose: '',
    Level.debug: '🐛 ',
    Level.info: '💡 ',
    Level.warning: '⚠️ ',
    Level.error: '⛔ ',
    Level.fatal: '⛔ ',
    Level.wtf: '👾 ',
    Level.trace: '',
  };

  var stackTraceRegex = RegExp(r'#[0-9]+[\s]+(.+) \(([^\s]+)\)');
  final int methodCount;
  final int lineLength;
  final bool colors;
  final bool printEmojis;
  final bool printTime;

  String _topBorder = '';
  String _middleBorder = '';
  String _bottomBorder = '';

  PrettyPrinter({
    ///输出日志关联的方法数
    this.methodCount = 3,
    this.lineLength = 120,
    this.colors = true,
    this.printEmojis = true,
    this.printTime = false,
  }) {
    var doubleDividerLine = StringBuffer();
    var singleDividerLine = StringBuffer();
    for (var i = 0; i < lineLength - 1; i++) {
      doubleDividerLine.write(_doubleDivider);
      singleDividerLine.write(_singleDivider);
    }

    _topBorder = '$_topLeftCorner$doubleDividerLine';
    _middleBorder = '$_middleCorner$singleDividerLine';
    _bottomBorder = '$_bottomLeftCorner$doubleDividerLine';
  }

  ///描述性消息解析
  String _stringifyMessage(dynamic message) {
    if (message == null) {
      return "";
    }
    try {
      if (message is Map || message is Iterable) {
        var encoder = JsonEncoder.withIndent('  ');
        return encoder.convert(message);
      } else {
        return message.toString();
      }
    } catch (e) {
      return message.toString();
    }
  }

  String _formatStackTrace(Level level, StackTrace stackTrace, int methodCount) {
    var lines = stackTrace.toString().split('\n');
    methodCount = (level == Level.trace ? (methodCount > 6 ? methodCount : methodCount) : methodCount);
    var formatted = <String>[];
    var count = 0;
    for (var line in lines) {
      var match = stackTraceRegex.matchAsPrefix(line);
      if (match != null) {
        var group = match.group(2);
        if (group.startsWith('package:logger') || group.startsWith(":0:12")) {
          continue;
        }
        var newLine = '#$count   ${match.group(1)} (${match.group(2)})';
        formatted.add(newLine.replaceAll('<anonymous closure>', '()'));
        if (++count == methodCount) {
          break;
        }
      } else {
        formatted.add(line);
      }
    }

    if (formatted.isEmpty) {
      return null;
    } else {
      return formatted.join('\n');
    }
  }

  ///堆栈信息解析
  String _stackMessage(Level level, StackTrace stackTrace) {
    if (methodCount <= 0) {
      return "";
    }
    return _formatStackTrace(level, stackTrace == null ? StackTrace.current : stackTrace, methodCount);
  }

  AnsiColor _getLevelColor(Level level) {
    if (colors) {
      return levelColors[level];
    } else {
      return AnsiColor.none();
    }
  }

  AnsiColor _getErrorColor(Level level) {
    if (colors) {
      if (level == Level.wtf) {
        return levelColors[Level.wtf].toBg();
      } else {
        return levelColors[Level.error].toBg();
      }
    } else {
      return AnsiColor.none();
    }
  }

  String _getEmoji(Level level) {
    if (printEmojis) {
      return emojis[level];
    } else {
      return '';
    }
  }

  void _markErrorColor(
      List<String> buffer, Level level, AnsiColor color, var verticalLine, dynamic errorMarkedMessage) {
    if (errorMarkedMessage == null) {
      return;
    }
    String error = errorMarkedMessage.toString();
    var errorColor = _getErrorColor(level);
    for (var line in error.split('\n')) {
      buffer.add(
        color('$verticalLine ') + errorColor.resetForeground + errorColor(line) + errorColor.resetBackground,
      );
    }
    buffer.add(color(_middleBorder));
  }

  void _printLine(List<String> buffer, AnsiColor color, String line) {
    int lineLen = 115;
    if (line.length > lineLen) {
      String content = '$color$_verticalLine ${line.substring(0, lineLen)}';
      buffer.add('$content');
      line = line.substring(lineLen);
      _printLine(buffer, color, line);
    } else {
      String content = '$color$_verticalLine $line';
      buffer.add('$content');
    }
  }

  List<String> _formatAndPrint(
      Level level, String message, dynamic errorMarkedMessage, String exception, String stacktrace, String time) {
    List<String> buffer = [];
    var color = _getLevelColor(level);
    buffer.add(color(_topBorder));
    _markErrorColor(buffer, level, color, _verticalLine, errorMarkedMessage);
    if (stacktrace != null && stacktrace.isNotEmpty) {
      var stackLines = stacktrace.split('\n');
      for (var line in stackLines) {
        _printLine(buffer, color, line);
      }
      buffer.add(color(_middleBorder));
    }
    if (exception != null && exception.isNotEmpty) {
      var exceptionLines = exception.split("\n");
      for (var line in exceptionLines) {
        _printLine(buffer, color, line);
      }
      buffer.add(color(_middleBorder));
    }
    if (time != null && time.isNotEmpty) {
      buffer
        ..add(color('$_verticalLine $time'))
        ..add(color(_middleBorder));
    }
    var emoji = _getEmoji(level);
    for (var line in message.split('\n')) {
      buffer.add(color('$_verticalLine $emoji$line'));
    }
    buffer.add(color(_bottomBorder));
    return buffer;
  }

  String _getTime(DateTime dateTime) {
    String _threeDigits(int n) {
      if (n >= 100) return '$n';
      if (n >= 10) return '0$n';
      return '00$n';
    }

    String _twoDigits(int n) {
      if (n >= 10) return '$n';
      return '0$n';
    }

    var now = DateTime.now();
    var h = _twoDigits(now.hour);
    var min = _twoDigits(now.minute);
    var sec = _twoDigits(now.second);
    var ms = _threeDigits(now.millisecond);
    var timeSinceStart = now.difference(dateTime).toString();
    return '$h:$min:$sec.$ms (+$timeSinceStart)';
  }

  @override
  LogFunctionNested log(LogEvent logEvent) {
    return LogFunctionNested(() {
      var message = _stringifyMessage(logEvent.message);
      var stack = _stackMessage(logEvent.level, logEvent.stackTrace);
      String time = printTime ? _getTime(logEvent.dateTime ?? DateTime.now()) : "";
      String tag = (logEvent.tag == null || logEvent.tag.isEmpty) ? "" : "${logEvent.tag}>>>";
      String exception = (logEvent.exception == null ? "" : logEvent.exception.toString());
      List<String> lines =
          _formatAndPrint(logEvent.level, '$tag$message', logEvent.errorMarkedMessage, exception, stack, time);
      var logResponse = LogResponse(logEvent.level, lines);
      return Future.value(logResponse);
    });
  }
}
