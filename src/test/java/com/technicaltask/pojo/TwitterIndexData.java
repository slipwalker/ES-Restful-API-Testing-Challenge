package com.technicaltask.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import static java.lang.String.format;

public class TwitterIndexData {

    @SerializedName("user")
    @JsonProperty("user")
    private String user;

    @SerializedName("post_date")
    @JsonProperty("post_date")
    private String postDate;

    @SerializedName("message")
    @JsonProperty("message")
    private String message;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return format("{ \"user\": \"%s\", \"post_date\": \"%s\", \"message\": \"%s\" }", getUser(), getPostDate(), getMessage());
    }
}