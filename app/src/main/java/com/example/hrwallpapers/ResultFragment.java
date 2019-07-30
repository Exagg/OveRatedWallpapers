package com.example.hrwallpapers;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class ResultFragment extends Fragment {

    private static final int RECYCLER_VIEW_COLUMN = 2;


    public wallpaperRecyclerViewAdapter recyclerViewAdapter;
    public RecyclerView recyclerView;
    private static Fragment popupFragment;
    private static FrameLayout fragmentHolder;
    private static HttpGetImagesAsync task = new HttpGetImagesAsync();


    private static queryModel activeQueryModel;
    public ResultFragment()
    {

    }

    public void setActiveQueryModel(queryModel activeQueryModel) {
        ResultFragment.activeQueryModel = activeQueryModel;

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
        popupFragment = setFragment(new wallpaperPopupFragment());

        if(activeQueryModel != null)
        {
            load();
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        if(recyclerView != null && recyclerViewAdapter != null )
        {
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    public void load()
    {
        if(recyclerView != null)
        {
            if(recyclerView.getChildCount() >0)
            {
                recyclerView.removeAllViews();
            }
            recyclerViewAdapter = new wallpaperRecyclerViewAdapter(new ArrayList<wallpaperModel>(),fragmentHolder,popupFragment,recyclerView,getActivity(),activeQueryModel,recyclerView);
            recyclerView.setAdapter(recyclerViewAdapter);
            MainActivity.setMenuClickListenerForRecyclerView(activeQueryModel,recyclerView,recyclerViewAdapter,task); // Load for the startup images (active page must be 1)

            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),RECYCLER_VIEW_COLUMN));

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
        }
    }
    protected Fragment setFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentHolder.getId(), fragment);
        fragmentTransaction.commit();
        return fragment;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(recyclerView != null)
        {
            getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}
