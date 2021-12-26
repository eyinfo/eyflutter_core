//
//  DeleteService.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation

class DeleteService: NSObject {
    func delete(entry: DbResultEntry) -> Void {
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
        guard SqliteManager.instance().execSql(sql: sql) else {
            print(String(format: "删除数据失败：%@", entry.tableName))
            ChannelPlugin.remove(resultKey: entry.callKey)
            return
        }
        ChannelPlugin.remove(resultKey: entry.callKey)
    }
    
    func buildSql(entry: DbResultEntry) -> String {
        var sql = String(format: "DELETE  FROM %@ ", entry.tableName)
        if entry.condition.count > 0 {
            sql = String(format: "%@ WHERE %@;",sql,entry.condition)
        }
        return sql
    }
}
