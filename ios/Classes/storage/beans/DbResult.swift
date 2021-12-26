//
//  DbResult.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation

public class DbResult: NSObject {
    public var success = false
    public var cursor: OpaquePointer? = nil
}
