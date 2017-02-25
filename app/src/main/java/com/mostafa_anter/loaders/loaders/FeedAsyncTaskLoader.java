package com.mostafa_anter.loaders.loaders;

import android.content.Context;
import android.os.FileObserver;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.mostafa_anter.loaders.model.FeedItem;
import com.mostafa_anter.loaders.model.FeedResponse;
import com.mostafa_anter.loaders.rest.ApiClient;
import com.mostafa_anter.loaders.rest.ApiInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mostafa_anter on 2/25/17.
 */

public class FeedAsyncTaskLoader extends
        AsyncTaskLoader<List<FeedItem>>{

    private List<FeedItem> mData;
    private static final String FILE_NAME = "feed_file";
    private FileObserver mFileObserver;

    // for retrofit request
    private Subscription subscription;
    private ApiInterface apiService;

    public FeedAsyncTaskLoader(Context context) {
        super(context);

        // get apiService
        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null){
            // use cashed data
            deliverResult(mData);
        }
        if (mFileObserver == null) {
            String path = new File(
                    getContext().getFilesDir(), FILE_NAME).getPath();
            mFileObserver = new FileObserver(path) {
                @Override
                public void onEvent(int event, String path) {
                    // Notify the loader to reload the data
                    onContentChanged();
                    // If the loader is started, this will kick off
                    // loadInBackground() immediately. Otherwise,
                    // the fact that something changed will be cached
                    // and can be later retrieved via takeContentChanged()
                }
            };
            mFileObserver.startWatching();
        }
        if (takeContentChanged() || mData == null) {
            // Something has changed or we have no data,
            // so kick off loading it
            forceLoad();
        }
    }

    @Override
    public List<FeedItem> loadInBackground() {
        Observable<FeedResponse> feedObservable =
                apiService.getFeeds();
        subscription = feedObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FeedResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(FeedResponse feedResponse) {
                        mData = feedResponse.getFeed();
                        deliverResult(mData);
                        File downloadedFile = new File(
                                getContext().getFilesDir(), FILE_NAME);

//                        try {
//                            FileOutputStream outputStream =
//                                    new FileOutputStream(downloadedFile);
//                            outputStream.write(string.getBytes());
//                            outputStream.close();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                    }
                });



        return null;
    }

    @Override
    public void deliverResult(List<FeedItem> data) {
        // We’ll save the data for later retrieval
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        if (mData != null){
            // use cashed data
            deliverResult(mData);
        }
    }

    protected void onReset() {
        // Stop watching for file changes
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
            mFileObserver = null;
        }
    }
}
