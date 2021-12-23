import 'package:flutter/material.dart';

class NetPage extends StatefulWidget {
  const NetPage({Key? key}) : super(key: key);

  @override
  _NetPageState createState() => _NetPageState();
}

class _NetPageState extends State<NetPage> {
  @override
  Widget build(BuildContext context) {
    return const SafeArea(
      child: Scaffold(
        backgroundColor: Colors.white,
        body: Text("dddd"),
      ),
      top: false,
    );
  }
}
