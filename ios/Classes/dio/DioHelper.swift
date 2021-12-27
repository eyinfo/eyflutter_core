//
//  DioHelper.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/27.
//

import Foundation
import Alamofire

class DioHelper: NSObject {
    func getParams(paramsDict: Dictionary<String, AnyObject>?) -> RequestParams {
        var reqParams = RequestParams.init();
        if paramsDict == nil {
            return reqParams;
        }
        let callKey = paramsDict?["flutter_result_call_key"]
        if callKey != nil {
            reqParams.callKey = (callKey as! String).trimmingCharacters(in: .whitespaces);
        }
        let method = paramsDict?["method"]
        if method != nil {
            reqParams.method = (method as! String).trimmingCharacters(in: .whitespaces);
        }
        let requestId = paramsDict?["requestId"]
        if requestId != nil {
            reqParams.requestId = (requestId as! String).trimmingCharacters(in: .whitespaces);
        }
        let contentType = paramsDict?["contentType"]
        if contentType != nil {
            reqParams.contentType = (contentType as! String).trimmingCharacters(in: .whitespaces);
        }
        let url = paramsDict?["url"]
        if url != nil {
            reqParams.url = (url as! String).trimmingCharacters(in: .whitespaces);
        }
        let headers = paramsDict?["headers"]
        if headers != nil {
            reqParams.headers = headers as? Dictionary<String, String>;
        }
        let data = paramsDict?["data"]
        if data != nil {
            reqParams.data = data as? Dictionary<String, AnyObject?>;
        }
        return reqParams;
    }
    
    func toParameters(params: Dictionary<String, AnyObject?>?) -> Parameters {
        var _params = Parameters.init();
        params?.forEach { (key: String, value: AnyObject?) in
            _params[key] = value;
        };
        return _params;
    }
    
    func toHTTPHeaders(headers: Dictionary<String, String>?, contentType: String?) -> HTTPHeaders {
        var _headers = HTTPHeaders.init();
        headers?.forEach({ (key: String, value: String) in
            _headers[key] = value;
        });
        //设置请求类型
        if contentType == "form" {
            //普通表单类型
            _headers["Content-Type"] = "application/x-www-form-urlencoded"
        } else {
            //json数据
            _headers["Content-Type"] = "application/json";
        }
        return _headers;
    }
}
