//
//  FN.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/25.
//

import UIKit

public class FN {
    public class func open(url: String) {
        open(url: url, modal:false, params: nil)
    }
    
    public class func open(url: String, modal:Bool) {
        open(url: url, modal:true, params: nil)
    }
    
    public class func open(url:String,params:Dictionary<String,AnyObject>?) {
        open(url: url,modal: false,params: params);
    }
    
    public class func open(url: String, modal: Bool, params: Dictionary<String, AnyObject>?) {
        let urlTmp = URL.init(string: url)
        let host = urlTmp?.host;
        let path = urlTmp?.path;
        var key = "";
        if(host==nil){
            key=path!;
        }else{
            key=host!+path!;
        }
        //如果为空则不处理
        if(key.isEmpty == true){
            return;
        }
        var existFlag = false;
        if(FNUrlMatcher.shared.urlDictionary[key] != nil){
            existFlag = true;
        }else{
            if(key.hasSuffix("/") == true){
                let len = key.count-1;
                key = String(key.prefix(len));
            }else{
                key = key+"/";
            }
            if(FNUrlMatcher.shared.urlDictionary[key] != nil){
                existFlag = true;
            }
        }
        //有注册过 && 注册的类遵循协议
        if (existFlag == true && (FNUrlMatcher.fetchModuleClass(key: key) as? FNUrlRouteDelegate != nil)) {
            
            let paramsWithUrlQuery = FNUtil.mergeDictionary(dic0: FNUtil.getQueryDictionary(url: key), dic1: params)
            
            let moduleType = FNUrlMatcher.fetchModuleClass(key: key) as! FNUrlRouteDelegate.Type
            let module = moduleType.init(params: paramsWithUrlQuery as Dictionary<String, AnyObject>?) as AnyObject
            if module.isKind(of: UINavigationController.self) {
                let viewController = module as! UINavigationController;
                let topViewController = FNUtil.currentTopViewController()
                if (topViewController.navigationController != nil) && !modal {
                    let navigation = topViewController.navigationController
                    navigation?.pushViewController(viewController, animated: true)
                }
                else {
                    topViewController.present(viewController, animated: true, completion: nil)
                }
            }else if module.isKind(of: UIViewController.self) {
                let viewController = module as! UIViewController;
                let topViewController = FNUtil.currentTopViewController()
                if (topViewController.navigationController != nil) && !modal {
                    let navigation = topViewController.navigationController
                    navigation?.pushViewController(viewController, animated: true)
                }
                else {
                    topViewController.present(viewController, animated: true, completion: nil)
                }
            }
        }
        else {
            urlRouteHandleOverBlock(key, modal, params)
        }
    }
}
