package com.example.coolrss.exception;

/*
 * Created by dutngyen on 4/18/2020.
 */

import androidx.annotation.Nullable;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Nullable
    @Override
    public String getMessage() {
        return "No Internet Connection";
    }
}
