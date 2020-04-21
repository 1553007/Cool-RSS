package com.example.coolrss.utils;

/*
 * Created by dutnguyen on 4/20/2020.
 */

public class ReturnObj {
    public static enum TYPE {
        UI_ERROR,
        EXCEPTION,
        NO_ERROR
    }

    private Boolean isError;
    private TYPE type;
    private String errorMessage;

    public ReturnObj(Boolean isError) {
        this.isError = isError;
        this.type = TYPE.NO_ERROR;
        this.errorMessage = "";
    }

    public ReturnObj(Boolean isError, TYPE type, String errorMessage) {
        this.isError = isError;
        this.type = type;
        this.errorMessage = errorMessage;
    }

    public Boolean isError() {
        return isError;
    }

    public TYPE getType() {
        return type;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
