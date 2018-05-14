package com.example.rssfeedaggregator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.rssfeedaggregator.network.Entry;
import com.example.rssfeedaggregator.network.FeedObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getCanonicalName();
    private RecyclerView mRecyclerView;
    private ListOfEntriesAdapter mAdapter;
    private List<Entry> mDataSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);

        // specify an adapter (see also next example)
        mDataSet = new ArrayList<>();
        mAdapter = new ListOfEntriesAdapter(mDataSet);

        mRecyclerView.setAdapter(mAdapter);

        List<String> feedUrls = Arrays.asList(
                "https://news.google.com/?output=atom",
                "http://www.theregister.co.uk/software/headlines.atom",
                "http://www.linux.com/news/soware?format=feed&type=atom"
        );

        //for each feed url, construct an observable and add it to a list.
        //the feed is only read once, there is no update functionality.
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

        Observable<List<Entry>> sortedListObservable =
                combinedObservable.map(this::sortList);

        sortedListObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateFeedView);

    }

    /**
     * sort the list.
     * @param list
     * @return
     */
    List<Entry> sortList(List<Entry> list) {
        List<Entry> sortedList = new ArrayList<>();
        sortedList.addAll(list);
        Collections.sort(sortedList);
        return sortedList;
    }

    /**
     * Print the contents to the terminal for now.
     * focus on Rx stuff first.
     * @param listItems
     */
    private void updateFeedView(List<Entry> listItems) {
        Log.v(TAG, "List size: "+listItems.size());

        mDataSet.addAll(listItems);
        mAdapter.notifyDataSetChanged();

    }


}
