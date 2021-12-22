package com.basic.eyflutter_core.nets;

import android.text.TextUtils;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-29
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class RequestCodeUtils {

    //https://baike.baidu.com/item/HTTP状态码/5053660?fr=aladdin
    public static int getCodeByError(String errorMessage) {
        if (TextUtils.isEmpty(errorMessage)) {
            return 0;
        }
        if (errorMessage.contains("timeout")) {
            return 408;
        }
        return 0;
    }
}
