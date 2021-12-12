package com.basic.eyflutter_core.channel;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-02-14
 * Description:
 * Modifier:
 * ModifyContent:
 */
abstract class ImplementedCallHandle<Result> {
    private Result result;
    private String action;
    private Object arguments;

    public ImplementedCallHandle(Result result, String action, Object arguments) {
        this.result = result;
        this.action = action;
        this.arguments = arguments;
    }

    public Result getResult() {
        return result;
    }

    public String getAction() {
        return action;
    }

    public Object getArguments() {
        return arguments;
    }

    protected abstract void onNotImplemented(Result result, String action, Object arguments);

    public void notImplemented() {
        onNotImplemented(this.result, this.action, this.arguments);
    }
}
