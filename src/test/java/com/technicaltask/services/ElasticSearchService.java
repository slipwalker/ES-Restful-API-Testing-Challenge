package com.technicaltask.services;

import com.technicaltask.pojo.SearchResult;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedString;

public interface ElasticSearchService {

    @PUT("/{indexName}")
    Object createIndex(@Body TypedString settingsJson, @Path("indexName") String indexName);

    @GET("/{indexName}")
    Response getIndex(@Path("indexName") String indexName);

    @PUT("/{indexName}/{type}/_mappings")
    Response createMappings(@Body TypedString mappingsJson, @Path("indexName") String indexName, @Path("type") String type);

    @POST("/{indexName}/{type}")
    Object populateIndexMappings(@Body TypedString dataJson, @Path("indexName") String indexName, @Path("type") String type);

    @GET("/{indexName}/_search")
    SearchResult searchBy(@Path("indexName") String indexName,
                          @Query("q") String query,
                          @Query("from") int from,
                          @Query("size") int size,
                          @Query("sort") String sort);

    @GET("/{indexName}/_search")
    SearchResult searchWithSpecifyingResponseFormat(@Path("indexName") String indexName,
                                                    @Query("q") String query,
                                                    @Query("format") String format);
}