import 'dart:collection';

import 'package:eyflutter_core/kit/click_event.dart';
import 'package:eyflutter_core/kit/utils/map/map_extension.dart';
import 'package:eyflutter_core/kit/utils/set/list_extention.dart';
import 'package:eyflutter_core/kit/utils/string/path_extension.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/mq/enums/route_event.dart';
import 'package:eyflutter_core/mq/route/cloud_route_observer.dart';
import 'package:eyflutter_core/mq/route/route_uri_parse.dart';
import 'package:flutter/material.dart';

class RouteUtils {
  factory RouteUtils() => _getInstance();

  static RouteUtils get instance => _getInstance();
  static RouteUtils? _instance;

  RouteUtils._internal();

  static RouteUtils _getInstance() {
    return _instance ??= new RouteUtils._internal();
  }

  /// scheme
  String get _routeScheme => "eyinfo";

  /// host
  String get _routeHost => "flutter";

  static Map<String, WidgetBuilder> _routeWidgets = {};

  Map<String, WidgetBuilder> get routeWidgets => _routeWidgets;

  /// 获取路由对应组件
  /// [routeName] 路由名称
  WidgetBuilder? getWidget(String routeName) {
    return _routeWidgets[routeName];
  }

  /// 添加路由组件
  /// [routeName] 路由名称
  /// [widgetBuilder] 组件
  void addWidget(String routeName, WidgetBuilder? widgetBuilder) {
    if (routeName.isEmptyString || widgetBuilder == null) {
      return;
    }
    _routeWidgets[routeName] = widgetBuilder;
  }

  /// 添加路由组件
  void addWidgets(Map<String, dynamic> widgets) {
    widgets.forEach((key, value) {
      addWidget(key, value);
    });
  }

  /// 获取路由
  /// [routeName] 路由名称
  /// [routeEvent] 用于区分页面路由、事件路由
  /// [arguments] 路由参数
  String route(String routeName, RouteEvent routeEvent, {Map<String, dynamic>? arguments}) {
    var path = '${_routeScheme}://${_routeHost}/${routeEvent.name}';
    path = path.appendPath(routeName);
    path = path.appendPath(_queryParameters(arguments));
    return path;
  }

  /// 获取页面路由
  /// [routeName] 路由名称
  /// [arguments] 路由参数
  String routePage(String routeName, {Map<String, dynamic>? arguments}) {
    return route(routeName, RouteEvent.page, arguments: arguments);
  }

  String _queryParameters(Map<String, dynamic>? query) {
    if (query.isEmptyMap()) {
      return '';
    }
    String queryString = '';
    query?.forEach((key, value) {
      if (queryString != '') {
        queryString += '&';
      } else {
        queryString += '?';
      }
      queryString += '$key=$value';
    });
    return queryString;
  }

  /// 页面跳转
  /// [routeName] 路由名称
  /// [url] 如果是web页面该项必填
  /// [title] h5标题
  /// [arguments] 参数
  void go(String routeName, {String? url, String? title, Map<String, dynamic>? arguments}) {
    if (ClickEvent.isFastClick()) {
      return;
    }
    if (_goIntercept(routeName, url: url, title: title, arguments: arguments)) {
      return;
    }
    Map<String, dynamic> parameters = {"route_detection": true};
    if (!url.isEmptyString) {
      parameters["url"] = url;
    }
    if (title.isNotEmptyString) {
      parameters["title"] = title;
    }
    if (arguments != null && arguments.isNotEmpty) {
      parameters.addAll(arguments);
    }
    CloudRouteObserver.instance.navigator?.pushNamed(routeName, arguments: parameters);
  }

  bool _goIntercept(String routeName, {String? url, String? title, Map<String, dynamic>? arguments}) {
    var routeEvent = CloudRouteObserver.instance.routeEvent;
    if (routeEvent == null) {
      return false;
    }
    return routeEvent.onGoIntercept(routeName, url: url ?? "", title: title ?? "", arguments: arguments ?? {});
  }

  /// 销毁页面
  /// [routeName] 路由名称或路径
  void finish(String routeName) {
    if (routeName.isEmptyString) {
      return;
    }
    var parse = RouteUriParse(routeName);
    var path = parse.withoutQueryPath;
    var routes = CloudRouteObserver.instance.getRoutes(path);
    if (routes.isEmptyList) {
      return;
    }
    CloudRouteObserver.instance.navigator?.removeRoute(routes.last);
  }

  /// 销毁页面到根页面
  void finishToRoot() {
    var routes = CloudRouteObserver.instance.getAllRoutes();
    if (routes.isEmptyList) {
      return;
    }
    var rootNames = rootRouteNames();
    routes.forEach((element) {
      if (!rootNames.contains(element.settings.name)) {
        CloudRouteObserver.instance.navigator?.removeRoute(element);
      }
    });
  }

  List<String> rootRouteNames() {
    return ["/", ":///main"];
  }

  /// 销毁指定的所有页面
  /// [routeNames] 需要销毁页面的路由集合
  void finishList(List<String> routeNames) {
    if (routeNames.isEmptyList) {
      return;
    }
    routeNames.forEach((element) {
      finish(element);
    });
  }

  /// 移除路由缓存
  /// [routeName] 路由名称或路径
  void removeRouteCache(String routeName) {
    if (routeName.isEmptyString) {
      return;
    }
    var parse = RouteUriParse(routeName);
    var path = parse.withoutQueryPath;
    CloudRouteObserver.instance.remove(path);
  }

  /// 回传数据(接收对象需要实现RouteStateNotification)
  /// [routeName] 数据回传的目标路由名称
  /// [arguments] 回传数据
  void postBack(String routeName, {String? action, Map<String, dynamic>? arguments}) {
    if (routeName.isEmptyString) {
      return;
    }
    if (routeName.endsWith("/") || routeName.endsWith("main")) {
      CloudRouteObserver.instance.notificationState("/", arguments ?? {}, action: action);
    } else {
      var parse = RouteUriParse(routeName);
      var path = parse.withoutQueryPath;
      CloudRouteObserver.instance.notificationState(path, arguments ?? {}, action: action);
    }
  }

  /// 获取路由参数
  /// [routeName] 路由名称
  Map<String, String> getArguments(String routeName) {
    if (routeName.isEmptyString) {
      return {};
    }
    var parse = RouteUriParse(routeName);
    var path = parse.withoutQueryPath;
    var routes = CloudRouteObserver.instance.getRoutes(path);
    if (routes.isEmptyList) {
      return {};
    }
    //默认取第一个参数
    Route route = routes.first;
    Map<String, String> _arguments = {};
    var arguments = route.settings.arguments;
    if (arguments is RouteUriParse) {
      RouteUriParse routerUrlParse = arguments;
      _arguments = routerUrlParse.queryParameters;
    } else if (arguments is LinkedHashMap<String, String>) {
      LinkedHashMap<String, String> map = arguments;
      map.forEach((key, value) {
        _arguments[key] = value;
      });
    }
    return _arguments;
  }

  /// 统计路由数
  /// [ruleOutRouteNames] 排除统计的路由
  int routeCount({List<String> ruleOutRouteNames = const []}) {
    var routes = CloudRouteObserver.instance.duplicateRemovalRoutes(ruleOutRouteNames: ruleOutRouteNames);
    return routes.length;
  }
}
