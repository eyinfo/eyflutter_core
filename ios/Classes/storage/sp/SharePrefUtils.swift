//
//  SharePrefUtils.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation

public class SharePrefUtils: NSObject {
    
    var userDef: UserDefaults = UserDefaults.standard
    static let _sharePrefUtils = SharePrefUtils()
    class func instance() -> SharePrefUtils {
        return _sharePrefUtils
    }
    
    public func put(key: String, value: Any?) -> Void {
        userDef.set(value, forKey: key)
    }
    
    private func _get(forKey key: String) -> Any? {
        return userDef.value(forKey: key)
    }
    
    public func removeObject(forKey key: String) -> Void {
        userDef.removeObject(forKey: key)
    }
    
    public func getInt(forKey key: String) -> Int {
        let value = _get(forKey: key)
        if value == nil {
            return 0
        }
        if value is Int {
            return value as! Int
        }
        return 0
    }
    
    public func getDouble(forKey key: String) -> Double {
        let value = _get(forKey: key)
        if value == nil {
            return 0
        }
        if value is Double {
            return value as! Double
        }
        return 0
    }
    
    public func getBool(forKey key: String) -> Bool {
        let value = _get(forKey: key)
        if value == nil {
            return false
        }
        return ObjectJudge.isTrue(value: value as AnyObject)
    }
    
    public func getString(forKey key: String) -> String {
        let value = _get(forKey: key)
        if value == nil {
            return ""
        }
        if value is String {
            return value as! String
        }
        return ""
    }
}
