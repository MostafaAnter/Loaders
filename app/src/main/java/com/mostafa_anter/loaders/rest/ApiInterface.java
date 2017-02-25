package com.mostafa_anter.loaders.rest;

import com.mostafa_anter.loaders.model.FeedResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by mostafa_anter on 1/1/17.
 */

public interface ApiInterface {
    @GET("feed/feed.json")
    Observable<FeedResponse> getFeeds();

}
