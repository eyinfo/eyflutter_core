import 'package:eyflutter_core/dio/beans/retrofit.dart';
import 'package:eyflutter_core/dio/enums/method.dart';

class TestApi {
  TestApi._();

  static Retrofit get getRequest => Retrofit(api: "/test/get", method: Method.GET);

  static Retrofit get putRequest => Retrofit(api: "/test/put", method: Method.PUT);

  static Retrofit get postRequest => Retrofit(api: "/test/post", method: Method.POST);

  static Retrofit get deleteRequest => Retrofit(api: "/test/delete", method: Method.DELETE);
}
