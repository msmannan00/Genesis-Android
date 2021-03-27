package com.darkweb.genesissearchengine.appManager.tabManager;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.TopCropImageView;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_DOMAIN_URL;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_HELP_URL;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_HELP_URL_CACHE;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_HELP_URL_CACHE_DARK;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_URL_CACHED;
import static com.darkweb.genesissearchengine.constants.constants.CONST_GENESIS_URL_CACHED_DARK;

public class tabAdapter extends RecyclerView.Adapter<tabAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<tabRowModel> mModelList = new ArrayList<>();
    private eventObserver.eventListener mEvent;
    private ArrayList<String> mSelectedList = new ArrayList<>();
    private Boolean mLongPressMenuEnabled = false;


    tabAdapter(ArrayList<tabRowModel> pModelList, eventObserver.eventListener event) {
        this.mModelList.clear();
        this.mModelList.addAll(pModelList);
        mModelList.add(new tabRowModel(null, null,null));
        this.mEvent = event;
    }

    private void reInitData(ArrayList<tabRowModel> pModelList){
        for(int mCounter=0;mCounter<pModelList.size();mCounter++){
            mModelList.add(0,pModelList.get(mCounter));
        }
        notifyItemRangeInserted(0, pModelList.size());
        notifyItemChanged(pModelList.size());
    }

    public void initFirstRow(){
        notifyItemChanged(0);
    }

    /*Initializations*/

    @NonNull @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(status.sTabGridLayoutEnabled){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_grid_view, parent, false);
            return new listViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_row_view, parent, false);
            return new listViewHolder(view);
        }
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

    private boolean isSelectionMenuShowing(){
        return mLongPressMenuEnabled;
    }

    private void onRemoveAllSelection() {
        if(mSelectedList!=null && mSelectedList.size()>0){
            for(int mCounter=0;mCounter<mSelectedList.size();mCounter++){
                for(int mCounterInner=0;mCounterInner<mModelList.size();mCounterInner++){
                    if(mSelectedList.get(mCounter).equals(mModelList.get(mCounterInner).getmId())){
                        mSelectedList.remove(mCounter);
                        mModelList.remove(mCounterInner);
                        notifyItemRemoved(mCounterInner);
                        notifyItemRangeChanged(mCounterInner,mModelList.size());
                        mEvent.invokeObserver(Collections.singletonList(mCounterInner), tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW_RETAIN_BACKUP);
                        mCounter=-1;
                        break;
                    }
                }
            }

            mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_SHOW_UNDO_DIALOG);
        }
    }

    private void onNotifyItemSwiped(int pIndex){
        for(int mCounter=0;mCounter<mSelectedList.size();mCounter++){
            if(mSelectedList.get(mCounter).equals(mModelList.get(pIndex).getmId())){
                mSelectedList.remove(mCounter);
                break;
            }
        }

        mModelList.remove(pIndex);
        notifyItemRemoved(pIndex);
        notifyItemRangeChanged(pIndex, mModelList.size());
        notifyItemChanged(mModelList.size()-1);
    }

    private void onRemoveAll(){
        int mSize = mModelList.size()-1;
        for(int mCounter=0;mCounter<mSize;mCounter++){
            mModelList.remove(0);
            notifyDataSetChanged();
            mEvent.invokeObserver(Collections.singletonList(0), tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW_RETAIN_BACKUP);
        }

        mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_SHOW_UNDO_DIALOG);
    }

    private void onClearAllSelection(){
        mEvent.invokeObserver(Arrays.asList(false, mSelectedList.size()), tabEnums.eTabAdapterCallback.ON_SHOW_SELECTION_MENU);

        for(int mCounter=0;mCounter<mSelectedList.size();mCounter++){
            boolean mIsChanged = false;
            for(int mCounterInner=0;mCounterInner<mModelList.size();mCounterInner++){
                if(mSelectedList.get(mCounter).equals(mModelList.get(mCounterInner).getmId())){
                    notifyItemChanged(mCounterInner, null);
                    mIsChanged = true;
                }
            }
            if(mIsChanged){
                mSelectedList.remove(mCounter);
                mCounter=mCounter-1;
            }
        }

        if(mSelectedList.size()<=0){
            mEvent.invokeObserver(Arrays.asList(true, mSelectedList.size()), tabEnums.eTabAdapterCallback.ON_HIDE_SELECTION);
        }else{
            mEvent.invokeObserver(Arrays.asList(true, mSelectedList.size()), tabEnums.eTabAdapterCallback.ON_SHOW_SELECTION_MENU);
        }
        notifyItemChanged(mModelList.size()-1);

    }

    public void onEnableLongClickMenu(){
        mLongPressMenuEnabled = true;
    }

    private void onSelectionCreate(FrameLayout mSelectedView){
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
        if(model.getSession()!=null){
            mEvent.invokeObserver(Arrays.asList(model.getSession(), false), tabEnums.eTabAdapterCallback.ON_LOAD_TAB);
            mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_BACK_PRESSED);
            mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_INIT_TAB_COUNT);
        }
    }

    private int getSelectionSize(){
        return mSelectedList.size();
    }

    private void onRemoveRowCross(int mIndex){
        for(int mCounter=0;mCounter<mSelectedList.size();mCounter++){
            if(mSelectedList.get(mCounter).equals(mModelList.get(mIndex).getmId())){
                mSelectedList.remove(mCounter);
                break;
            }
        }

        mModelList.remove(mIndex);
        if(mModelList.size()!=1){
            notifyItemRemoved(mIndex);
            notifyItemChanged(mModelList.size()-1);
            notifyItemRangeChanged(mIndex, mModelList.size());
        }

        mEvent.invokeObserver(Collections.singletonList(mIndex), tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW);
        mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_SHOW_UNDO_DIALOG);
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
        FrameLayout mItemSelectionMenu;
        Button mItemSelectionMenuButton;
        ConstraintLayout mItemSelectionMenuReference;
        ImageView mBorder;

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
            mItemSelectionMenu = itemView.findViewById(R.id.pItemSelectionMenu);
            mItemSelectionMenuButton = itemView.findViewById(R.id.pItemSelectionMenuButton);
            mItemSelectionMenuReference = itemView.findViewById(R.id.pRowContainer);
            mBorder = itemView.findViewById(R.id.pBorder);

            itemView.setClickable(true);
            mRemoveRow.setEnabled(true);

            if(model.getmId()==null){
                mItemSelectionMenu.setVisibility(View.VISIBLE);
                mItemSelectionMenuButton.setOnClickListener(this);
            }else {
                if(model.getSession().getTheme()==null){
                    mBorder.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.c_ripple_v2));
                }else {
                    try{
                        mBorder.setBackgroundColor(Color.parseColor(model.getSession().getTheme()));
                    }catch (Exception ignored){}
                }

                String mURL = model.getSession().getCurrentURL();
                if(mURL.startsWith(CONST_GENESIS_URL_CACHED) || mURL.startsWith(CONST_GENESIS_URL_CACHED_DARK)){
                    mURL = CONST_GENESIS_DOMAIN_URL;
                }
                else if(mURL.startsWith(CONST_GENESIS_HELP_URL_CACHE) || mURL.startsWith(CONST_GENESIS_HELP_URL_CACHE_DARK)){
                    mURL = CONST_GENESIS_HELP_URL;
                }

                mItemSelectionMenu.setVisibility(View.GONE);
                if(model.getSession().getTitle().equals("$TITLE") || model.getSession().getTitle().toLowerCase().equals("loading")){
                    mHeader.setText(helperMethod.getDomainName(mURL));
                }else {
                    mHeader.setText(model.getSession().getTitle());
                }

                if(status.sTabGridLayoutEnabled){
                    mDescription.setText(helperMethod.getDomainName(mURL));
                }else {
                    mDescription.setText(mURL);
                }
                mDate.setText(model.getDate());
                mWebThumbnail.setImageBitmap(model.getBitmap());

                if(getLayoutPosition()==0){
                   // mEvent.invokeObserver(Arrays.asList(mWebThumbnail, mURL), enums.etype.fetch_thumbnail);
                }

                if(mSelectedList.contains(model.getSession().getSessionID())){
                    onSelectionCreate(mSelectedView);
                }else {
                    onSelectionClear(mSelectedView);
                }

                if(model.getSession().equals(mModelList.get(0).getSession())){
                    if(!status.sTabGridLayoutEnabled){
                        itemView.setBackgroundColor(ContextCompat.getColor(activityContextManager.getInstance().getHomeController(), R.color.c_list_item_current));
                    }
                }else {
                    Drawable mDrawable;
                    Resources res = itemView.getContext().getResources();
                    try {
                        mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.hx_border));
                        itemView.setBackground(mDrawable);
                    } catch (Exception ignored) {
                    }
                }
                mLoadSession.setOnLongClickListener(this);
                mRemoveRow.setOnClickListener(this);
                mLoadSession.setOnClickListener(this);
            }

            if(this.getLayoutPosition()==mModelList.size()-1){
                if(mSelectedList.size()>0){
                    itemView.setVisibility(View.GONE);
                    mLongPressMenuEnabled = true;
                }else {
                    itemView.setVisibility(View.VISIBLE);
                    mLongPressMenuEnabled = false;
                    mItemSelectionMenuButton.animate().cancel();
                    mItemSelectionMenuButton.animate().setDuration(250).alpha(1);
                }
            }else {
                if(model.getmId()!=null){
                    mItemSelectionMenuReference.animate().cancel();
                }
                itemView.setVisibility(View.VISIBLE);
                mLongPressMenuEnabled = false;
                mItemSelectionMenuButton.animate().setDuration(250).alpha(1);
            }
            mRemoveRow.bringToFront();
        }


        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.pLoadSession){
                if(mSelectedView.getVisibility() == View.GONE){
                    if(mLongPressMenuEnabled){
                        mSelectedList.add(mModelList.get(this.getLayoutPosition()).getSession().getSessionID());
                        mEvent.invokeObserver(Arrays.asList(true, mSelectedList.size()), tabEnums.eTabAdapterCallback.ON_SHOW_SELECTION_MENU);
                        onSelectionCreate(mSelectedView);
                        notifyItemChanged(mModelList.size()-1);

                    }else {
                        onTriggerURL(mModelList.get(this.getLayoutPosition()));
                    }
                }else {
                    for(int mCounter=0;mCounter<mSelectedList.size();mCounter++){
                        if(mSelectedList.get(mCounter).equals(mModelList.get(this.getLayoutPosition()).getSession().getSessionID())){
                            mSelectedList.remove(mCounter);
                            if(mSelectedList.size()<=0){
                                mEvent.invokeObserver(Arrays.asList(true, mSelectedList.size()), tabEnums.eTabAdapterCallback.ON_HIDE_SELECTION);
                                onClearAllSelection();
                            }else{
                                mEvent.invokeObserver(Arrays.asList(true, mSelectedList.size()), tabEnums.eTabAdapterCallback.ON_SHOW_SELECTION_MENU);
                            }
                        }
                    }
                    onSelectionClear(mSelectedView);
                    notifyItemChanged(mModelList.size()-1);
                }
            }else if(v.getId() == R.id.pRemoveRow){
                v.setEnabled(false);
                onRemoveRowCross(this.getLayoutPosition());
            }
            else if(v.getId() == R.id.pItemSelectionMenuButton){
                onEnableLongClickMenu();
                v.animate().cancel();
                v.animate().setDuration(350).alpha(0);
                mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_SHOW_SELECTION);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(v.getId() == R.id.pLoadSession){
                if(mSelectedView.getVisibility() == View.GONE){
                    mSelectedList.add(mModelList.get(this.getLayoutPosition()).getSession().getSessionID());
                    onSelectionCreate(mSelectedView);
                    notifyItemChanged(mModelList.size()-1);
                    mEvent.invokeObserver(Arrays.asList(false, mSelectedList.size()), tabEnums.eTabAdapterCallback.ON_SHOW_SELECTION_MENU);
                }else {
                    v.performClick();
                }
                mLoadSession.setPressed(false);
            }
            return true;
        }
    }


    public Object onTrigger(tabEnums.eTabAdapterCommands pCommands, List<Object> pData){
        if(pCommands.equals(tabEnums.eTabAdapterCommands.M_SELECTION_MENU_SHOWING)){
            return isSelectionMenuShowing();
        }else if(pCommands.equals(tabEnums.eTabAdapterCommands.M_REMOVE_ALL_SELECTION)){
            onRemoveAllSelection();
        }else if(pCommands.equals(tabEnums.eTabAdapterCommands.M_CLEAR_ALL_SELECTION)){
            onClearAllSelection();
        }else if(pCommands.equals(tabEnums.eTabAdapterCommands.ENABLE_LONG_CLICK_MENU)){
            onEnableLongClickMenu();
        }else if(pCommands.equals(tabEnums.eTabAdapterCommands.INIT_FIRST_ROW)){
            initFirstRow();
        }else if(pCommands.equals(tabEnums.eTabAdapterCommands.REINIT_DATA)){
            reInitData((ArrayList<tabRowModel>)pData.get(0));
        }else if(pCommands.equals(tabEnums.eTabAdapterCommands.NOTIFY_SWIPE)){
            onNotifyItemSwiped((int)pData.get(0));
        }else if(pCommands.equals(tabEnums.eTabAdapterCommands.GET_SELECTION_SIZE)){
            return getSelectionSize();
        }else if(pCommands.equals(tabEnums.eTabAdapterCommands.REMOVE_ALL)){
            onRemoveAll();
        }else if(pCommands.equals(tabEnums.eTabAdapterCommands.REMOVE_ROW_CROSSED)){
            onRemoveRowCross((int)pData.get(0));
        }
        return null;
    }
}


