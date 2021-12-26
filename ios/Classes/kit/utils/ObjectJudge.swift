//
//  ObjectJudge.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//

import Foundation

public class ObjectJudge: NSObject {
    
    public static func isEmptyOrNull(value: AnyObject?) -> Bool {
        if let data = value {
            if data == nil {
                return true
            }else if "\(data)".lowercased() == "<null>"{
                return true
            } else {
                return false
            }
        }else{
            return true
        }
    }
    
    public static func isTrue(value: AnyObject?) -> Bool {
        if isEmptyOrNull(value: value) {
            return false
        }
        if value is Bool {
            return value as! Bool
        } else if value is Int {
            return (value as! Int) == 1 ?true:false
        } else if value is String {
            let data = "\(String(describing: value))".lowercased().trimmingCharacters(in: .whitespaces)
            if data == "true" || data == "1" {
                return true
            }
        }
        return false
    }
}
