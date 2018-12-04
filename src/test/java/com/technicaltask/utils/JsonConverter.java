package com.technicaltask.utils;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

public class JsonConverter {

    private static final Gson GSON = new Gson();

    public static <T> T readJsonFile(File file, Type type) {
        if (Objects.isNull(file)) return null;
        try {
            String json = IOUtils.toString(new FileReader(file));
            return GSON.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readJsonFile(File file) {
        if (Objects.isNull(file)) return null;
        try {
            return IOUtils.toString(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}