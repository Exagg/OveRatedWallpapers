package com.example.hrwallpapers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.hrwallpapers.DataAccessLayer.SqliteCheckErrors;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

public class AddToListDialog extends DialogFragment implements View.OnClickListener,
        AutoWallpaperListViewAdapter.onDeleteListener,
        AutoWallpaperListViewAdapter.onEditListener,
        AutoWallpaperListViewAdapter.onListSelected
{
    private static final String TAG = "AddToListDialog";
    private ListView containerListView;
    private ImageButton addButton;
    private View noContentView;
    private AutoWallpaperListViewAdapter listViewAdapter;
    private List<wallpaperListModel> wallpaperListModels;
    private SlidingUpPanelLayout slidingPanel;
    private Button createButton;
    private EditText createListEditText;
    private ImageButton backButton;
    private wallpaperModel selectedModel;
    public AddToListDialog(wallpaperModel selectedModel) {
        super();
        this.selectedModel = selectedModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_to_list_layout,null,false);
        this.containerListView = view.findViewById(R.id.add_to_list_list_view);
        this.addButton = view.findViewById(R.id.add_to_list_add_button);
        this.noContentView =view.findViewById(R.id.add_to_list_no_content);
        this.createButton = view.findViewById(R.id.add_to_list_create_button);
        this.slidingPanel = view.findViewById(R.id.add_to_list_sliding_up_panel);
        this.createListEditText = view.findViewById(R.id.add_to_list_edit_text);
        this.backButton = view.findViewById(R.id.add_to_list_back_button);


        wallpaperListModels = MainActivity.database.getWallpaperLists();
        this.listViewAdapter = new AutoWallpaperListViewAdapter(getContext(),R.layout.layout_custom_list_of_wallpaper,wallpaperListModels,this.selectedModel);


        this.containerListView.setAdapter(listViewAdapter);

        this.addButton.setOnClickListener(this);
        this.backButton.setOnClickListener(this);
        this.createButton.setOnClickListener(this);
        this.listViewAdapter.setDeleteListener(this);
        this.listViewAdapter.setEditListener(this);
        this.listViewAdapter.setSelectedListener(this);
        return view;
    }


    @Override
    public void onStart() {
        if (this.containerListView != null && this.noContentView != null)
        {
            if (listViewAdapter.getCount() == 0)
            {
                this.containerListView.setVisibility(View.GONE);
                this.noContentView.setVisibility(View.VISIBLE);
            }
            else
            {
                this.containerListView.setVisibility(View.VISIBLE);
                this.noContentView.setVisibility(View.GONE);
            }
        }
        super.onStart();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.add_to_list_add_button:
                this.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                break;
            case R.id.add_to_list_back_button:
                this.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                break;
            case R.id.add_to_list_create_button:
                createList();
                break;
        }
    }

    private void createList() {
        if (this.createListEditText.getText().toString() == "") return;

        String listName = this.createListEditText.getText().toString();

        long createdID = MainActivity.database.createNewList(listName);

        if (!SqliteCheckErrors.checkErrors(createdID))
        {
            MainActivity.showToast("List is created..", Toast.LENGTH_SHORT,getContext());
            refreshList();
            onClick(backButton);

        }
        else
        {
            MainActivity.showToast("We got failed while adding list..", Toast.LENGTH_SHORT,getContext());
        }



    }

    private void refreshList() {
        if (listViewAdapter != null)
        {
            listViewAdapter.setList(MainActivity.database.getWallpaperLists());
            onStart();
        }
    }

    @Override
    public void onEditSelected(wallpaperListModel wallpaperListModel) {
        Log.i(TAG, "onEditSelected: " + wallpaperListModel.getListName() + " - " + wallpaperListModel.getID());
    }

    @Override
    public void onDeleteSelected(final wallpaperListModel wallpaperListModel) {

        if (MainActivity.database.deleteList(wallpaperListModel.getID()))
        {
            Toast.makeText(AddToListDialog.this.getContext(), "Wallpaper list is deleted. -" + wallpaperListModel.getListName() , Toast.LENGTH_SHORT).show();
            refreshList();
        }
        else
        {
            Toast.makeText(AddToListDialog.this.getContext(), "An error occured while processing your request.", Toast.LENGTH_SHORT).show();
        }
        if (wallpaperListModel.getID() == 0)
        {
            new AlertDialog.Builder(AddToListDialog.this.getContext())
                    .setTitle("Title")
                    .setMessage("Do you really want to whatever?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (MainActivity.database.deleteList(wallpaperListModel.getID()))
                            {
                                Toast.makeText(AddToListDialog.this.getContext(), "Wallpaper list is deleted. -" + wallpaperListModel.getListName() , Toast.LENGTH_SHORT).show();
                                refreshList();
                            }
                            else
                            {
                                Toast.makeText(AddToListDialog.this.getContext(), "An error occured while processing your request.", Toast.LENGTH_SHORT).show();
                            }
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    @Override
    public void onListSelected(View v,wallpaperListModel wallpaperListModel) {
        if (this.selectedModel != null)
        {
            if(!MainActivity.database.isWallpaperAddtoList(wallpaperListModel.getID(),this.selectedModel))
            {
                if(MainActivity.database.addToList(wallpaperListModel.getID(),this.selectedModel))
                {
                    Toast.makeText(AddToListDialog.this.getContext(), "This wallpaper add to " + wallpaperListModel.getListName(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(AddToListDialog.this.getContext(), "An error occured while processing your request.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                if (MainActivity.database.deleteFromList(wallpaperListModel.getID(),this.selectedModel))
                {
                    Toast.makeText(AddToListDialog.this.getContext(), "This wallpaper is removed on " + wallpaperListModel.getListName(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(AddToListDialog.this.getContext(), "An error occured while processing your request.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
