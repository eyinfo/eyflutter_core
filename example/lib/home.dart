import 'package:eyflutter_core/eyflutter_core.dart';
import 'package:eyflutter_core_example/pages/net_page.dart';
import 'package:flutter/material.dart';

class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);

  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('eyflutter demo'),
      ),
      body: ConstListView(
        itemCount: 20,
        itemExtent: 50,
        buttons: [
          ConstButton(
            text: "网络请求",
            onLongPress: () {
              ConstNavigator.go(context: context, target: const NetPage());
            },
          )
        ],
      ),
    );
  }
}
