class ConfigManager {
  factory ConfigManager() => _getInstance();

  static ConfigManager get instance => _getInstance();
  static ConfigManager? _instance;

  ConfigManager._internal();

  static ConfigManager _getInstance() {
     return _instance ??= new ConfigManager._internal();
  }

  Map<String, dynamic> _configMap = {};

  /// 添加配置项
  /// [key] 必须唯一,定义在各模块中
  /// [value] 配置对象
  void addConfig(String? key, dynamic value) {
    if (key == null || key.isEmpty || value == null) {
      return;
    }
    _configMap[key] = value;
  }

  /// 获取配置项
  /// [key] 必须唯一,定义在各模块中
  dynamic getConfig(String? key) {
    if (key == null || key.isEmpty) {
      return null;
    }
    return _configMap[key];
  }

  /// 获取配置项
  /// [key] 必须唯一,定义在各模块中
  String getString(String key) {
    var value = getConfig(key);
    if (value == null || !(value is String)) {
      return "";
    }
    return "$value".trim();
  }

  /// 获取配置项
  /// [key] 必须唯一,定义在各模块中
  bool getBool(String key) {
    var value = getConfig(key);
    if (value == null || !(value is bool)) {
      return false;
    }
    return value;
  }
}
