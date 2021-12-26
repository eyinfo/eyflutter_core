//
//  QueryService.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation
import SQLite3
import SwiftyJSON

class QueryService: NSObject {
    
    func query(entry: DbResultEntry, isList: Bool) -> Void {
        if entry.success == false {
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
        //查询数据
        let sql = buildSql(entry: entry)
        let queryList: [Dictionary<String, AnyObject>] = queryData(sql: sql)
        if isList == true {
            let response = JsonUtils.getJSONStringFromDictionary(dataObject: queryList)
            ChannelPlugin.resultCall(resultKey: entry.callKey, callbackData: response)
        }
        if queryList.count > 0 {
            let response = JsonUtils.getJSONStringFromDictionary(dataObject: queryList[0])
            ChannelPlugin.resultCall(resultKey: entry.callKey, callbackData: response)
        } else {
            ChannelPlugin.resultCall(resultKey: entry.callKey, callbackData: "{}")
        }
    }
    
    func buildSql(entry: DbResultEntry) -> String {
        var sql = "SELECT "
        var position = 0
        let list = entry.fields.arrayValue
        let length = list.count
        for item in list {
            let nameObject = item["name"]
            sql = String(format: "%@`%@`", sql,nameObject.stringValue)
            position += 1
            if position < length {
                sql = String(format: "%@,", sql)
            }
        }
        sql = String(format: "%@ FROM %@ WHERE %@;", sql,entry.tableName,entry.condition)
        return sql
    }
    
    func queryData(sql: String) -> [Dictionary<String, AnyObject>] {
        var queryList: [Dictionary<String, AnyObject>] = []
        let result = SqliteManager.instance().query(sql: sql)
        if result.success == false {
            return queryList
        }
        while sqlite3_step(result.cursor) == SQLITE_ROW {
            let dic = SqliteUtils.readRow(cursor: result.cursor!)
            queryList.append(dic)
        }
        SqliteManager.instance().finalize(stmt: result.cursor!)
        SqliteManager.instance().close()
        return queryList
    }
    
    func queryCount(entry: DbResultEntry, isCount: Bool) -> Void {
        if entry.success == false {
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
        //查询数据
        let sql = buildCountSql(entry: entry)
        var count = 0
        let result = SqliteManager.instance().query(sql: sql)
        if result.success == true {
            while sqlite3_step(result.cursor) == SQLITE_ROW {
                let dic = SqliteUtils.readRow(cursor: result.cursor!)
                count = ObjectJudge.isEmptyOrNull(value: dic["count"]) ? 0 : dic["count"] as! Int
                break
            }
        }
        SqliteManager.instance().finalize(stmt: result.cursor!)
        SqliteManager.instance().close()
        if isCount {
            ChannelPlugin.resultCall(resultKey: entry.callKey, callbackData: count)
        } else {
            if count > 0 {
                ChannelPlugin.resultCall(resultKey: entry.callKey, callbackData: true)
            } else {
                ChannelPlugin.resultCall(resultKey: entry.callKey, callbackData: false)
            }
        }
    }
    
    func buildCountSql(entry: DbResultEntry) -> String {
        var sql = "SELECT count(1) as `count` FROM "
        sql = String(format: "%@ %@ WHERE %@;", sql,entry.tableName,entry.condition)
        return sql
    }
}
