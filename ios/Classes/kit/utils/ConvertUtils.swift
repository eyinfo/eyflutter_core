//
//  ConvertUtils.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//

import Foundation

public class ConvertUtils: NSObject {
    public static func toString(value: AnyObject?) -> String {
        if let data = value {
            if data == nil {
                return ""
            }else if "\(data)".lowercased() == "<null>"{
                return ""
            }else{
                return "\(data)".trimmingCharacters(in: .whitespaces)
            }
        }else{
            return ""
        }
    }
    
    public static func toDictionary(ocDictionary: NSDictionary) -> Dictionary<String, AnyObject?> {
        var dict : Dictionary<String, AnyObject?> = Dictionary<String, AnyObject?>()
        for key : Any in ocDictionary.allKeys {
            let stringKey = key as! String
            if let keyValue = ocDictionary.value(forKey: stringKey) {
                dict[stringKey] = keyValue as AnyObject
            }
        }
        return dict
    }
}
