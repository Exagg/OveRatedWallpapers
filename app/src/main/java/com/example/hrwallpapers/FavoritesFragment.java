package com.example.hrwallpapers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.List;

import static android.support.constraint.motion.MotionScene.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoritesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final int RECYCLER_VIEW_COLUMN = 2;

    private View noContentContainer;
    private RecyclerView favoritesRecyclerView;
    private wallpaperRecyclerViewAdapter favoritesAdapter;
    private FrameLayout favoritesPopupFragmentHolder;
    private Fragment popupFragment;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);


        noContentContainer = view.findViewById(R.id.favorites_no_content_container);
        favoritesRecyclerView = view.findViewById(R.id.favorites_recyclerview);
        favoritesPopupFragmentHolder = view.findViewById(R.id.favorites_fragment_holder);


        popupFragment = setFragment(new wallpaperPopupFragment());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<String> favorites = MainActivity.database.getFavorites();

        if (favorites.size() > 0)
        {
            List<wallpaperModel> wallpaperModelList = new ArrayList<>();
            for (String id :
                    favorites) {
                Log.i(TAG, "onCreateView: " + id);
                String thumbUrl = String.format("https://th.wallhaven.cc/small/%s/%s.jpg",id.substring(0,2),id);
                String originalUrl = String.format("https://w.wallhaven.cc/full/%s/wallhaven-%s.jpg",id.substring(0,2),id);

                wallpaperModel m = new wallpaperModel(thumbUrl,originalUrl,id);

                wallpaperModelList.add(m);
            }

            favoritesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),RECYCLER_VIEW_COLUMN));
            this.favoritesAdapter = new wallpaperRecyclerViewAdapter(wallpaperModelList,favoritesPopupFragmentHolder,popupFragment,favoritesRecyclerView,getContext(),null,favoritesRecyclerView);
            this.favoritesRecyclerView.setAdapter(favoritesAdapter);
        }
        else
        {
            noContentContainer.setVisibility(View.VISIBLE);
        }

    }

    protected Fragment setFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(favoritesPopupFragmentHolder.getId(), fragment);
        fragmentTransaction.commit();
        return fragment;
    }


    public void setInteractionListener(FavoritesFragment.OnFragmentInteractionListener listener) {
        this.mListener = listener;
        Log.i(TAG, "setInteractionListener: " + this.mListener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (this.mListener == null)
        {
            throw new RuntimeException(context.toString()
                    + " must define listener before call this instance");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
