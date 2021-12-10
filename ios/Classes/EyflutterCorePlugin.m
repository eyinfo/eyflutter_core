#import "EyflutterCorePlugin.h"
#if __has_include(<eyflutter_core/eyflutter_core-Swift.h>)
#import <eyflutter_core/eyflutter_core-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "eyflutter_core-Swift.h"
#endif

@implementation EyflutterCorePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftEyflutterCorePlugin registerWithRegistrar:registrar];
}
@end
