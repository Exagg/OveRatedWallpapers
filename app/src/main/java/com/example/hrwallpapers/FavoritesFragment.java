package com.example.hrwallpapers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoritesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {
    private static final int RECYCLER_VIEW_COLUMN = 2;
    private static final String FRAGMENT_TITLE = "Favorites";
    private View noContentContainer;
    private RecyclerView favoritesRecyclerView;
    private wallpaperRecyclerViewAdapter favoritesAdapter;
    private FrameLayout favoritesPopupFragmentHolder;
    private wallpaperPopupFragment popupFragment;
    private int lastShowIndex;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);


        noContentContainer = view.findViewById(R.id.favorites_no_content_container);
        favoritesRecyclerView = view.findViewById(R.id.favorites_recyclerview);
        favoritesPopupFragmentHolder = view.findViewById(R.id.favorites_fragment_holder);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),RECYCLER_VIEW_COLUMN));


        popupFragment = (wallpaperPopupFragment) MainActivity.setFragment(new wallpaperPopupFragment(),favoritesPopupFragmentHolder,getChildFragmentManager());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(FRAGMENT_TITLE);
        popupFragment = (wallpaperPopupFragment) MainActivity.setFragment(new wallpaperPopupFragment(),favoritesPopupFragmentHolder,getChildFragmentManager());

        List<String> favorites = MainActivity.database.getFavorites();

        if (favorites.size() > 0)
        {
            List<wallpaperModel> wallpaperModelList = new ArrayList<>();
            for (String id :
                    favorites) {
                wallpaperModel m = new wallpaperModel(id);

                wallpaperModelList.add(m);
            }
            this.favoritesAdapter = new wallpaperRecyclerViewAdapter(wallpaperModelList,favoritesPopupFragmentHolder,popupFragment, getView(),getContext(),null,favoritesRecyclerView);

            this.favoritesAdapter.updateAdapter(wallpaperModelList);
            this.favoritesRecyclerView.setAdapter(favoritesAdapter);
            this.favoritesRecyclerView.scrollToPosition(this.lastShowIndex);


            noContentContainer.setVisibility(View.GONE);
        }
        else
        {
            noContentContainer.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if(this.favoritesAdapter != null) this.lastShowIndex = this.favoritesAdapter.getClickedItemPosition();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
