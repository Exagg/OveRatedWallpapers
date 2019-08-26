package com.example.hrwallpapers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private static final queryModel homeQueryModel = new queryModel(true,true,true,true,true,false,
            0,0,0,0,0,
            "","desc","","toplist","3d");
    private static final queryModel popularQueryModel = new queryModel(true,true,true,true,true,false,
            0,0,0,0,0,
            "","desc","","date_added",null);

    public static mainViewPagerAdapter viewPagerAdapter;
    public static ViewPager viewPager;
    public static TabLayout tabLayout;
    public static Context context;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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

    public static void toggleResultTab(int visibility)
    {
        ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0).setVisibility(visibility);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        MenuModel categoriesMenuModel = new MenuModel("Categories",false,false,true,R.drawable.ic_list,null);
        MenuModel resultMenuModel = new MenuModel("Result",false,false,true,R.drawable.ic_search,null);
        MenuModel homeMenuModel = new MenuModel("Popular",false,false,true,R.drawable.ic_home,null);
        MenuModel popularMenuModel = new MenuModel("Latest",false,false,true,R.drawable.ic_hot,null);

        HomeFragment homeFragment= new HomeFragment();
        homeFragment.setActiveQueryModel(homeQueryModel);

        CategoriesFragment categoriesFragment = new CategoriesFragment();
        PopularFragment popularFragment = new PopularFragment();
        popularFragment.setActiveQueryModel(popularQueryModel);

        ResultFragment resultFragment = new ResultFragment();

        tabLayout= view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.main_view_pager);
        viewPagerAdapter = new mainViewPagerAdapter(getActivity().getSupportFragmentManager());

        viewPagerAdapter.AddFragment(resultFragment,resultMenuModel);
        viewPagerAdapter.AddFragment(categoriesFragment,categoriesMenuModel);
        viewPagerAdapter.AddFragment(homeFragment,homeMenuModel);
        viewPagerAdapter.AddFragment(popularFragment,popularMenuModel);

        viewPager.setAdapter(viewPagerAdapter);


        tabLayout.setupWithViewPager(viewPager);
        drawTabMenu(getContext(),tabLayout,viewPagerAdapter.getFragmentMenuList());

        return view;
    }

    public void setInteractionListener(MainFragment.OnFragmentInteractionListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.app_name);
    }


    public static void drawTabMenu(Context context,TabLayout tabLayout,List<MenuModel> menuModels)
    {
        for (MenuModel model:menuModels
        ) {
            int index = menuModels.indexOf(model);
            LinearLayout view =(LinearLayout) LayoutInflater.from(context).inflate(R.layout.custom_tab,null);
            tabLayout.getTabAt(index).setCustomView(view);

            ImageView imageView = view.findViewById(R.id.custom_tab_image);
            TextView textview = view.findViewById(R.id.custom_tab_textview);


            textview.setText(model.name);

            Drawable listDrawable = context.getDrawable(model.drawableID);
            imageView.setImageDrawable(listDrawable);


        }
        if(tabLayout.getChildCount() > 0)
        {
            toggleResultTab(View.GONE);
        }
        if(tabLayout.getTabCount() > 1)
        {
            tabLayout.getTabAt(1).select();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
