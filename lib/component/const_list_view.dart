import 'package:eyflutter_core/kit/utils/set/list_extention.dart';
import 'package:flutter/material.dart';

class ConstListView<T extends ConstButton> extends StatelessWidget {
  final int itemCount;

  final double? itemExtent;

  final List<T> buttons;

  const ConstListView({Key? key, this.itemCount = 0, this.itemExtent, this.buttons = const []}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemBuilder: (BuildContext context, int index) {
        if (this.buttons.isEmptyList) {
          return Center(child: Text("$index"));
        }
        if (this.buttons.length > index) {
          return this.buttons[index];
        }
        return Center(child: Text("$index"));
      },
      itemCount: this.itemCount,
      itemExtent: this.itemExtent,
    );
  }
}

class ConstButton extends StatelessWidget {
  final VoidCallback? onLongPress;
  final String? text;

  const ConstButton({Key? key, this.onLongPress, this.text}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return TextButton(onPressed: this.onLongPress, child: Text(this.text ?? ""));
  }
}
