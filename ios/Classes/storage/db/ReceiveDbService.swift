//
//  ReceiveDbService.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation

class ReceiveDbService {
    
    static func receive(params: Dictionary<String, AnyObject>) -> Void {
        if params.isEmpty {
            return
        }
        let entry = SqliteUtils.getDbResultEntry(params: params)
        let oType = params["operationType"] as! String
        if oType == "insertOrReplace" {
            InsertOrReplaceService.init().insertOrReplace(entry: entry)
        } else if oType == "deleteInTx" {
            DeleteService.init().delete(entry: entry)
        } else if oType == "query" {
            QueryService.init().query(entry: entry, isList: false)
        } else if oType == "queryList" {
            QueryService.init().query(entry: entry, isList: true)
        } else if oType == "count" {
            QueryService.init().queryCount(entry: entry, isCount: true)
        } else if oType == "exists" {
            QueryService.init().queryCount(entry: entry, isCount: false)
        }
    }
}
