//
//  DeviceBundleInfo.h
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

#ifndef DeviceBundleInfo_h
#define DeviceBundleInfo_h


#endif /* DeviceBundleInfo_h */

#import <Foundation/Foundation.h>

@interface DeviceBundleInfo : NSObject
{
@private NSString *_deviceName;
@private NSString *_sysName;
@private NSString *_sysVersion;
@private NSString *_model;
@private NSString *_locModel;
@private NSString *_appName;
@private NSString *_appVersion;
@private NSString *_appBuildVersion;
@private NSString *_sysLanguage;
@private NSString *_countryCode;
@private NSString *_uniqueGlobalDeviceId;
@private NSString *_manufacturer;
@private NSString *_brand;
@private NSString *_display;
@private NSString *_density;
@private NSString *_ip;
@private NSString *_mac;
}
-(void)setDeviceName:(NSString *)deviceName;
-(NSString *)getDeviceName;

-(void)setSysName:(NSString *)sysName;
-(NSString *)getSysName;

-(void)setSysVersion:(NSString *)sysVersion;
-(NSString *)getSysVersion;

-(void)setModel:(NSString *)model;
-(NSString *)getModel;

-(void)setLocModel:(NSString *)locModel;
-(NSString *)getLocModel;

-(void)setAppName:(NSString *)appName;
-(NSString *)getAppName;

-(void)setAppVersion:(NSString *)appVersion;
-(NSString *)getAppVersion;

-(void)setAppBuildVersion:(NSString *)appBuildVersion;
-(NSString *)getAppBuildVersion;

-(void)setSysLanguage:(NSString *)sysLanguage;
-(NSString *)getSysLanguage;

-(void)setCountryCode:(NSString *)countryCode;
-(NSString *)getCountryCode;

-(void)setUniqueGlobalDeviceId:(NSString *)uniqueid;
-(NSString *)getUniqueGlobalDeviceId;

-(void)setManufacturer:(NSString *)manufacturer;
-(NSString *)getManufacturer;

-(void)setBrand:(NSString *)brand;
-(NSString *)getBrand;

-(void)setDisplay:(NSString *)display;
-(NSString *)getDisplay;

-(void)setDensity:(NSString *)density;
-(NSString *)getDensity;

-(void)setIp:(NSString *)ip;
-(NSString *)getIp;

-(void)setMac:(NSString *)mac;
-(NSString *)getMac;
@end
