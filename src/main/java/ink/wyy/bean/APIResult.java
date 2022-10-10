package ink.wyy.bean;

import ink.wyy.constant.ApiConstant;

public class APIResult {

    private int code;
    private String msg;
    private Object data;

    public static APIResult createOk(Object data) {
        return createWithCodeAndData(ApiConstant.Code.OK.ordinal(), null, data);
    }

    public static APIResult createOKMessage(String msg) {
        APIResult result = new APIResult();
        result.setCode(ApiConstant.Code.OK.ordinal());
        result.setMsg(msg);
        return result;
    }

    public static APIResult createNg(String message) {
        return createWithCodeAndData(ApiConstant.Code.NG.ordinal(), message, null);
    }

    private static APIResult createWithCodeAndData(int code, String msg, Object data) {
        APIResult result = new APIResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
