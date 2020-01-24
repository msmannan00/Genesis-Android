package com.darkweb.genesissearchengine.appManager.historyManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;

public class historyAdapter extends RecyclerView.Adapter<historyAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<historyRowModel> mModelList;
    private ArrayList<historyRowModel> tempModelList;
    private eventObserver.eventListener mEvent;
    private String filter = strings.EMPTY_STR;
    private boolean isClosing = false;

    historyAdapter(ArrayList<historyRowModel> mModelList, eventObserver.eventListener mEvent) {
        this.mModelList = mModelList;
        this.mEvent = mEvent;
        tempModelList = new ArrayList<>();
    }

    /*Initializations*/

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull historyAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(tempModelList.get(position));
        clearMessageItem(holder.mMessageButton,position);
    }

    @Override
    public int getItemCount() {
        return tempModelList.size();
    }

    /*Listeners*/

    private void setItemViewOnClickListener(View itemView, String url)
    {
        itemView.setOnClickListener(v -> mEvent.invokeObserver(Collections.singletonList(url),enums.etype.url_triggered));
    }

    private void clearMessageItem(ImageButton clearButton, int index)
    {
        clearButton.setOnClickListener(v ->
        {
            if(!isClosing){
                isClosing = true;
                int size = mModelList.size();

                int index_temp = tempModelList.get(index).getmId();
                mEvent.invokeObserver(Collections.singletonList(tempModelList.get(index).getmHeader()),enums.etype.url_clear_at);
                mEvent.invokeObserver(Collections.singletonList(mModelList.get(index_temp).getmId()),enums.etype.remove_from_database);
                mEvent.invokeObserver(Collections.singletonList(tempModelList.get(index).getmId()),enums.etype.url_clear);
                invokeFilter(false);
                mEvent.invokeObserver(Collections.singletonList(index),enums.etype.is_empty);

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
        TextView mHeaaderText;
        TextView mDescriptionText;
        ImageButton mMessageButton;
        ImageView mEmptyMessage;
        LinearLayout mItemContainer;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(historyRowModel model) {

            mHeaaderText = itemView.findViewById(R.id.mHeader);
            mDescriptionText = itemView.findViewById(R.id.mDescription);
            mItemContainer = itemView.findViewById(R.id.item_container);

            String header = model.getmHeader();

            mDescriptionText.setText(model.getmDescription());
            mHeaaderText.setText(model.getmHeader());
            mMessageButton = itemView.findViewById(R.id.message_button);
            mEmptyMessage = itemView.findViewById(R.id.empty_list);

            setItemViewOnClickListener(mItemContainer, header);
        }
    }

    void setFilter(String filter){
        this.filter = filter;
    }

    void invokeFilter(boolean notify){
        tempModelList.clear();
        for(int counter = 0; counter< mModelList.size(); counter++){
            if(mModelList.get(counter).getmHeader().contains(filter)){
                historyRowModel model = mModelList.get(counter);
                tempModelList.add(new historyRowModel(model.getmHeader(),model.getmDescription(),counter));
            }
        }

        if(notify){
            notifyDataSetChanged();
        }
    }
}
