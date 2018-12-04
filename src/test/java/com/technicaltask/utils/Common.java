package com.technicaltask.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Common {

    public static URI createUriFromUrl(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}