package com.example.rssfeedaggregator.network;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class FeedObservable {
    private static final String TAG = FeedObservable.class.getSimpleName();

    private FeedObservable() {

    }

    /**
     * Create a feed that will download from a url and parse the contents into a list of entries.
     *
     */
    public static Observable<List<Entry>> getFeed(final String url) {
        Log.v(TAG, "feed url " + url );

        return RawNetworkObservable.create(url)
                .map(response -> {
                    FeedParser parser = new FeedParser();
                    try {
                        List<Entry> entries = parser.parse(response.body().byteStream());
                        Log.v(TAG, "Number of entries from url " + url + ": " + entries.size());

                        return entries;
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing feed", e);
                    }
                    return new ArrayList<>();
                });
    }
}
