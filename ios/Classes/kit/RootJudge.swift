//
//  RootJudge.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation

class RootJudge: NSObject {
    func isRoot() -> Bool {
        let apps = ["/APPlications/Cydia.app","/APPlications/limera1n.app","/APPlications/greenpois0n.app","/APPlications/blackra1n.app","/APPlications/blacksn0w.app","/APPlications/redsn0w.app","/APPlications/Absinthe.app"]
        for app in apps {
            if FileManager.default.fileExists(atPath: app){
                return true
            }
        }
        return false
    }
}
