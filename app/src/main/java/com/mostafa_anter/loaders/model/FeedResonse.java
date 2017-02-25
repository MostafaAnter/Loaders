package com.mostafa_anter.loaders.model;

import java.util.List;

/**
 * Created by mostafa_anter on 2/25/17.
 */

public class FeedResonse {
    private List<FeedItem> feed = null;

    public List<FeedItem> getFeed() {
        return feed;
    }

    public void setFeed(List<FeedItem> feed) {
        this.feed = feed;
    }
}
