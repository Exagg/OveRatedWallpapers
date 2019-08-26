package com.example.hrwallpapers;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements Spinner.OnItemSelectedListener,
                                                        ToggleButton.OnCheckedChangeListener,
                                                        View.OnClickListener
{
    private static final String TAG = "SearchFragment";
    private static final queryModel query = new queryModel();


    private static EditText keywordInput;
    private static ToggleButton animeButton;
    private static ToggleButton peopleButton;
    private static ToggleButton othersButton;
    private static ToggleButton safeForWorkButton;
    private static ToggleButton sketchyButton;
    private static ToggleButton orderByButton;
    private static Spinner colorSpinner;
    private static Spinner resolutionSpinner;
    private static Spinner sortingSpinner;
    private static Button searchButton;

    private static ColorSpinnerAdapter colorAdapter;

    private static List<String> colorList = new ArrayList<>();

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        colorSpinner = view.findViewById(R.id.search_color_spinner);
        keywordInput = view.findViewById(R.id.search_keyword);
        animeButton = view.findViewById(R.id.search_anime);
        peopleButton = view.findViewById(R.id.search_people);
        othersButton = view.findViewById(R.id.search_others);
        safeForWorkButton = view.findViewById(R.id.search_safe_for_work);
        sketchyButton = view.findViewById(R.id.search_sketchy);
        resolutionSpinner = view.findViewById(R.id.search_resolution_spinner);
        sortingSpinner = view.findViewById(R.id.search_sorting_spinner);
        searchButton = view.findViewById(R.id.search_search_button);
        orderByButton = view.findViewById(R.id.search_order_by_button);


        animeButton.setOnCheckedChangeListener(this);
        peopleButton.setOnCheckedChangeListener(this);
        othersButton.setOnCheckedChangeListener(this);
        safeForWorkButton.setOnCheckedChangeListener(this);
        sketchyButton.setOnCheckedChangeListener(this);
        sketchyButton.setOnCheckedChangeListener(this);

        colorSpinner.setOnItemSelectedListener(this);
        resolutionSpinner.setOnItemSelectedListener(this);
        sortingSpinner.setOnItemSelectedListener(this);

        searchButton.setOnClickListener(this);


        query.setPeople(peopleButton.isChecked());
        query.setAnime(animeButton.isChecked());
        query.setGeneral(othersButton.isChecked());
        query.setNsfw(false);
        query.setSfw(safeForWorkButton.isChecked());
        query.setSketchy(sketchyButton.isChecked());
        query.setOrderBy("desc");


        String[] colors = getResources().getStringArray(R.array.colorArray);
        for (String color: colors
        ) {
            this.colorList.add(color);
        }

        colorAdapter = new ColorSpinnerAdapter(getContext(),R.layout.custom_color_layout,colorList);
        colorSpinner.setAdapter(colorAdapter);


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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int viewId = parent.getId();

        switch (viewId)
        {
            case R.id.search_color_spinner:

                String colorHex = colorSpinner.getSelectedItem().toString();
                if(colorHex.length() > 0)
                {
                    colorHex = colorHex.replace("#","");
                    query.setColorHex(colorHex);
                }
                else query.setColorHex(null);
                break;
            case R.id.search_sorting_spinner:

                String sorting = sortingSpinner.getSelectedItem().toString();
                if (sorting.length()>0)
                {
                    query.setSorting(sorting);
                }
                else query.setOrderBy(null);

                break;
            case R.id.search_resolution_spinner:
                String resolution = String.valueOf(resolutionSpinner.getSelectedItem());
                if (resolution.length() > 0)
                {
                    int height = Integer.parseInt(resolution.substring(resolution.indexOf("x")).trim().replace("x",""));
                    int width = Integer.parseInt(resolution.substring(0,resolution.indexOf("x")).trim().replace("x",""));
                    query.setResolutionH(height);
                    query.setResolutionW(width);
                }
                else
                {
                    query.setResolutionH(0);
                    query.setResolutionW(0);
                }

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

        switch (id)
        {
            case R.id.search_anime:
                query.setAnime(isChecked);
                break;
            case R.id.search_people:
                query.setPeople(isChecked);
                break;
            case R.id.search_others:
                query.setGeneral(isChecked);
                break;
            case R.id.search_sketchy:
                query.setSketchy(isChecked);
                break;
            case R.id.search_safe_for_work:
                query.setSfw(isChecked);
                break;
            case R.id.search_order_by_button:
                if (isChecked)
                {
                    setAsc();
                    query.setOrderBy("asc");
                }
                else
                {
                    setDesc();
                    query.setOrderBy("desc");
                }
                break;
        }
    }


    ObjectAnimator animation;
    private void setAsc()
    {
        animation = ObjectAnimator.ofFloat(orderByButton,View.ROTATION,orderByButton.getRotation(),180f).setDuration(300);
        animation.start();
    }
    private void setDesc()
    {
        animation = ObjectAnimator.ofFloat(orderByButton,View.ROTATION,orderByButton.getRotation(),0f).setDuration(300);
        animation.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id)
        {
            case R.id.search_search_button:
                Activity activity = getActivity();
                query.setQuery(keywordInput.getText().toString());
                query.setActivePage(0);
                if (activity instanceof MainActivity)
                {
                    ((MainActivity) activity).showResultTab(query);
                    activity.onBackPressed();
                }
                break;
        }
    }
}
class ColorSpinnerAdapter extends ArrayAdapter<String>
{

    private List<String> colorList;
    private Context mContext;


    public ColorSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.colorList = objects;
        this.mContext = context;
    }

        @Override
    public int getCount() {
        return colorList.size();
    }

    @Override
    public String getItem(int position) {
        return colorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.custom_color_layout, parent,false);
        TextView textView = view.findViewById(R.id.custom_color_textview);
        if(getItem(position).trim().length() > 0)
        {
            textView.setBackgroundColor(Color.parseColor(getItem(position)));
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.custom_color_layout, parent,false);
        TextView textView = view.findViewById(R.id.custom_color_textview);
        if(getItem(position).trim().length() > 0)
        {
            textView.setBackgroundColor(Color.parseColor(getItem(position)));
        }
        return view;
    }
}
