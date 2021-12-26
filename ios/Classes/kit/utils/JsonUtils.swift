//
//  JsonUtils.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//

import Foundation

public class JsonUtils: NSObject {
    public static func getJSONStringFromDictionary(dataObject: Any) -> String {
        if (!JSONSerialization.isValidJSONObject(dataObject)) {
            return ""
        }
        let data : NSData! = try! JSONSerialization.data(withJSONObject: dataObject, options: []) as NSData?
        let JSONString = NSString(data:data as Data,encoding: String.Encoding.utf8.rawValue)
        return JSONString! as String
    }
}
