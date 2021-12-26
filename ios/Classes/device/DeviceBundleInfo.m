//
//  DeviceBundleInfo.m
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

#import "DeviceBundleInfo.h"

@implementation DeviceBundleInfo
-(void)setDeviceName:(NSString *)deviceName
{
    _deviceName=deviceName;
}
-(NSString *)getDeviceName
{
    return _deviceName;
}
-(void)setSysName:(NSString *)sysName
{
    _sysName=sysName;
}
-(NSString *)getSysName
{
    return _sysName;
}
-(void)setSysVersion:(NSString *)sysVersion
{
    _sysVersion=sysVersion;
}
-(NSString *)getSysVersion
{
    return _sysVersion;
}
-(void)setModel:(NSString *)model
{
    _model=model;
}
-(NSString *)getModel
{
    return _model;
}
-(void)setLocModel:(NSString *)locModel
{
    _locModel=locModel;
}
-(NSString *)getLocModel
{
    return _locModel;
}
-(void)setAppName:(NSString *)appName
{
    _appName=appName;
}
-(NSString *)getAppName
{
    return _appName;
}
-(void)setAppVersion:(NSString *)appVersion
{
    _appVersion=appVersion;
}
-(NSString *)getAppVersion
{
    return _appVersion;
}
-(void)setAppBuildVersion:(NSString *)appBuildVersion
{
    _appBuildVersion=appBuildVersion;
}
-(NSString *)getAppBuildVersion
{
    return _appBuildVersion;
}
-(void)setSysLanguage:(NSString *)sysLanguage
{
    _sysLanguage=sysLanguage;
}
-(NSString *)getSysLanguage
{
    return _sysLanguage;
}
-(void)setCountryCode:(NSString *)countryCode
{
    _countryCode=countryCode;
}
-(NSString *)getCountryCode
{
    return _countryCode;
}
-(void)setUniqueGlobalDeviceId:(NSString *)uniqueid
{
    _uniqueGlobalDeviceId=uniqueid;
}
-(NSString *)getUniqueGlobalDeviceId
{
    return _uniqueGlobalDeviceId;
}
-(void)setManufacturer:(NSString *)manufacturer {
    _manufacturer = manufacturer;
}
- (NSString *)getManufacturer {
    return _manufacturer;
}
- (void)setBrand:(NSString *)brand {
    _brand = brand;
}
- (NSString *)getBrand {
    return _brand;
}
- (void)setDisplay:(NSString *)display {
    _display = display;
}
- (NSString *)getDisplay {
    return _display;
}
- (void)setDensity:(NSString *)density {
    _density = density;
}
- (NSString *)getDensity {
    return _density;
}

- (void)setIp:(NSString *)ip {
    _ip = ip;
}
- (NSString *)getIp {
    return _ip;
}

- (void)setMac:(NSString *)mac {
    _mac = mac;
}
- (NSString *)getMac {
    return _mac;
}
@end
