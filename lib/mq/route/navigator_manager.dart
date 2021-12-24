import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/mq/route/route_uri_parse.dart';
import 'package:eyflutter_core/mq/route/route_utils.dart';
import 'package:flutter/material.dart';

class NavigatorManager {
  factory NavigatorManager() => _getInstance();

  static NavigatorManager get instance => _getInstance();
  static NavigatorManager? _instance;

  NavigatorManager._internal();

  static NavigatorManager _getInstance() {
    return _instance ??= new NavigatorManager._internal();
  }

  /// 获取路由参数(一般配合NavigatorManager.go一起使用)
  /// [context]页面或控件视图的上下文
  Map<String, String> getRouteParams(BuildContext context) {
    var arguments = ModalRoute.of(context)?.settings.arguments;
    if (arguments is Map<String, String>) {
      return arguments;
    }
    return {};
  }

  /// 路由跳转(一般配合onGenerateRoute一起使用)
  /// 通过此方式跳转的对应页面以NavigatorManager.getRouteParams方式获取
  /// [route]路由及参数地址,通过[CSRouterUrlParse]解析后查找[CSRouter.pageRoutes()]再跳转;
  /// [defaultRoute]默认路由,在route解析失败或未搭到对应的路由时渲染当前页面
  /// [initParams]初始化参数
  MaterialPageRoute go({String route = "/", required WidgetBuilder defaultRoute, Map<String, dynamic>? initParams}) {
    WidgetBuilder builder;
    String _routeName;
    if (initParams == null) {
      initParams = {};
    }
    if (route == "/") {
      _routeName = "/";
      builder = defaultRoute;
    } else {
      var parse = RouteUriParse(route);
      Map<String, String> parameter = parse.queryParameters;
      _routeName = parse.withoutQueryPath;
      initParams.addAll(parameter);
      if (_routeName.isEmptyString) {
        _routeName = "/";
        //在不符合定义规则的情况下,parse.withoutQueryPath可能为空
        builder = defaultRoute;
      } else {
        builder = RouteUtils.instance.getWidget(_routeName) ?? defaultRoute;
      }
    }
    var pageRoute =
        MaterialPageRoute(builder: builder, settings: RouteSettings(name: _routeName, arguments: initParams));
    return pageRoute;
  }
}
