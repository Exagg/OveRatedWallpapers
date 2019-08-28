package com.example.hrwallpapers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.motion.MotionScene.TAG;

public class HistoryFragment extends Fragment {


    private static final int RECYCLER_VIEW_COLUMN = 2;
    private static final String FRAGMENT_TITLE = "History";
    private View noContentContainer;
    private RecyclerView downloadedRecyclerView;
    private wallpaperRecyclerViewAdapter downloadedRecyclerViewAdapter;
    private FrameLayout popupFragmentHolder;
    private Fragment popupFragment;
    private int lastShowIndex;

    public HistoryFragment( ) {
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        this.noContentContainer= view.findViewById(R.id.history_no_content_container);
        this.downloadedRecyclerView = view.findViewById(R.id.history_recyclerview);
        this.popupFragmentHolder = view.findViewById(R.id.history_fragment_holder);

        downloadedRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),RECYCLER_VIEW_COLUMN));

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();

        if(downloadedRecyclerViewAdapter != null)this.lastShowIndex = downloadedRecyclerViewAdapter.getClickedItemPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(FRAGMENT_TITLE);

        popupFragment = setFragment(new wallpaperPopupFragment());


        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                Log.i(TAG, "accept: " + pathname + " - " + pathname.getName().startsWith("HQ_"));
                return pathname.getName().startsWith("HQ_");
            }
        };
        File[] downloadedImages = MainActivity.downloadFolder.listFiles(fileFilter);

        if (downloadedImages.length > 0)
        {
            List<wallpaperModel> wallpaperModelList = new ArrayList<>();
            for (File file :
                    downloadedImages) {
                String wallpaperID = file.getName().replace("HQ_","").replace(".jpg","");
                wallpaperModel model = new wallpaperModel(wallpaperID);
                model.setFilePath(file);
                if (MainActivity.wallpaperInFavorites.contains(wallpaperID)) model.isFavorite.setValue(true);
                else model.isFavorite.setValue(false);
                wallpaperModelList.add(model);
            }

            downloadedRecyclerViewAdapter = new wallpaperRecyclerViewAdapter(wallpaperModelList,popupFragmentHolder,popupFragment,getView(),getContext(),null,downloadedRecyclerView);

            downloadedRecyclerView.setAdapter(downloadedRecyclerViewAdapter);
            downloadedRecyclerView.scrollToPosition(lastShowIndex);

            noContentContainer.setVisibility(View.GONE);

        }
        else
        {
            noContentContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected Fragment setFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(popupFragmentHolder.getId(), fragment);
        fragmentTransaction.commit();
        return fragment;
    }
}
