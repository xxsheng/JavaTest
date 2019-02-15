package com.warm.share.model;

public class BaseModel<T> {
    public static final String SPLIT = "&&";

    private int code;
    private String message;
    private T result;

    public BaseModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static <T> BaseModel<T> success() {
        BaseModel<T> baseModel = new BaseModel<>(200, "成功");
        return baseModel;
    }

    public static <T> BaseModel<T> success(T result) {
        BaseModel<T> baseModel = new BaseModel<>(200, "成功");
        baseModel.setResult(result);
        return baseModel;
    }


    public static BaseModel fail(int code, String message) {
        BaseModel baseModel = new BaseModel<>(code, message);
        return baseModel;
    }

    public static BaseModel fail(String message) {
        return fail(message, SPLIT);
    }


    public static BaseModel fail(String message, String split) {
        int defaultCode = 20001;

        int code = defaultCode;
        String[] cm = message.split(split);

        if (cm.length != 2) {
            return fail(code, message);
        } else {
            try {
                code = Integer.parseInt(cm[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return fail(code, cm[1]);
        }
    }



}
