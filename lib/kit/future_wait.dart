typedef OnFutureAction<T> = Future<T> Function();

typedef OnFutureWaitCall = void Function(bool success, List<dynamic> resultList);

/// 同步任务，确保所有任务执行完后回调结果
class FutureWait {
  List<Future<dynamic>> _task = [];

  /// 添加Future任务
  FutureWait addFuture<T>(Future<T>? futureTask) {
    if (futureTask == null) {
      return this;
    }
    _task.add(futureTask);
    return this;
  }

  /// 添加Future任务
  FutureWait addActionFuture<T>(OnFutureAction? action) {
    if (action == null) {
      return this;
    }
    addFuture(action());
    return this;
  }

  /// 执行任务
  void perform({OnFutureWaitCall? call}) {
    Future.wait(_task, cleanUp: (successValue) {
      if (call != null) {
        call(false, []);
      }
    }).then((responses) {
      if (call != null) {
        call(true, responses);
      }
    });
  }
}
