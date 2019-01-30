package com.example.android.basicmediadecoder;

public class DataBean {
    public String key;
    public int type;
    public int min;
    public int max;

    public int num;

    public String description;

    public DataBean(String key, int type, int min, int max, int num, String description) {
        this.key = key;
        this.type = type;
        this.min = min;
        this.max = max;
        this.num = num;
        this.description = description;
    }
}
