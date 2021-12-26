//
//  FNUrlMatcher.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//

import UIKit

public class FNUrlMatcher {
    static let shared = FNUrlMatcher()
    var urlDictionary: Dictionary<String, AnyClass> = [:]

    public class func fetchModuleClass(key: String) -> AnyClass? {
        return FNUrlMatcher.shared.urlDictionary[key]
    }
}
