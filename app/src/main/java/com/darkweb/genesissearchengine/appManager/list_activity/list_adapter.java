package com.darkweb.genesissearchengine.appManager.list_activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.data_helper.database_controller;
import com.darkweb.genesissearchengine.appManager.main_activity.app_model;
import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.helperMethod;
import com.darkweb.genesissearchengine.pluginManager.orbot_manager;
import com.example.myapplication.R;

public class list_adapter extends RecyclerView.Adapter<list_adapter.listViewHolder>
{
    private ImageView empty_list;
    private list_controller controller;

    public list_adapter()
    {
    }

    public void initialize(ImageView empty_list,list_controller controller)
    {
        this.empty_list = empty_list;
        this.controller = controller;
    }

    @Override
    public listViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_manager_row, parent, false);
        listViewHolder viewHolder = new listViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(list_adapter.listViewHolder holder, int position)
    {
        holder.bindListView(list_model.getInstance().getModel().get(position));
        clearMessageItem(holder.messageButton,holder.empty_message,position);
    }

    @Override
    public int getItemCount() {
        return list_model.getInstance().getModel().size();
    }

    public class listViewHolder extends RecyclerView.ViewHolder
    {
        public TextView heaaderText;
        public TextView descriptionText;
        public ImageButton messageButton;
        public ImageView empty_message;

        public listViewHolder(View itemView) {
            super(itemView);
        }

        public void bindListView(list_row_model model) {

            heaaderText = itemView.findViewById(R.id.header);
            descriptionText = itemView.findViewById(R.id.description);

            String header = model.getHeader();
            descriptionText.setText(model.getDescription());

            if(model.getHeader().length()<=2)
            {
                header = model.getDescription() + model.getHeader();
            }
            heaaderText.setText(header);


            messageButton = itemView.findViewById(R.id.message_button);
            empty_message = itemView.findViewById(R.id.empty_list);

            setItemViewOnClickListener(itemView,header);
        }
    }

    public void setItemViewOnClickListener(View itemView,String url)
    {
        itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {

                String tempurl =  url;
                isValidUrl(tempurl);
                controller.finish();
            }
        });
    }

    public boolean isValidUrl(String url)
    {
        url = helperMethod.completeURL(url);

        if(!url.contains("boogle") && !url.equals(constants.backendGoogle) && !url.equals(constants.backendBing))
        {
            if(orbot_manager.getInstance().initOrbot(url))
            {
                app_model.getInstance().getAppInstance().onloadURL(url,true,false);
                return true;
            }
            return false;
        }
        else
        {
            app_model.getInstance().getAppInstance().onloadURL(url,false,false);
            return true;
        }
    }

    public void clearMessageItem(ImageButton messageButton,ImageView view,int index)
    {
        ImageView newview = view;
        messageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteFromDatabase(list_model.getInstance().getModel().get(index).getId());
                list_model.getInstance().getModel().remove(index);
                list_model.getInstance().removeFromMainList(list_model.getInstance().getIndex().get(index));
                list_model.getInstance().removeIndex(index);
                list_adapter.this.notifyItemRemoved(index);
                list_adapter.this.notifyDataSetChanged();
                isListEmpty(newview);
            }
        });
    }

    public void deleteFromDatabase(int index)
    {
        String table="bookmark";
        if(list_model.getInstance().getType()==constants.list_history)
        {
            table = "history";
        }
        Log.i("dgfdfg","delete from "+table+" where id="+index);
        database_controller.getInstance().execSQL("delete from "+table+" where id="+index);
    }

    public void isListEmpty(ImageView view)
    {
        if(list_model.getInstance().getModel().size()<=0)
        {
            empty_list.setAlpha(0f);
            empty_list.setVisibility(View.VISIBLE);
            empty_list.animate().alpha(1);
        }

    }

}
