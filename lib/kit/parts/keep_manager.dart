import 'dart:collection';

typedef Future<KeepResponse> KeepFunctionNestedCall();

typedef TaskFunction = Future<bool> Function(String params);

class KeepResponse {
  final String params;
  final TaskFunction task;
  int currCount;
  final int maxCount;

  KeepResponse(this.params, this.task, this.currCount, this.maxCount);
}

class KeepFunctionNested {
  final KeepFunctionNestedCall _nestedCall;

  KeepFunctionNested(this._nestedCall);

  Future<KeepResponse> perform() {
    return _nestedCall();
  }
}

class KeepManager {
  factory KeepManager() => _getInstance();

  static KeepManager get instance => _getInstance();
  static KeepManager? _instance;

  KeepManager._internal();

  static KeepManager _getInstance() {
    return _instance ??= new KeepManager._internal();
  }

  static var _taskQueue = Queue<KeepFunctionNested>();
  bool _isPerforming = false;

  /// 确保任务执行成功，若执行失败或异常则继续将任务添加至队首等待下次执行;
  /// [maxCount] 最多执行次数
  void perform({required String params, required TaskFunction function, int maxCount = 1}) {
    _taskQueue.addFirst(KeepFunctionNested(() {
      return Future.value(KeepResponse(params, function, 0, maxCount));
    }));
    if (!_isPerforming) {
      _performTask();
    }
  }

  void _performTask() {
    if (_taskQueue.isEmpty) {
      _isPerforming = false;
      return;
    }
    _isPerforming = true;
    var removeLast = _taskQueue.removeLast();
    var performCall = removeLast.perform();
    performCall.then((response) async {
      try {
        var fun = response.task;
        var status = await fun(response.params);
        response.currCount += 1;
        if (status) {
          _performTask();
        } else {
          if (response.currCount <= response.maxCount) {
            _taskQueue.addFirst(KeepFunctionNested(() {
              return Future.value(response);
            }));
          }
          Future.delayed(Duration(milliseconds: 1000), () {
            _performTask();
          });
        }
      } catch (e) {
        _performTask();
      }
    }, onError: (e) {
      _performTask();
    });
  }
}
