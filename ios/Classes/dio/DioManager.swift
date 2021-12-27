//
//  DioManager.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/27.
//

import Foundation
import Alamofire

class DioManager: NSObject {
    
    func request(params: RequestParams, method: HTTPMethod = HTTPMethod.get) -> Void {
        let helper = DioHelper.init();
        let _requestHeaders = helper.toHTTPHeaders(headers: params.headers, contentType: params.contentType);
        let _requestData = helper.toParameters(params: params.data);
        EyNetManager.shareManager().request(params.url,method: method,parameters: _requestData,headers: _requestHeaders).responseJSON { response in
            switch response.result {
            case .success(let responseData):
                let jsonData = JsonUtils.getJSONStringFromDictionary(dataObject: responseData);
                self.responseHanndler(callKey: params.callKey, type: "success", data: jsonData);
                self.notifyComplete(requestId: params.requestId);
                break;
            case .failure(let error):
                self.responseHanndler(callKey: params.callKey, type: "error", data: "");
                self.notifyComplete(requestId: params.requestId);
                print(error);
                break;
            }
        };
    }
    
    func notifyComplete(requestId: String) -> Void {
        ChannelPlugin.invokeMethod(action: "812454d85242c263", params: requestId);
    }
    
    func responseHanndler(callKey: String, type: String, data: String) -> Void {
        var _response:Dictionary<String,String> = [:];
        _response["type"] = type;
        _response["data"] = data;
        let callData = JsonUtils.getJSONStringFromDictionary(dataObject: _response);
        ChannelPlugin.resultCall(resultKey: callKey, callbackData: callData);
    }
}
