/// dio响应回调
mixin OnResponseCall {
  /// 成功回调
  /// [dataMap] dio响应数据
  void onSuccess(Map<String, dynamic> dataMap) {}

  /// 失败回调
  /// [message] 异常消息
  /// [dataMap] dio响应数据
  void onError(String message, Map<String, dynamic> dataMap) {}

  /// 完成回调
  void onComplete() {}
}
