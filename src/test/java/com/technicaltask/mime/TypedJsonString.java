package com.technicaltask.mime;

import retrofit.mime.TypedString;

public class TypedJsonString extends TypedString {

    public TypedJsonString(String string) {
        super(string);
    }

    @Override
    public String mimeType() {
        return "application/json";
    }
}