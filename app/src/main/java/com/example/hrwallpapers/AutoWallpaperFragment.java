package com.example.hrwallpapers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
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
        AutoWallpaperListViewAdapter.onListSelected,AutoWallpaperListViewAdapter.onDeleteListener,AutoWallpaperListViewAdapter.onEditListener,
        AutoWallpaperFolderAdapter.onFolderListSelected,AutoWallpaperFolderAdapter.onFolderEditSelected,AutoWallpaperFolderAdapter.onFolderDeleteSelected,
        TextView.OnEditorActionListener,
        Dialog.OnShowListener,
        Dialog.OnCancelListener,
        Dialog.OnDismissListener,
        SharedPreferences.OnSharedPreferenceChangeListener
{

    private static final int FILE_CHOOSE_REQUEST_CODE = 1;
    public static final String TAG = "AutoWallpaperFragment";
    public static final String IsEnabledKey = "AutoWallpaperIsEnabled";
    public static final String IntervalKey = "AutoWallpaperInterval";
    public static final String IsFolderActiveKey = "FolderSourceActive";
    public static final String IsListActiveKey = "ListSourceActive";
    private static boolean IsEnabled = false;
    private static int interval = 0;
    private static boolean IsFolderActive = false;
    private static boolean IsListActive = false;
    private AutoWallpaperListViewAdapter autoWallpaperListViewAdapter;
    private AutoWallpaperFolderAdapter autoWallpaperFolderViewAdapter;
    private RadioGroup radioGroup;
    private RadioButton folderRadioButton;
    private RadioButton listRadioButton;
    private AlertDialog createNewDialog;
    private SharedPreferences sharedPreferences;
    private ArrayAdapter numberAdapter;
    private Spinner numberSpinner;
    private ListView autoWallpaperListView;
    private ToggleButton toggleButton;
    private ImageButton addButton;
    private ImageButton createNewAddButton;
    private ImageButton createNewCancelButton;
    private EditText createNewEditText;


    ArrayList intervalArray;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == FILE_CHOOSE_REQUEST_CODE && resultCode == RESULT_OK)
        {

            Uri uri = data.getData();
            String folderName = FileUtils.getFileName(uri);
            Log.i(TAG, "onActivityResult: " + Environment.getExternalStorageDirectory().getAbsolutePath());
            Log.i(TAG, "onActivityResult: " + uri.getPath());
            String path = uri.getPath().replace("primary:","").replace("/tree","").replace("/storage","").replace(":",File.separator);
            if (uri.getPath().contains("primary"))
            {
                // this folder is selected on externalstorage
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + folderName;
            }
            else
            {
                // this folder selected on sd card
                path = File.separator + "storage" + path;
            }

            if (path.contains(folderName))
            {
                int index = path.lastIndexOf(folderName);
                if (index > path.lastIndexOf(File.separator))
                {
                    path = path.substring(0,index);
                }
            }
            Log.i(TAG, "onActivityResult: FilePath - " + path);
            Log.i(TAG, "onActivityResult: File Name - " + folderName);

            File file = new File(path + File.separator + folderName);

            if (!MainActivity.database.isFolderAddedBefore(path,folderName))
            {
                boolean isAdded = MainActivity.database.addFolder(folderName,path);

                if (isAdded)
                {
                    refreshFolderUI();
                }
                else MainActivity.showToast("Somethings went wrong, please try again later..", Toast.LENGTH_SHORT,getContext());
            }
            else MainActivity.showToast("This folder is already added..", Toast.LENGTH_SHORT,getContext());

        }
    }

    private void refreshFolderUI() {
        if (autoWallpaperFolderViewAdapter != null)
        {
            autoWallpaperFolderViewAdapter.setList(MainActivity.database.getAllFolders(null,null));
            autoWallpaperFolderViewAdapter.notifyDataSetChanged();
            autoWallpaperFolderViewAdapter.setDeleteListener(this);
            autoWallpaperFolderViewAdapter.setEditListener(this);
            autoWallpaperFolderViewAdapter.setSelectedListener(this);
            autoWallpaperListView.setAdapter(autoWallpaperFolderViewAdapter);
        }
    }


    private void refreshListUI() {
        if (autoWallpaperListViewAdapter != null)
        {
            autoWallpaperListViewAdapter.setList(MainActivity.database.getWallpaperLists(null,null));
            autoWallpaperListViewAdapter.notifyDataSetChanged();
            autoWallpaperListViewAdapter.setDeleteListener(this);
            autoWallpaperListViewAdapter.setEditListener(this);
            autoWallpaperListViewAdapter.setSelectedListener(this);
            autoWallpaperListView.setAdapter(autoWallpaperListViewAdapter);

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
        autoWallpaperListViewAdapter = new AutoWallpaperListViewAdapter(getContext(),R.layout.layout_custom_list_of_wallpaper,MainActivity.database.getWallpaperLists(null,null),null);
        autoWallpaperFolderViewAdapter =new AutoWallpaperFolderAdapter(getContext(),R.layout.layout_custom_list_of_wallpaper,MainActivity.database.getAllFolders(null,null));


        View createNewLayout = LayoutInflater.from(getContext()).inflate(R.layout.create_new_list_dialog,null,false);
        createNewDialog = new AlertDialog.Builder(getContext())
                .setView(createNewLayout).create();


        createNewAddButton = createNewLayout.findViewById(R.id.create_new_list_add_button);
        createNewCancelButton = createNewLayout.findViewById(R.id.create_new_list_cancel_button);
        createNewEditText = createNewLayout.findViewById(R.id.create_new_list_edit_text);

        createNewCancelButton.setOnClickListener(this);
        createNewAddButton.setOnClickListener(this);
        createNewEditText.setOnEditorActionListener(this);
        createNewDialog.setOnShowListener(this);
        createNewDialog.setOnDismissListener(this);
        createNewDialog.setOnCancelListener(this);


        numberAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.intervalArray, R.layout.custom_def_spinner_item);

        numberSpinner.setAdapter(numberAdapter);
        numberSpinner.setOnItemSelectedListener(this);

        intervalArray = new ArrayList();
        Collections.addAll(intervalArray, getResources().getStringArray(R.array.intervalArray));


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        IsEnabled = sharedPreferences.getBoolean(this.IsEnabledKey, false);
        interval = sharedPreferences.getInt(IntervalKey, 1);
        IsFolderActive = sharedPreferences.getBoolean(IsFolderActiveKey,false);
        IsListActive = sharedPreferences.getBoolean(IsListActiveKey,false);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        toggleButton.setChecked(IsEnabled);
        setSpinnerDefaultValue(interval);


        numberSpinner.setOnItemSelectedListener(this);
        toggleButton.setOnCheckedChangeListener(this);
        autoWallpaperListViewAdapter.setSelectedListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        addButton.setOnClickListener(this);
        autoWallpaperFolderViewAdapter.setDeleteListener(this);
        autoWallpaperFolderViewAdapter.setEditListener(this);
        autoWallpaperFolderViewAdapter.setSelectedListener(this);
        autoWallpaperListViewAdapter.setDeleteListener(this);
        autoWallpaperListViewAdapter.setEditListener(this);
        autoWallpaperListViewAdapter.setSelectedListener(this);



        autoWallpaperListView.setOnItemClickListener(this);

        return view;
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

        getActivity().setTitle("Auto Wallpaper");
        IsEnabled = sharedPreferences.getBoolean(this.IsEnabledKey, false);
        interval = sharedPreferences.getInt(IntervalKey, 1);
        IsFolderActive = sharedPreferences.getBoolean(IsFolderActiveKey,false);
        IsListActive = sharedPreferences.getBoolean(IsListActiveKey,false);


        autoWallpaperListViewAdapter = new AutoWallpaperListViewAdapter(getContext(),R.layout.layout_custom_list_of_wallpaper,MainActivity.database.getWallpaperLists(null,null),null);
        autoWallpaperFolderViewAdapter =new AutoWallpaperFolderAdapter(getContext(),R.layout.layout_custom_list_of_wallpaper,MainActivity.database.getAllFolders(null,null));

        radioGroup.clearCheck();
        if(IsFolderActive)
        {
            radioGroup.check(folderRadioButton.getId());
        }
        else if(IsListActive)
        {
            radioGroup.check(listRadioButton.getId());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //start and check service
        SpliceWallpaperBackgroundService.activeService.run();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.auto_wallpaper_add_button:
                runAdd();
                break;
            case R.id.create_new_list_add_button:
                // TO DO create new list func
                createNewList();
                break;
            case R.id.create_new_list_cancel_button:
                createNewDialog.dismiss();
                break;

        }

    }

    private void createNewList()
    {
        String listName = createNewEditText.getText().toString();
        if(!MainActivity.database.isWallpaperListCreatedBefore(listName))
        {
            long listId = MainActivity.database.createNewList(listName);
            if (listId > 0)
            {
                MainActivity.showToast("This wallpaper list created. Lets add something..",Toast.LENGTH_SHORT,getContext());
                createNewDialog.dismiss();
                refreshListUI();
            }
            else
                MainActivity.showToast("Somethings went wrong, please try again later..", Toast.LENGTH_SHORT,getContext());
        }
        else MainActivity.showToast("Every list name declares as the once. You should be imaginative.",Toast.LENGTH_SHORT,getContext());
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
                createNewDialog.show();
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

                setValueToSharedPreferences(IntervalKey, interval);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private Object setValueToSharedPreferences(String key, Object value) {
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
        return value;
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
                IsEnabled = (boolean) setValueToSharedPreferences(IsEnabledKey, isChecked);
                if (isChecked)SpliceWallpaperBackgroundService.activeService.run();
                else SpliceWallpaperBackgroundService.activeService.stop();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        wallpaperListModel model = autoWallpaperListViewAdapter.getItem(position);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId)
        {
            case R.id.auto_wallpaper_folder_button:
                this.autoWallpaperListView.setAdapter(this.autoWallpaperFolderViewAdapter);
                IsFolderActive = (boolean) setValueToSharedPreferences(IsFolderActiveKey,true);
                IsListActive = (boolean) setValueToSharedPreferences(IsListActiveKey,false);
                refreshFolderUI();
                break;
            case R.id.auto_wallpaper_list_button:
                this.autoWallpaperListView.setAdapter(this.autoWallpaperListViewAdapter);
                IsFolderActive = (boolean) setValueToSharedPreferences(IsFolderActiveKey,false);
                IsListActive = (boolean) setValueToSharedPreferences(IsListActiveKey,true);
                refreshListUI();
                break;
        }
    }

    @Override
    public void onListSelected(View view, wallpaperListModel wallpaperListModel,@NonNull boolean newState) {
        MainActivity.database.setActiveList(wallpaperListModel.getID(),newState);
    }

    @Override
    public void onEditSelected(wallpaperListModel wallpaperListModel) {
        Log.i(TAG, "onEditSelected: " + wallpaperListModel.getListName());
    }

    @Override
    public void onDeleteSelected(wallpaperListModel wallpaperListModel) {
        boolean isDeleted = MainActivity.database.deleteList(wallpaperListModel.getID());
        if (isDeleted) refreshListUI();
    }

    @Override
    public void onEditSelected(FolderModel folderModel) {

    }

    @Override
    public void onDeleteSelected(FolderModel folderModel) {
        if (folderModel != null)
        {
            boolean isDeleted = MainActivity.database.deleteFolder(folderModel.getFolderID());

            if (isDeleted)
            {
                refreshFolderUI();
            }
            else
            {
                MainActivity.showToast("Somethings went wrong, please try again later..", Toast.LENGTH_SHORT,getContext());
            }
        }
    }

    @Override
    public void onListSelected(View view, @NonNull FolderModel folderModel, @NonNull boolean toggleState) {
        MainActivity.database.setActiveFolder(folderModel.getFolderID(),toggleState);

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled =false;
        int id = v.getId();
        Log.i(TAG, "onEditorAction: " + id);
        switch (id)
        {
            case R.id.create_new_list_edit_text:
                if (actionId == EditorInfo.IME_ACTION_SEND)
                {
                    createNewList();
                    handled = true;
                }
                break;
        }
        return handled;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (dialog == createNewDialog)
        {
            createNewEditText.setText("");
            MainActivity.showKeyboard(createNewEditText,getContext());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SpliceWallpaperBackgroundService.activeService.run();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (dialog == createNewDialog)
        {
            MainActivity.hideKeyboard(createNewEditText,getContext());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (dialog == createNewDialog)
        {
            MainActivity.hideKeyboard(createNewEditText,getContext());
        }
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
                    stateToggleButton.setChecked(!stateToggleButton.isChecked());
                    selectedListener.onListSelected(view,listItem,stateToggleButton.isChecked());
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
            //this area will running only while wallpaper added to list
            if (MainActivity.database.isWallpaperAddtoList(listItem.getID(),this.wallpaperModel))
            {
                stateToggleButton.setChecked(true);
            }
            else
            {
                stateToggleButton.setChecked(false);
            }
        }
        else if (listItem != null)
        {
            //this area will running on autowallaper list view
            if (MainActivity.database.isListActive(listItem.getID()))
            {
                stateToggleButton.setChecked(true);
            }
            else{
                stateToggleButton.setChecked(false);
            }
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
        void onListSelected(View view,@NonNull wallpaperListModel wallpaperListModel,@NonNull boolean newState);
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
    List<FolderModel> list;

    Context context;
    public AutoWallpaperFolderAdapter(@NonNull Context context, int resource, @NonNull List<FolderModel> objects) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
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
        listItemNameTextView.setText(listItem.getFolderPath() + File.separator + listItem.getFolderName());

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
                    if (stateToggleButton.isChecked()) stateToggleButton.setChecked(false);
                    else stateToggleButton.setChecked(true);

                    selectedListener.onListSelected(view,listItem,stateToggleButton.isChecked());

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

        stateToggleButton.setChecked(listItem.isActive());

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
        void onListSelected(View view,@NonNull FolderModel folderModel,@NonNull boolean toggleState);
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

