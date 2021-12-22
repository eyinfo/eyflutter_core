package com.basic.eyflutter_core.nets.beans;

import android.graphics.Bitmap;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019-09-29
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class SuccessBitmapResponse {
    /**
     * net bitmap
     */
    private Bitmap bitmap;
    /**
     * net request code
     */
    private int code;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
