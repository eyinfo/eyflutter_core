class ConfigManager {
  factory ConfigManager() => _getInstance();

  static ConfigManager get instance => _getInstance();
  static ConfigManager _instance;

  ConfigManager._internal();

  static ConfigManager _getInstance() {
    if (_instance == null) {
      _instance = new ConfigManager._internal();
    }
    return _instance;
  }

  Map<String, dynamic> _configMap = {};

  /// scheme listener key
  String schemeListenerKey = "a7e4d67828da1192";

  /// image list listener key
  String imageListListenerKey = "6994d97a73c937d6";

  /// 图片配置key
  String imageConfigKey = "0604d4dba92a5709";

  /// lang type config key
  String langTypeConfigKey = "2d94b1ab596a34e7";

  /// cos config key
  String cosConfigKey = "01540f8bf060494a";

  /// xml cache config key
  String xmlCacheConfigKey = "4424264858bc2343";

  /// state lifecycle listener
  String stateLifecycleKey = "d654e2591793e0c7";

  /// 添加配置项
  /// [key] 必须唯一,定义在各模块中
  /// [value] 配置对象
  void addConfig(String key, dynamic value) {
    if (key == null || key.isEmpty || value == null) {
      return;
    }
    _configMap[key] = value;
  }

  /// 获取配置项
  /// [key] 必须唯一,定义在各模块中
  dynamic getConfig(String key) {
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
