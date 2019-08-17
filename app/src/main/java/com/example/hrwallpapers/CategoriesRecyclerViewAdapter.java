package com.example.hrwallpapers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;


public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.containerRecyclerView> {
    private String TAG ="CategoriesRecyclerAdapter";
    public List<MenuModel> menuModels;
    public Context context;

    public CategoriesRecyclerViewAdapter(List<MenuModel> menuModels, Context context)
    {
        this.menuModels = menuModels;
        this.context = context;
    }

    @NonNull
    @Override
    public containerRecyclerView onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.categories_container_template,viewGroup,false);

        View container = view.findViewById(R.id.categories_container);
        TextView textView = view.findViewById(R.id.categories_header_textview);
        RecyclerView recyclerView = view.findViewById(R.id.categories_container_recyclerview);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
        layoutManager.setFlexDirection(FlexDirection.COLUMN);
        layoutManager.setJustifyContent(JustifyContent.FLEX_END);
        recyclerView.setLayoutManager(layoutManager);

        textView.setText(menuModels.get(i).name);

        wallpaperRecyclerViewAdapter adapter = new wallpaperRecyclerViewAdapter(new ArrayList<wallpaperModel>(),null,null,view,context,getQueryModel(i),recyclerView);

        HttpGetImagesAsync task = new HttpGetImagesAsync();
        MainActivity.setMenuClickListenerForRecyclerView(getQueryModel(i),recyclerView,adapter,task);

        recyclerView.setAdapter(adapter);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*MainActivity.toggleResultTab(View.VISIBLE);
                MainActivity.viewPager.setCurrentItem(0);
                Fragment fragment = MainActivity.viewPagerAdapter.getFragment(0);
                if(fragment.getClass() == ResultFragment.class)
                {
                    ResultFragment resultFragment = (ResultFragment) fragment;
                    resultFragment.setActiveQueryModel(getQueryModel(i));
                    resultFragment.load();
                }*/
            }
        });

        return new containerRecyclerView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull containerRecyclerView flexboxHolder, int i) {
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return this.menuModels.size();
    }


    public queryModel getQueryModel(int position)
    {
        return this.menuModels.get(position).queryModel;
    }
    public MenuModel getMenu(int position)
    {
        return this.menuModels.get(0);
    }


    public class containerRecyclerView extends RecyclerView.ViewHolder
    {

        public containerRecyclerView(View itemView)
        {
            super(itemView);
        }
    }

}
