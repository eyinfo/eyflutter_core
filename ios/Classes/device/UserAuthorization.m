//
//  UserAuthorization.m
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

#import "UserAuthorization.h"
#import <UIKit/UIKit.h>
#import <Photos/PHPhotoLibrary.h>
#import <CoreLocation/CoreLocation.h>
#import <AVFoundation/AVCaptureDevice.h>
#import <objc/runtime.h>
#import <UserNotifications/UserNotifications.h>
@implementation UserAuthorization
+ (void)authorizationsWithParams:(NSDictionary *)params callBack:(authCallBack)callBack{
    NSString *callKey = params[@"flutter_result_call_key"];
    NSString *prompt = params[@"prompt"];
    NSString *cancle = params[@"cancle"];
    NSString *setting = params[@"setting"];
    NSArray *permissions = params[@"permissions"];
    
    NSMutableDictionary *alertParam = @{
        @"message":[self isBlankString:prompt]?@"易由信息需要获取您的授权信息":prompt,
        @"cancle":[self isBlankString:cancle]?@"cancle":cancle,
        @"setting":[self isBlankString:setting]?@"setting":setting,
        @"autoauth":@(YES)
    }.mutableCopy;
    
    if (permissions.count == 0) {
        callBack(callKey,false);
        return;
    }
    BOOL hasAuth = YES;
    [self _authorizationsWithIndex:0 array:permissions parame:alertParam callkey:callKey callBack:callBack hasAuth:hasAuth];
    
}

+ (void)_authorizationsWithIndex:(int)index array:(NSArray *)array parame:(NSMutableDictionary *)parame callkey:(NSString *)callkey callBack:(authCallBack)callBack hasAuth:(BOOL)hasAuth{
    if (index == array.count) {
        callBack(callkey,hasAuth);
        return;
    }
    if (index > array.count) {
        return;
    }
    NSString *permission = [array objectAtIndex:index];
    parame[@"autoauth"] = @(YES);
    __block BOOL _hasAuth = hasAuth;
    __block int _index = index;
    
    authCompleteBlock compblock = ^(BOOL graden){
        // 只要授权队列里面的有一个未授权就返回no
        if (!graden) {
            _hasAuth = graden;
        }
        _index++;
        [self _authorizationsWithIndex:_index array:array parame:parame callkey:callkey callBack:callBack hasAuth:_hasAuth];
    };
    
    SEL sel = NSSelectorFromString([NSString stringWithFormat:@"%@:complete:",permission]);
    if ([self respondsToSelector:sel]) {
       [self performSelector:sel withObject:parame withObject:compblock];
    }else{
        compblock(YES);
    }
}
//麦克风
+ (void)hasAudioAuthorization:(NSMutableDictionary *)parame  complete:(void (^)(BOOL))complete{
    
    AVAuthorizationStatus status = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeAudio];
    BOOL  autoauth =[parame[@"autoauth"] boolValue];
    if (status==AVAuthorizationStatusDenied||status==AVAuthorizationStatusRestricted) {
        if (autoauth) {
            [self openSystemSettingParame:parame];
        }
        if (complete) {
            complete(NO);
        }
    }else if(status ==AVAuthorizationStatusNotDetermined){
        [AVCaptureDevice requestAccessForMediaType:AVMediaTypeAudio completionHandler:^(BOOL granted) {
            if (complete) {
                complete(granted);
            }
        }];
    }else{
        if (complete) {
            complete(YES);
        }
    }
}
//相机
+ (void)hasVideoAuthorization:(NSMutableDictionary *)parame  complete:(void (^)(BOOL))complete{

    AVAuthorizationStatus status = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    BOOL  autoauth =[parame[@"autoauth"] boolValue];
    if (status==AVAuthorizationStatusDenied||status==AVAuthorizationStatusRestricted) {
        if (autoauth) {
            [self openSystemSettingParame:parame];
        }
        if (complete) {
            complete(NO);
        }
    } if(status ==AVAuthorizationStatusNotDetermined){
        [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
            if (complete) {
                complete(granted);
            }
        }];
    }else{
        if (complete) {
            complete(YES);
        }
    }
    
}

//相册
+ (void)hasPhotoAuthorization:(NSMutableDictionary *)parame complete:(void (^)(BOOL))complete{
    
    PHAuthorizationStatus status = [PHPhotoLibrary authorizationStatus];
    BOOL  autoauth =[parame[@"autoauth"] boolValue];
    if (status ==PHAuthorizationStatusDenied||status==PHAuthorizationStatusRestricted) {
        if (autoauth) {
            [self openSystemSettingParame:parame];
        }
        if (complete) {
            complete(NO);
        }
    }else if (status == PHAuthorizationStatusNotDetermined){
        [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) {
            parame[@"autoauth"] = @(NO);
            [self hasPhotoAuthorization:parame complete:complete];
        }];
    }else{
        if (complete) {
            complete(YES);
        }
    }
}
//位置
+ (void)hasLocationAuthorization:(NSMutableDictionary *)parame complete:(void (^)(BOOL))complete{
    
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    BOOL  autoauth =[parame[@"autoauth"] boolValue];
    if (status == kCLAuthorizationStatusRestricted||status == kCLAuthorizationStatusDenied) {
        if (autoauth) {
            [self openSystemSettingParame:parame];
        }
        if (complete) {
            complete(NO);
        }
    }else{
        if (complete) {
            complete(YES);
        }
    }
    
}

//推送
+ (void)hasUserNotificationAuthorization:(NSMutableDictionary *)parame complete:(void (^)(BOOL))complete{
    
    UIUserNotificationType status = [[UIApplication sharedApplication] currentUserNotificationSettings].types;
    BOOL  autoauth =[parame[@"autoauth"] boolValue];
    if (status == UIUserNotificationTypeNone) {
        if (autoauth) {
            [self openSystemSettingParame:parame];
        }
        if (complete) {
            complete(NO);
        }
    }else {
        if (@available(iOS 10.0, *)) {
          [[UNUserNotificationCenter currentNotificationCenter]requestAuthorizationWithOptions:(UNAuthorizationOptionAlert | UNAuthorizationOptionBadge | UNAuthorizationOptionSound) completionHandler:^(BOOL granted, NSError * _Nullable error) {
              if (complete) {
                  complete(granted);
              }
          }];
        }else {
          //iOS10之前，系统对于申请推送权限没有具体的API，只有设置NotificationSettings时，会自动请求权限
          UIUserNotificationSettings *setting = [UIUserNotificationSettings settingsForTypes:UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge categories:nil];
          [[UIApplication sharedApplication] registerUserNotificationSettings:setting];
            parame[@"autoauth"] = @(NO);
            [self hasUserNotificationAuthorization:parame complete:complete];
        }
    }
}


//打开设置界面
+ (void)openSystemSettingParame:(NSDictionary *)parame{
    NSString *message = parame[@"message"];
    NSString *cancle = parame[@"cancle"];
    NSString *setting = parame[@"setting"];
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"" message:[self isBlankString:message]?@"":message preferredStyle:UIAlertControllerStyleAlert];
    
    [alert addAction:[UIAlertAction actionWithTitle:[self isBlankString:cancle]?@"":cancle style:UIAlertActionStyleCancel handler:nil]];
    [alert addAction:[UIAlertAction actionWithTitle:[self isBlankString:setting]?@"":setting style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        
        NSURL *settingUrl = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
        
        if ([[UIApplication sharedApplication]canOpenURL:settingUrl]) {
            
            if (@available(iOS 10.0, *)) {
                [[UIApplication sharedApplication]openURL:settingUrl options:[NSDictionary dictionary] completionHandler:nil];
            }else{
                [[UIApplication sharedApplication]openURL:settingUrl];
            }
            
        }
        
    }]];
    
    UIViewController *rootvc = [UIApplication sharedApplication].keyWindow.rootViewController;
    alert.modalPresentationStyle = UIModalPresentationFullScreen;
    
    [rootvc presentViewController:alert animated:YES completion:nil];
}





#pragma mark ——————string相关——————
+  (BOOL) isBlankString:(id )string {
    if (string == nil || string == NULL) {
        return YES;
    }
    
    if ([string isKindOfClass:[NSNull class]]) {
        return YES;
    }
    if (![string isKindOfClass:[NSString class]]) {
        return NO;
    }
    NSAssert([string isKindOfClass:[NSString class]], @"当前的string不是字符串或者不是字符串的子类");
    
    if ([string isEqualToString:@""] || [string isEqual:@"<null>"] || [string isEqual:@"(null)"]) {
        return YES;
    }
    
    if ([[string stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]] length]==0) {
        return YES;
    }
    return NO;
}
@end
