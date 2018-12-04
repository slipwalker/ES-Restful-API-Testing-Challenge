package com.technicaltask.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import java.io.IOException;
import java.lang.reflect.Type;

public class YamlConverter implements Converter {

    private static final String MIME_TYPE = "application/yaml; charset=UTF-8";
    private final ObjectMapper objectMapper;

    public YamlConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        try {
            JavaType javaType = this.objectMapper.getTypeFactory().constructType(type);
            return this.objectMapper.readValue(body.in(), javaType);
        } catch (IOException ex) {
            throw new ConversionException(ex);
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        try {
            byte[] yamlData = this.objectMapper.writeValueAsBytes(object);
            return new TypedByteArray(MIME_TYPE, yamlData);
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }
}