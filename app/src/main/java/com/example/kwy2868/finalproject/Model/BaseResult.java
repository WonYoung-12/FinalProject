package com.example.kwy2868.finalproject.Model;

/**
 * Created by kwy2868 on 2017-08-10.
 */

public class BaseResult {
    protected int resultCode;

    public BaseResult() {
    }

    public BaseResult(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "BaseResult{" +
                "resultCode=" + resultCode +
                '}';
    }
}
