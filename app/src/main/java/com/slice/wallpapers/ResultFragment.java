package com.slice.wallpapers;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResultFragment extends Fragment implements HttpGetImagesAsync.onAsyncTaskFisinhed {

    private static final String TAG = "ResultFragment";
    private static final int RECYCLER_VIEW_COLUMN = 2;


    public wallpaperRecyclerViewAdapter recyclerViewAdapter;
    public RecyclerView recyclerView;
    private static wallpaperPopupFragment popupFragment;
    private static FrameLayout fragmentHolder;
    private static HttpGetImagesAsync task = new HttpGetImagesAsync();
    private int onPausePosition = 0;
    private FrameLayout noContentView;


    private static queryModel activeQueryModel;
    public ResultFragment()
    {

    }

    public void setActiveQueryModel(queryModel activeQueryModel) {
        ResultFragment.activeQueryModel = activeQueryModel;
        if(recyclerViewAdapter != null)recyclerViewAdapter.clearModels();
        if (recyclerView != null) recyclerView.setAdapter(null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_result,container,false);
        fragmentHolder = view.findViewById(R.id.result_fragment_holder);

        recyclerView = view.findViewById(R.id.result_recyclerView);
        recyclerView.getItemAnimator().setChangeDuration(0);
        popupFragment = (wallpaperPopupFragment) MainActivity.setFragment(new wallpaperPopupFragment(),fragmentHolder,getChildFragmentManager());
        noContentView = view.findViewById(R.id.result_no_content);

        if(activeQueryModel != null)
        {
            load();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int calculatedYPos = 0;
            private int actualHeight = 0;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                calculatedYPos = recyclerView.computeVerticalScrollOffset();
                actualHeight = recyclerView.computeVerticalScrollRange();



                if(actualHeight - (recyclerView.computeVerticalScrollExtent() + calculatedYPos) < MainActivity.LOAD_MORE_SCROLL_RANGE)
                {
                    if(task == null) task = new HttpGetImagesAsync();
                    if(task.getStatus() == AsyncTask.Status.FINISHED) task = new HttpGetImagesAsync();

                    task.setTaskFisinhed(ResultFragment.this);
                    if(task.getStatus() != AsyncTask.Status.RUNNING)
                    {
                        if(activeQueryModel != null)
                        {
                            activeQueryModel.setActivePage(activeQueryModel.getActivePage() + 1);
                            activeQueryModel.prepareUrl();
                            MainActivity.setMenuClickListenerForRecyclerView(activeQueryModel,recyclerView,recyclerViewAdapter,task);
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        return view;
    }

    public void load()
    {
        if(recyclerView != null)
        {
            if(recyclerView.getChildCount() > 0)
            {
                recyclerView.removeAllViews();
            }
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),RECYCLER_VIEW_COLUMN));


            if(task == null) task = new HttpGetImagesAsync();
            if(task.getStatus() == AsyncTask.Status.FINISHED) task = new HttpGetImagesAsync();
            task.setTaskFisinhed(ResultFragment.this);
            recyclerViewAdapter = new wallpaperRecyclerViewAdapter(new ArrayList<wallpaperModel>(),fragmentHolder,popupFragment,recyclerView,getActivity(),activeQueryModel,recyclerView);
            recyclerView.setAdapter(recyclerViewAdapter);
            MainActivity.setMenuClickListenerForRecyclerView(activeQueryModel,task); // Load for the startup images (active page must be 1)

        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(recyclerView != null)
        {
            getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(recyclerView != null && recyclerViewAdapter != null)
        {
            this.onPausePosition = recyclerViewAdapter.getClickedItemPosition();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(recyclerView != null && recyclerViewAdapter != null)
        {
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.scrollToPosition(this.onPausePosition);
        }
    }

    @Override
    public void taskFinished(List<wallpaperModel> list) {
        if (list.size() == 0 && this.recyclerViewAdapter.getModelList().size() == 0)
        {
            // there is no content to searchable query. no content view will shown
            noContentView.setVisibility(View.VISIBLE);
        }
        else
        {
            // content is loaded
            MainActivity.loadWallpaperToRecyclerView(list,recyclerViewAdapter);
            noContentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOneTagLoaded(List<wallpaperModel> list) {

    }
}
