import 'formatter.dart';

class IntFormatter extends Formatter {
  int _arg;
  static const int MAX_INT = 0x1FFFFFFFFFFFFF; // javascript 53bit

  IntFormatter(this._arg, var formatType, var options) : super(formatType, options);

  String asString() {
    String ret = '';
    String prefix = '';
    int radix = formatType == 'x' ? 16 : (formatType == 'o' ? 8 : 10);
    if (_arg < 0) {
      if (radix == 10) {
        _arg = _arg.abs();
        options['sign'] = '-';
      } else {
        // sort of reverse twos complement
        _arg = (MAX_INT - (~_arg) & MAX_INT);
      }
    }
    ret = _arg.toRadixString(radix);
    if (options['alternate_form']) {
      if (radix == 16 && _arg != 0) {
        prefix = "0x";
      } else if (radix == 8 && _arg != 0) {
        prefix = "0";
      }
      if (options['sign'] == '+' && radix != 10) {
        options['sign'] = '';
      }
    }
    // space "prefixes non-negative signed numbers with a space"
    if ((options['add_space'] && options['sign'] == '' && _arg > -1 && radix == 10)) {
      options['sign'] = ' ';
    }

    if (radix != 10) {
      options['sign'] = '';
    }
    String padding = '';
    var minDigits = options['precision'];
    var minChars = options['width'];
    int numLength = ret.length;
    var signLength = options['sign'].length;
    num strLen = 0;
    if (radix == 8 && minChars <= minDigits) {
      numLength += prefix.length;
    }
    if (minDigits > numLength) {
      padding = Formatter.getPadding(minDigits - numLength, '0');
      ret = "$padding$ret";
      numLength = ret.length;
      padding = '';
    }
    strLen = numLength + signLength + prefix.length;
    if (minChars > strLen) {
      if (options['padding_char'] == '0' && !options['left_align']) {
        padding = Formatter.getPadding(minChars - strLen, '0');
      } else {
        padding = Formatter.getPadding(minChars - strLen, ' ');
      }
    }
    if (options['left_align']) {
      ret = "${options['sign']}$prefix$ret$padding";
    } else if (options['padding_char'] == '0') {
      ret = "${options['sign']}$prefix$padding$ret";
    } else {
      ret = "$padding${options['sign']}$prefix$ret";
    }
    if (options['is_upper']) {
      ret = ret.toUpperCase();
    }
    return ret;
  }
}
