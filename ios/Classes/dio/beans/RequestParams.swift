//
//  RequestParams.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/27.
//

import Foundation

struct RequestParams {
    //GET PUT POST DELETE
    var method = ""
    //请求id，当前请求唯一标识;用于区分请求的回调函数
    var requestId = ""
    //请求内容类型：form或json
    var contentType = ""
    //请求地址
    var url = ""
    //请求头信息
    var headers:Dictionary<String, String>?
    //请求数据：1.当GET或DELETE请求时参数将以query方式处理;
    //2.contentType=form以key:value表单形式提交数据;
    //3.contentType=json以json形式提交数据
    var data:Dictionary<String, AnyObject?>?
    //数据回调key
    var callKey = ""
}
