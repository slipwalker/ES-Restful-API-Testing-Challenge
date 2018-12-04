package com.technicaltask.utils;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

import static com.technicaltask.utils.Common.createUriFromUrl;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;

public class FilePathUtils {
    public static String getFilePath(String fileName) {
        URL resource = currentThread().getContextClassLoader().getResource(fileName);
        if (Objects.isNull(resource)) {
            throw new RuntimeException(format("Resource %s could not be found or the invoker doesn't have adequate privileges to get the resource!", fileName));
        }
        return Paths.get(createUriFromUrl(resource)).toFile().getAbsolutePath();
    }
}