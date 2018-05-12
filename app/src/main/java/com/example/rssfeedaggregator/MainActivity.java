package com.example.rssfeedaggregator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.rssfeedaggregator.network.Entry;
import com.example.rssfeedaggregator.network.FeedObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> feedUrls = Arrays.asList(
                "https://news.google.com/?output=atom",
                "http://www.theregister.co.uk/software/headlines.atom",
                "http://www.linux.com/news/soware?format=feed&type=atom"
        );

        //for each feed url, construct an observable and add it to a list.
        List<Observable<List<Entry>>> observableList = new ArrayList<>();
        for (String feedUrl : feedUrls) {
            final Observable<List<Entry>> feedObservable =
                    FeedObservable.getFeed(feedUrl)
                            .subscribeOn(Schedulers.io())
                            .onErrorReturn(e -> new ArrayList<Entry>());
            observableList.add(feedObservable);
        }

        //Combine the results from each observable into a single list.
        Observable<List<Entry>> combinedObservable =
                Observable.combineLatest(observableList,
                        (listOfLists) -> {
                            final List<Entry> combinedList = new ArrayList<>();
                            for (Object list : listOfLists) {
                                combinedList.addAll((List<Entry>) list);
                            }
                            return combinedList;
                        }
                );

        combinedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::drawList);

    }

    private void drawList(List<Entry> listItems) {
        Log.v(TAG, "List size: "+listItems.size());
        for(Entry e: listItems)
            Log.v(TAG, "Entry: "+e.toString());

    }


}
