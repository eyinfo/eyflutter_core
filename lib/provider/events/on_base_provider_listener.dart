/// 数据请求方式
enum RequestWay {
  /// 初始化
  initialize,

  /// 刷新
  refresh,

  /// 加载
  load
}

mixin OnBaseModelListener {
  /// 请求数据
  void onRequestData(RequestWay way);
}
