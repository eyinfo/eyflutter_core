import 'package:eyflutter_core/kit/utils/string/string_extension.dart';
import 'package:eyflutter_core/mq/cloud_channel_manager.dart';

class SharedPreferencesUtils {
  SharedPreferencesUtils._();

  static String _sharedPreferencesAction = "0d29e9936fd1419ca59baf1643f4f682";

  /// 向本地sp文件中添加数据
  /// [key] 存储key
  /// [value] 存储数据,仅支持String、int、double类型
  static void put({String? key, dynamic value}) {
    if (key.isEmptyString || !_isMatchType(value)) {
      return;
    }
    CloudChannelManager.instance.send(_sharedPreferencesAction, arguments: {"key": key, "type": "set", "value": value});
  }

  static Future<T?> _get<T>({String? key, String? getType}) {
    if (key.isEmptyString) {
      return Future.value();
    }
    return CloudChannelManager.instance
        .send<T>(_sharedPreferencesAction, arguments: {"key": key, "type": "get", "getType": getType});
  }

  static Future<String?> getString({String? key}) {
    return _get<String>(key: key, getType: "string");
  }

  static Future<bool?> getBool({String? key}) {
    return _get<bool>(key: key, getType: "bool");
  }

  static Future<int?> getInt({String? key}) {
    return _get<int>(key: key, getType: "int");
  }

  static Future<double?> getDouble({String? key}) {
    return _get<double>(key: key, getType: "double");
  }

  static void clear({String? key}) {
    if (key.isEmptyString) {
      return;
    }
    CloudChannelManager.instance.send(_sharedPreferencesAction, arguments: {"key": key, "type": "clear"});
  }

  static bool _isMatchType(dynamic value) {
    if ((value is String) || (value is num) || (value is bool)) {
      return true;
    }
    return false;
  }
}
