import 'package:cloud_basic_mq/cloud_basic_mq.dart';

class NetworkManager {
  factory NetworkManager() => _getInstance();

  static NetworkManager get instance => _getInstance();
  static NetworkManager _instance;

  NetworkManager._internal();

  static NetworkManager _getInstance() {
    if (_instance == null) {
      _instance = new NetworkManager._internal();
    }
    return _instance;
  }

  String _networkConnectChannelName = "c964903972a508fd";

  /// 网络是否已连接
  Future<bool> isConnected() {
    return CloudChannelManager.instance.channel.invokeMethod<bool>(_networkConnectChannelName);
  }
}
