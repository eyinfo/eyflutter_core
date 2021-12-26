//
//  ChannelPlugin.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//

import Foundation
import Flutter

public class ChannelPlugin {
    public static var _flutterResultMap : Dictionary<String, FlutterResult> = [:]
    private static var channel : FlutterMethodChannel?
    
    public static func setChannel(channel : FlutterMethodChannel) -> Void {
        ChannelPlugin.channel = channel
    }
    
    public static func getResultkey(params : Dictionary<String,AnyObject>) -> String {
        if(params.isEmpty){
            return "";
        }
        let key = params[ChannelKeys.init().resultKey()] as! String;
        return key;
    }
    
    /// 分发channel事件
    /// [action] 分发事件
    /// [params] 接收flutter参数
    /// [result] 在此事件中通过FlutterResult将数据传回flutter端
    public static func distribution(action : String, params : Dictionary<String,AnyObject>, result : @escaping FlutterResult, resultKey : String) -> Void {
        //保存回调对象在事件处理完成后移除
        _flutterResultMap[resultKey] = result;
        FN.open(url: action,params: params)
    }
    
    /// 移除当前channel事件回调对象
    public static func remove(resultKey : String) -> Void {
        if (resultKey.isEmpty) {
            return;
        }
        _flutterResultMap.removeValue(forKey: resultKey)
    }
    
    ///  移除当前channel事件回调对象
    public static func remove(params : Dictionary<String, AnyObject>)->Void {
        let resultKey = getResultkey(params : params);
        remove(resultKey: resultKey)
    }
    
    /// 将数据传回dart端
    /// [resultKey] 获取FlutterResult#key值
    /// [callbackData] 回调数据
    public static func resultCall(resultKey : String, callbackData : Any) -> Void {
        if (resultKey.isEmpty) {
            return;
        }
        let result = _flutterResultMap[resultKey];
        if(result == nil){
            return;
        }
        result!(callbackData);
        _flutterResultMap.removeValue(forKey: resultKey);
    }
    
    /// 将数据传回dart端
    /// [params] 接收flutter参数
    /// [callbackData] 回调数据
    public static func resultCall(params : Dictionary<String,AnyObject>, callbackData : AnyObject) -> Void {
        let resultKey = getResultkey(params : params);
        resultCall(resultKey: resultKey, callbackData: callbackData);
    }
    
    /// 改善消息到flutter端
    public static func invokeMethod(action: String, params : Any?) -> Void {
        let channel = ChannelPlugin.channel
        if channel == nil {
            return
        }
        channel?.invokeMethod(action, arguments: params)
    }
}
