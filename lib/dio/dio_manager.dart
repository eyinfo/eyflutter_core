import 'package:eyflutter_core/dio/beans/retrofit.dart';
import 'package:eyflutter_core/dio/enums/interceptor_type.dart';
import 'package:eyflutter_core/dio/event/dio_calls.dart';
import 'package:eyflutter_core/dio/event/dio_interceptor.dart';
import 'package:eyflutter_core/eyflutter_core.dart';
import 'package:eyflutter_core/kit/utils/string/url_extension.dart';
import 'package:eyflutter_core/mq/cloud_channel_manager.dart';

class DioManager {
  factory DioManager() => _getInstance();

  static DioManager get instance => _getInstance();
  static DioManager? _instance;

  DioManager._internal();

  static DioManager _getInstance() {
    return _instance ??= new DioManager._internal();
  }

  String _requestMethodName = "e004dc5b1e384739";
  Map<String, dynamic> _interceptorMap = {};

  /// 添加拦截器
  /// [type] 拦截器类型
  /// [interceptor] 子拦截器
  void addInterceptor({required InterceptorType type, required dynamic interceptor}) {
    _interceptorMap[type.name] = interceptor;
  }

  OnRequestInterceptor? _getConfigInterceptor() {
    var interceptorObj = _interceptorMap[InterceptorType.CONFIG.name];
    if (interceptorObj is! OnRequestInterceptor) {
      return null;
    }
    return interceptorObj;
  }

  String _getBaseUrl(Retrofit retrofit, OnRequestInterceptor? interceptor) {
    if (!(retrofit.api.isUrl)) {
      if (interceptor == null) {
        return "";
      }
      var baseUrl = interceptor.baseUrl(hostType: retrofit.hostType);
      return baseUrl.appendPath(retrofit.api);
    }
    return retrofit.api;
  }

  /// 网络请求实例
  /// [retrofit] 请求对象
  /// [params] 请求参数
  /// [responseCall] 响应回调
  void request<T>({required Retrofit retrofit, Map<String, dynamic>? params, OnResponseCall? responseCall}) async {
    var interceptor = _getConfigInterceptor();
    var url = _getBaseUrl(retrofit, interceptor);
    if (url.isEmptyString) {
      _callComplete(responseCall);
      return;
    }
    var response = await CloudChannelManager.instance.send<String>(_requestMethodName, arguments: {
      "method": retrofit.method.name,
      "contentType": retrofit.contentType.name,
    });
    bindResponse(response, responseCall, interceptor);
  }

  void bindResponse(String? response, OnResponseCall? responseCall, OnRequestInterceptor? interceptor) {
    if (response == null || response.isEmpty) {
      _callComplete(responseCall);
      return;
    }
    var map = JsonUtils.fromJson(response);
    if (!map.containsKey(interceptor?.codeKey())) {
      _callSuccess(map, responseCall);
      _callComplete(responseCall);
      return;
    }
    var code = map[interceptor?.codeKey()];
    if (code is int) {
      if (code == 0 || code == 200) {
        _callSuccess(map, responseCall);
      } else {
        _callError(map, responseCall, interceptor);
      }
    } else {
      _callSuccess(map, responseCall);
    }
    _callComplete(responseCall);
  }

  void _callSuccess(Map<String, dynamic> map, OnResponseCall? responseCall) {
    if (responseCall != null) {
      responseCall.onSuccess(map);
    }
  }

  void _callError(Map<String, dynamic> map, OnResponseCall? responseCall, OnRequestInterceptor? interceptor) {
    if (responseCall != null) {
      var message = map[interceptor?.msgKey()];
      responseCall.onError(message, map);
    }
  }

  void _callComplete(OnResponseCall? responseCall) {
    if (responseCall != null) {
      responseCall.onComplete();
    }
  }
}
