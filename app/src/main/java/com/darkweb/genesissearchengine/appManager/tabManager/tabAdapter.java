package com.darkweb.genesissearchengine.appManager.tabManager;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.dataManager.models.tabRowModel;
import com.darkweb.genesissearchengine.libs.views.ThumbnailCrop;
import com.darkweb.genesissearchengine.eventObserver;
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
    private boolean mViewLoaded = false;
    private ObjectAnimator mFirstRow = null;


    tabAdapter(ArrayList<tabRowModel> pModelList, eventObserver.eventListener event) {
        initialize(pModelList);
        this.mEvent = event;
    }

    private void initialize(ArrayList<tabRowModel> pModelList){
        this.mModelList.clear();
        this.mModelList.addAll(pModelList);
        mModelList.add(new tabRowModel(null, null,null));
        mViewLoaded = false;
    }


    private void reInitData(ArrayList<tabRowModel> pModelList){
        mModelList.addAll(0, pModelList);
        notifyItemRangeInserted(0,pModelList.size());
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
        holder.itemView.findViewById(R.id.pOrbotRowRemove).setTag(position);
        holder.itemView.findViewById(R.id.pLoadSession).setTag(position);

        if(position == 0 && status.sTabGridLayoutEnabled && !mViewLoaded){

        }else{
        }
    }

    public void scaleView(View v, tabRowModel mTabRowModel) {

        mEvent.invokeObserver(Arrays.asList(mTabRowModel.getSession(), false), tabEnums.eTabAdapterCallback.ON_LOAD_TAB);
        onTriggerURL(mTabRowModel);

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
        if(mSelectedList.size()>0){
            int mSelectionInitialSize = mSelectedList.size();
            for(int mCounter=0;mCounter<mSelectedList.size();mCounter++){
                for(int mCounterInner=0;mCounterInner<mModelList.size();mCounterInner++){
                    if(mSelectedList.get(mCounter).equals(mModelList.get(mCounterInner).getmId())){
                        mSelectedList.remove(mCounter);
                        mModelList.remove(mCounterInner);
                        notifyItemRemoved(mCounterInner);
                        notifyItemRangeChanged(mCounterInner,mModelList.size());
                        if(mSelectionInitialSize == 1){
                            mEvent.invokeObserver(Arrays.asList(mCounterInner, true), tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW_RETAIN_BACKUP);
                        }else {
                            mEvent.invokeObserver(Arrays.asList(mCounterInner, false), tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW_RETAIN_BACKUP);
                        }
                        mCounter=-1;
                        break;
                    }
                }
            }
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
        if(mSize==1){
            mModelList.remove(0);
            notifyDataSetChanged();
            mEvent.invokeObserver(Arrays.asList(0, true), tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW_RETAIN_BACKUP);
        }else {
            for(int mCounter=0;mCounter<mSize;mCounter++){
                mModelList.remove(0);
                mEvent.invokeObserver(Arrays.asList(0, false), tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW_RETAIN_BACKUP);
            }
            mEvent.invokeObserver(Arrays.asList(0, true), tabEnums.eTabAdapterCallback.ON_SHOW_UNDO_POPUP);
            notifyDataSetChanged();
        }
    }

    private void onClearAllSelection(){
        mLongPressMenuEnabled = false;
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
        //if(mSelectedList.size()==0){
        //    mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_HIDE_SELECTION);
         //   mLongPressMenuEnabled = false;
        //}
    }

    private void onTriggerURL(tabRowModel model){
        if(model.getSession()!=null){
            mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_BACK_PRESSED);
            mEvent.invokeObserver(null, tabEnums.eTabAdapterCallback.ON_INIT_TAB_COUNT);
            mEvent.invokeObserver(Arrays.asList(model.getSession(), false), tabEnums.eTabAdapterCallback.ON_LOAD_TAB);
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
        if(mSelectedList.size()<=0){
            mEvent.invokeObserver(Arrays.asList(true, mSelectedList.size()), tabEnums.eTabAdapterCallback.ON_HIDE_SELECTION);
            onClearAllSelection();
        }

        mModelList.remove(mIndex);
        notifyItemRemoved(mIndex);
        notifyItemRangeChanged(mIndex, mModelList.size());

        mEvent.invokeObserver(Collections.singletonList(mIndex), tabEnums.eTabAdapterCallback.M_CLEAR_BACKUP);
        mEvent.invokeObserver(Collections.singletonList(mIndex), tabEnums.eTabAdapterCallback.ON_REMOVE_TAB_VIEW);
    }

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        TextView mHeader;
        TextView mDescription;
        TextView mDate;
        ImageView mLoadSession;
        ImageButton mRemoveRow;
        ThumbnailCrop mWebThumbnail;
        FrameLayout mSelectedView;
        FrameLayout mItemSelectionMenu;
        Button mItemSelectionMenuButton;
        ConstraintLayout mItemSelectionMenuReference;
        ImageView mLogo;
        ImageView mBorder;

        listViewHolder(View itemView) {
            super(itemView);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        void bindListView(tabRowModel model) {
            mHeader = itemView.findViewById(R.id.pOrbotRowHeader);
            mDescription = itemView.findViewById(R.id.pOrbotRowDescription);
            mLoadSession = itemView.findViewById(R.id.pLoadSession);
            mRemoveRow = itemView.findViewById(R.id.pOrbotRowRemove);
            mWebThumbnail = itemView.findViewById(R.id.pWebThumbnail);
            mDate = itemView.findViewById(R.id.pDate);
            mSelectedView = itemView.findViewById(R.id.pSelectedView);
            mItemSelectionMenu = itemView.findViewById(R.id.pItemSelectionMenu);
            mItemSelectionMenuButton = itemView.findViewById(R.id.pItemSelectionMenuButton);
            mItemSelectionMenuReference = itemView.findViewById(R.id.pRowContainer);
            mBorder = itemView.findViewById(R.id.pBorder);

            if(status.sTabGridLayoutEnabled){
                mLogo = itemView.findViewById(R.id.pLogo);
            }

            itemView.setScaleX(1);
            itemView.setScaleY(1);
            itemView.setClickable(true);
            itemView.setFocusable(true);
            itemView.setEnabled(true);
            mRemoveRow.setEnabled(true);

            if(model.getmId()==null){
                mItemSelectionMenu.setVisibility(View.VISIBLE);
                mItemSelectionMenuButton.setOnClickListener(this);

                if(status.sTabGridLayoutEnabled){
                    itemView.setVisibility(View.GONE);
                    itemView.setClickable(false);
                    itemView.setFocusable(false);
                    itemView.setEnabled(false);
                    return;
                }

            }else {
                mLoadSession.setOnLongClickListener(this);
                mRemoveRow.setOnClickListener(this);
                mLoadSession.setOnClickListener(this);

                if(model.getSession().getTheme()==null){
                    if(status.sTabGridLayoutEnabled){
                        mBorder.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.c_ripple_gray));
                    }else {
                        mBorder.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.c_ripple_gray));
                    }
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

                if(status.sTabGridLayoutEnabled){
                    mDescription.setText((model.getSession().getTitle()));
                }else {
                    mHeader.setText(model.getSession().getTitle());
                    mDescription.setText(mURL);
                }

                String mHeadText = mHeader.getText().toString();
                String mDescText = mDescription.getText().toString();

                if(status.sTabGridLayoutEnabled){
                    if(model.getSession().getCurrentURL().contains("genesishiddentechnologies.com") || model.getSession().getCurrentURL().contains("genesis.onion")){
                        mLogo.setImageDrawable(itemView.getResources().getDrawable(R.drawable.genesis));
                    }
                    else{
                        if(mLogo.getDrawable() == null){
                            mEvent.invokeObserver(Arrays.asList(mLogo, "https://" + helperMethod.getDomainName(model.getSession().getCurrentURL())), enums.etype.fetch_favicon);
                        }
                    }
                }

                if(mHeadText.equals("$TITLE") || mDescText.startsWith("http://loading") || mDescText.startsWith("loading")){
                    mHeader.setText("about:blank");
                }

                if(mHeadText.startsWith("resource")){
                    if(mHeadText.equals(CONST_GENESIS_URL_CACHED) || mHeadText.equals(CONST_GENESIS_URL_CACHED_DARK)){
                        mHeader.setText(CONST_GENESIS_DOMAIN_URL);
                    }
                    else if(mHeadText.equals(CONST_GENESIS_HELP_URL_CACHE) || mHeadText.equals(CONST_GENESIS_HELP_URL_CACHE_DARK)){
                        mHeader.setText(CONST_GENESIS_HELP_URL);
                    }
                }

                if(mDescText.equals("$TITLE") || mDescText.startsWith("http://loading") || mDescText.startsWith("loading")){
                    mDescription.setText(mURL);
                }

                mDate.setText(model.getDate());

                if(model.getSession().getTitle().equals("about:blank")){
                    mWebThumbnail.setAlpha(0f);
                }else {
                    new Handler().postDelayed(() ->
                    {
                        if(mWebThumbnail.getDrawable()==null){
                            mWebThumbnail.setImageBitmap(model.getBitmap());
                        }else {
                            Drawable mDrawable = new BitmapDrawable(itemView.getContext().getResources(), model.getBitmap());
                            helperMethod.setImageDrawableWithAnimation(mWebThumbnail, mDrawable,150);
                        }
                        Log.i("SUPERFFF", "SUPERFFF : " + getLayoutPosition());
                    }, getLayoutPosition());
                }

                if(mSelectedList.contains(model.getSession().getSessionID())){
                    onSelectionCreate(mSelectedView);
                }else {
                    onSelectionClear(mSelectedView);
                }

                //if(!model.getSession().equals(mModelList.get(0).getSession()) && !status.sTabGridLayoutEnabled){
                    Drawable mDrawable;
                    Resources res = itemView.getContext().getResources();
                    try {
                        mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.hx_border));
                        itemView.setBackground(mDrawable);
                    } catch (Exception ignored) {
                    }
                //}
            }

            new Handler().postDelayed(() ->
            {
                itemView.setBackgroundColor(ContextCompat.getColor(activityContextManager.getInstance().getHomeController(), R.color.clear_alpha));
                if(!status.sTabGridLayoutEnabled){
                    if(getLayoutPosition() == 0){
                        itemView.setBackgroundColor(ContextCompat.getColor(activityContextManager.getInstance().getHomeController(), R.color.c_list_item_current));
                    }
                }

            if(status.sTabGridLayoutEnabled){
                CardView mLayout = itemView.findViewById(R.id.pTABRowContainer);
                CardView mCardView = itemView.findViewById(R.id.pCardViewParent);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLayout.getLayoutParams();
                ViewGroup.MarginLayoutParams params_main = (ViewGroup.MarginLayoutParams) mItemSelectionMenuReference.getLayoutParams();

                params_main.leftMargin = helperMethod.pxFromDp(2f);
                params_main.rightMargin = helperMethod.pxFromDp(2f);
                params_main.topMargin = helperMethod.pxFromDp(2f);
                params_main.bottomMargin = helperMethod.pxFromDp(0f);

                if(getLayoutPosition() == 0){
                    params.leftMargin = helperMethod.pxFromDp(3.5f);
                    params.rightMargin = helperMethod.pxFromDp(3.5f);
                    params.topMargin = helperMethod.pxFromDp(3.5f);
                    params.bottomMargin = helperMethod.pxFromDp(3.5f);
                    params_main.topMargin = helperMethod.pxFromDp(3f);

                    if(status.sTheme == enums.Theme.THEME_DARK || status.sDefaultNightMode){
                        mCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.c_button_text_v1_inverted));
                    }else {
                        mCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.c_alert_rateus_header));
                    }

                }else {

                    if(status.sTheme == enums.Theme.THEME_DARK || status.sDefaultNightMode){
                        params.leftMargin = helperMethod.pxFromDp(2.5f);
                        params.rightMargin = helperMethod.pxFromDp(2.5f);
                        params.topMargin = helperMethod.pxFromDp(2.5f);
                        params.bottomMargin = helperMethod.pxFromDp(2.5f);

                        mCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.c_tab_background));
                    }else {
                        params.leftMargin = helperMethod.pxFromDp(2.5f);
                        params.rightMargin = helperMethod.pxFromDp(2.5f);
                        params.topMargin = helperMethod.pxFromDp(2.5f);
                        params.bottomMargin = helperMethod.pxFromDp(2.5f);

                        mCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.clear_alpha));
                    }

                }

            }

            if(this.getLayoutPosition()==mModelList.size()-1){
                if(mSelectedList.size()>0){
                    itemView.setVisibility(View.GONE);
                }else {
                    itemView.setVisibility(View.VISIBLE);
                    mItemSelectionMenuButton.animate().cancel();
                    mItemSelectionMenuButton.animate().setDuration(250).alpha(1);
                }
            }else {
                itemView.setVisibility(View.VISIBLE);
                mItemSelectionMenuButton.animate().setDuration(250).alpha(1);
            }
            mRemoveRow.bringToFront();
            }, 10 * getLayoutPosition());

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
                        if(v.getAlpha()==1){
                            if(status.sTabGridLayoutEnabled){
                                v.setEnabled(false);
                                v.setClickable(false);
                                v.setFocusable(false);
                                scaleView(itemView, mModelList.get(this.getLayoutPosition()));
                            }else {
                                onTriggerURL(mModelList.get(this.getLayoutPosition()));
                            }
                        }
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
            }else if(v.getId() == R.id.pOrbotRowRemove){
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
                onEnableLongClickMenu();
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
        }else if(pCommands.equals(tabEnums.eTabAdapterCommands.M_INITIALIZE)){
            initialize((ArrayList<tabRowModel>) pData.get(0));
        }
        return null;
    }
}


