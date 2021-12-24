import 'package:eyflutter_core/dio/enums/content_type.dart';
import 'package:eyflutter_core/dio/enums/method.dart';

class Retrofit {
  /// 接口相对地址
  final String api;

  /// 接口请求方法
  final Method method;

  /// 请求内容数据格式
  final ContentType contentType;

  /// api主机类型
  final int hostType;

  Retrofit({this.api = "", this.method = Method.POST, this.contentType = ContentType.json, this.hostType = 0});
}
