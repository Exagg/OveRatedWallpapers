package com.example.hrwallpapers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AutoWallpaperFragment extends Fragment implements ListView.OnItemSelectedListener,
        View.OnClickListener,
        ToggleButton.OnCheckedChangeListener,
        AdapterView.OnItemClickListener,
        RadioGroup.OnCheckedChangeListener,
        AutoWallpaperListViewAdapter.onListSelected
{

    private static final int FILE_CHOOSE_REQUEST_CODE = 1;
    private static final String TAG = "AutoWallpaperFragment";
    private static final String IsEnabledKey = "AutoWallpaperIsEnabled";
    private static final String IntervalKey = "AutoWallpaperInterval";
    private static final String ListKey = "AutoWallpaperSelectedListID";
    private static final String FolderKey = "AutoWalpaperSelectedFolderID";
    private static final String ListSourceKey = "AutoWallpaperListSource";
    private static final int SourceIsFolder = 1;
    private static final int SourceIsList = 2;
    private static boolean IsEnabled = false;
    private static int interval = 0;
    private static int selectedListID = 0;
    private static int selectedFolderID = 0;
    private static int sourceCode = 0;
    private AutoWallpaperListViewAdapter autoWallpaperListViewAdapter;
    private AutoWallpaperFolderAdapter autoWallpaperFolderViewAdapter;
    private RadioGroup radioGroup;
    private RadioButton folderRadioButton;
    private RadioButton listRadioButton;

    SharedPreferences sharedPreferences;
    ArrayAdapter numberAdapter;
    Spinner numberSpinner;
    ListView autoWallpaperListView;
    ToggleButton toggleButton;
    ImageButton addButton;


    ArrayList intervalArray;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == FILE_CHOOSE_REQUEST_CODE && resultCode == RESULT_OK)
        {

            Uri uri = data.getData();
            String folderName = FileUtils.getFileName(uri);
            String folderDest = FileUtils.getPath(getContext(),uri).replace(File.separator + folderName,"");
            Log.i(TAG, "onActivityResult: FilePath - " + folderName);
            Log.i(TAG, "onActivityResult: File Name - " + folderDest);

            boolean isAdded = MainActivity.database.addFolder(folderName,folderDest);

            if (isAdded)
            {
                refreshFolderUI();
            }
            else MainActivity.showToast("Somethings went wrong, please try again later..", Toast.LENGTH_SHORT,getContext());
        }
    }

    private void refreshFolderUI() {
        if (autoWallpaperFolderViewAdapter != null)
        {
            autoWallpaperFolderViewAdapter.setList(MainActivity.database.getAllFolders());
            autoWallpaperFolderViewAdapter.notifyDataSetChanged();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_wallpaper, null, false);

        numberSpinner = view.findViewById(R.id.auto_wallpaper_interval_spinner);
        toggleButton = view.findViewById(R.id.auto_wallpaper_enable_button);
        autoWallpaperListView = view.findViewById(R.id.auto_wallpaper_lists);
        radioGroup = view.findViewById(R.id.auto_wallpaper_radio_group);
        folderRadioButton = view.findViewById(R.id.auto_wallpaper_folder_button);
        listRadioButton = view.findViewById(R.id.auto_wallpaper_list_button);
        addButton = view.findViewById(R.id.auto_wallpaper_add_button);
        autoWallpaperListViewAdapter = new AutoWallpaperListViewAdapter(getContext(),R.layout.layout_custom_list_of_wallpaper,MainActivity.database.getWallpaperLists(),null);
        autoWallpaperFolderViewAdapter =new AutoWallpaperFolderAdapter(getContext(),R.layout.layout_custom_list_of_wallpaper,MainActivity.database.getAllFolders());

        numberAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.intervalArray, R.layout.custom_def_spinner_item);

        numberSpinner.setAdapter(numberAdapter);
        numberSpinner.setOnItemSelectedListener(this);

        intervalArray = new ArrayList();
        Collections.addAll(intervalArray, getResources().getStringArray(R.array.intervalArray));


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        IsEnabled = sharedPreferences.getBoolean(this.IsEnabledKey, false);
        interval = sharedPreferences.getInt(IntervalKey, 1);
        selectedListID = sharedPreferences.getInt(ListKey,0);
        sourceCode = sharedPreferences.getInt(ListSourceKey,0);
        selectedFolderID = sharedPreferences.getInt(FolderKey,0);


        toggleButton.setChecked(IsEnabled);
        setSpinnerDefaultValue(interval);


        numberSpinner.setOnItemSelectedListener(this);
        toggleButton.setOnCheckedChangeListener(this);
        autoWallpaperListViewAdapter.setSelectedListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        addButton.setOnClickListener(this);



        autoWallpaperListView.setOnItemClickListener(this);
        if (sourceCode == SourceIsList)
        {
            if (selectedListID != 0)
            {
                radioGroup.check(this.listRadioButton.getId());
                selectOnList(selectedListID);
            }
        }
        else if(sourceCode == SourceIsFolder)
        {
            if(selectedFolderID != 0)
            {
                radioGroup.check(this.folderRadioButton.getId());
                // default selector will run
            }
        }
        else
        {
            radioGroup.clearCheck();
        }

        return view;
    }

    private void selectOnList(int id) {
        for (wallpaperListModel model: autoWallpaperListViewAdapter.list
             ) {
            if (model.getID() == id)
            {
                autoWallpaperListViewAdapter.notifyDataSetChanged();
                int index = autoWallpaperListViewAdapter.list.indexOf(model);
                Log.i(TAG, "selectOnList: " + index);
                autoWallpaperListView.setSelected(true);
                autoWallpaperListView.requestFocus();
                autoWallpaperListView.requestFocusFromTouch();
                autoWallpaperListView.setSelection(index);
                autoWallpaperListView.setItemChecked(index,true);

            }
        }
    }

    private void setSpinnerDefaultValue(int interval) {
        if (interval == 1) numberSpinner.setSelection(0);
        if (interval == 3) numberSpinner.setSelection(1);
        if (interval == 5) numberSpinner.setSelection(2);
        if (interval == 10) numberSpinner.setSelection(3);
        if (interval == 30) numberSpinner.setSelection(4);
        if (interval == 60) numberSpinner.setSelection(5);
        if (interval == 120) numberSpinner.setSelection(6);
        if (interval == 240) numberSpinner.setSelection(7);
        if (interval == 360) numberSpinner.setSelection(8);
    }

    @Override
    public void onResume() {
        super.onResume();
        autoWallpaperListViewAdapter = new AutoWallpaperListViewAdapter(getContext(),R.layout.layout_custom_list_of_wallpaper,MainActivity.database.getWallpaperLists(),null);
        autoWallpaperFolderViewAdapter =new AutoWallpaperFolderAdapter(getContext(),R.layout.layout_custom_list_of_wallpaper,MainActivity.database.getAllFolders());
        selectOnList(selectedListID);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.auto_wallpaper_add_button:
                runAdd();
                break;

        }

    }

    private void runAdd() {
        int buttonID = this.radioGroup.getCheckedRadioButtonId();
        switch (buttonID)
        {
            case -1 :
                Log.i(TAG, "runAdd: There is no selection");
                break;
            case R.id.auto_wallpaper_folder_button:
                Log.i(TAG, "runAdd: Folder selector will open here");
                startChooseFileIntent();
                break;
            case R.id.auto_wallpaper_list_button:
                Log.i(TAG, "runAdd: AddtoListDialog will be showen ");
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int viewId = parent.getId();

        switch (viewId) {
            case R.id.auto_wallpaper_interval_spinner:

                String selectedItem = (String) numberAdapter.getItem(position);
                if (selectedItem.equals(getResources().getString(R.string.interval_1m)))
                    interval = 1;
                else if (selectedItem.equals(getResources().getString(R.string.interval_3m)))
                    interval = 3;
                else if (selectedItem.equals(getResources().getString(R.string.interval_5m)))
                    interval = 5;
                else if (selectedItem.equals(getResources().getString(R.string.interval_10m)))
                    interval = 10;
                else if (selectedItem.equals(getResources().getString(R.string.interval_30m)))
                    interval = 30;
                else if (selectedItem.equals(getResources().getString(R.string.interval_1h)))
                    interval = 60;
                else if (selectedItem.equals(getResources().getString(R.string.interval_2h)))
                    interval = 120;
                else if (selectedItem.equals(getResources().getString(R.string.interval_4h)))
                    interval = 240;
                else if (selectedItem.equals(getResources().getString(R.string.interval_6h)))
                    interval = 360;

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(IntervalKey, interval);
                editor.apply();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setValueToSharedPreferences(String key, Object value) {
        if (sharedPreferences != null) {
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

    private void startChooseFileIntent()
    {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(i,FILE_CHOOSE_REQUEST_CODE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int id = buttonView.getId();
        switch (id) {
            case R.id.auto_wallpaper_enable_button:
                IsEnabled = isChecked;
                setValueToSharedPreferences(IsEnabledKey, IsEnabled);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        wallpaperListModel model = autoWallpaperListViewAdapter.getItem(position);

        if (model.getID() != 0)
        {
            setValueToSharedPreferences(ListKey,model.getID());
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId)
        {
            case R.id.auto_wallpaper_folder_button:
                this.autoWallpaperListView.setAdapter(this.autoWallpaperFolderViewAdapter);
                this.autoWallpaperFolderViewAdapter.notifyDataSetChanged();
                break;
            case R.id.auto_wallpaper_list_button:
                this.autoWallpaperListView.setAdapter(this.autoWallpaperListViewAdapter);
                this.autoWallpaperListViewAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onListSelected(View view, wallpaperListModel wallpaperListModel) {
        selectOnList(wallpaperListModel.getID());
    }
}

class AutoWallpaperListViewAdapter extends ArrayAdapter<wallpaperListModel> {

    private String TAG = "autoWallpaperListViewAdapter";
    private onEditListener editListener;
    private onDeleteListener deleteListener;
    private onListSelected selectedListener;
    private wallpaperModel wallpaperModel;
    List<wallpaperListModel> list;
    Context context;
    public AutoWallpaperListViewAdapter(@NonNull Context context, int resource, @NonNull List<wallpaperListModel> objects,wallpaperModel wallpaperModel) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
        this.wallpaperModel = wallpaperModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.layout_custom_list_of_wallpaper,null,false);
        TextView listItemNameTextView = view.findViewById(R.id.list_item_name);
        ImageButton editButton = view.findViewById(R.id.list_item_edit);
        ImageButton deleteButton = view.findViewById(R.id.list_item_delete);
        final ToggleButton stateToggleButton = view.findViewById(R.id.list_item_state);
        final CustomRotateAnimation customRotateAnimation = new CustomRotateAnimation(
                stateToggleButton,
                300,
                true,
                getContext().getResources().getColor(R.color.white),
                getContext().getResources().getColor(R.color.red),
                true);
        AutoWallpaperListHolder holder = new AutoWallpaperListHolder(deleteButton,editButton,listItemNameTextView);

        final wallpaperListModel listItem = getItem(position);
        listItemNameTextView.setText(listItem.getListName());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editListener != null)
                {
                    editListener.onEditSelected(listItem);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteListener != null)
                {
                    deleteListener.onDeleteSelected(listItem);
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedListener != null)
                {
                    selectedListener.onListSelected(view,listItem);

                    if (stateToggleButton.isChecked()) stateToggleButton.setChecked(false);
                    else stateToggleButton.setChecked(true);
                }
            }
        });

        stateToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) customRotateAnimation.rotateTo(315,isChecked);
                else customRotateAnimation.rotateTo(0,isChecked);
            }
        });


        if (this.wallpaperModel != null)
        {
            if (MainActivity.database.isWallpaperAddtoList(listItem.getID(),this.wallpaperModel)) stateToggleButton.setChecked(true);
            else stateToggleButton.setChecked(false);
        }

        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void setList(List<wallpaperListModel> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public wallpaperListModel getItem(int position) {
        return this.list.get(position);
    }

    public void setDeleteListener(onDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setEditListener(onEditListener editListener) {
        this.editListener = editListener;
    }

    public void setSelectedListener(onListSelected selectedListener) {
        this.selectedListener = selectedListener;
    }

    public interface onEditListener{
        void onEditSelected(wallpaperListModel wallpaperListModel);
    }

    public interface onDeleteListener{
        void onDeleteSelected(wallpaperListModel wallpaperListModel);
    }
    public interface onListSelected{
        void onListSelected(View view,wallpaperListModel wallpaperListModel);
    }


}

class AutoWallpaperListHolder
{
    ImageButton deleteButton;
    ImageButton editButton;
    TextView listNameTextView;

    public AutoWallpaperListHolder(@NonNull ImageButton deleteButton, @NonNull ImageButton editButton, @NonNull TextView listNameTextView)
    {
        this.deleteButton = deleteButton;
        this.editButton = editButton;
        this.listNameTextView = listNameTextView;
    }
}


class AutoWallpaperFolderAdapter extends ArrayAdapter<FolderModel> {

    private String TAG = "autoWallpaperListViewAdapter";
    private onFolderEditSelected editListener;
    private onFolderDeleteSelected deleteListener;
    private onFolderListSelected selectedListener;
    private wallpaperModel wallpaperModel;
    private boolean isEditable;
    List<FolderModel> list;
    Context context;
    public AutoWallpaperFolderAdapter(@NonNull Context context, int resource, @NonNull List<FolderModel> objects) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
        this.wallpaperModel = wallpaperModel;
        this.isEditable = isEditable;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.layout_custom_list_of_wallpaper,null,false);
        TextView listItemNameTextView = view.findViewById(R.id.list_item_name);
        ImageButton editButton = view.findViewById(R.id.list_item_edit);
        ImageButton deleteButton = view.findViewById(R.id.list_item_delete);
        final ToggleButton stateToggleButton = view.findViewById(R.id.list_item_state);
        final CustomRotateAnimation customRotateAnimation = new CustomRotateAnimation(
                stateToggleButton,
                300,
                true,
                getContext().getResources().getColor(R.color.white),
                getContext().getResources().getColor(R.color.red),
                true);
        AutoWallpaperListHolder holder = new AutoWallpaperListHolder(deleteButton,editButton,listItemNameTextView);

        final FolderModel listItem = getItem(position);
        listItemNameTextView.setText(listItem.getFolderPath() + listItem.getFolderPath());

        if (this.isEditable)
        {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editListener != null)
                    {
                        editListener.onEditSelected(listItem);
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteListener != null)
                    {
                        deleteListener.onDeleteSelected(listItem);
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedListener != null)
                    {
                        selectedListener.onListSelected(view,listItem);

                        if (stateToggleButton.isChecked()) stateToggleButton.setChecked(false);
                        else stateToggleButton.setChecked(true);
                    }
                }
            });

            stateToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) customRotateAnimation.rotateTo(315,isChecked);
                    else customRotateAnimation.rotateTo(0,isChecked);
                }
            });


            if (MainActivity.database.isWallpaperAddtoList(listItem.getFolderID(),this.wallpaperModel)) stateToggleButton.setChecked(true);
            else stateToggleButton.setChecked(false);
        }
        else
        {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            stateToggleButton.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void setList(List<FolderModel> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public FolderModel getItem(int position) {
        return this.list.get(position);
    }

    public void setDeleteListener(onFolderDeleteSelected deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setEditListener(onFolderEditSelected editListener) {
        this.editListener = editListener;
    }

    public void setSelectedListener(onFolderListSelected selectedListener) {
        this.selectedListener = selectedListener;
    }

    public interface onFolderEditSelected{
        void onEditSelected(FolderModel folderModel);
    }

    public interface onFolderDeleteSelected{
        void onDeleteSelected(FolderModel folderModel);
    }
    public interface onFolderListSelected{
        void onListSelected(View view,FolderModel folderModel);
    }


}


class AutoWallpaperFolderHolder
{
    ImageButton deleteButton;
    ImageButton editButton;
    TextView listNameTextView;

    public AutoWallpaperFolderHolder(@NonNull ImageButton deleteButton, @NonNull ImageButton editButton, @NonNull TextView listNameTextView)
    {
        this.deleteButton = deleteButton;
        this.editButton = editButton;
        this.listNameTextView = listNameTextView;
    }
}

