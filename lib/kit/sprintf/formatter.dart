abstract class Formatter {
  var formatType;
  var options;

  Formatter(this.formatType, this.options);

  static String getPadding(int count, String pad) {
    String paddingPiece = pad;
    StringBuffer padding = new StringBuffer();
    while (count > 0) {
      if ((count & 1) == 1) {
        padding.write(paddingPiece);
      }
      count >>= 1;
      paddingPiece = "$paddingPiece$paddingPiece";
    }
    return padding.toString();
  }

  String asString();
}
