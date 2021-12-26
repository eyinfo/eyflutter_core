//
//  InsertOrReplaceService.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation
import SwiftyJSON

class InsertOrReplaceService: NSObject {
    
    func insertOrReplace(entry: DbResultEntry) -> Void {
        if !entry.success {
            return
        }
        //检查表是否存在不存在创建
        let createTableSql = SqliteUtils.getCreateTableSql(entry: entry)
        if !SqliteManager.instance().execSql(sql: createTableSql) {
            return
        }
        //检查并补齐列
        let columns: [ColumnProperty] = SqliteUtils.getDefineColumns(entry: entry)
        SqliteUtils.addColumnIfNotExist(entry: entry, columns: columns)
        //保存与更新
        if entry.data != nil && (entry.data?.count)! > 0 {
            let sql = buildSql(entry: entry, map: (entry.data as! Dictionary<String,AnyObject>))
            guard SqliteManager.instance().execSql(sql: sql) else {
                print(String(format: "保存与更新数据失败：%@", entry.tableName))
                ChannelPlugin.remove(resultKey: entry.callKey)
                return
            }
        }
        ChannelPlugin.remove(resultKey: entry.callKey)
    }
    
    func buildSql(entry: DbResultEntry,map: Dictionary<String,AnyObject>) -> String {
        var sql = String(format: "INSERT or replace INTO %@ (", entry.tableName)
        var valueSql = ""
        var position = 0
        let list = entry.fields.arrayValue
        let length = list.count
        for item in list {
            let nameObject = item["name"]
            let typeObject = item["type"]
            sql = String(format: "%@`%@`", sql,nameObject.stringValue)
            let valueObject = map[nameObject.stringValue] as AnyObject
            valueSql = String(format: "%@%@", valueSql,SqliteUtils.getSqlValue(dartType: typeObject.stringValue, data: valueObject))
            position += 1
            if position < length {
                sql = String(format: "%@,", sql)
                valueSql = String(format: "%@,", valueSql)
            }
        }
        sql = String(format: "%@)VALUES(%@);", sql,valueSql)
        valueSql = ""
        return sql
    }
}
