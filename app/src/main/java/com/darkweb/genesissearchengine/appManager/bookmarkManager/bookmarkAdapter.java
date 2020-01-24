package com.darkweb.genesissearchengine.appManager.bookmarkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;

public class bookmarkAdapter extends RecyclerView.Adapter<bookmarkAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<bookmarkRowModel> mModelList;
    private ArrayList<bookmarkRowModel> temp_model_list;
    private eventObserver.eventListener event;
    private String filter = strings.EMPTY_STR;
    private boolean isClosing = false;

    bookmarkAdapter(ArrayList<bookmarkRowModel> model_list, eventObserver.eventListener event) {
        this.mModelList = model_list;
        this.event = event;
        temp_model_list = new ArrayList<>();
    }

    /*Initializations*/

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookmarkAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(temp_model_list.get(position));
        clearMessageItem(holder.messageButton,position);
    }

    @Override
    public int getItemCount() {
        return temp_model_list.size();
    }

    /*Listeners*/

    private void setItemViewOnClickListener(View itemView, String url)
    {
        itemView.setOnClickListener(v ->event.invokeObserver(Collections.singletonList(url),enums.etype.url_triggered));
    }

    private void clearMessageItem(ImageButton clearButton, int index)
    {
        clearButton.setOnClickListener(v ->
        {
            if(!isClosing){
                isClosing = true;
                int size = mModelList.size();

                event.invokeObserver(Collections.singletonList(mModelList.get(temp_model_list.get(index).getmId()).getmId()),enums.etype.remove_from_database);
                event.invokeObserver(Collections.singletonList(temp_model_list.get(index).getmId()),enums.etype.url_clear);
                invokeFilter(false);
                event.invokeObserver(Collections.singletonList(index),enums.etype.is_empty);

                if(size>1){
                    new Thread(){
                        public void run(){
                            try
                            {
                                sleep(500);
                                isClosing = false;
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });

    }

    /*View Holder Extensions*/

    class listViewHolder extends RecyclerView.ViewHolder
    {
        TextView heaaderText;
        TextView descriptionText;
        ImageButton messageButton;
        ImageView empty_message;
        LinearLayout itemContainer;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(bookmarkRowModel model) {

            heaaderText = itemView.findViewById(R.id.mHeader);
            descriptionText = itemView.findViewById(R.id.mDescription);
            itemContainer = itemView.findViewById(R.id.item_container);

            String header = model.getmHeader();

            descriptionText.setText(model.getmHeader());
            heaaderText.setText(model.getmDescription());
            messageButton = itemView.findViewById(R.id.message_button);
            empty_message = itemView.findViewById(R.id.empty_list);

            setItemViewOnClickListener(itemContainer,header);
        }
    }

    void setFilter(String filter){
        this.filter = filter;
    }

    void invokeFilter(boolean notify){
        temp_model_list.clear();
        for(int counter = 0; counter< mModelList.size(); counter++){
            if(mModelList.get(counter).getmHeader().contains(filter) || mModelList.get(counter).getmDescription().contains(filter)){
                bookmarkRowModel model = mModelList.get(counter);
                temp_model_list.add(new bookmarkRowModel(model.getmHeader(),model.getmDescription(),counter));
            }
        }

        if(notify){
            notifyDataSetChanged();
        }
    }
}
