//
//  SqliteUtils.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation
import SwiftyJSON
import SQLite3

class SqliteUtils: NSObject {
    
    static func getSqlType(dartType: String) -> String {
        switch dartType {
            case "num":
                return "integer";
            case "int":
                return "integer";
            case "bool":
                return "integer";
            case "date":
                return "integer";
            case "double":
                return "REAL";
            case "float":
                return "REAL";
            default:
                return "TEXT";
        }
    }
    
    static func getDbResultEntry(params: Dictionary<String, AnyObject>) -> DbResultEntry {
        var entry = DbResultEntry.init()
        //表名
        let schemaObject = params["schema"]
        if schemaObject != nil {
            let schemaJson = (schemaObject as! String).trimmingCharacters(in: .whitespaces)
            let schemaFromString = schemaJson.data(using: String.Encoding.utf8, allowLossyConversion: false) ?? Data()
            let schema = JSON.init(schemaFromString as Any)
            entry.tableName = schema["tableName"].stringValue
            if entry.tableName.isEmpty {
                return entry
            }
        } else {
            return entry
        }
        //条件
        let whereObject = params["where"]
        if whereObject != nil {
            entry.condition = (whereObject as! String).trimmingCharacters(in: .whitespaces)
        }
        //字段
        let fieldsObject = params["fields"]
        if fieldsObject != nil {
            let fieldsJson = (fieldsObject as! String).trimmingCharacters(in: .whitespaces)
            let fieldsFromString = fieldsJson.data(using: String.Encoding.utf8, allowLossyConversion: false)
            let fields = JSON.init(fieldsFromString as Any)
            if fields.isEmpty {
                return entry
            }
            entry.fields = fields
        } else {
            return entry
        }
        //数据
        entry.data = params["data"]
        //call key
        let callKeyObject = params["flutter_result_call_key"]
        if !ObjectJudge.isEmptyOrNull(value: callKeyObject) {
            entry.callKey = callKeyObject as! String
        }
        entry.success = true
        return entry
    }
    
    //获取创建表sql
    static func getCreateTableSql(entry: DbResultEntry) -> String {
        var position = 0
        let list = entry.fields.arrayValue
        let length = list.count
        var sql = String(format: "CREATE TABLE IF NOT EXISTS %@ (", entry.tableName)
        for item in list {
            let nameObject = item["name"]
            let typeObject = item["type"]
            sql = String(format: "%@`%@` %@", sql,nameObject.stringValue,SqliteUtils.getSqlType(dartType: typeObject.stringValue))
            let primaryObject = item["primary"]
            if primaryObject.boolValue {
                sql = String(format: "%@ PRIMARY KEY ", sql)
            }
            let autoincrementObject = item["autoincrement"]
            if autoincrementObject.boolValue {
                sql = String(format: "%@ AUTOINCREMENT", sql)
            }
            let uniqueObject = item["unique"]
            if uniqueObject.boolValue {
                sql = String(format: "%@ UNIQUE", sql)
            }
            position += 1
            if position < length {
                sql = String(format: "%@,", sql)
            }
        }
        sql = String(format: "%@)", sql)
        return sql
    }
    
    static func getColumnCachekeyForTable(tableName: String) -> String {
        return String(format: "adc662e7734648ce9f111462adec9e61_%@", tableName)
    }
    
    static func setTableVersionForDb(tableName: String) -> Void {
        let systemInfo = SystemInfoUtils.getSystemInfo()
        let key = getColumnCachekeyForTable(tableName: tableName)
        SharePrefUtils.instance().put(key: key, value: systemInfo.versionCode)
    }
    
    static func getTableVersionForDb(tableName: String) -> Int {
        let key = getColumnCachekeyForTable(tableName: tableName)
        let value = SharePrefUtils.instance().getInt(forKey: key)
        return value
    }
    
    static func isNeedCheckFields(tableName: String) -> Bool {
        let versionCode = getTableVersionForDb(tableName: tableName)
        if versionCode <= 0 {
            return true
        }
        let systemInfo = SystemInfoUtils.getSystemInfo()
        let code = Int(systemInfo.versionCode) ?? 0
        if code > versionCode {
            return true
        }
        return false
    }
    
    public static func readRow(cursor: OpaquePointer) -> Dictionary<String, AnyObject> {
        var dic: Dictionary<String, AnyObject> = [:]
        let columnCount = sqlite3_column_count(cursor)
        for colIndex in 0..<columnCount {
            let colName = String.init(cString: sqlite3_column_name(cursor, colIndex)!)
            let colType = sqlite3_column_type(cursor, colIndex)
            var value: AnyObject?
            switch colType {
            case SQLITE_FLOAT:
                value = sqlite3_column_double(cursor, colIndex) as AnyObject
            case SQLITE_INTEGER:
                value = Int(sqlite3_column_int64(cursor, colIndex)) as AnyObject
            case SQLITE_NULL:
                value = nil
            default:
                value = String.init(cString: sqlite3_column_text(cursor, colIndex)!) as AnyObject
            }
            dic[colName] = value
        }
        return dic
    }
    
    static func getTableStruct(tableName: String) -> String {
        let sql = String(format: "select [sql] from sqlite_master where `type`='table' and tbl_name = '%@'", tableName);
        let result = SqliteManager.instance().query(sql: sql)
        if result.success == false {
            return ""
        }
        var tableStruct = ""
        while sqlite3_step(result.cursor) == SQLITE_ROW {
            let dic = readRow(cursor: result.cursor!)
            tableStruct = dic["sql"] as! String
        }
        SqliteManager.instance().finalize(stmt: result.cursor!)
        SqliteManager.instance().close()
        return tableStruct
    }
    
    static func getDefineColumns(entry: DbResultEntry) -> [ColumnProperty] {
        var properties: [ColumnProperty] = []
        let list = entry.fields.arrayValue
        for item in list {
            var property = ColumnProperty.init()
            property.columnName = item["name"].stringValue
            property.columnType = item["type"].stringValue
            properties.append(property)
        }
        return properties
    }
    
    static func filterAddColumns(sqlText: String,columns:[ColumnProperty]) -> [ColumnProperty] {
        var addColumns: [ColumnProperty] = []
        for property in columns {
            if sqlText.contains(property.columnName) {
                continue
            }
            addColumns.append(property)
        }
        return addColumns
    }
    
    static func getSqlColumnType(propertyType: String) -> String {
        var typeName = ""
        switch (propertyType.lowercased()) {
            case "long":
                typeName = "INTEGER"
            case "int":
                typeName = "INTEGER"
            case "boolean":
                typeName = "INTEGER"
            case "date":
                typeName = "INTEGER"
                break
            case "double":
                typeName = "REAL"
            case "float":
                typeName = "REAL"
                break
            default:
                typeName = "TEXT"
                break
        }
        return typeName
    }
    
    static func addColumnToDb(tableName: String,addColumns:[ColumnProperty]) -> Void {
        var sqls: [String] = []
        for property in addColumns {
            var sql = String(format: "alter table %@ add ", tableName)
            sql = String(format: "%@%@ %@;", sql,property.columnName,getSqlColumnType(propertyType: property.columnType))
            sqls.append(sql)
        }
        if SqliteManager.instance().batchExec(sqls: sqls) {
            //保存版本避免重复检测
            setTableVersionForDb(tableName: tableName)
        }
    }
    
    static func addColumnIfNotExist(entry: DbResultEntry, columns: [ColumnProperty]) -> Void {
        if !isNeedCheckFields(tableName: entry.tableName) {
            return
        }
        let tableStruct = getTableStruct(tableName: entry.tableName)
        if tableStruct.isEmpty {
            return
        }
        let addColumns = filterAddColumns(sqlText: tableStruct, columns: columns)
        if addColumns.count == 0 {
            //如果没有字段匹配视为成功
            //保存版本避免重复检测
            setTableVersionForDb(tableName: entry.tableName)
            return
        }
        addColumnToDb(tableName: entry.tableName, addColumns: addColumns)
    }
    
    static func getSqlValue(dartType: String, data: AnyObject) -> String {
        if ObjectJudge.isEmptyOrNull(value: data) {
            if dartType == "num" || dartType == "int" || dartType == "double" || dartType == "float" {
                return "0"
            } else if dartType == "bool" {
                return "false"
            } else if dartType == "date" {
                let date = Date()
                let timeInterval: TimeInterval = date.timeIntervalSince1970
                let millisecond = CLongLong(round(timeInterval*1000))
                return "\(millisecond)"
            } else {
                return "''"
            }
        } else {
            if dartType == "num" || dartType == "int" || dartType == "double" || dartType == "float" || dartType == "date" {
                return "\(data)"
            } else if dartType == "bool" {
                return "\(ObjectJudge.isTrue(value: data))"
            } else  {
                return String(format: "'%@'", "\(data)")
            }
        }
    }
}
