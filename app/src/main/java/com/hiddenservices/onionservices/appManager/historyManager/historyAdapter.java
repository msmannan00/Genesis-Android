package com.hiddenservices.onionservices.appManager.historyManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.dataManager.models.historyRowModel;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;

@SuppressLint("NotifyDataSetChanged")
public class historyAdapter extends RecyclerView.Adapter<historyAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<historyRowModel> mModelList = new ArrayList<>();
    private ArrayList<historyRowModel> mCurrentList;
    private ArrayList<historyRowModel> mPassedList;
    private ArrayList<Integer> mRealID = new ArrayList<>();
    private ArrayList<Integer> mRealIndex = new ArrayList<>();
    private ArrayList<Date> mLongSelectedDate = new ArrayList<>();
    private ArrayList<String> mLongSelectedIndex = new ArrayList<>();
    private ArrayList<Integer> mLongSelectedID = new ArrayList<>();

    private AppCompatActivity mContext;
    private historyAdapterView mHistroyAdapterView;
    private Context mListHolderContext;
    private PopupWindow mPopupWindow = null;
    private eventObserver.eventListener mEvent;
    private String mFilter = strings.GENERIC_EMPTY_STR;
    private boolean mLongPressedMenuActive = false;

    /*Local Variables*/

    private boolean mDisableCallable = false;
    private boolean mSearchEnabled = false;

    historyAdapter(ArrayList<historyRowModel> pModelList, eventObserver.eventListener mEvent, AppCompatActivity pMainContext) {
        this.mEvent = mEvent;
        this.mCurrentList = new ArrayList<>();
        this.mPassedList = pModelList;
        this.mContext = pMainContext;
        this.mHistroyAdapterView = new historyAdapterView(mContext);

        initializeModelWithDate(false);
    }

    public void onLoadMore(ArrayList<historyRowModel> pModelList){
        //notifyDataSetChanged();
        initializeModelWithDate(false);
    }


    private void initializeModelWithDate(boolean pFilterEnabled){
        int m_real_counter=0;

        mRealID.clear();
        mRealIndex.clear();
        mCurrentList.clear();
        this.mModelList.clear();
        onVerifyLongSelectedURL(true);

        ArrayList<historyRowModel> p_model_list = mPassedList;
        int m_date_state = -1;
        int m_last_day = -1;
        for(int counter = 0; counter< p_model_list.size(); counter++){

            if(pFilterEnabled){
                if(!p_model_list.get(counter).getHeader().toLowerCase().contains(this.mFilter.toLowerCase()) && !p_model_list.get(counter).getDescription().toLowerCase().contains(this.mFilter)){
                    continue;
                }
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(p_model_list.get(counter).getDate());

            int m_date_1 = cal.get(Calendar.DAY_OF_YEAR);
            cal.setTime(Calendar.getInstance().getTime());
            int m_date_2 = cal.get(Calendar.DAY_OF_YEAR);

            float diff = m_date_2-m_date_1;

            if(diff==0){
                if(m_date_state!=1 && p_model_list.get(counter).getID()!=-2){
                    this.mModelList.add(new historyRowModel("Today ",null,-1));
                    mRealID.add(m_real_counter);
                    mRealIndex.add(counter);
                    m_date_state = 1;
                }
            }else if (diff>=1){

                if(m_date_state!=2 || m_last_day!=(int)(Math.ceil(diff/7)*7)){
                    m_last_day = (int)(Math.ceil(diff/7)*7);
                    this.mModelList.add(new historyRowModel("Last " + m_last_day + " Days",null,-1));
                    mRealID.add(m_real_counter);
                    mRealIndex.add(counter);
                    m_date_state = 2;
                }
            }else {
                if(m_date_state!=3){
                    this.mModelList.add(new historyRowModel("Older ",null,-1));
                    mRealID.add(m_real_counter);
                    mRealIndex.add(counter);
                    m_date_state = 3;
                }
            }

            mRealID.add(p_model_list.get(counter).getID());
            mRealIndex.add(counter);
            this.mModelList.add(p_model_list.get(counter));
            m_real_counter+=1;
        }
        mCurrentList.addAll(this.mModelList);
    }

    /*Initializations*/

    private ArrayList<String> getLongSelectedleURL(){
        return mLongSelectedIndex;
    }

    public void onDeleteSelected(){
        for(int m_counter = 0; m_counter< mLongSelectedIndex.size(); m_counter++){
            for(int m_counter_inner = 0; m_counter_inner< mCurrentList.size(); m_counter_inner++){
                if(mCurrentList.get(m_counter_inner).getDate() == mLongSelectedDate.get(m_counter) && mLongSelectedIndex.get(m_counter).equals("https://"+ mCurrentList.get(m_counter_inner).getDescription())){
                    mEvent.invokeObserver(Collections.singletonList(mRealIndex.get(m_counter_inner)),enums.etype.url_clear);
                    mEvent.invokeObserver(Collections.singletonList(mLongSelectedID.get(m_counter)),enums.etype.url_clear_at);
                    invokeFilter(false);
                    mEvent.invokeObserver(Collections.singletonList(m_counter_inner),enums.etype.is_empty);

                    boolean mDateVerify = false;
                    if(mCurrentList.size()>0 && mCurrentList.size()<m_counter_inner+1 && mCurrentList.get(m_counter_inner-1).getDescription()==null && (mCurrentList.size()>m_counter_inner+1 && mCurrentList.get(m_counter_inner+1).getDescription()==null || mCurrentList.size()==m_counter_inner+1)){
                        mDateVerify = true;
                    }

                    if(m_counter_inner==0){
                        //notifyDataSetChanged();
                    }else {

                        if(mDateVerify){
                            notifyItemRemoved(m_counter_inner-1);
                            mCurrentList.remove(m_counter_inner-1);
                            notifyItemRangeChanged(m_counter_inner-1, mCurrentList.size());
                        }else {
                            notifyItemRemoved(m_counter_inner);
                            mCurrentList.remove(m_counter_inner);
                            notifyItemRangeChanged(m_counter_inner, mCurrentList.size());
                        }
                    }
                    break;
                }
            }
        }
        clearLongSelectedURL();
    }

    private void clearLongSelectedURL(){
        for(int m_counter = 0; m_counter< mCurrentList.size(); m_counter++){
            for(int m_counter_inner = 0; m_counter_inner< mLongSelectedID.size(); m_counter_inner++){
                if(mCurrentList.get(m_counter).getID() == mLongSelectedID.get(m_counter_inner)){
                    mLongSelectedID.remove(m_counter_inner);
                    notifyItemChanged(m_counter);
                    m_counter-=1;
                    break;
                }
            }
        }

        mLongSelectedDate.clear();
        mLongSelectedIndex.clear();
        mLongSelectedID.clear();
    }

    private String getSelectedURL(){
        String m_joined_url = "\n";
        for(int m_counter = 0; m_counter< mLongSelectedIndex.size(); m_counter++){
            m_joined_url = m_joined_url.concat("\n"+ mLongSelectedIndex.get(m_counter));
        }
        return m_joined_url;
    }

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mListHolderContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_bookmark_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull historyAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(mCurrentList.get(position), position);
    }

    public int getItem(){
        return mCurrentList.size();
    }

    @Override
    public int getItemCount() {
        return mCurrentList.size();
    }

    /*Listeners*/
    public void onUpdateSearchStatus(boolean p_is_searched){
        mSearchEnabled = p_is_searched;
    }

    public void onSelectView(View pItemView, String pUrl, View pMenuItem, ImageView pLogoImage, boolean pIsForced, int pId, Date pDate){
        if(!mSearchEnabled){
            try {
                mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(historyEnums.eHistoryViewAdapterCommands.M_SELECT_VIEW, Arrays.asList(pItemView, pMenuItem, pLogoImage, pIsForced, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!pIsForced){
                mLongSelectedDate.add(pDate);
                mLongSelectedIndex.add(pUrl);
                mLongSelectedID.add(pId);
                if(mLongSelectedIndex.size()<=1){
                    notifyDataSetChanged();
                }
            }
            onVerifyLongSelectedURL(false);
            pItemView.clearFocus();
            pItemView.setPressed(false);
            pItemView.setVisibility(View.GONE);
            pItemView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isSwipable(int mIndex){
        if(mCurrentList.get(mIndex).getID() == -1){
            return false;
        }else {
            return true;
        }
    }

    public void onVerifyLongSelectedURL(boolean pIsComputing){
        if(mLongSelectedIndex.size()>0){
            mEvent.invokeObserver(Collections.singletonList(false),enums.etype.on_verify_selected_url_menu);
        }else {
            if(!pIsComputing){
                notifyDataSetChanged();
            }
            mEvent.invokeObserver(Collections.singletonList(true),enums.etype.on_verify_selected_url_menu);
        }
    }

    public void onClearHighlight(View pItemView, String pUrl, View pMenuItem, ImageView pLogoImage, boolean pIsForced, int pId, Date pDate)
    {
        try {
            mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(historyEnums.eHistoryViewAdapterCommands.M_CLEAR_HIGHLIGHT, Arrays.asList(pItemView, pMenuItem, pLogoImage, pIsForced));
            mLongSelectedDate.remove(pDate);
            mLongSelectedIndex.remove(pUrl);
            mLongSelectedID.remove((Integer) pId);
            onVerifyLongSelectedURL(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClearAdapter(){

    }

    public void notifyFilter(){
        if(mFilter.length()>0){
            initializeModelWithDate(true);
            notifyDataSetChanged();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onSwipe(View pItemView, String pUrl, View pMenuItem, ImageView pLogoImage, int pId, Date pDate){

        Handler handler = new Handler();

        Runnable mLongPressed = () -> {
            if(!mDisableCallable){
                if(!mLongSelectedIndex.contains(pUrl) || !mLongSelectedID.contains(pId)) {
                    mLongPressedMenuActive = true;
                    onSelectView(pItemView, pUrl,pMenuItem, pLogoImage, false, pId, pDate);
                }else {
                    onClearHighlight(pItemView, pUrl,pMenuItem, pLogoImage, false, pId, pDate);
                    mLongPressedMenuActive = true;
                }
            }else {
                pItemView.setPressed(false);
                pItemView.clearFocus();
            }

            if(mFilter.length()>0){
                initializeModelWithDate(true);
                notifyDataSetChanged();
            }
        };

         pItemView.setOnTouchListener((v, event) -> {

             if (event.getAction() == MotionEvent.ACTION_UP) {

                 if (mLongSelectedIndex.size() > 0) {
                     if (!mLongPressedMenuActive) {
                         if (mLongSelectedIndex.contains(pUrl) && mLongSelectedID.contains(pId)) {
                             handler.removeCallbacks(mLongPressed);
                             historyAdapter.this.onClearHighlight(pItemView, pUrl, pMenuItem, pLogoImage, false, pId, pDate);
                         } else{
                             handler.removeCallbacks(mLongPressed);
                             historyAdapter.this.onSelectView(pItemView, pUrl, pMenuItem, pLogoImage, false, pId, pDate);
                         }
                     }

                     initializeModelWithDate(true);
                     return false;
                 }

                 v.setPressed(false);
                 handler.removeCallbacks(mLongPressed);
                 mEvent.invokeObserver(Collections.singletonList(pUrl), enums.etype.url_triggered);
                 initializeModelWithDate(true);
                 return true;

             } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                 mDisableCallable = false;
                 mLongPressedMenuActive = false;
                 v.setPressed(true);
                 handler.postDelayed(mLongPressed, ViewConfiguration.getLongPressTimeout());
                 initializeModelWithDate(true);
                 return true;
             } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                 handler.removeCallbacks(mLongPressed);
                 mDisableCallable = true;
                 if (!mLongSelectedIndex.contains(pUrl) || !mLongSelectedID.contains(pId)) {
                     v.setPressed(false);
                 }
                 handler.removeCallbacks(mLongPressed);

                 initializeModelWithDate(true);
                 return true;
             }
             initializeModelWithDate(true);
             return false;

         });
        //initializeModelWithDate(false);
    }

    void onOpenMenu(View pView, String pUrl, int pPosition, String pTitle){
        LayoutInflater layoutInflater = (LayoutInflater) pView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View mPopupView = layoutInflater.inflate(R.layout.history_bookmark__row_menu, null);
        mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(historyEnums.eHistoryViewAdapterCommands.M_OPEN_MENU, Arrays.asList(mPopupWindow, pView, mPopupView));

        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuCopy), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuShare), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuOpenCurrentTab), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuOpenNewTab), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuDelete), pUrl, pPosition, pTitle);

        if(mFilter.length()>0){
            initializeModelWithDate(true);
            notifyDataSetChanged();
        }
    }

    public void invokeSwipeClose(int pPosition){
        onClose(pPosition);
        invokeFilter(true);

        if(mPopupWindow!=null){
            mPopupWindow.dismiss();
        }
    }

    public void setPopupWindowEvents(View pView, String pUrl, int pPosition, String pTitle){
        pView.setOnClickListener(v -> {
            if(v.getId() == R.id.pMenuCopy){
                helperMethod.copyURL(pUrl, mListHolderContext);
                mPopupWindow.dismiss();
            }
            else if(v.getId() == R.id.pMenuShare){
                helperMethod.shareApp((AppCompatActivity)mListHolderContext, pUrl, pTitle);
                mPopupWindow.dismiss();
            }
            else if(v.getId() == R.id.pMenuOpenCurrentTab){
                mEvent.invokeObserver(Collections.singletonList(pUrl),enums.etype.url_triggered);
                mPopupWindow.dismiss();
            }
            else if(v.getId() == R.id.pMenuOpenNewTab){
                mEvent.invokeObserver(Collections.singletonList(pUrl),enums.etype.url_triggered_new_tab);
                mPopupWindow.dismiss();
            }
            else if(v.getId() == R.id.pMenuDelete){
                onClose(pPosition);
                invokeFilter(true);
                mPopupWindow.dismiss();
            }
        });
    }

    private void setItemViewOnClickListener(View pItemView, View pItemMenu, String pUrl, int pPosition, String pTitle, View pMenuItem, ImageView pLogoImage, int pId, Date pDate)
    {
        pItemMenu.setOnClickListener((View v) -> onOpenMenu(v, pUrl, pPosition, pTitle));
        onSwipe(pItemView, pUrl,pMenuItem, pLogoImage, pId, pDate);
    }

    private void onClose(int pIndex){
        mEvent.invokeObserver(Collections.singletonList(mRealIndex.get(pIndex)),enums.etype.url_clear);
        mEvent.invokeObserver(Collections.singletonList(mRealID.get(pIndex)),enums.etype.url_clear_at);
        mEvent.invokeObserver(Collections.singletonList(mRealID.get(pIndex)),enums.etype.is_empty);

        if(mPassedList.size()<=0){
            mCurrentList.clear();
            return;
        }

        mCurrentList.remove(pIndex);
        notifyItemRemoved(pIndex);
        notifyItemRangeChanged(0, mCurrentList.size());
        clearLongSelectedURL();
    }

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder
    {
        TextView mHeader;
        TextView mDescription;
        TextView mDate;
        TextView mWebLogo;
        ImageButton mRowMenu;
        ImageView mLogoImage;
        ImageView mFaviconLogo;
        LinearLayout mRowContainer;
        LinearLayout mDateContainer;
        LinearLayout mLoadingContainer;
        ImageView mHindTypeIconTemp;

        listViewHolder(View itemView) {
            super(itemView);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        void bindListView(historyRowModel model, int p_position) {
            mDateContainer = itemView.findViewById(R.id.pDateContainer);
            mHeader = itemView.findViewById(R.id.pOrbotRowHeader);
            mDescription = itemView.findViewById(R.id.pOrbotRowDescription);
            mRowContainer = itemView.findViewById(R.id.pRowContainer);
            mRowMenu = itemView.findViewById(R.id.pRowMenu);
            mDate = itemView.findViewById(R.id.pDate);
            mLogoImage = itemView.findViewById(R.id.pLogoImage);
            mWebLogo = itemView.findViewById(R.id.pWebLogo);
            mLoadingContainer = itemView.findViewById(R.id.pLoadingContainer);
            mFaviconLogo = itemView.findViewById(R.id.pFaviconLogo);
            mHindTypeIconTemp = new ImageView(mContext);

            if(model.getID() == -1){
                mDate.setText(model.getHeader());
                mDateContainer.setVisibility(View.VISIBLE);
                mRowContainer.setVisibility(View.GONE);
                mRowMenu.setVisibility(View.INVISIBLE);
                mRowMenu.setClickable(false);
                mWebLogo.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.GONE);
            }
            else if(model.getID() == -2){
                mDate.setText(model.getHeader());
                mDateContainer.setVisibility(View.GONE);
                mRowContainer.setVisibility(View.GONE);
                mRowMenu.setVisibility(View.INVISIBLE);
                mRowMenu.setClickable(false);
                mWebLogo.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.VISIBLE);
                return;
            }
            else {
                mDateContainer.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.GONE);
                mRowContainer.setVisibility(View.VISIBLE);
                if(mLongSelectedID.size()>0){
                    mRowMenu.setVisibility(View.INVISIBLE);
                    mRowMenu.setClickable(false);
                }else {
                    mRowMenu.setVisibility(View.VISIBLE);
                    mRowMenu.setClickable(true);
                }

                if(model.getDescription().startsWith(constants.CONST_PRIVACY_POLICY_URL_NON_TOR)){
                    String m_title = model.getDescription().replace(constants.CONST_PRIVACY_POLICY_URL_NON_TOR,"https://genesis.onion/privacy");
                    String m_description = model.getHeader().replace(constants.CONST_PRIVACY_POLICY_URL_NON_TOR,"https://genesis.onion/privacy");

                    mHeader.setText(m_title);
                    mDescription.setText(m_description);
                }else {
                    mHeader.setText(model.getHeader());
                    mDescription.setText(("https://"+model.getDescription()));
                }

                mWebLogo.setVisibility(View.VISIBLE);
                mWebLogo.setText((helperMethod.getDomainName(model.getHeader()).toUpperCase().charAt(0)+""));
                String header = model.getHeader();

                if(model.getDescription().contains("trcip42ymcgvv5hsa7nxpwdnott46ebomnn5pm5lovg5hpszyo4n35yd.onion") || model.getDescription().contains("genesis.onion")){
                    mFaviconLogo.setImageDrawable(itemView.getResources().getDrawable(R.drawable.genesis));
                }
                else{
                    mEvent.invokeObserver(Arrays.asList(mFaviconLogo, "https://" + helperMethod.getDomainName(model.getDescription())), enums.etype.fetch_favicon);
                }

                setItemViewOnClickListener(mRowContainer, mRowMenu, mDescription.getText().toString(), p_position, header, mRowMenu, mLogoImage, model.getID(), model.getDate());
            }

            if(mLongSelectedID.size()>0){
                mRowMenu.setVisibility(View.INVISIBLE);
                mRowMenu.setClickable(false);
            }else {
                mRowMenu.setVisibility(View.VISIBLE);
                mRowMenu.setClickable(true);
            }

            if(mLongSelectedIndex.contains("https://" + model.getDescription()) && mLongSelectedID.contains(model.getID())){
                mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(historyEnums.eHistoryViewAdapterCommands.M_SELECT_VIEW, Arrays.asList(mRowContainer, mRowMenu, mLogoImage, true, false));
            }else if(mLogoImage.getAlpha()>0){
                mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(historyEnums.eHistoryViewAdapterCommands.M_CLEAR_HIGHLIGHT, Arrays.asList(mRowContainer, mRowMenu, mLogoImage, true, false));
            }
        }
    }

    void setFilter(String pFilter){
        this.mFilter = pFilter.toLowerCase();
    }

    void invokeFilter(boolean notify){
        if(notify){
            initializeModelWithDate(true);
        }
   }

    private boolean isLongPressMenuActive(){
        return mLongSelectedIndex.size()>0;
    }

    public Object onTrigger(historyEnums.eHistoryAdapterCommands pCommands, List<Object> pData){
        if(pCommands == historyEnums.eHistoryAdapterCommands.GET_SELECTED_URL){
            return getSelectedURL();
        }
        else if(pCommands == historyEnums.eHistoryAdapterCommands.M_CLEAR_LONG_SELECTED_URL){
            clearLongSelectedURL();
        }
        else if(pCommands == historyEnums.eHistoryAdapterCommands.GET_LONG_SELECTED_URL){
            return getLongSelectedleURL();
        }
        else if(pCommands == historyEnums.eHistoryAdapterCommands.GET_LONG_SELECTED_STATUS){
            return isLongPressMenuActive();
        }
        else if(pCommands == historyEnums.eHistoryAdapterCommands.ON_CLOSE){
            onClose((int)pData.get(0));
        }
        return null;
    }

}
