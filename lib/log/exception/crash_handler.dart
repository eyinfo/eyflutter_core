import 'dart:async';

import 'package:eyflutter_core/log/beans/crash_info.dart';
import 'package:eyflutter_core/log/logger.dart';
import 'package:flutter/cupertino.dart';

/// 正式环境需要上报回调
typedef OnReleaseLogReport = void Function(CrashInfo crashInfo);

/// 错误信息收集回调
typedef OnCrashCollectCall = CrashInfo Function(dynamic error);

typedef OnCompletedCall = void Function();

class CrashHandler {
  CrashHandler._();

  static CrashHandler _instance;

  static CrashHandler get instance {
    _instance ??= CrashHandler._();
    return _instance;
  }

  /// 构建crash节点
  /// [app] 根节点
  /// [report] 正式环境上报回调
  /// [environment] 0-正式;1-预发;2-测试;
  /// [crashCollectCall] crash收集回调
  /// [completedCall] 处理完成回调
  void build(Widget app,
      {OnReleaseLogReport report,
      int environment,
      OnCrashCollectCall crashCollectCall,
      OnCompletedCall completedCall}) {
    const bool inProduction = bool.fromEnvironment("dart.vm.product");
    FlutterError.onError = (FlutterErrorDetails details) {
      if (inProduction) {
        Zone.current.handleUncaughtError(details.exception, details.stack);
      } else {
        FlutterError.dumpErrorToConsole(details);
      }
    };
    runZoned(() => runApp(app), onError: (error, stackTrace) {
      if (completedCall != null) {
        completedCall();
      }
      if (crashCollectCall == null) {
        return;
      }
      var crash = crashCollectCall(error);
      if (crash == null || crash.error == null || crash.error.isEmpty) {
        return;
      }
      if (environment == 0 && report != null) {
        report(crash);
      } else if (environment == 1 || environment == 2) {
        //输出到控制台
        if (!_checkError(crash.errorType)) {
          return;
        }
        Logger.instance.error(crash.error);
      }
    });
  }

  bool _checkError(String errorType) {
    var type = errorType ?? "";
    if (type == "DioError") {
      return false;
    }
    return true;
  }
}
