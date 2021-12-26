//
//  SqliteManager.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation
import SwiftUI
import URITemplate
import SQLite3

protocol OnSqlExecCall {
    func call(sqls: [String]) -> Bool
}

public class SqliteManager: NSObject {
    var db: OpaquePointer? = nil
    static var _sqliteManager: SqliteManager? = nil
    class func instance() -> SqliteManager {
        if _sqliteManager == nil {
            _sqliteManager = SqliteManager.init()
        }
        return _sqliteManager!
    }
    
    public override init() {
        let filePath = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.documentDirectory, FileManager.SearchPathDomainMask.userDomainMask,true).last!
        let dbFile = String(format: "%@/3ba855eba286403288dc0de02508301e.sqlite", filePath)
        let cfile = dbFile.cString(using: String.Encoding.utf8)
        let dbStatus = sqlite3_open(cfile, &db)
        if dbStatus != SQLITE_OK {
            print("打开数据库失败")
        }
    }
    
    // 销毁cursor
    func finalize(stmt: OpaquePointer) -> Void {
        sqlite3_finalize(stmt)
    }
    
    // 关闭连接
    func close() -> Void {
        if db != nil {
            sqlite3_close(db)
            db = nil
        }
        SqliteManager._sqliteManager = nil
    }
    
    func _execSingleSql(sql: String) -> Bool {
        let csql = sql.cString(using: String.Encoding.utf8)
        let result = sqlite3_exec(db, csql, nil, nil, nil)
        return result == SQLITE_OK
    }
    
    private func _execSqls(sqls: [String], execCall: OnSqlExecCall) -> Bool {
        let execStatus = execCall.call(sqls: sqls)
        return execStatus
    }
    
    // MARK: - 事务处理
    /// 开启事务
    private func _beginTransaction() {
        sqlite3_exec(db, "BEGIN TRANSACTION;", nil, nil, nil)
    }
     
    /// 提交事务
    private func _commitTransaction() {
        sqlite3_exec(db, "COMMIT TRANSACTION;", nil, nil, nil)
    }
     
    /// 回滚事务
    private func _rollbackTransaction() {
        sqlite3_exec(db, "ROLLBACK TRANSACTION;", nil, nil, nil)
    }

    
    private struct _batchHandleCall: OnSqlExecCall {
        func call(sqls: [String]) -> Bool {
            var execStatus = true
            SqliteManager.instance()._beginTransaction()
            for sql in sqls {
                execStatus = SqliteManager.instance()._execSingleSql(sql: sql)
                if !execStatus {
                    SqliteManager.instance()._rollbackTransaction()
                    break
                }
            }
            SqliteManager.instance()._commitTransaction()
            return execStatus
        }
    }
    
    func execSql(sql: String) -> Bool {
        if sql.isEmpty {
            return false
        }
        return _execSqls(sqls: [sql], execCall: _batchHandleCall())
    }
    
    /// 批量执行sql（任意一条语句执行失败均为失败）
    func batchExec(sqls: [String]) -> Bool {
        if sqls.isEmpty {
            return false
        }
        return _execSqls(sqls: sqls, execCall: _batchHandleCall())
    }
    
    func query(sql: String) -> DbResult {
        if sql.isEmpty {
            return DbResult.init()
        }
        let result = DbResult.init()
        if sqlite3_prepare_v2(db, sql, -1, &result.cursor, nil) != SQLITE_OK {
            finalize(stmt: result.cursor!)
            return result
        }
        result.success = true
        return result
    }
}
