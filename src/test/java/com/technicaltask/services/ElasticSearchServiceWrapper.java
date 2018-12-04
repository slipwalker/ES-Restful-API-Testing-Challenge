package com.technicaltask.services;

import com.google.gson.reflect.TypeToken;
import com.technicaltask.data.ResponseContentTypeFormat;
import com.technicaltask.mime.TypedJsonString;
import com.technicaltask.pojo.SearchResult;
import com.technicaltask.pojo.TwitterIndexData;
import com.technicaltask.utils.FilePathUtils;
import com.technicaltask.utils.JsonConverter;
import retrofit.RetrofitError;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import static com.technicaltask.services.ServiceBuilder.createService;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class ElasticSearchServiceWrapper {

    // some defaults just for testing purposes
    private String defaultType = "_doc";

    private String esDataFolder = "es_data";
    private String twitterMappingFile = "twitter_mappings_data.json";
    private String twitterIndexDataFile = "twitter_index_data.json";
    private String twitterSettingsFile = "twitter_settings_data.json";

    private String endPoint;
    private ElasticSearchService elasticSearchService;

    public ElasticSearchServiceWrapper(String endPoint) {
        this.endPoint = endPoint;
        this.elasticSearchService = createService(ElasticSearchService.class, endPoint);
    }

    public void createIndexWithData(String indexName) {
        createIndexWithData(indexName, twitterSettingsFile, twitterMappingFile, twitterIndexDataFile);
    }

    private void createIndexWithData(String indexName, String settingsFile, String mappingsFile, String dataIndexFile) {
        if (!isIndexExist(indexName)) {
            createIndex(indexName, settingsFile);
            createIndexMappings(indexName, mappingsFile);
            populateIndexMappings(indexName, dataIndexFile);
        }
    }

    private Object populateIndexMappings(String indexName, String type, String dataIndexJson) throws RetrofitError {
        return elasticSearchService.populateIndexMappings(new TypedJsonString(dataIndexJson), indexName, type);
    }

    public boolean isIndexExist(String indexName) {
        try {
            elasticSearchService.getIndex(indexName); // either get an index or RetrofitError if index does not exist. Also possible NPE if index is null
        } catch (RetrofitError | NullPointerException ex) {
            return false;
        }
        return true;
    }

    public SearchResult searchBy(String indexName, String query, int from, int size, String sort) {
        waitUntilDataProperlyIndexed(() -> Objects.nonNull(elasticSearchService.searchBy(indexName, query, from, size, sort).getHitsResult().getHits()));
        return elasticSearchService.searchBy(indexName, query, from, size, sort);
    }

    public SearchResult searchWithSpecifyingResponseFormat(String indexName, String query, ResponseContentTypeFormat format) {
        waitUntilDataProperlyIndexed(() -> Objects.nonNull(elasticSearchService.searchWithSpecifyingResponseFormat(indexName, query,"json&pretty"))); // just to check that data is indexed

        return createService(ElasticSearchService.class, endPoint, format).searchWithSpecifyingResponseFormat(indexName, query, format.toString());
    }

    private Object createIndex(String indexName, String settingsFile) {
        String settingsJson = JsonConverter.readJsonFile(getESResourceFile(settingsFile));
        return elasticSearchService.createIndex(new TypedJsonString(settingsJson), indexName);
    }

    private Object createIndexMappings(String indexName, String mappingsFile) {
        String mappingsJson = JsonConverter.readJsonFile(getESResourceFile(mappingsFile));
        return createIndexMappings(indexName, defaultType, mappingsJson);
    }

    private Object createIndexMappings(String indexName, String type, String mappingsJson) {
        return elasticSearchService.createMappings(new TypedJsonString(mappingsJson), indexName, type);
    }

    private void populateIndexMappings(String indexName, String dataIndexFile) {
        List<TwitterIndexData> esIndexDataList = getESIndexDataList(dataIndexFile);
        try {
            esIndexDataList.forEach(data -> populateIndexMappings(indexName, defaultType, data.toString()));
        } catch (RetrofitError e) {
            e.printStackTrace();
        }
    }

    private List<TwitterIndexData> getESIndexDataList(String esIndexDataFile) {
        Type resultType = new TypeToken<List<TwitterIndexData>>() {
        }.getType();
        return JsonConverter.readJsonFile(getESResourceFile(esIndexDataFile), resultType);
    }

    private File getESResourceFile(String relativePath) {
        return new File(FilePathUtils.getFilePath(format("%s/%s", esDataFolder, relativePath)));
    }

    private void waitUntilDataProperlyIndexed(Callable<Boolean> conditionEvaluator) {
        // wait until data is properly indexed in ES
        await().atMost(30, SECONDS)
                .pollDelay(1, SECONDS)
                .pollInterval(2, SECONDS)
                .until((conditionEvaluator));
    }
}