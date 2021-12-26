//
//  DeviceUtils.m
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

#import "DeviceUtils.h"
#import <UIKit/UIKit.h>
#import <ifaddrs.h>
#import <arpa/inet.h>
#import <net/if.h>
#import <CommonCrypto/CommonDigest.h>


#define IOS_CELLULAR    @"pdp_ip0"
#define IOS_WIFI        @"en0"
#define IOS_VPN         @"utun0"
#define IP_ADDR_IPv4    @"ipv4"
#define IP_ADDR_IPv6    @"ipv6"

@implementation DeviceUtils
+(NSString *)getGuid
{
    CFUUIDRef uuid_ref=CFUUIDCreate(nil);
    CFStringRef cfstring_ref=CFUUIDCreateString(nil, uuid_ref);
    CFRelease(uuid_ref);
    NSString *uuid=[NSString stringWithString:(__bridge NSString *)(cfstring_ref)];
    CFRelease(cfstring_ref);
    return uuid;
}
+(NSString *)getRuntimeDirectory
{
    NSString *escapedPath =[[NSBundle mainBundle] bundlePath];
    NSArray *strings=[escapedPath componentsSeparatedByString:@"/"];
    NSString *tmpFilename=[strings objectAtIndex:[strings count]-1];
    NSRange iStart=[escapedPath rangeOfString:tmpFilename];
    return [escapedPath substringToIndex:iStart.location-1];
}
+(DeviceBundleInfo *)getDeviceOrBundleInfo
{
    DeviceBundleInfo *dinfo=[[DeviceBundleInfo alloc] init];
    UIDevice *device=[UIDevice currentDevice];
    [dinfo setDeviceName:device.name];//设备名称
    [dinfo setSysName:device.systemName];//系统名称
    [dinfo setSysVersion:device.systemVersion];//系统版本
    [dinfo setModel:device.model];//设备模式
    [dinfo setLocModel:device.localizedModel];//本地设备模式
    [dinfo setUniqueGlobalDeviceId:device.uniqueGlobalDeviceIdentifier];
    NSDictionary *dicinfo=[[NSBundle mainBundle] infoDictionary];
    [dinfo setAppName:[dicinfo objectForKey:@"CFBundleDisplayName"]];
    [dinfo setAppVersion:[dicinfo objectForKey:@"CFBundleShortVersionString"]];
    [dinfo setAppBuildVersion:[dicinfo objectForKey:@"CFBundleVersion"]];
    [dinfo setSysLanguage:[[NSLocale preferredLanguages] objectAtIndex:0]];
    [dinfo setCountryCode:[[NSLocale currentLocale] localeIdentifier]];
    [dinfo setManufacturer:@"Apple"];
    [dinfo setBrand:@"Apple"];
    [dinfo setIp: [self getIPAddress:true]];
    CGRect screenFrame = [UIScreen mainScreen].bounds;
    int screenWidth = screenFrame.size.width;
    int screenHeight = screenFrame.size.height;
    [dinfo setDisplay:[NSString stringWithFormat:@"%dx%d", screenWidth, screenHeight]];
    float scale_screen = [UIScreen mainScreen].scale;
    [dinfo setDensity:[NSString stringWithFormat:@"%f", scale_screen]];
    return dinfo;
}

+ (NSString *)getIPAddress:(BOOL)preferIPv4 {
    NSArray *searchArray = preferIPv4 ?
    @[ IOS_VPN @"/" IP_ADDR_IPv4, IOS_VPN @"/" IP_ADDR_IPv6, IOS_WIFI @"/" IP_ADDR_IPv4, IOS_WIFI @"/" IP_ADDR_IPv6, IOS_CELLULAR @"/" IP_ADDR_IPv4, IOS_CELLULAR @"/" IP_ADDR_IPv6 ] :
    @[ IOS_VPN @"/" IP_ADDR_IPv6, IOS_VPN @"/" IP_ADDR_IPv4, IOS_WIFI @"/" IP_ADDR_IPv6, IOS_WIFI @"/" IP_ADDR_IPv4, IOS_CELLULAR @"/" IP_ADDR_IPv6, IOS_CELLULAR @"/" IP_ADDR_IPv4 ] ;
    
    NSDictionary *addresses = [self getIPAddresses];
    NSLog(@"addresses: %@", addresses);
    
    __block NSString *address;
    [searchArray enumerateObjectsUsingBlock:^(NSString *key, NSUInteger idx, BOOL *stop)
     {
         address = addresses[key];
         //筛选出IP地址格式
         if([self isValidatIP:address]) *stop = YES;
     } ];
    return address ? address : @"0.0.0.0";
}

+ (BOOL)isValidatIP:(NSString *)ipAddress {
    if (ipAddress.length == 0) {
        return NO;
    }
    NSString *urlRegEx = @"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    NSError *error;
    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:urlRegEx options:0 error:&error];
    if (regex != nil) {
        NSTextCheckingResult *firstMatch=[regex firstMatchInString:ipAddress options:0 range:NSMakeRange(0, [ipAddress length])];
        if (firstMatch) {
            return YES;
        }
    }
    return NO;
}

+ (NSDictionary *)getIPAddresses {
    NSMutableDictionary *addresses = [NSMutableDictionary dictionaryWithCapacity:8];
    // retrieve the current interfaces - returns 0 on success
    struct ifaddrs *interfaces;
    if(!getifaddrs(&interfaces)) {
        // Loop through linked list of interfaces
        struct ifaddrs *interface;
        for(interface=interfaces; interface; interface=interface->ifa_next) {
            if(!(interface->ifa_flags & IFF_UP) /* || (interface->ifa_flags & IFF_LOOPBACK) */ ) {
                continue; // deeply nested code harder to read
            }
            const struct sockaddr_in *addr = (const struct sockaddr_in*)interface->ifa_addr;
            char addrBuf[ MAX(INET_ADDRSTRLEN, INET6_ADDRSTRLEN) ];
            if(addr && (addr->sin_family==AF_INET || addr->sin_family==AF_INET6)) {
                NSString *name = [NSString stringWithUTF8String:interface->ifa_name];
                NSString *type;
                if(addr->sin_family == AF_INET) {
                    if(inet_ntop(AF_INET, &addr->sin_addr, addrBuf, INET_ADDRSTRLEN)) {
                        type = IP_ADDR_IPv4;
                    }
                } else {
                    const struct sockaddr_in6 *addr6 = (const struct sockaddr_in6*)interface->ifa_addr;
                    if(inet_ntop(AF_INET6, &addr6->sin6_addr, addrBuf, INET6_ADDRSTRLEN)) {
                        type = IP_ADDR_IPv6;
                    }
                }
                if(type) {
                    NSString *key = [NSString stringWithFormat:@"%@/%@", name, type];
                    addresses[key] = [NSString stringWithUTF8String:addrBuf];
                }
            }
        }
        // Free memory
        freeifaddrs(interfaces);
    }
    return [addresses count] ? addresses : nil;
}

@end
