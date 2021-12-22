package com.basic.eyflutter_core.nets.beans;

import com.basic.eyflutter_core.nets.annotations.ApiHeadersCall;
import com.basic.eyflutter_core.nets.annotations.BaseUrlTypeName;
import com.basic.eyflutter_core.nets.enums.CallStatus;
import com.basic.eyflutter_core.nets.enums.RequestContentType;
import com.basic.eyflutter_core.nets.enums.RequestType;
import com.basic.eyflutter_core.nets.enums.ResponseDataType;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2017/6/1
 * Description:http 改造参数
 * Modifier:
 * ModifyContent:
 */
public class RetrofitParams {
    /**
     * 请求类型
     */
    private RequestType requestType = RequestType.GET;
    /**
     * 请求地址
     */
    private String requestUrl = "";
    /**
     * 头信息
     */
    private HashMap<String, String> headParams = null;
    /**
     * 请求参数
     * (对于当次请求,线程间切换数据都是顺序传递的,因此使用TreeMap[非线程安全结构]不影响)
     */
    private TreeMap<String, Object> params = null;
    /**
     * 文件后缀参数
     */
    private HashMap<String, String> fileSuffixParams = null;
    /**
     * del query请求参数
     */
    private HashMap<String, String> delQueryParams = null;

    /**
     * 缓存key
     */
    private String cacheKey = "";

    /**
     * 缓存时间
     */
    private Duration duration;
    /**
     * api名称
     */
    private String apiName = "";
    /**
     * 数据类
     */
    private Class dataClass = null;
    /**
     * 是否集合数据类型
     */
    private boolean isCollectionDataType = false;
    /**
     * 请求验证是否通过(默认为true)
     */
    private boolean flag = true;
    /**
     * 是否拼接url
     */
    private boolean isJoinUrl = false;

    private BaseUrlTypeName urlTypeName = null;
    /**
     * 数据提交方式
     */
    private RequestContentType requestContentType = null;
    /**
     * 允许接口返回码
     */
    private List<String> allowRetCodes = null;

    /**
     * 末尾是否包含/
     */
    private boolean isLastContainsPath = false;
    /**
     * api请求头回调注解
     */
    private ApiHeadersCall apiHeadersCall = null;
    /**
     * 请求总时间
     */
    private long requestTotalTime = 0;
    /**
     * 当前请求时间
     */
    private long currentRequestTime = 0;
    /**
     * 回调数据类型(默认OnlyNet)
     */
    private CallStatus callStatus = CallStatus.OnlyNet;
    /**
     * 请求方法名
     */
    private String invokeMethodName = "";
    /**
     * 数据响应类型
     */
    private ResponseDataType responseDataType = ResponseDataType.object;
    /**
     * 当responseDataType()为byteData或stream类型时且value()为File类型,
     * 则最终将转换为以targetFilePath()作为路径的文件;
     */
    private String targetFilePath = "";
    /**
     * 失败后是否启用重试
     */
    private boolean isFailureRetry = false;
    /**
     * 请求失败重试次数(isFailureRetry==true时有效)
     */
    private int failureRetryCount = 0;
    /**
     * 是否需要token验证
     */
    private boolean isTokenValid = false;
    /**
     * 超时毫秒数(connection write request <=0不处理)
     */
    private long timeoutMillis;
    /**
     * 解析字段映射
     */
    private String parsingFieldMapping;

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public HashMap<String, String> getHeadParams() {
        if (headParams == null) {
            headParams = new HashMap<String, String>();
        }
        return headParams;
    }

    public TreeMap<String, Object> getParams() {
        if (params == null) {
            params = new TreeMap<String, Object>();
        }
        return params;
    }

    public HashMap<String, String> getFileSuffixParams() {
        if (fileSuffixParams == null) {
            fileSuffixParams = new HashMap<String, String>();
        }
        return fileSuffixParams;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public Duration getCacheTime() {
        return duration;
    }

    public void setCacheTime(Duration cacheTime) {
        this.duration = cacheTime;
    }

    /**
     * 获取api名称
     */
    public String getApiName() {
        if (apiName == null) {
            apiName = "";
        }
        return apiName;
    }

    /**
     * 设置api名称
     * <p>
     * param apiName
     */
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Class getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class dataClass) {
        this.dataClass = dataClass;
    }

    public boolean isCollectionDataType() {
        return isCollectionDataType;
    }

    public void setCollectionDataType(boolean collectionDataType) {
        isCollectionDataType = collectionDataType;
    }

    /**
     * 获取请求验证是否通过
     */
    public boolean getFlag() {
        return flag;
    }

    /**
     * 设置请求验证是否通过
     * <p>
     * param flag
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * 获取是否拼接url
     */
    public boolean getIsJoinUrl() {
        return isJoinUrl;
    }

    /**
     * 设置是否拼接url
     * <p>
     * param isJoinUrl
     */
    public void setIsJoinUrl(boolean isJoinUrl) {
        this.isJoinUrl = isJoinUrl;
    }

    public HashMap<String, String> getDelQueryParams() {
        if (delQueryParams == null) {
            delQueryParams = new HashMap<String, String>();
        }
        return delQueryParams;
    }

    public void setDelQueryParams(HashMap<String, String> delQueryParams) {
        this.delQueryParams = delQueryParams;
    }

    public BaseUrlTypeName getUrlTypeName() {
        return urlTypeName;
    }

    public void setUrlTypeName(BaseUrlTypeName urlTypeName) {
        this.urlTypeName = urlTypeName;
    }

    public RequestContentType getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(RequestContentType requestContentType) {
        this.requestContentType = requestContentType;
    }

    public List<String> getAllowRetCodes() {
        return allowRetCodes;
    }

    public void setAllowRetCodes(List<String> allowRetCodes) {
        this.allowRetCodes = allowRetCodes;
    }

    public boolean isLastContainsPath() {
        return isLastContainsPath;
    }

    public void setLastContainsPath(boolean lastContainsPath) {
        isLastContainsPath = lastContainsPath;
    }

    public ApiHeadersCall getApiHeadersCall() {
        return apiHeadersCall;
    }

    public void setApiHeadersCall(ApiHeadersCall apiHeadersCall) {
        this.apiHeadersCall = apiHeadersCall;
    }

    public long getRequestTotalTime() {
        return requestTotalTime;
    }

    public void setRequestTotalTime(long requestTotalTime) {
        this.requestTotalTime = requestTotalTime;
    }

    public long getCurrentRequestTime() {
        return currentRequestTime;
    }

    public void setCurrentRequestTime(long currentRequestTime) {
        this.currentRequestTime = currentRequestTime;
    }

    public CallStatus getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    public String getInvokeMethodName() {
        return invokeMethodName;
    }

    public void setInvokeMethodName(String invokeMethodName) {
        this.invokeMethodName = invokeMethodName;
    }

    public ResponseDataType getResponseDataType() {
        if (responseDataType == null) {
            responseDataType = ResponseDataType.object;
        }
        return responseDataType;
    }

    public void setResponseDataType(ResponseDataType responseDataType) {
        this.responseDataType = responseDataType;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    public boolean isFailureRetry() {
        return isFailureRetry;
    }

    public void setFailureRetry(boolean failureRetry) {
        isFailureRetry = failureRetry;
    }

    public int getFailureRetryCount() {
        return failureRetryCount;
    }

    public void setFailureRetryCount(int failureRetryCount) {
        this.failureRetryCount = failureRetryCount;
    }

    public boolean isTokenValid() {
        return isTokenValid;
    }

    public void setTokenValid(boolean tokenValid) {
        isTokenValid = tokenValid;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public String getParsingFieldMapping() {
        return parsingFieldMapping == null ? "" : parsingFieldMapping;
    }

    public void setParsingFieldMapping(String parsingFieldMapping) {
        this.parsingFieldMapping = parsingFieldMapping;
    }
}
