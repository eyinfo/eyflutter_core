import 'dart:math';

import 'package:eyflutter_core/kit/utils/num_extension.dart';
import 'package:eyflutter_core/kit/utils/string/string_extension.dart';

import 'formatter.dart';

class FloatFormatter extends Formatter {
  static final _numberRx = new RegExp(r'^[\-\+]?(\d+)\.(\d+)$');
  static final _expoRx = new RegExp(r'^[\-\+]?(\d)\.(\d+)e([\-\+]?\d+)$');
  static final _leadingZeroesRx = new RegExp(r'^(0*)[1-9]+');

  double? _arg;
  List<int> _digits = <int>[];
  int _exponent = 0;
  int _decimal = 0;
  bool _isNegative = false;
  bool _hasInit = false;
  String? _output;

  FloatFormatter(this._arg, var formatType, var options) : super(formatType, options) {
    if ((_arg ?? 0) < 0) {
      this._isNegative = true;
      _arg = -(_arg ?? 0);
    }
    String argStr = "${(_arg ?? 0)}";
    Match? m1 = _numberRx.firstMatch(argStr);
    if (m1 != null) {
      String intPart = m1.group(1) ?? "";
      String fraction = m1.group(2) ?? "";
      /*
       * Cases:
       * 1.2345    = 1.2345e0  -> [12345]    e+0 d1  l5
       * 123.45    = 1.2345e2  -> [12345]    e+2 d3  l5
       * 0.12345   = 1.2345e-1 -> [012345]   e-1 d1  l6
       * 0.0012345 = 1.2345e-3 -> [00012345] e-3 d1  l8
       */
      _decimal = intPart.length;
      _digits.addAll(intPart.split('').map(int.parse));
      _digits.addAll(fraction.split('').map(int.parse));
      if (intPart.length == 1) {
        if (intPart == '0') {
          Match? leadingZeroesMatch = _leadingZeroesRx.firstMatch(fraction);
          if (leadingZeroesMatch != null) {
            int zeroesCount = leadingZeroesMatch.group(1)?.length ?? 0;
            _exponent = zeroesCount > 0 ? -(zeroesCount + 1) : zeroesCount - 1;
          } else {
            _exponent = 0;
          }
        } // else int_part != 0
        else {
          _exponent = 0;
        }
      } else {
        _exponent = intPart.length - 1;
      }
    } else {
      Match? m2 = _expoRx.firstMatch(argStr);
      if (m2 != null) {
        String intPart = m2.group(1) ?? "";
        String fraction = m2.group(2) ?? "";
        _exponent = m2.group(3).toInt;

        if (_exponent > 0) {
          int diff = _exponent - fraction.length + 1;
          _decimal = _exponent + 1;
          _digits.addAll(intPart.split('').map(int.parse));
          _digits.addAll(fraction.split('').map(int.parse));
          _digits.addAll(Formatter.getPadding(diff, '0').split('').map(int.parse));
        } else {
          int diff = intPart.length - _exponent - 1;
          _decimal = intPart.length;
          _digits.addAll(Formatter.getPadding(diff, '0').split('').map(int.parse));
          _digits.addAll(intPart.split('').map(int.parse));
          _digits.addAll(fraction.split('').map(int.parse));
        }
      } // else something wrong
    }
    _hasInit = true;
  }

  String asString() {
    String ret = '';
    if (!_hasInit) {
      return ret;
    }
    if (_output != null) {
      return _output ?? "";
    }
    if (options['add_space'] && options['sign'] == '' && (_arg ?? 0) >= 0) {
      options['sign'] = ' ';
    }
    if ((_arg ?? 0).isInfinite) {
      if ((_arg ?? 0).isNegative) {
        options['sign'] = '-';
      }
      ret = 'inf';
      options['padding_char'] = ' ';
    }
    if ((_arg ?? 0).isNaN) {
      ret = 'nan';
      options['padding_char'] = ' ';
    }
    if (options['precision'] == -1) {
      options['precision'] = 6;
    } else if (formatType == 'g' && options['precision'] == 0) {
      options['precision'] = 1;
    }
    if (_arg is num) {
      if (_isNegative) {
        options['sign'] = '-';
      }
      if (formatType == 'e') {
        ret = asExponential(options['precision'], removeTrailingZeros: false);
      } else if (formatType == 'f') {
        ret = asFixed(options['precision'], removeTrailingZeros: false);
      } else {
        // type == g
        int _exp = _exponent;
        var sigDigs = options['precision'];
        if (-4 <= _exp && _exp < options['precision']) {
          sigDigs -= _decimal;
          num precision = max(options['precision'] - 1 - _exp, sigDigs);
          ret = asFixed(precision.toNumInt, removeTrailingZeros: !options['alternate_form']);
        } else {
          ret = asExponential(options['precision'] - 1, removeTrailingZeros: !options['alternate_form']);
        }
      }
    }
    var minChars = options['width'];
    num strLen = ret.length + options['sign'].length;
    String padding = '';
    if (minChars > strLen) {
      if (options['padding_char'] == '0' && !options['left_align']) {
        padding = Formatter.getPadding(minChars - strLen, '0');
      } else {
        padding = Formatter.getPadding(minChars - strLen, ' ');
      }
    }
    if (options['left_align']) {
      ret = "${options['sign']}$ret$padding";
    } else if (options['padding_char'] == '0') {
      ret = "${options['sign']}$padding$ret";
    } else {
      ret = "$padding${options['sign']}$ret";
    }
    if (options['is_upper']) {
      ret = ret.toUpperCase();
    }
    return (_output = ret);
  }

  String asFixed(int precision, {bool removeTrailingZeros = true}) {
    // precision is the number of decimal places after the decimal point to keep
    int offset = _decimal + precision - 1;
    int extraZeroes = precision - (_digits.length - offset);
    if (extraZeroes > 0) {
      _digits.addAll(Formatter.getPadding(extraZeroes, '0').split('').map(int.parse));
    }
    _round(offset + 1, offset);
    String ret = _digits.sublist(0, _decimal).fold('', (i, e) => "$i$e");
    List<int> trailingDigits = _digits.sublist(_decimal, _decimal + precision);
    if (removeTrailingZeros) {
      trailingDigits = _removeTrailingZeros(trailingDigits);
    }
    var trailingZeroes = trailingDigits.fold('', (i, e) => "$i$e");
    if (trailingZeroes.length == 0) {
      return ret;
    }
    ret = "$ret.$trailingZeroes";
    return ret;
  }

  String asExponential(int precision, {bool removeTrailingZeros = true}) {
    int offset = _decimal - _exponent;
    int extraZeroes = precision - (_digits.length - offset) + 1;
    if (extraZeroes > 0) {
      _digits.addAll(Formatter.getPadding(extraZeroes, '0').split('').map(int.parse));
    }
    _round(offset + precision, offset);
    String ret = _digits[offset - 1].toString();
    List<int> trailingDigits = _digits.sublist(offset, offset + precision);
    String _expStr = _exponent.abs().toString();
    if (_exponent < 10 && _exponent > -10) {
      _expStr = "0$_expStr";
    }
    _expStr = (_exponent < 0) ? "e-$_expStr" : "e+$_expStr";
    if (removeTrailingZeros) {
      trailingDigits = _removeTrailingZeros(trailingDigits);
    }
    if (trailingDigits.length > 0) {
      ret += '.';
    }
    ret = trailingDigits.fold(ret, (i, e) => "$i$e");
    ret = "$ret$_expStr";
    return ret;
  }

  List<int> _removeTrailingZeros(List<int> trailingDigits) {
    int nZeroes = 0;
    for (int i = trailingDigits.length - 1; i >= 0; i--) {
      if (trailingDigits[i] == 0) {
        nZeroes++;
      } else {
        break;
      }
    }
    return trailingDigits.sublist(0, trailingDigits.length - nZeroes);
  }

  /*
	rounding_offset: Where to start rounding from
	offset: where to end rounding
	 */
  void _round(var roundingOffset, var offset) {
    int carry = 0;
    if (roundingOffset >= _digits.length) {
      return;
    }
    // Round the digit after the precision
    int d = _digits[roundingOffset];
    carry = d >= 5 ? 1 : 0;
    _digits[roundingOffset] = d % 10;
    roundingOffset -= 1;
    //propagate the carry
    while (carry > 0) {
      d = _digits[roundingOffset] + carry;
      if (roundingOffset == 0 && d > 9) {
        _digits.insert(0, 0);
        _decimal += 1;
        roundingOffset += 1;
      }
      carry = d < 10 ? 0 : 1;
      _digits[roundingOffset] = d % 10;
      roundingOffset -= 1;
    }
  }
}
