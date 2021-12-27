//
//  EyNetManager.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/27.
//

import Foundation
import Alamofire

class EyNetManager: SessionManager {
    static var _manager: EyNetManager!;
    
    class func shareManager(outTime: TimeInterval = 60) -> EyNetManager {
        let config = NetConfig.shareConfig();
        config.timeoutIntervalForRequest = outTime;
        config.timeoutIntervalForResource = outTime;
        config.httpAdditionalHeaders = SessionManager.defaultHTTPHeaders;
        if _manager == nil {
            _manager = EyNetManager(configuration: config);
        }
        return _manager;
    }
}

class NetConfig: URLSessionConfiguration {
    static var _config:URLSessionConfiguration?;
    class func shareConfig() -> URLSessionConfiguration {
        if _config == nil {
            _config = URLSessionConfiguration.default;
        }
        return _config!;
    }
}
