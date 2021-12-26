#import "EyflutterCorePlugin.h"
#if __has_include(<eyflutter_core/eyflutter_core-Swift.h>)
#import <eyflutter_core/eyflutter_core-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "eyflutter_core-Swift.h"
#endif

#import <MMKV/MMKV.h>
#import "device/DeviceUtils.h"
#import "device/DeviceBundleInfo.h"

NSString *mmkvUserKey = @"70746218ab58dc96";
NSString *mmkvOrdinaryKey = @"c694f6da00968d67";
NSString *mmkvStateKey = @"fa7463fbf7bc5a65";

static MMKV *userMmkv;
static MMKV *ordinaryMmkv;
static MMKV *stateMmkv;

@implementation EyflutterCorePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    [SwiftEyflutterCorePlugin registerWithRegistrar:registrar];
    //init mmkv
    [self initMmkv];
}

+ (void)initMmkv {
    NSString *mmkvGroupId = @"com.cloud.mmkv";
    NSString *groupDir = [[NSFileManager defaultManager] containerURLForSecurityApplicationGroupIdentifier:mmkvGroupId].path;
    [MMKV initializeMMKV:nil groupDir:groupDir logLevel:MMKVLogInfo];
    [self checkMmkv];
}

+ (void)checkMmkv {
    if (ordinaryMmkv == nil) {
        ordinaryMmkv = [MMKV defaultMMKV];
    }
    if (userMmkv == nil) {
        userMmkv = [MMKV mmkvWithID:mmkvUserKey];
    }
    if (stateMmkv == nil) {
        stateMmkv = [MMKV mmkvWithID:mmkvStateKey];
    }
}

+ (void)cleanMmkv {
    ordinaryMmkv = nil;
    userMmkv = nil;
    stateMmkv = nil;
}

+ (id)getMMKVInstance:(NSString *)withId {
    if ([withId isEqual:mmkvUserKey]) {
        return userMmkv;
    } else if ([withId isEqual:mmkvStateKey]) {
        return stateMmkv;
    } else {
        return ordinaryMmkv;
    }
}

//缓存数据
+ (void) cacheData:(NSDictionary*) params
onMmkvIntCall:(MmkvIntCall) intCall
onMmkvDoubleCall:(MmkvDoubleCall) doubleCall
onMmkvStringCall:(MmkvStringCall) stringCall
onMmkvBoolCall:(MmkvBoolCall) boolCall; {
    if (params == nil) {
        return;
    }
    NSString *callKey = [params valueForKey:@"flutter_result_call_key"];
    NSString *withId = [params valueForKey:@"withId"];
    NSString *key = [params valueForKey:@"key"];
    NSString *type = [params valueForKey:@"type"];
    NSString *value = [params valueForKey:@"value"];
    if (key == nil || key.length == 0 || value == nil) {
        return;
    }
    MMKV *_mmkv = [self getMMKVInstance:withId];
    @try {
        if ([type isEqual:@"int"]) {
            [_mmkv setInt32:[value intValue] forKey:key];
            
            intCall(callKey,[value intValue]);
            
        } else if ([type isEqual:@"double"]) {
            [_mmkv setDouble:[value doubleValue] forKey:key];
            
            doubleCall(callKey,[value doubleValue]);
            
        } else if ([type isEqual:@"string"]) {
            [_mmkv setString:value forKey:key];
            
            stringCall(callKey,value);
        
        } else if ([type isEqual:@"bool"]) {
            [_mmkv setBool:[value boolValue] forKey:key];
            
            boolCall(callKey,[value boolValue]);
            
        }
    } @catch (NSException *exception) {
        NSLog(@"%@", exception);
    }
}

+ (void)takeData:(NSDictionary *)params onMmkvIntCall:(MmkvIntCall)intCall onMmkvDoubleCall:(MmkvDoubleCall)doubleCall onMmkvStringCall:(MmkvStringCall)stringCall onMmkvBoolCall:(MmkvBoolCall)boolCall {
    if (params == nil) {
        return;
    }
    NSString *callKey = [params valueForKey:@"flutter_result_call_key"];
    NSString *withId = [params valueForKey:@"withId"];
    NSString *key = [params valueForKey:@"key"];
    NSString *type = [params valueForKey:@"type"];
    MMKV *_mmkv = [self getMMKVInstance:withId];
    @try {
        if ([type isEqual:@"int"]) {
            intCall(callKey, [_mmkv getInt32ForKey:key]);
        } else if ([type isEqual:@"double"]) {
            doubleCall(callKey, [_mmkv getDoubleForKey:key]);
        } else if ([type isEqual:@"string"]) {
            stringCall(callKey, [_mmkv getStringForKey:key]);
        } else if ([type isEqual:@"bool"]) {
            boolCall(callKey, [_mmkv getBoolForKey:key]);
        }
    } @catch (NSException *exception) {
        NSLog(@"%@", exception);
    }
}

+ (void)removeMmkvKey:(NSDictionary *)params
     onMmkvRemoveCall:(MmkvRemoveCall) removeCall{
    NSString *callKey = [params valueForKey:@"flutter_result_call_key"];
    NSString *withId = [params valueForKey:@"withId"];
    NSString *key = [params valueForKey:@"key"];
    MMKV *_mmkv = [self getMMKVInstance:withId];
    @try {
        [_mmkv removeValueForKey:key];
        removeCall(callKey,withId);
    } @catch (NSException *exception) {
        NSLog(@"%@", exception);
    }
}

+ (NSString*)getString:(NSString *)key{
    MMKV *_mmkv = [self getMMKVInstance:mmkvOrdinaryKey];
    NSString *result = [_mmkv getStringForKey:key];
    if (result == nil) {
        return @"";
    }
    return result;
}

+ (NSDictionary *)getDeviceResponse {
    NSDictionary *deviceMap = [[NSMutableDictionary alloc] init];
    DeviceBundleInfo* bundle = [DeviceUtils getDeviceOrBundleInfo];
    [deviceMap setValue:@"deviceId" forKey:[bundle getUniqueGlobalDeviceId]];
    [deviceMap setValue:@"deviceName" forKey:[bundle getDeviceName]];
    [deviceMap setValue:@"systemName" forKey:[bundle getSysName]];
    [deviceMap setValue:@"sysVersion" forKey:[bundle getSysVersion]];
    [deviceMap setValue:@"model" forKey:[bundle getModel]];
    [deviceMap setValue:@"manufacturer" forKey:[bundle getManufacturer]];
    [deviceMap setValue:@"ip" forKey:[bundle getIp]];
    [deviceMap setValue:@"brand" forKey:[bundle getBrand]];
    [deviceMap setValue:@"resolution" forKey:[bundle getDisplay]];
    [deviceMap setValue:@"density" forKey:[bundle getDensity]];
    [deviceMap setValue:@"language" forKey:[bundle getSysLanguage]];
    return deviceMap;
}

@end
