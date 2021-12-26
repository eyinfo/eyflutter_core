//
//  ChannelKeys.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//

import Foundation

public struct ChannelKeys {
    //channel桥接名
    var _channelMethodName : String;
    //channel接收参数中的回调key
    var _resultKey : String;
    
    init() {
        _channelMethodName = "0eff8bd070f64d1890193686196f5a31";
        _resultKey = "flutter_result_call_key";
    }
    
    public func channelMethodName() -> String {
        return _channelMethodName;
    }
    
    public func resultKey() -> String {
        return _resultKey;
    }
}
