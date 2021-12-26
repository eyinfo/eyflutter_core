//
//  FNUrlRouteHandleOver.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//
import SafariServices

//在 key 都没有匹配上的时候，处理 url 的 block
var urlRouteHandleOverBlock = { (url: String?, modal: Bool?, params:Dictionary<String,AnyObject>?) in
    
    let uri = URL(string: url!);
    if(uri!.host == nil || uri!.isFileURL == true){
        return;
    }
}
