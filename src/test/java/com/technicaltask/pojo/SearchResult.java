package com.technicaltask.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class SearchResult {

    private int took;

    @JsonProperty("timed_out")
    private boolean timedOut;

    @JsonProperty("_shards")
    private Map<String, Integer> shards;

    @JsonProperty("hits")
    private SearchHit hits;

    public int getTook() {
        return took;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public Map<String, Integer> getShards() {
        return shards;
    }

    public SearchHit getHitsResult() {
        return hits;
    }
}