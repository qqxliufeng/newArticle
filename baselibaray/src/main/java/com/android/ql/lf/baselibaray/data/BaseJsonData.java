package com.android.ql.lf.baselibaray.data;

import java.util.ArrayList;

/**
 * Created by lf on 2017/11/13 0013.
 *
 * @author lf on 2017/11/13 0013
 */

public class BaseJsonData {

    private String code;
    private String msg;
    private Object arr;
    private Object arr1;
    private ArrayList<Object> result;

    public ArrayList<Object> getData() {
        return result;
    }

    public void setData(ArrayList<Object> data) {
        this.result = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getArr() {
        return arr;
    }

    public void setArr(String arr) {
        this.arr = arr;
    }

    public Object getArr1() {
        return arr1;
    }

    public void setArr1(String arr1) {
        this.arr1 = arr1;
    }

}
