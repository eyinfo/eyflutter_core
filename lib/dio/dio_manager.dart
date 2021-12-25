import 'package:eyflutter_core/dio/beans/retrofit.dart';
import 'package:eyflutter_core/dio/enums/interceptor_type.dart';
import 'package:eyflutter_core/dio/event/dio_calls.dart';
import 'package:eyflutter_core/dio/event/dio_interceptor.dart';
import 'package:eyflutter_core/kit/utils/dynamic_utils.dart';
import 'package:eyflutter_core/kit/utils/json_utils.dart';
import 'package:eyflutter_core/kit/utils/string/path_extension.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/kit/utils/string/url_extension.dart';
import 'package:eyflutter_core/mq/cloud_channel_manager.dart';
import 'package:uuid/uuid.dart';

typedef OnResponseSuccessCall = void Function(Map<String, dynamic> dataMap);
typedef OnResponseErrorCall = void Function(String message, Map<String, dynamic> dataMap);
typedef OnResponseCompleteCall = void Function();

class DioManager {
  factory DioManager() => _getInstance();

  static DioManager get instance => _getInstance();
  static DioManager? _instance;

  static DioManager _getInstance() {
    return _instance ??= new DioManager._internal();
  }

  String _requestMethodName = "e004dc5b1e384739";
  String _requestCompleteAction = "812454d85242c263";
  Map<String, dynamic> _interceptorMap = {};
  Map<String, OnResponseCompleteCall> _requestActMap = {};

  DioManager._internal() {
    CloudChannelManager.instance.channel?.setMethodCallHandler((call) {
      if (call.method == _requestCompleteAction) {
        if (call.arguments is! String) {
          return Future.value();
        }
        String requestId = call.arguments ?? "";
        if (_requestActMap.containsKey(requestId)) {
          var actCall = _requestActMap[requestId];
          if (actCall != null) {
            actCall();
          }
          _requestActMap.remove(requestId);
        }
      }
      return Future.value();
    });
  }

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
  void _request<T>(
      {required Retrofit retrofit,
      Map<String, String>? headers,
      Map<String, dynamic>? params,
      required String requestId,
      OnResponseCall? responseCall}) async {
    var interceptor = _getConfigInterceptor();
    var url = _getBaseUrl(retrofit, interceptor);
    if (url.isEmptyString) {
      _callComplete(responseCall);
      return;
    }
    var resultData = await CloudChannelManager.instance.send<String>(_requestMethodName, arguments: {
      "method": retrofit.method.name,
      "requestId": requestId,
      "contentType": retrofit.contentType.name,
      "url": url,
      "headers": headers,
      "data": params,
    });
    if (resultData == null) {
      _callComplete(responseCall);
      return;
    }
    var resultMap = JsonUtils.fromJson(resultData);
    String type = resultMap["type"];
    if (type.isEmptyString || type == "error") {
      if (responseCall != null) {
        responseCall.onError("error", {});
      }
      _callComplete(responseCall);
    } else if (type == "success") {
      String response = resultMap["data"];
      _bindResponse(requestId, response, responseCall, interceptor);
    } else {
      _callComplete(responseCall);
    }
  }

  void _bindResponse(
      String requestId, String? response, OnResponseCall? responseCall, OnRequestInterceptor? interceptor) {
    if (response == null || response.isEmpty) {
      _callComplete(responseCall);
      _requestActMap.remove(requestId);
      return;
    }
    var map = JsonUtils.fromJson(response);
    if (!map.containsKey(interceptor?.codeKey())) {
      _callSuccess(map, responseCall);
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

  /// 网络请求实例
  /// [retrofit] 请求对象
  /// [params] 请求参数
  /// [responseCall] 响应回调
  void request<T>(
      {required Retrofit retrofit,
      Map<String, String>? headers,
      Map<String, dynamic>? params,
      OnResponseSuccessCall? successCall,
      OnResponseErrorCall? errorCall,
      OnResponseCompleteCall? completeCall}) async {
    String requestId = Uuid().v4().toString();
    if (completeCall != null) {
      _requestActMap[requestId] = completeCall;
    }
    _request(
        retrofit: retrofit,
        headers: headers,
        params: params,
        requestId: requestId,
        responseCall: _InternalResponseCall(successCall, errorCall, completeCall));
  }
}

class _InternalResponseCall with OnResponseCall {
  final OnResponseSuccessCall? successCall;
  final OnResponseErrorCall? errorCall;
  final OnResponseCompleteCall? completeCall;

  _InternalResponseCall(this.successCall, this.errorCall, this.completeCall);

  @override
  void onComplete() {
    if (completeCall != null) {
      completeCall!();
    }
  }

  @override
  void onError(String message, Map<String, dynamic> dataMap) {
    if (errorCall != null) {
      errorCall!(message, dataMap);
    }
  }

  @override
  void onSuccess(Map<String, dynamic> dataMap) {
    if (successCall != null) {
      successCall!(dataMap);
    }
  }
}
