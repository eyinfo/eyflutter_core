import 'package:eyflutter_core/eyflutter_core.dart';
import 'package:flutter/material.dart';

class NetPage extends StatefulWidget {
  const NetPage({Key? key}) : super(key: key);

  @override
  _NetPageState createState() => _NetPageState();
}

class _NetPageState extends State<NetPage> {
  @override
  Widget build(BuildContext context) {
    if ("".isEmptyString) {}
    return SafeArea(
      child: Scaffold(
        backgroundColor: Colors.white,
        appBar: AppBar(
          title: const Text('网络请求'),
        ),
        body: const Text("test"),
      ),
      top: false,
    );
  }

  void getRequest() {}
}
