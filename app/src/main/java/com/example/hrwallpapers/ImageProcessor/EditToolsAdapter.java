package com.example.hrwallpapers.ImageProcessor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hrwallpapers.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.motion.MotionScene.TAG;

class EditToolsAdapter extends RecyclerView.Adapter<EditToolsAdapter.ToolsViewHolder>
{
    private ToolSelectedListener toolSelectedListener;
    List<ToolModel> toolModelList = new ArrayList();
    public EditToolsAdapter()
    {
        toolModelList.add(new ToolModel("Brush", R.drawable.ic_brush,ToolType.BRUSH));
        toolModelList.add(new ToolModel("Text",R.drawable.ic_add_text,ToolType.BRUSH));
        toolModelList.add(new ToolModel("Eraser",R.drawable.ic_eraser,ToolType.BRUSH));
        toolModelList.add(new ToolModel("Filter",R.drawable.ic_photography,ToolType.BRUSH));
        toolModelList.add(new ToolModel("Emoji",R.drawable.ic_cowboy,ToolType.BRUSH));
        toolModelList.add(new ToolModel("Sticker",R.drawable.ic_mountains,ToolType.BRUSH));
        Log.i(TAG, "EditToolsAdapter: Tools adapter is created");
    }

    @NonNull
    @Override
    public ToolsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.imageprocess_tool_layout,viewGroup,false);
        return new ToolsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolsViewHolder viewHolder, int i) {
        ToolModel toolModel = getToolModel(i);
        viewHolder.imageView.setImageResource(toolModel.drawableID);
        viewHolder.textview.setText(toolModel.name);
    }

    @Override
    public int getItemCount() {
        return toolModelList.size();
    }

    public ToolModel getToolModel(int position)
    {
        return this.toolModelList.get(position);
    }

    class ToolsViewHolder extends RecyclerView.ViewHolder {

        TextView textview;
        ImageView imageView;
        public ToolsViewHolder(@NonNull View itemView) {
            super(itemView);

            this.textview = itemView.findViewById(R.id.tool_textview);
            this.imageView = itemView.findViewById(R.id.tool_imageview);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolSelectedListener.onToolSelected(getToolModel(getLayoutPosition()));
                }
            });
        }
    }
}