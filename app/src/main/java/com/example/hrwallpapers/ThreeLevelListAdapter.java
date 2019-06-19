package com.example.hrwallpapers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ThreeLevelListAdapter extends BaseExpandableListAdapter {
    List<MenuModel> parentHeader;
    List<MenuModel[]> secondLevel;
    private Context context;
    List<LinkedHashMap<MenuModel, MenuModel[]>> data;

    public ThreeLevelListAdapter(Context context, List<MenuModel> parentHeader, List<MenuModel[]> secondLevel, List<LinkedHashMap<MenuModel, MenuModel[]>> data) {
        this.context = context;

        this.parentHeader = parentHeader;

        this.secondLevel = secondLevel;

        this.data = data;
    }
    @Override
    public int getGroupCount() {
        return parentHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public MenuModel getGroup(int groupPosition) {
        return parentHeader.get(groupPosition);
    }

    @Override
    public MenuModel getChild(int groupPosition, int childPosition) {
        return null;
    }


    public MenuModel getChild(int groupPosition, int childPosition,int innerGroupPosition) {
        LinkedHashMap<MenuModel, MenuModel[]> linkedHashMap = data.get(groupPosition);
        MenuModel[] m = (MenuModel[]) linkedHashMap.values().toArray()[innerGroupPosition];
        return m[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final MenuModel model = getGroup(groupPosition);
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.menu_groups,null);
            TextView tv = convertView.findViewById(R.id.group_Text);
            tv.setText(model.name);

            ImageView im = convertView.findViewById(R.id.group_Image);
            im.setImageDrawable(context.getResources().getDrawable(model.drawableID));

        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(secondLevel != null && data.get(groupPosition) != null)
        {
            final SecondLevelExpandableListView secondLevelELV = new SecondLevelExpandableListView(context);

            final MenuModel header[] = secondLevel.get(groupPosition);

            final List<MenuModel[]> childData = new ArrayList<>();

            final HashMap<MenuModel, MenuModel[]> secondLevelData = data.get(groupPosition);

            for(MenuModel key: secondLevelData.keySet())
            {
                childData.add(secondLevelData.get(key));
            }

            secondLevelELV.setAdapter(new SecondLevelAdapter(context,header,childData));
            secondLevelELV.setGroupIndicator(null);

            secondLevelELV.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int prevGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {
                    if(groupPosition != prevGroup)
                    {
                        secondLevelELV.collapseGroup(prevGroup);
                        prevGroup = groupPosition;
                    }
                }
            });

            secondLevelELV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    MenuModel parentModel = header[groupPosition];
                    MenuModel[] childrens = secondLevelData.get(parentModel);
                    Log.i("a",parentModel.name);
                    MainActivity.setMenuClickListener(childrens[childPosition]);

                    return true;
                }
            });

            secondLevelELV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    MenuModel parentModel = header[groupPosition];
                    MenuModel[] childrens = secondLevelData.get(parentModel);
                    if (childrens == null)
                    {
                        try
                        {
                            MainActivity.setMenuClickListener(parentModel);
                        }
                        catch (Exception ex)
                        {
                            Log.i("Exception" ,ex.getMessage());
                        }
                    }
                    return false;
                }
            });

            return secondLevelELV;
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
