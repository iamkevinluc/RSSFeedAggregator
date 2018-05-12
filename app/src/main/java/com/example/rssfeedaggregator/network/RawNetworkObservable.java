package com.example.rssfeedaggregator.network;

import android.util.Log;

import org.reactivestreams.Subscriber;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An observable that will download the stream of contents from a url.
 */
public class RawNetworkObservable {
    private static final String TAG = RawNetworkObservable.class.getSimpleName();

    private RawNetworkObservable() {

    }

    public static Observable<Response> create(final String url) {
        return Observable.create((subscriber) ->{
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(new Request.Builder().url(url).build()).execute();
                subscriber.onNext(response);
                subscriber.onComplete();
                if (!response.isSuccessful()) subscriber.onError(new Exception("error"));
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    public static Observable<String> getString(String url) {
        return create(url)
                .map(response -> {
                    try {
                        return response.body().string();
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading url " + url);
                    }
                    return null;
                });
    }
}
