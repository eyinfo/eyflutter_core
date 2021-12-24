import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/log/logger.dart';

class EBusItem {
  final String action;
  final Function method;

  EBusItem(this.action, this.method);
}

class BaseCloudBus {
  void addObjectEvent(Map<int, Map<String, EBusItem>> map, dynamic registerObject, {String? action, Function? method}) {
    if (registerObject == null || action.isEmptyString || method == null) {
      return;
    }
    var registerHashcode = registerObject.hashCode;
    var busList = map[registerHashcode];
    if (busList == null) {
      busList = {};
      busList[action ?? ""] = EBusItem(action ?? "", method);
      map[registerHashcode] = busList;
    }
    //如果同一对象(即内存地址相同)，直接覆盖
    busList[action ?? ""] = EBusItem(action ?? "", method);
  }

  void disposeObjectEvent(Map<int, Map<String, EBusItem>> map, dynamic registerObject, {String? action}) {
    if (registerObject == null || action.isEmptyString) {
      return;
    }
    var registerHashcode = registerObject.hashCode;
    var busList = map[registerHashcode];
    if (busList == null) {
      return;
    }
    busList.remove(action);
  }

  void dispatchObject(Map<int, Map<String, EBusItem>> map, String action, {dynamic params}) {
    if (action.isEmptyString) {
      return;
    }
    map.forEach((objectKey, busList) {
      try {
        var bus = busList[action];
        if (bus != null) {
          if (params == null) {
            bus.method.call();
          } else {
            bus.method.call(params);
          }
        }
      } catch (e) {
        Logger.instance.error(e);
      }
    });
  }
}

class CloudEBus extends BaseCloudBus {
  factory CloudEBus() => _getInstance();

  static CloudEBus get instance => _getInstance();
  static CloudEBus? _instance;

  CloudEBus._internal();

  static CloudEBus _getInstance() {
    return _instance ??= new CloudEBus._internal();
  }

  static Map<int, Map<String, EBusItem>> _map = {};

  /// 添加事件
  /// [registerObject] 事件订阅所在容器的对象(用于区分同一class实例后不同的内存地址)
  /// [action] 事件发送与接收标识
  /// [method] 事件执行的方法
  void addEvent(dynamic registerObject, {String? action, Function? method}) {
    super.addObjectEvent(_map, registerObject, action: action, method: method);
  }

  /// 销毁事件对象
  /// [registerObject] 事件订阅所在容器的对象(用于区分同一class实例后不同的内存地址)
  /// [action] 事件发送与接收标识
  void disposeEvent(dynamic registerObject, {String? action}) {
    super.disposeObjectEvent(_map, registerObject, action: action);
  }

  /// 事件分发
  /// [action] 事件发送与接收标识
  /// [params] 参数
  void dispatch(String action, {dynamic params}) {
    super.dispatchObject(_map, action, params: params);
  }
}
