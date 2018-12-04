package com.technicaltask.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.squareup.okhttp.OkHttpClient;
import com.technicaltask.converter.YamlConverter;
import com.technicaltask.data.ResponseContentTypeFormat;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.net.util.TrustManagerUtils.getAcceptAllTrustManager;

public class ServiceBuilder {

    private static final long REQUEST_TIMEOUT_IN_SECONDS = 180;

    public static <S> S createService(Class<S> serviceClass, String endPoint) {
        return createService(serviceClass, endPoint, new JacksonConverter(getObjectMapper()));
    }

    public static <S> S createService(Class<S> serviceClass, String endPoint, ResponseContentTypeFormat format) {
        switch (format) {
            case JSON: return createService(serviceClass, endPoint);
            case YAML: return createService(serviceClass, endPoint, new YamlConverter(getYamlObjectMapper()));
            default: return createService(serviceClass, endPoint);
        }
    }

    private static <S> S createService(Class<S> serviceClass, String endPoint, Converter converter) {
        return buildService(endPoint, converter)
                .build()
                .create(serviceClass);
    }

    private static RestAdapter.Builder buildService(String endPoint, Converter converter) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(REQUEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        client.setReadTimeout(REQUEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        client.setWriteTimeout(REQUEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        setCertificates(client);

        return new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setEndpoint(endPoint)
                .setConverter(converter)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setLog(message -> LoggerFactory.getLogger(RestAdapter.class).info(message))
                .setErrorHandler(retrofitError -> {
                    LoggerFactory.getLogger(RestAdapter.class).error(formatError(retrofitError));
                    return retrofitError;
                });
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    private static ObjectMapper getYamlObjectMapper() {
        return new ObjectMapper(new YAMLFactory());
    }

    private static OkHttpClient setCertificates(OkHttpClient client) {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = new TrustManager[]{getAcceptAllTrustManager()};
            sslContext.init(null, trustManagers, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        assert sslContext != null;
        client.setSslSocketFactory(sslContext.getSocketFactory());
        return client;
    }

    private static String formatError(RetrofitError error) {
        try {
            Response response = error.getResponse();
            InputStream in = response.getBody().in();
            String payload = IOUtils.toString(in, Charset.defaultCharset());
            in.close();
            return String.format("Status: [%d], reason: [%s], body: [%s]", response.getStatus(), response.getReason(), payload);
        } catch (NullPointerException | IOException e) {
            throw new RuntimeException(error);
        }
    }
}