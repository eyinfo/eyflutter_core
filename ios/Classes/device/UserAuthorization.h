//
//  UserAuthorization.h
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

#ifndef UserAuthorization_h
#define UserAuthorization_h


#endif /* UserAuthorization_h */

#import <Foundation/Foundation.h>

typedef  void (^authCallBack)(NSString *callkey,BOOL granted);
typedef  void (^authCompleteBlock)(BOOL granted);
@interface UserAuthorization : NSObject
+ (void)authorizationsWithParams:(NSDictionary *)params callBack:(authCallBack)callBack;

@end
