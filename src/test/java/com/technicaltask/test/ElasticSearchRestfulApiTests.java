package com.technicaltask.test;

import com.technicaltask.data.ResponseContentTypeFormat;
import com.technicaltask.pojo.SearchHit;
import com.technicaltask.pojo.SearchResult;
import com.technicaltask.pojo.TwitterIndexData;
import com.technicaltask.services.ElasticSearchServiceWrapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.technicaltask.data.ResponseContentTypeFormat.JSON;
import static com.technicaltask.data.ResponseContentTypeFormat.YAML;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ElasticSearchRestfulApiTests extends TestBase {

    private ElasticSearchServiceWrapper elasticSearchApiWrapper;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        elasticSearchApiWrapper = new ElasticSearchServiceWrapper("http://" + container.getHttpHostAddress());
    }

    @Test(priority = 1)
    public void checkThatIndexCreated() {
        String indexName = "twitter";

        elasticSearchApiWrapper.createIndexWithData(indexName);

        assertThat(format("'%s' index should exist after creation!", indexName), elasticSearchApiWrapper.isIndexExist(indexName));
    }

    @Test(priority = 2, dataProvider = "searchQueryProvider")
    public void checkSearchingByMatchQuery(String query, String userName, String postDate, String message) {
        String indexName = "twitter";
        String type = "_doc";
        int numberOfShards = 3;
        String sortBy = "post_date:desc";

        SearchResult searchResult = elasticSearchApiWrapper.searchBy(indexName, query, 0, 10, sortBy);
        checkSearchResult(searchResult, numberOfShards, indexName, type, userName, postDate, message);
    }

    @Test(priority = 3, dataProvider = "responseFormatProvider")
    public void checkSearchResultWithDifferentFormatOfResponseContentType(ResponseContentTypeFormat format) {
        String indexName = "twitter";
        String type = "_doc";
        int numberOfShards = 3;
        String query = "willy wonka";
        String postDate = "2012-11-02 14:33:22";
        String message = "really eager to know";

        SearchResult searchResult = elasticSearchApiWrapper.searchWithSpecifyingResponseFormat(indexName, query, format);
        checkSearchResult(searchResult, numberOfShards, indexName, type, query, postDate, message);
    }

    private void checkSearchResult(SearchResult searchResult, int numberOfShards, String indexName, String type,
                                   String userName, String postDate, String message) {

        SearchHit searchHit = searchResult.getHitsResult().getHits().get(0);
        TwitterIndexData twitterIndexData = searchHit.getTwitterIndexData();

        assertThat(format("Number of shards should be: '%s'", numberOfShards), searchResult.getShards().get("total"), is(numberOfShards));
        assertThat(format("Index name should be: '%s'", indexName), searchHit.getIndex(), equalTo(indexName));
        assertThat(format("Type should be: '%s'", type), searchHit.getType(), equalTo(type));

        assertThat(format("User name should be: '%s'.", userName), twitterIndexData.getUser(), equalTo(userName));
        assertThat(format("Post date should be: '%s'.", postDate), twitterIndexData.getPostDate(), equalTo(postDate));
        assertThat(format("Message should be: '%s'", message), twitterIndexData.getMessage(), equalTo(message));
    }

    @DataProvider
    protected Object[][] searchQueryProvider() {
        return new Object[][]{
                {"kimchy", "kimchy", "2005-11-02 10:33:22", "new trying out Elasticsearch"},
                {"kimchy AND message:\"trying out Elasticsearch\"", "kimchy", "2001-11-02 10:33:22", "trying out Elasticsearch"}
        };
    }

    @DataProvider
    protected Object[][] responseFormatProvider() {
        return new Object[][]{
                { JSON },
                { YAML }
        };
    }
}