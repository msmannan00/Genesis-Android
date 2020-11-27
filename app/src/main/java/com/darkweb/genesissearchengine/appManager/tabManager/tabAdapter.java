package com.darkweb.genesissearchengine.appManager.tabManager;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.helperManager.TopCropImageView;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class tabAdapter extends RecyclerView.Adapter<tabAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<tabRowModel> mModelList;
    private eventObserver.eventListener mEvent;
    private ArrayList<Integer> mSelectedList = new ArrayList<>();

    tabAdapter(ArrayList<tabRowModel> model_list, eventObserver.eventListener event) {
        this.mModelList = model_list;
        this.mEvent = event;

    }

    /*Initializations*/

    @NonNull @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tabview_row, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull tabAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(mModelList.get(position));
        holder.itemView.setTag(position);
        holder.itemView.findViewById(R.id.pRemoveRow).setTag(position);
        holder.itemView.findViewById(R.id.pLoadSession).setTag(position);
    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }

    /*Listeners*/

    private int getSelectedListSize(){
        return mSelectedList.size();
    }

    private void onRemoveAllSelection() {
        mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_CLEAR_TAB_BACKUP);
        for(int mCounter=0;mCounter<mModelList.size();mCounter++){
            if(mSelectedList.contains(mModelList.get(mCounter).getSession().getSessionID())){
                mSelectedList.remove((Integer) mModelList.get(mCounter).getSession().getSessionID());
                mEvent.invokeObserver(Collections.singletonList(mCounter), tabEnums.eTabAdapterCallback.ON_REMOVE_TAB);
                notifyItemRemoved(mCounter);
                mCounter-=1;
            }
        }
    }

    private void onClearAllSelection(){
        mSelectedList.clear();
        notifyDataSetChanged();
    }

    private void onSelectionCreate(FrameLayout mSelectedView){
        mSelectedView.setAlpha(0);
        mSelectedView.setVisibility(View.VISIBLE);
        mSelectedView.animate().alpha(1);
        mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_SHOW_SELECTION);
    }

    private void onSelectionClear(FrameLayout mSelectedView){
        mSelectedView.animate().alpha(0).withEndAction(() -> mSelectedView.setVisibility(View.GONE));
        if(mSelectedList.size()==0){
            mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_HIDE_SELECTION);
        }
    }

    private void onTriggerURL(tabRowModel model){
        mEvent.invokeObserver(Arrays.asList(model.getSession(), false), tabEnums.eTabAdapterCallback.ON_LOAD_TAB);
        mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_BACK_PRESSED);
        mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_INIT_TAB_COUNT);
    }

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        TextView mHeader;
        TextView mDescription;
        TextView mDate;
        ImageView mLoadSession;
        ImageButton mRemoveRow;
        TopCropImageView mWebThumbnail;
        FrameLayout mSelectedView;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(tabRowModel model) {
            mHeader = itemView.findViewById(R.id.pHeader);
            mDescription = itemView.findViewById(R.id.pDescription);
            mLoadSession = itemView.findViewById(R.id.pLoadSession);
            mRemoveRow = itemView.findViewById(R.id.pRemoveRow);
            mWebThumbnail = itemView.findViewById(R.id.pWebThumbnail);
            mDate = itemView.findViewById(R.id.pDate);
            mSelectedView = itemView.findViewById(R.id.pSelectedView);

            mHeader.setText(model.getSession().getTitle());
            mDescription.setText(model.getSession().getCurrentURL());
            mDate.setText(model.getDate());
            mWebThumbnail.setImageBitmap(model.getBitmap());

            if(mSelectedList.contains(model.getSession().getSessionID())){
                onSelectionCreate(mSelectedView);
            }else {
                onSelectionClear(mSelectedView);
            }

            if(model.getSession().equals(mModelList.get(0).getSession())){
                itemView.setBackgroundColor(ContextCompat.getColor(activityContextManager.getInstance().getTabController(), R.color.c_list_item_current));
            }

            mLoadSession.setOnLongClickListener(this);
            mRemoveRow.setOnClickListener(this);
            mLoadSession.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.pLoadSession){
                if(mSelectedView.getVisibility() == View.GONE){
                    if(mSelectedList.size()>0){
                        mSelectedList.add(mModelList.get(this.getLayoutPosition()).getSession().getSessionID());
                        onSelectionCreate(mSelectedView);
                    }else {
                        onTriggerURL(mModelList.get(this.getLayoutPosition()));
                    }
                }else {
                    for(int mCounter=0;mCounter<mSelectedList.size();mCounter++){
                        if(mSelectedList.get(mCounter) == mModelList.get(this.getLayoutPosition()).getSession().getSessionID()){
                            mSelectedList.remove(mCounter);
                        }
                    }
                    onSelectionClear(mSelectedView);
                }
            }else if(v.getId() == R.id.pRemoveRow){
                mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_CLEAR_TAB_BACKUP);
                mEvent.invokeObserver(Collections.singletonList(this.getLayoutPosition()), tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(v.getId() == R.id.pLoadSession){
                if(mSelectedView.getVisibility() == View.GONE){
                    mSelectedList.add(mModelList.get(this.getLayoutPosition()).getSession().getSessionID());
                    onSelectionCreate(mSelectedView);
                }else {
                    mSelectedList.remove(this.getLayoutPosition());
                    onSelectionClear(mSelectedView);
                }
                mLoadSession.setPressed(false);
            }
            return true;
        }
    }

    public Object onTrigger(tabEnums.eTabModelCommands pCommands, List<Object> pData){
        if(pCommands.equals(tabEnums.eTabModelCommands.M_SELECTED_LIST_SIZE)){
            return getSelectedListSize();
        }else if(pCommands.equals(tabEnums.eTabModelCommands.M_REMOVE_ALL_SELECTION)){
            onRemoveAllSelection();
        }else if(pCommands.equals(tabEnums.eTabModelCommands.M_CLEAR_ALL_SELECTION)){
            onClearAllSelection();
        }
        return null;
    }
}


