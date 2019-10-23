package com.slice.wallpapers;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
    public static final queryModel homeQueryModel = new queryModel(true,true,true,true,true,false,
            0,0,0,0,0,
            "","desc","","toplist","3d");
    public static final queryModel latestQueryModel = new queryModel(true,true,true,true,true,false,
            0,0,0,0,0,
            "","desc","","date_added",null);

    public static mainViewPagerAdapter viewPagerAdapter;
    public static ViewPager viewPager;
    public static TabLayout tabLayout;
    public static Context context;

    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        popularFragment.setActiveQueryModel(latestQueryModel);

        ResultFragment resultFragment = new ResultFragment();

        tabLayout= view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.main_view_pager);
        viewPagerAdapter = new mainViewPagerAdapter(getChildFragmentManager(),4);

        viewPagerAdapter.AddFragment(resultFragment,resultMenuModel);
        viewPagerAdapter.AddFragment(categoriesFragment,categoriesMenuModel);
        viewPagerAdapter.AddFragment(homeFragment,homeMenuModel);
        viewPagerAdapter.AddFragment(popularFragment,popularMenuModel);

        viewPager.setAdapter(viewPagerAdapter);


        tabLayout.setupWithViewPager(viewPager);
        drawTabMenu(getContext(),tabLayout,viewPagerAdapter.getFragmentMenuList());
        return view;
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
        if(menuModels.size() > 0)
        {
            toggleResultTab(View.GONE);
        }
        if(menuModels.size() > 1)
        {
            tabLayout.getTabAt(1).select();
        }
    }

}
