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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
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
    private File downloadedFile = new File(
            getContext().getFilesDir(), FILE_NAME);

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
        mData = readObjectsFromFile(downloadedFile);
        if (mData != null){
            // use cashed data
            deliverResult(mData);
        }

        forceLoad();
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

                        deleteFileContent(downloadedFile);
                        saveObjectsInsideFile(downloadedFile, mData);


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

    protected void onReset() {

    }

    private static void deleteFileContent(File file){
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private <T> void saveObjectsInsideFile(File file, List<T> items){
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeInt(items.size()); // Save size first
            for(T item:items)
                objectOutStream.writeObject(item);
            objectOutStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> List<T> readObjectsFromFile(File file){
        try {
            FileInputStream inStream = new FileInputStream(file);
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            int count = objectInStream.readInt(); // Get the number of regions
            List<T> items = new ArrayList<>();
            for (int c=0; c < count; c++)
                items.add((T) objectInStream.readObject());
            objectInStream.close();
            return items;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
