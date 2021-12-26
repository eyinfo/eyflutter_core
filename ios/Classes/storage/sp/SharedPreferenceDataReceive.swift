//
//  SharedPreferenceDataReceive.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation

class SharedPreferenceDataReceive: NSObject {
    
    static func receive(params: Dictionary<String, AnyObject>) -> Void {
        if params.isEmpty {
            return
        }
        let entry = getPreferenceEntry(params: params)
        if entry.success == false || ObjectJudge.isEmptyOrNull(value: entry.skey as AnyObject){
            return
        }
        if entry.stype == "set" {
            SharePrefUtils.instance().put(key: entry.skey, value: entry.svalue)
        } else if entry.stype == "get" {
            getPreference(entry: entry)
        } else if entry.stype == "clear" {
            SharePrefUtils.instance().removeObject(forKey: entry.skey)
        }
    }
    
    static func getPreference(entry: PreferenceEntry) -> Void {
        if entry.getType == "bool" {
            let value = SharePrefUtils.instance().getBool(forKey: entry.skey)
            ChannelPlugin.resultCall(resultKey: entry.callKey, callbackData: value)
        } else if entry.getType == "int" {
            let value = SharePrefUtils.instance().getInt(forKey: entry.skey)
            ChannelPlugin.resultCall(resultKey: entry.callKey, callbackData: value)
        } else if entry.getType == "double" {
            let value = SharePrefUtils.instance().getDouble(forKey: entry.skey)
            ChannelPlugin.resultCall(resultKey: entry.callKey, callbackData: value)
        } else if entry.getType == "string" {
            let value = SharePrefUtils.instance().getString(forKey: entry.skey)
            ChannelPlugin.resultCall(resultKey: entry.callKey, callbackData: value)
        }
    }
    
    static func getPreferenceEntry(params: Dictionary<String, AnyObject>) -> PreferenceEntry {
        var entry = PreferenceEntry.init()
        //call key
        let callKeyObject = params["flutter_result_call_key"]
        if !ObjectJudge.isEmptyOrNull(value: callKeyObject) {
            entry.callKey = callKeyObject as! String
        }
        entry.skey = ConvertUtils.toString(value: params["key"])
        entry.stype = ConvertUtils.toString(value: params["type"])
        entry.getType = ConvertUtils.toString(value: params["getType"])
        entry.svalue = params["value"]
        entry.success = true
        return entry
    }
}
