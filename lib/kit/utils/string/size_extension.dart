import 'package:flutter/material.dart';

extension StringSizeExtension on String {

  Size sizeWithStyle(TextStyle style) {
    final TextPainter textPainter = TextPainter(
        text: TextSpan(text: this, style: style),
        maxLines: 1,
        textDirection: TextDirection.ltr)
      ..layout(minWidth: 0, maxWidth: double.infinity);
    return textPainter.size;
  }

  double widthWithStyle(TextStyle style) {
    return sizeWithStyle(style).width;
  }

  double heigthWithStyle(TextStyle style) {
    return sizeWithStyle(style).height;
  }
}