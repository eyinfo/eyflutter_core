//
//  DbResultEntry.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation
import SwiftyJSON

struct DbResultEntry {
    var tableName = ""
    var condition = ""
    var fields: JSON = []
    var data: AnyObject?
    var callKey: String = ""
    var success = false
}
