package com.technicaltask.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchHit {

    private int total;

    @JsonProperty("max_score")
    private int maxScore;

    @JsonProperty("_index")
    private String index;

    @JsonProperty("_type")
    private String type;

    @JsonProperty("_id")
    private String id;

    @JsonProperty("_source")
    private TwitterIndexData twitterIndexData;

    private List<SearchHit> hits;

    public int getTotal() {
        return total;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public TwitterIndexData getTwitterIndexData() {
        return twitterIndexData;
    }

    public List<SearchHit> getHits() {
        return hits;
    }
}