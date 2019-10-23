package com.slice.wallpapers;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class CategoriesFragment extends Fragment {

    private static final int RECYCLER_VIEW_COLUMN = 1;
    private static final String TAG = "CategoriesFragment";
    private static RecyclerView recyclerView;
    private static CategoriesRecyclerViewAdapter adapter;
    private static List<MenuModel> menuModels = new ArrayList<>();

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories,container,false);
        recyclerView = view.findViewById(R.id.categories_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),RECYCLER_VIEW_COLUMN));

        loadCategories();

        adapter = new CategoriesRecyclerViewAdapter(menuModels,getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadCategories()
    {
        queryModel AnimeMangaQueryModel = new queryModel(false,true,false,true,true,true,
                0,0,0,0,0,
                "","desc","","date_added",null);

        queryModel ArtDesignQueryModel = new queryModel(true,false,true,true,true,true,
                0,0,0,0,0,
                "","desc","art+design+architecture+digitall+photography+traditional","date_added",null);

        queryModel EntertainmentQueryModel = new queryModel(true,true,true,true,true,true,
                0,0,0,0,0,
                "","desc","entertainment+events+games+literature+movies+music+sports+television","date_added",null);

        queryModel KnowledgeQueryModel = new queryModel(true,true,true,true,true,true,
                0,0,0,0,0,
                "","desc","knowledge+science+history+lore+experience+news+notice","date_added",null);

        queryModel LocationQueryModel = new queryModel(true,false,false,true,true,true,
                0,0,0,0,0,
                "","desc","cities+countries+space","relevance",null);

        queryModel MiscellaneousQueryModel = new queryModel(true,false,true,true,true,false,
                0,0,0,0,0,
                "","desc","clothing+colors+companies+logos+food+technology","date_added",null);

        queryModel NatureQueryModel = new queryModel(true,false,false,true,true,false,
                0,0,0,0,0,
                "","desc","animals+landscapes+plants","relevance",null);

        queryModel PeopleQueryModel = new queryModel(false,false,true,true,true,false,
                0,0,0,0,0,
                "","desc","artists+celebrities+fictional+characters+models+figures+pornstars","date_added",null);

        queryModel VehiclesQueryModel = new queryModel(true,false,false,true,true,false,
                0,0,0,0,0,
                "","desc","aircraft+plane+car+motorcycle+ship+spacecrafts+train+f-35+f-16+apollo","date_added",null);

        MenuModel AnimeMangaModel = new MenuModel("ANIME & MANGA",true,true,false,R.string.fa_chevron_right_solid,AnimeMangaQueryModel);
        MenuModel ArtDesignModel = new MenuModel("ART & DESIGN",true,true,false,R.string.fa_chevron_right_solid,ArtDesignQueryModel);
        MenuModel EntertainmentModel = new MenuModel("ENTERTAINMENT",true,true,false,R.string.fa_chevron_right_solid,EntertainmentQueryModel);
        MenuModel KnowledgeModel = new MenuModel("KNOWLEDGE",true,true,false,R.string.fa_chevron_right_solid,KnowledgeQueryModel);
        MenuModel LocationModel = new MenuModel("LOCATION",true,true,false,R.string.fa_chevron_right_solid, LocationQueryModel);
        MenuModel MiscellaneousModel = new MenuModel("MISCELLANEOUS",true,true,false,R.string.fa_chevron_right_solid,MiscellaneousQueryModel);
        MenuModel NatureModel = new MenuModel("NATURE",true,true,false,R.string.fa_chevron_right_solid,NatureQueryModel);
        MenuModel PeopleModel = new MenuModel("PEOPLE",true,true,false,R.string.fa_chevron_right_solid,PeopleQueryModel);
        MenuModel VehiclesModel = new MenuModel("VEHICLES",true,true,false,R.string.fa_chevron_right_solid,VehiclesQueryModel);


        menuModels.clear();
        menuModels.add(AnimeMangaModel);
        menuModels.add(ArtDesignModel);
        menuModels.add(EntertainmentModel);
        menuModels.add(KnowledgeModel);
        menuModels.add(LocationModel);
        menuModels.add(MiscellaneousModel);
        menuModels.add(NatureModel);
        menuModels.add(PeopleModel);
        menuModels.add(VehiclesModel);
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(recyclerView != null)
        {
            getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Log.i(TAG, "onConfigurationChanged: ");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: " + context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
    }
}
