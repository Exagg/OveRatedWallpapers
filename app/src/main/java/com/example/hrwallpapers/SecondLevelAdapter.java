package com.example.hrwallpapers;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class SecondLevelAdapter extends BaseExpandableListAdapter {

    private Context context;
    List<MenuModel[]> data;
    MenuModel[] headers;

    private final float arrowsHeight = 75;
    private final float arrowsWidth = 75;
    private final float dotHeight = 75;
    private final float dotWidth = 75;

    private final float arrowsMargin = 150;
    private final float dotsMargin = 225;

    private final float marginDivision = 3;


    public SecondLevelAdapter(Context context, MenuModel[] headers, List<MenuModel[]> data)
    {
        this.context = context;
        this.headers = headers;
        this.data = data;
    }
    @Override
    public int getGroupCount() {
        return headers != null ? headers.length : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        MenuModel[] children = data.size() > groupPosition ? data.get(groupPosition) : null;


        return children != null ? children.length : 0;
    }

    @Override
    public MenuModel getGroup(int groupPosition) {
        return headers[groupPosition];
    }

    @Override
    public MenuModel getChild(int groupPosition, int childPosition) {
        MenuModel[] childData;

        childData = data.get(groupPosition);


        return childData[childPosition];
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
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.menu_child_of_group,null);

        }

        LinearLayout container = convertView.findViewById(R.id.menu_container);
        TextView tv = convertView.findViewById(R.id.child_id);

        tv.setText(model.name);


        if(model.drawableID != 0)
        {
            ImageView im = convertView.findViewById(R.id.child_image);
            LinearLayout.LayoutParams imageParam = new LinearLayout.LayoutParams(
                    im.getLayoutParams().width,
                    im.getLayoutParams().height
            );


            if(getChildrenCount(groupPosition) == 0)
            {
                //Dot menu style
                container.setPadding((int) MainActivity.setPxToDP(dotsMargin,context),(int) MainActivity.setPxToDP(dotsMargin / marginDivision,context),0,0);
                imageParam.topMargin = (int) MainActivity.setPxToDP((float) 25,context);
                imageParam.height = (int) MainActivity.setPxToDP((float)dotHeight,context);
                imageParam.width = (int) MainActivity.setPxToDP((float)dotWidth,context);
            }
            else
            {
                //arrow menu style
                container.setPadding((int) MainActivity.setPxToDP(arrowsMargin,context),(int) MainActivity.setPxToDP(arrowsMargin / marginDivision,context),0,0);
                imageParam.topMargin = (int) MainActivity.setPxToDP((float) 25,context);
                imageParam.height = (int) MainActivity.setPxToDP((float)arrowsHeight,context);
                imageParam.width = (int) MainActivity.setPxToDP((float)arrowsWidth,context);
            }
            im.setLayoutParams(imageParam);
            MainActivity.setIconToImageView(im,this.context,model.drawableID,true,false,50,model.colorID);
        }



        if(tv.getText() != model.name)
        {
            tv.setText(model.name);
        }


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final MenuModel model = getChild(groupPosition,childPosition);

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.menu_child_of_group,null);
        }

        TextView tv = convertView.findViewById(R.id.child_id);
        LinearLayout container = convertView.findViewById(R.id.menu_container);

        tv.setText(model.name);

        ImageView childImage = convertView.findViewById(R.id.child_image);
        convertView.requestLayout();
        LinearLayout.LayoutParams imageParam = new LinearLayout.LayoutParams(
                childImage.getLayoutParams().width,
                childImage.getLayoutParams().height
        );

        container.setPadding((int) MainActivity.setPxToDP(dotsMargin,context),(int) MainActivity.setPxToDP(dotsMargin / marginDivision,context),0,0);
        imageParam.topMargin = (int) MainActivity.setPxToDP((float) 25,context);
        imageParam.height = (int) MainActivity.setPxToDP((float)dotHeight,context);
        imageParam.width = (int) MainActivity.setPxToDP((float)dotWidth,context);
        childImage.setLayoutParams(imageParam);

        if(model.drawableID != 0)
        {
            MainActivity.setIconToImageView(childImage,this.context,model.drawableID,true,false,50,model.colorID);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
