package com.example.coolrss.utils;

/*
 * Created by dutnguyen on 4/20/2020.
 */

public class ReturnObj {
    public enum TYPE {
        UI_ERROR,
        ERROR_EXCEPTION,
        CONNECTIVITY_EXCEPTION,
        NO_ERROR
    }
    private TYPE type;
    private String errorMessage;

    public ReturnObj() {
        this.type = TYPE.NO_ERROR;
        this.errorMessage = "";
    }

    public ReturnObj(TYPE type, String errorMessage) {
        this.type = type;
        this.errorMessage = errorMessage;
    }

    public Boolean isError() {
        return type != TYPE.NO_ERROR;
    }

    public TYPE getType() {
        return type;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
