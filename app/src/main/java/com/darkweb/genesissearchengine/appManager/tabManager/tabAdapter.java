package com.darkweb.genesissearchengine.appManager.tabManager;

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
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Collections;

public class tabAdapter extends RecyclerView.Adapter<tabAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<tabRowModel> mModelList;
    private ArrayList<tabRowModel> mTempModelList;
    private eventObserver.eventListener mEvent;
    private String filter = strings.GENERIC_EMPTY_STR;
    private boolean isClosing = false;

    tabAdapter(ArrayList<tabRowModel> model_list, eventObserver.eventListener event) {
        this.mModelList = model_list;
        this.mEvent = event;
        mTempModelList = new ArrayList<>();
    }

    /*Initializations*/

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull tabAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(mTempModelList.get(position));
        clearMessageItem(holder.messageButton,position,holder.data_model);
        clearMessageItemContainer(holder.itemContainer,position,holder.data_model);
    }

    @Override
    public int getItemCount() {
        return mTempModelList.size();
    }

    /*Listeners*/

    private void clearMessageItem(ImageButton clearButton, int index,tabRowModel model)
    {
        clearButton.setOnClickListener(v ->
        {
            if(!isClosing){
                isClosing = true;
                int size = mModelList.size();

                mEvent.invokeObserver(Collections.singletonList(mTempModelList.get(index).getmId()),enums.etype.url_clear);
                mEvent.invokeObserver(Collections.singletonList(index),enums.etype.is_empty);
                invokeFilter(false);
                model.getSession().stop();
                model.getSession().close();
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




    private void clearMessageItemContainer(LinearLayout clearButton, int index,tabRowModel model)
    {
        clearButton.setOnClickListener(v ->
        {
            if(mTempModelList.size()>index){
                invokeFilter(false);
                mEvent.invokeObserver(Collections.singletonList(model),enums.etype.url_triggered);
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
        tabRowModel data_model;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(tabRowModel model) {

            heaaderText = itemView.findViewById(R.id.pHeader);
            descriptionText = itemView.findViewById(R.id.pDescription);
            //itemContainer = itemView.findViewById(R.id.pRowContainer);

            if(model.getSession().getTitle().equals("") || model.getSession().getTitle().equals("loading")){
                heaaderText.setText(helperMethod.getDomainName(model.getSession().getCurrentURL()));
            }
            else {
                heaaderText.setText(model.getSession().getTitle());
            }

            /*Large file or content based url handling*/
            String url = model.getSession().getCurrentURL();
            if(url.length()>200){
                url = url.substring(0,200);
            }

            descriptionText.setText(url);
            // messageButton = itemView.findViewById(R.id.message_button);
            empty_message = itemView.findViewById(R.id.pEmptyListNotification);
            data_model = model;
        }
    }

    void setFilter(String filter){
        this.filter = filter;
    }

    void invokeFilter(boolean notify){
        mTempModelList.clear();
        for(int counter = 0; counter< mModelList.size(); counter++){
            if(mModelList.get(counter).getSession().getTitle().contains(filter) || mModelList.get(counter).getSession().getCurrentURL().contains(filter)){
                tabRowModel model = mModelList.get(counter);
                mTempModelList.add(new tabRowModel(model.getSession(),counter));
            }
        }

        if(notify){
            mEvent.invokeObserver(null,enums.etype.clear_recycler);
            notifyDataSetChanged();
        }
    }
}
