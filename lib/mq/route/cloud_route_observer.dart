import 'dart:collection';

import 'package:eyflutter_core/kit/utils/map/map_extension.dart';
import 'package:eyflutter_core/kit/utils/dynamic_utils.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:flutter/material.dart';

mixin RouteStateNotification {
  /// 接收到其它页面回传数据通知，一般通过RouteUtils.instance.postBack发送
  /// [action] 识别标识(可为空)
  /// [result] 回传数据
  void onStateResult(String action, Map<String, dynamic> result);
}

/// 路由事件拦截
mixin OnRouteEventIntercept {
  /// 路由跳转拦截
  /// [routeName] 路由名称
  /// [url] 如果是web页面该项必填
  /// [title] h5标题
  /// [arguments] 参数
  bool onGoIntercept(String routeName, {String url, String title, Map<String, dynamic> arguments});

  /// 路由改变监听
  /// [route] 路由对象
  /// [isDispose] 是否被销毁的路由
  void onRouteChanged(Route? route, bool isDispose);
}

class CloudRouteObserver extends NavigatorObserver {
  factory CloudRouteObserver() => _getInstance();

  static CloudRouteObserver get instance => _getInstance();
  static CloudRouteObserver? _instance;

  CloudRouteObserver._internal();

  static CloudRouteObserver _getInstance() {
    return _instance ??= new CloudRouteObserver._internal();
  }

  List<String> _routes = [];
  Map<String, Route> _routeMap = {};
  static Map<String, RouteStateNotification> _notificationMap = {};

  //路由事件拦截器
  static OnRouteEventIntercept? _routeEvent;

  void setRouteEvent(OnRouteEventIntercept routeEventIntercept) {
    _routeEvent = routeEventIntercept;
  }

  OnRouteEventIntercept? get routeEvent => _routeEvent;

  /// 添加路由状态通知(该方法框架内部调用)
  void addNotification(String routeName, dynamic stateInstance) {
    if (routeName.isEmptyString || (stateInstance is! RouteStateNotification)) {
      return;
    }
    _notificationMap[routeName] = stateInstance;
  }

  /// 移除路由状态通知(该方法框架内部调用)
  void removeNotification(String routePath) {
    if (routePath.isEmptyString || !_notificationMap.containsKey(routePath)) {
      return;
    }
    _notificationMap.remove(routePath);
  }

  /// 通知State状态
  /// [routePath] 路由地址
  /// [action] 数据回传识别标识(可为空)
  /// [result] 回传通知参数
  void notificationState(String routePath, Map<String, dynamic> result, {String? action}) {
    if (routePath.isEmptyString || !_notificationMap.containsKey(routePath)) {
      return;
    }
    var notification = _notificationMap[routePath];
    notification?.onStateResult(action ?? "", result);
  }

  @override
  void didPush(Route route, Route? previousRoute) {
    if (_goIntercept(route)) {
      navigator?.removeRoute(route);
      return;
    }
    var routeSign = "${(route.settings.name ?? "")}#hash#${route.hashCode}";
    if (_routes.contains(routeSign)) {
      _routes.remove(routeSign);
    }
    _routes.insert(0, routeSign);
    _routeMap[routeSign] = route;
    _callRouteChange(route, false);
    super.didPush(route, previousRoute);
  }

  @override
  void didReplace({Route? newRoute, Route? oldRoute}) {
    if (newRoute == null) {
      return;
    }
    if (_goIntercept(newRoute)) {
      navigator?.removeRoute(newRoute);
      return;
    }
    var oldSign = "${(oldRoute?.settings.name ?? "")}#hash#${oldRoute?.hashCode ?? 0}";
    var newSign = "${(newRoute.settings.name ?? "")}#hash#${newRoute.hashCode}";
    if (_routes.contains(oldSign)) {
      var index = _routes.indexOf(oldSign);
      _routeMap.remove(oldSign);
      _routes.insert(index, newSign);
      _routeMap[newSign] = newRoute;
      _routes.remove(oldSign);
      //更新notification集合中新的key
      if (_notificationMap.containsKey(oldSign)) {
        var notification = _notificationMap.remove(oldSign);
        if (notification != null) {
          _notificationMap[newSign] = notification;
        }
      }
    } else {
      _routes.insert(0, newSign);
      _routeMap[newSign] = newRoute;
    }
    _callRouteChange(newRoute, false);
  }

  void _callRouteChange(Route? route, bool isDispose) {
    routeEvent?.onRouteChanged(route, isDispose);
  }

  bool _goIntercept(Route route) {
    if (routeEvent == null) {
      return false;
    }
    var argObj = route.settings.arguments;
    var arguments = Map<String, dynamic>();
    if (argObj is Map<String, dynamic>) {
      arguments.addAll(argObj);
    }
    //通过RouteUtils.go跳转跳过检测避免重复检测
    var detectionState = arguments.getValue<Object>("route_detection");
    if (detectionState.isTrue) {
      return false;
    }
    var url = "";
    if (arguments.containsKey("url")) {
      url = "${arguments.remove("url")}";
    }
    var title = "";
    if (arguments.containsKey("title")) {
      title = "${arguments.remove("title")}";
    }
    var routeName = route.settings.name ?? "";
    return routeEvent?.onGoIntercept(routeName, url: url, title: title, arguments: arguments) ?? false;
  }

  @override
  void didPop(Route route, Route? previousRoute) {
    _remove(route);
    super.didPop(route, previousRoute);
  }

  @override
  void didRemove(Route route, Route? previousRoute) {
    _remove(route);
    super.didRemove(route, previousRoute);
  }

  void _remove(Route? route) {
    var routeSign = "${(route?.settings.name ?? "")}#hash#${route?.hashCode}";
    if (_routes.contains(routeSign)) {
      _routes.remove(routeSign);
    }
    if (_routeMap.containsKey(routeSign)) {
      _routeMap.remove(routeSign);
    }
    //移除对应的状态通知
    if (_notificationMap.containsKey(routeSign)) {
      _notificationMap.remove(routeSign);
    }
    _callRouteChange(route, true);
  }

  /// 根据路由地址获取同一页面所有路由对象
  List<Route> getRoutes(String routePath) {
    List<Route> lst = [];
    _routeMap.forEach((key, value) {
      var signList = key.split("#hash#");
      if (signList[0] == routePath) {
        lst.add(value);
      }
    });
    return lst;
  }

  /// 获取当前pushlist所有路由
  List<Route> getAllRoutes() {
    List<Route> lst = [];
    _routeMap.forEach((key, value) {
      lst.add(value);
    });
    return lst;
  }

  /// 去重后路由集合
  /// [ruleOutRouteName] 排除路由名称
  List<Route> duplicateRemovalRoutes({List<String> ruleOutRouteNames = const []}) {
    List<Route> lst = [];
    Set<String> keys = HashSet();
    _routeMap.forEach((key, value) {
      var signList = key.split("#hash#");
      if (!ruleOutRouteNames.contains(signList[0]) && !keys.contains(signList[0])) {
        keys.add(signList[0]);
        lst.add(value);
      }
    });
    return lst;
  }

  /// 移除路由缓存
  void remove(String routePath) {
    Route? route;
    _routeMap.forEach((key, value) {
      var signList = key.split("#hash#");
      if (signList[0] == routePath) {
        route = value;
        return;
      }
    });
    _remove(route);
  }
}
