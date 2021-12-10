import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:eyflutter_core/eyflutter_core.dart';

void main() {
  const MethodChannel channel = MethodChannel('eyflutter_core');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await EyflutterCore.platformVersion, '42');
  });
}
