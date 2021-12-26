//
//  UIDevice+IdentifierAddition.h
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

#ifndef UIDevice_IdentifierAddition_h
#define UIDevice_IdentifierAddition_h


#endif /* UIDevice_IdentifierAddition_h */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


@interface UIDevice (IdentifierAddition)

/*
 * @method uniqueDeviceIdentifier
 * @description use this method when you need a unique identifier in one app.
 * It generates a hash from the MAC-address in combination with the bundle identifier
 * of your app.
 */

- (NSString *) uniqueDeviceIdentifier;

/*
 * @method uniqueGlobalDeviceIdentifier
 * @description use this method when you need a unique global identifier to track a device
 * with multiple apps. as example a advertising network will use this method to track the device
 * from different apps.
 * It generates a hash from the MAC-address only.
 */

- (NSString *) uniqueGlobalDeviceIdentifier;

@end
