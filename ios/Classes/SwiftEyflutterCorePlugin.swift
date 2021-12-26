import Flutter
import UIKit

public class SwiftEyflutterCorePlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: ChannelKeys.init().channelMethodName(), binaryMessenger: registrar.messenger())
        let instance = SwiftEyflutterCorePlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
        ChannelPlugin.setChannel(channel: channel)
        // init mmkv instance
        EyflutterCorePlugin.checkMmkv()
        //网络检测
        FNUrlRoute.register(key: "c964903972a508fd", module:NetConnectCheckReceive.self);
        //数据库存储
        FNUrlRoute.register(key: "cabc4e3d3aca4fd394c0b2d18c1c1d3a", module:StorageDbDataReceive.self);
        //文件存储
        FNUrlRoute.register(key: "0d29e9936fd1419ca59baf1643f4f682", module: SharedPreferencesReceive.self)
        // mmkv save
        FNUrlRoute.register(key: "8064e9487a0c42799d18e8978ead8d69", module: MmkvCacheReceive.self)
        // mmkv take
        FNUrlRoute.register(key: "a0caee30d73f4f499fc34870b2a39303", module: MmkvTakeReceive.self)
        // mmkv delete
        FNUrlRoute.register(key: "91aa77c467c44eeeb60e3bb5b5dfe2c2", module: MmkvDeleteReceive.self)
        //get cache size
        FNUrlRoute.register(key: "69f45198f940bd8c", module: GetCacheSizeReceive.self)
        //clean cache
        FNUrlRoute.register(key: "72c4d6ebf196bda4", module: CleanCacheReceive.self)
        //判断设备是否被root
        FNUrlRoute.register(key: "6e4472f833a5307c", module:DeviceIsRootReceive.self);
        //判断设备是否在模拟器运行
        FNUrlRoute.register(key: "ddf4af599e73eb61", module:DeviceIsEmulatorReceive.self);
        //获取设备信息
        FNUrlRoute.register(key: "1c94d6dafa7e7903", module:DeviceInfoReceive.self);
    }
    
    static func getCallKey(params: Dictionary<String, AnyObject>?) -> String {
        let callKeyObject = params?["flutter_result_call_key"]
        if !ObjectJudge.isEmptyOrNull(value: callKeyObject) {
            return callKeyObject as! String
        }
        return ""
    }
    
    public func handle(_ arguments : FlutterMethodCall, result : @escaping FlutterResult) {
        var _params: [String:AnyObject] = [:]
        if arguments.arguments != nil {
            _params = arguments.arguments as! Dictionary<String,AnyObject>
        }
        let callTag = UUID().uuidString
        _params[ChannelKeys.init().resultKey()] = callTag as AnyObject;
        ChannelPlugin.distribution(action: arguments.method, params: _params, result: result, resultKey: callTag)
    }
    
    public class NetConnectCheckReceive: FNUrlRouteDelegate {
        public required init(params: Dictionary<String, AnyObject>?) {
            let status = NetworkUtils.isConnected()
            ChannelPlugin.resultCall(params: params!, callbackData: status as AnyObject)
        }
    }
    
    public class GetCacheSizeReceive: FNUrlRouteDelegate {
        public required init(params: Dictionary<String, AnyObject>?) {
            let cacheSize = CacheReceive.getCacheSize(params: params!)
            let callKeyObject = params!["flutter_result_call_key"]
            let callKey = callKeyObject as! String
            ChannelPlugin.resultCall(resultKey: callKey, callbackData: cacheSize)
        }
    }
    
    public class CleanCacheReceive: FNUrlRouteDelegate {
        public required init(params: Dictionary<String, AnyObject>?) {
            let cleanCache = CacheReceive.cleanCache(params: params!)
            let callKeyObject = params!["flutter_result_call_key"]
            let callKey = callKeyObject as! String
            ChannelPlugin.resultCall(resultKey: callKey, callbackData: cleanCache)
        }
    }
    
    public class StorageDbDataReceive:FNUrlRouteDelegate{
        public required init(params: Dictionary<String, AnyObject>?) {
            ReceiveDbService.receive(params: params!)
        }
    }
    
    public class SharedPreferencesReceive: FNUrlRouteDelegate {
        public required init(params: Dictionary<String, AnyObject>?) {
            SharedPreferenceDataReceive.receive(params: params!)
        }
    }
    
    public class MmkvCacheReceive: FNUrlRouteDelegate {
        public required init(params: Dictionary<String, AnyObject>?) {
            EyflutterCorePlugin.cacheData(params) { (callKey, value) in
                ChannelPlugin.resultCall(resultKey: callKey!, callbackData: value)
            } onMmkvDoubleCall: { (callKey, value) in
                ChannelPlugin.resultCall(resultKey: callKey!, callbackData: value)
            } onMmkvStringCall: { (callKey, value) in
                ChannelPlugin.resultCall(resultKey: callKey!, callbackData: value as Any)
            } onMmkvBoolCall: { (callKey, value) in
                ChannelPlugin.resultCall(resultKey: callKey!, callbackData: value)
            }
        }
    }
    
    public class MmkvTakeReceive: FNUrlRouteDelegate {
        public required init(params: Dictionary<String, AnyObject>?) {
            EyflutterCorePlugin.takeData(params) { (callKey, value) in
                ChannelPlugin.resultCall(resultKey: callKey!, callbackData: value)
            } onMmkvDoubleCall: { (callKey, value) in
                ChannelPlugin.resultCall(resultKey: callKey!, callbackData: value)
            } onMmkvStringCall: { (callKey, value) in
                ChannelPlugin.resultCall(resultKey: callKey!, callbackData: value as Any)
            } onMmkvBoolCall: { (callKey, value) in
                ChannelPlugin.resultCall(resultKey: callKey!, callbackData: value)
            }

        }
    }
    
    public class MmkvDeleteReceive: FNUrlRouteDelegate {
        public required init(params: Dictionary<String, AnyObject>?) {
            EyflutterCorePlugin.removeMmkvKey(params) { (callKey, withId) in
                ChannelPlugin.resultCall(resultKey: callKey!, callbackData: withId as Any);
            };
        }
    }
    
    public func detachFromEngine(for registrar: FlutterPluginRegistrar) {
        EyflutterCorePlugin.cleanMmkv()
    }
    
    public class DeviceIsRootReceive:FNUrlRouteDelegate{
        public required init(params: Dictionary<String, AnyObject>?) {
            let callKey = getCallKey(params: params)
            let isRoot = RootJudge.init().isRoot()
            ChannelPlugin.resultCall(resultKey: callKey, callbackData: isRoot)
        }
    }
    
    public class DeviceIsEmulatorReceive:FNUrlRouteDelegate{
        public required init(params: Dictionary<String, AnyObject>?) {
            let callKey = getCallKey(params: params)
            let isEmulator = EmulatorJudge.init().isEmulator()
            ChannelPlugin.resultCall(resultKey: callKey, callbackData: isEmulator)
        }
    }
    
    public class DeviceInfoReceive:FNUrlRouteDelegate{
        public required init(params: Dictionary<String, AnyObject>?) {
            let callKey = getCallKey(params: params)
            let ocDict = EyflutterCorePlugin.getDeviceResponse()! as NSDictionary
            let diviceMap: Dictionary<String, AnyObject?> = ConvertUtils.toDictionary(ocDictionary:ocDict)
            let response = JsonUtils.getJSONStringFromDictionary(dataObject: diviceMap)
            ChannelPlugin.resultCall(resultKey: callKey, callbackData: response)
        }
    }

}
