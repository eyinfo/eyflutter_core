//
//  SystemInfoUtils.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//

import Foundation

public class SystemInfoUtils: NSObject {
    
    public static func getSystemInfo() -> SystemInfo {
        var systemInfo = SystemInfo.init()
        let dic = Bundle.main.infoDictionary
        if let infoDic = dic {
            systemInfo.versionName = infoDic["CFBundleShortVersionString"] as! String
            systemInfo.versionCode = infoDic["CFBundleVersion"] as! String
        }
        return systemInfo
    }
}
