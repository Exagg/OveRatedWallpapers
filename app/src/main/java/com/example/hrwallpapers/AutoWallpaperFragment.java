package com.example.hrwallpapers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;

public class AutoWallpaperFragment extends Fragment implements ListView.OnItemSelectedListener,
        View.OnClickListener,
        ToggleButton.OnCheckedChangeListener
{

    private static final String TAG = "AutoWallpaperFragment";
    private static final String IsEnabledKey = "AutoWallpaperIsEnabled";
    private static final String IntervalKey = "AutoWallpaperInterval";
    private static boolean IsEnabled = false;
    private static int interval = 0;
    SharedPreferences sharedPreferences;
    ArrayAdapter numberAdapter;
    Spinner numberSpinner;
    ListView autoWallpaperListView;
    ToggleButton toggleButton;


    ArrayList intervalArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_wallpaper,null,false);

        numberSpinner = view.findViewById(R.id.auto_wallpaper_interval_spinner);
        toggleButton = view.findViewById(R.id.auto_wallpaper_enable_button);


        numberAdapter = ArrayAdapter.createFromResource(this.getContext(),R.array.intervalArray,R.layout.custom_def_spinner_item);

        numberSpinner.setAdapter(numberAdapter);
        numberSpinner.setOnItemSelectedListener(this);

        intervalArray = new ArrayList();
        Collections.addAll(intervalArray,getResources().getStringArray(R.array.intervalArray));


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        IsEnabled = sharedPreferences.getBoolean(this.IsEnabledKey,false);
        interval = sharedPreferences.getInt(IntervalKey,1);


        toggleButton.setChecked(IsEnabled);
        setSpinnerDefaultValue(interval);

        numberSpinner.setOnItemSelectedListener(this);
        toggleButton.setOnCheckedChangeListener(this);

        return view;
    }

    private void setSpinnerDefaultValue(int interval) {
        if (interval == 1)numberSpinner.setSelection(0);
        if (interval == 3)numberSpinner.setSelection(1);
        if (interval == 5)numberSpinner.setSelection(2);
        if (interval == 10)numberSpinner.setSelection(3);
        if (interval == 30)numberSpinner.setSelection(4);
        if (interval == 60)numberSpinner.setSelection(5);
        if (interval == 120)numberSpinner.setSelection(6);
        if (interval == 240)numberSpinner.setSelection(7);
        if (interval == 360)numberSpinner.setSelection(8);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: " + sharedPreferences.getInt(IntervalKey,0));
        Log.i(TAG, "onResume: " + sharedPreferences.getBoolean(IsEnabledKey,false));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: " + sharedPreferences.getInt(IntervalKey,0));
        Log.i(TAG, "onPause: " + sharedPreferences.getBoolean(IsEnabledKey,false));
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int viewId = parent.getId();

        switch (viewId)
        {
            case R.id.auto_wallpaper_interval_spinner:

                String selectedItem = (String) numberAdapter.getItem(position);
                if (selectedItem.equals(getResources().getString(R.string.interval_1m))) interval = 1;
                else if (selectedItem.equals(getResources().getString(R.string.interval_3m)))interval = 3;
                else if (selectedItem.equals(getResources().getString(R.string.interval_5m))) interval= 5;
                else if (selectedItem.equals(getResources().getString(R.string.interval_10m))) interval = 10;
                else if (selectedItem.equals(getResources().getString(R.string.interval_30m))) interval = 30;
                else if (selectedItem.equals(getResources().getString(R.string.interval_1h))) interval = 60;
                else if (selectedItem.equals(getResources().getString(R.string.interval_2h))) interval = 120;
                else if (selectedItem.equals(getResources().getString(R.string.interval_4h))) interval = 240;
                else if (selectedItem.equals(getResources().getString(R.string.interval_6h))) interval = 360;

                SharedPreferences.Editor  editor = sharedPreferences.edit();
                editor.putInt(IntervalKey,interval);
                editor.apply();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setValueToSharedPreferences(String key,Object value)
    {
        if (sharedPreferences != null)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (value instanceof Integer)
                editor.putInt(key, (Integer) value);
            else if (value instanceof String)
                editor.putString(key, (String) value);
            else if (value instanceof Boolean)
                editor.putBoolean(key, (Boolean) value);

            editor.apply();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int id = buttonView.getId();
        switch (id)
        {
            case R.id.auto_wallpaper_enable_button:
                IsEnabled = isChecked;
                setValueToSharedPreferences(IsEnabledKey,IsEnabled);
                break;
        }
    }
}
