package com.technicaltask.data;

public enum ResponseContentTypeFormat {
    JSON("json"),
    YAML("yaml");

    private String format;

    ResponseContentTypeFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }
}