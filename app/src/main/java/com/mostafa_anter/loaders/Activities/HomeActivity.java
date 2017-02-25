package com.mostafa_anter.loaders.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.mostafa_anter.loaders.R;
import com.mostafa_anter.loaders.R2;
import com.mostafa_anter.loaders.adapters.FeedAdapter;
import com.mostafa_anter.loaders.loaders.FeedAsyncTaskLoader;
import com.mostafa_anter.loaders.model.FeedItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @Nullable
    @BindView(R2.id.noData)
    LinearLayout noDataView;
    @Nullable
    @BindView(R2.id.recyclerView)
    RecyclerView mRecyclerView;
    @Nullable
    @BindView(R2.id.progressBar1)
    ProgressBar progressBar;


    // for recycler view
    private static final String TAG = "HomeActivity";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    protected LayoutManagerType mCurrentLayoutManagerType;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected FeedAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<FeedItem> mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set recyclerView
        setRecyclerView(savedInstanceState);
    }

    private void setRecyclerView(Bundle savedInstanceState) {
        // initiate mDataSet
        mDataset = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new FeedAdapter(this, mDataset);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);


        // i think here is best place to initiate loader
        getSupportLoaderManager().initLoader(0, null, mLoaderCallbacks);
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    // Our Callbacks. Could also have the Activity/Fragment implement
    // LoaderManager.LoaderCallbacks<List<String>>
    private LoaderManager.LoaderCallbacks<List<FeedItem>>
            mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<FeedItem>>() {
                @Override
                public Loader<List<FeedItem>> onCreateLoader(
                        int id, Bundle args) {
                    return new FeedAsyncTaskLoader(HomeActivity.this);
                }
                @Override
                public void onLoadFinished(
                        Loader<List<FeedItem>> loader, List<FeedItem> data) {
                    // Display our data, for instance updating our adapter
                    if (data != null) {
                        mDataset.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onLoaderReset(Loader<List<FeedItem>> loader) {
                    // Loader reset, throw away our data,
                    // unregister any listeners, etc.
                    mDataset = null;
                    // Of course, unless you use destroyLoader(),
                    // this is called when everything is already dying
                    // so a completely empty onLoaderReset() is
                    // totally acceptable
                }
            };
}
