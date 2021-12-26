//
//  CacheReceive.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//

import Foundation

class CacheReceive: NSObject {
    static func getCacheSize(params: Dictionary<String, AnyObject>) -> String {
        // 取出cache文件夹目录 缓存文件都在这个目录下
        let cachePath = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.cachesDirectory, FileManager.SearchPathDomainMask.userDomainMask, true).first
        // 取出文件夹下所有文件数组
        let fileArr = FileManager.default.subpaths(atPath: cachePath!)
        //快速枚举出所有文件名 计算文件大小
        var size = 0
        for file in fileArr! {
            // 把文件名拼接到路径中
            let path = cachePath?.appending("/\(file)")
            // 取出文件属性
            let floder = try! FileManager.default.attributesOfItem(atPath: path!)
            // 用元组取出文件大小属性
            for (abc, bcd) in floder {
                // 累加文件大小
                if abc == FileAttributeKey.size {
                    size += (bcd as AnyObject).integerValue
                }
            }
        }
        let cacheSize = getFormatSize(size: size)
        return cacheSize
    }
    
   static private func getFormatSize(size: Int) -> String {
        let kiloByte = Double(size) / Double(1024)
        if kiloByte < 1 {
            return "0KB"
        }
        let megaByte = Double(kiloByte) / Double(1024)
        if megaByte < 1 {
            return String(format: "%.2fKB", kiloByte)
        }
        let gigaByte = Double(megaByte) / Double(1024)
        if gigaByte < 1 {
            return String(format: "%.2fMB", megaByte)
        }
        let teraBytes = Double(gigaByte) / Double(1024)
        if teraBytes < 1 {
            return String(format: "%.2fGB", gigaByte)
        }
        return String(format: "%.2fTB", teraBytes)
    }
    
    static func cleanCache(params: Dictionary<String, AnyObject>) -> Bool {
        do {
            try deleteLibraryFolderContents(folder: "Caches")
            return true
        } catch {
            return false
        }
    }
    
    static private func deleteLibraryFolderContents(folder: String) throws {
        let manager = FileManager.default
        let library = manager.urls(for: FileManager.SearchPathDirectory.libraryDirectory, in: .userDomainMask)[0]
        let dir = library.appendingPathComponent(folder)
        let contents = try manager.contentsOfDirectory(atPath: dir.path)
        for content in contents {
            //如果是快照就继续
            if(content == "Snapshots"){continue;}
            do {
                try manager.removeItem(at: dir.appendingPathComponent(content))
                //print("remove cache success:"+content)
            } catch where ((error as NSError).userInfo[NSUnderlyingErrorKey] as? NSError)?.code == Int(EPERM) {
                //print("remove cache error:"+content)
                // "EPERM: operation is not permitted". We ignore this.
                #if DEBUG
                    //print("Couldn't delete some library contents.")
                #endif
            }
        }
    }
}
