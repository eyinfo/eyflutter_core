#import <Flutter/Flutter.h>

typedef void(^MmkvIntCall) (NSString* callKey, int value);
typedef void(^MmkvDoubleCall) (NSString* callKey, double value);
typedef void(^MmkvStringCall) (NSString* callKey, NSString* value);
typedef void(^MmkvBoolCall) (NSString* callKey, bool value);
typedef void(^MmkvRemoveCall) (NSString* callKey, NSString* withId);

@interface EyflutterCorePlugin : NSObject<FlutterPlugin>

+ (void) initMmkv;
+ (void) checkMmkv;
+ (void) cleanMmkv;

//实例化mmkv
+ (id) getMMKVInstance:(NSString*) withId;
////缓存数据
//+ (void) cacheData:(NSDictionary*) params
//缓存数据
+ (void) cacheData:(NSDictionary*) params
     onMmkvIntCall:(MmkvIntCall) intCall
  onMmkvDoubleCall:(MmkvDoubleCall) doubleCall
  onMmkvStringCall:(MmkvStringCall) stringCall
    onMmkvBoolCall:(MmkvBoolCall) boolCall;
//提取数据
+ (void) takeData:(NSDictionary*) params
    onMmkvIntCall:(MmkvIntCall) intCall
 onMmkvDoubleCall:(MmkvDoubleCall) doubleCall
 onMmkvStringCall:(MmkvStringCall) stringCall
   onMmkvBoolCall:(MmkvBoolCall) boolCall;
//删除key对应的缓存
+ (void) removeMmkvKey:(NSDictionary*) params
    onMmkvRemoveCall:(MmkvRemoveCall) removeCall;

//获取字符串数据
+ (NSString*) getString:(NSString*) key;

+(NSDictionary *)getDeviceResponse;
@end
