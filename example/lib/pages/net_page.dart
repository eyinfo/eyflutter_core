import 'package:eyflutter_core/dio/enums/interceptor_type.dart';
import 'package:eyflutter_core/eyflutter_core.dart';
import 'package:eyflutter_core_example/test_api.dart';
import 'package:flutter/material.dart';

class NetPage extends StatefulWidget {
  const NetPage({Key? key}) : super(key: key);

  @override
  _NetPageState createState() => _NetPageState();
}

class RequestConfig with OnRequestInterceptor {
  @override
  String baseUrl({int hostType = 0}) {
    return "http://192.168.8.13:8089/gyadmin/";
  }
}

class _NetPageState extends State<NetPage> {
  @override
  void initState() {
    DioManager.instance.addInterceptor(type: InterceptorType.CONFIG, interceptor: RequestConfig());
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    if ("".isEmptyString) {}
    return SafeArea(
      child: Scaffold(
        backgroundColor: Colors.white,
        appBar: AppBar(
          title: const Text('网络请求'),
        ),
        body: ConstListView(
          itemCount: 20,
          itemExtent: 50,
          buttons: [
            ConstButton(
              text: "GET请求",
              onLongPress: getRequest,
            )
          ],
        ),
      ),
      top: false,
    );
  }

  void getRequest() {
    DioManager.instance.request(
        retrofit: TestApi.getRequest,
        successCall: (Map<String, dynamic> dataMap) {
          Logger.instance.info(dataMap);
        },
        completeCall: () {
          Logger.instance.info("request complete");
        });
  }
}
