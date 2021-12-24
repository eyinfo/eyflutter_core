/// dio请求拦截器
mixin OnRequestInterceptor {
  /// 基础地址
  /// [hostType] 主机类型(根据hostType配置基础地址)
  String baseUrl({int hostType});

  /// 响应成功返回状态码值
  int successCode() {
    return 0;
  }

  /// 响应状态码字段名
  String codeKey() {
    return "code";
  }

  /// 响应消息字段名
  String msgKey() {
    return "msg";
  }
}
