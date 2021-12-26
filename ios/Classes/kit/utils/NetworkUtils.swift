//
//  NetworkUtils.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//

import Foundation
import Reachability

public class NetworkUtils: NSObject {
    var reachability : Reachability!
    
    override init() {
        if reachability != nil {
            reachability.stopNotifier()
        }
        reachability = try! Reachability()
        reachability.whenReachable = { reachability in

        }
        do {
            try reachability.startNotifier()
        } catch {
          print("start Reachability fail")
        }
    }
    
    static let sharedInstance = NetworkUtils()
    
    /// 会存在第一次无法准确获取
    public static func isConnected() -> Bool {
        if sharedInstance.reachability == nil {
            return false
        }
        switch sharedInstance.reachability.connection {
        case .cellular:
            return true
        case .wifi:
            return true
        default:
            return false
        }
    }
}
