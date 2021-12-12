import 'package:eyflutter_core/kit/utils/set/list_extention.dart';

class RouteUriParse {
  //包含query全路径
  final String routeUri;

  Uri _uri;

  RouteUriParse(this.routeUri) {
    _uri = Uri.parse(this.routeUri);
  }

  String get scheme {
    return _uri.scheme;
  }

  String get host {
    return _uri.host;
  }

  String get path {
    return _uri.path;
  }

  String get userInfo {
    return _uri.userInfo;
  }

  // scheme + host + path
  String get withoutQueryPath {
    return '$scheme://$host$path';
  }

  List<String> get pathSegments => _uri.pathSegments;

  String get rootPath {
    var segments = pathSegments;
    if (segments.isNotEmptyList) {
      return segments.first;
    }
    return "";
  }

  Map<String, String> get queryParameters => _uri.queryParameters;
}
