import 'formatter.dart';

class StringFormatter extends Formatter {
  var _arg;

  StringFormatter(this._arg, var formatType, var options) : super(formatType, options) {
    options['padding_char'] = ' ';
  }

  String asString() {
    String ret = _arg.toString();
    if (options['precision'] > -1 && options['precision'] <= ret.length) {
      ret = ret.substring(0, options['precision']);
    }
    if (options['width'] > -1) {
      int diff = (options['width'] - ret.length);
      if (diff > 0) {
        String padding = Formatter.getPadding(diff, options['padding_char']);
        if (!options['left_align']) {
          ret = "$padding$ret";
        } else {
          ret = "$ret$padding";
        }
      }
    }
    return ret;
  }
}
