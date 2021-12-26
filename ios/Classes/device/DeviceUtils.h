//
//  DeviceUtils.h
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

#ifndef DeviceUtils_h
#define DeviceUtils_h


#endif /* DeviceUtils_h */

#import <Foundation/Foundation.h>
#import "DeviceBundleInfo.h"
#import "MD5Addition.h"
#import "UIDevice+IdentifierAddition.h"

@interface DeviceUtils : NSObject
+(NSString *)getGuid;
+(NSString *)getRuntimeDirectory;
+(DeviceBundleInfo *)getDeviceOrBundleInfo;
@end
