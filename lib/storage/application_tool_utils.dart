import 'package:eyflutter_core/mq/cloud_channel_manager.dart';

/// 应用工具管理类
class ApplicationToolUtils {
  factory ApplicationToolUtils() => _getInstance();

  static ApplicationToolUtils get instance => _getInstance();
  static ApplicationToolUtils? _instance;

  ApplicationToolUtils._internal();

  static ApplicationToolUtils _getInstance() {
    return _instance ??= new ApplicationToolUtils._internal();
  }

  String _cacheSizeMethodName = "69f45198f940bd8c";
  String _cleanCacheMethodName = "72c4d6ebf196bda4";

  /// 获取缓存大小
  /// [return] e.g 0MB
  Future<String?> getCacheSize() {
    return CloudChannelManager.instance.send<String>(_cacheSizeMethodName);
  }

  /// 清空缓存
  /// [return] true-清空缓存成功，反之失败;
  Future<bool?> cleanCache() {
    return CloudChannelManager.instance.send<bool>(_cleanCacheMethodName);
  }
}
