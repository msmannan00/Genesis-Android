package com.hiddenservices.onionservices.appManager.bookmarkManager.bookmarkHome;

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

import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.dataManager.models.bookmarkRowModel;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

@SuppressLint("NotifyDataSetChanged")
public class bookmarkAdapter extends RecyclerView.Adapter<bookmarkAdapter.listViewHolder> {
    /*Private Variables*/

    private ArrayList<bookmarkRowModel> mModelList = new ArrayList<>();
    private ArrayList<bookmarkRowModel> mCurrentList;
    private ArrayList<bookmarkRowModel> mPassedList;
    private ArrayList<Integer> mRealID = new ArrayList<>();
    private ArrayList<Integer> mRealIndex = new ArrayList<>();
    private ArrayList<Date> mLongSelectedDate = new ArrayList<>();
    private ArrayList<String> mLongSelectedIndex = new ArrayList<>();
    private ArrayList<Integer> mLongSelectedID = new ArrayList<>();

    private AppCompatActivity mContext;
    private bookmarkAdapterView mHistroyAdapterView;
    private Context mListHolderContext;
    private PopupWindow mPopupWindow = null;
    private eventObserver.eventListener mEvent;
    private String mFilter = strings.GENERIC_EMPTY_STR;
    private boolean mLongPressedMenuActive = false;

    /*Local Variables*/

    private boolean mDisableCallable = false;
    private boolean mSearchEnabled = false;

    bookmarkAdapter(ArrayList<bookmarkRowModel> pModelList, eventObserver.eventListener mEvent, AppCompatActivity pMainContext) {
        this.mEvent = mEvent;
        this.mCurrentList = new ArrayList<>();
        this.mPassedList = pModelList;
        this.mContext = pMainContext;
        this.mHistroyAdapterView = new bookmarkAdapterView(mContext);

        initializeModelWithDate(false);
    }

    private void initializeModelWithDate(boolean pFilterEnabled) {
        mRealID.clear();
        mRealIndex.clear();
        mCurrentList.clear();
        this.mModelList.clear();
        onVerifyLongSelectedURL(true);

        ArrayList<bookmarkRowModel> p_model_list = mPassedList;
        for (int counter = 0; counter < p_model_list.size(); counter++) {

            if (pFilterEnabled) {
                if (!p_model_list.get(counter).getHeader().toLowerCase().contains(this.mFilter.toLowerCase()) && !p_model_list.get(counter).getDescription().toLowerCase().contains(this.mFilter)) {
                    continue;
                }
            }

            mRealID.add(p_model_list.get(counter).getID());
            mRealIndex.add(counter);
            this.mModelList.add(p_model_list.get(counter));
        }
        mCurrentList.addAll(this.mModelList);
    }

    /*Initializations*/

    private ArrayList<String> getLongSelectedleURL() {
        return mLongSelectedIndex;
    }

    public void onDeleteSelected() {
        for (int m_counter = 0; m_counter < mLongSelectedIndex.size(); m_counter++) {
            for (int m_counter_inner = 0; m_counter_inner < mCurrentList.size(); m_counter_inner++) {
                if (mCurrentList.get(m_counter_inner).getDate() == mLongSelectedDate.get(m_counter) && mLongSelectedIndex.get(m_counter).equals(helperMethod.completeURL(mCurrentList.get(m_counter_inner).getDescription()))) {
                    mEvent.invokeObserver(Collections.singletonList(mRealIndex.get(m_counter_inner)), bookmarkEnums.eBookmarkAdapterCallback.ON_URL_CLEAR);
                    mEvent.invokeObserver(Collections.singletonList(mLongSelectedID.get(m_counter)), bookmarkEnums.eBookmarkAdapterCallback.ON_URL_CLEAR_AT);
                    invokeFilter(false);
                    mEvent.invokeObserver(Collections.singletonList(m_counter_inner), bookmarkEnums.eBookmarkAdapterCallback.IS_EMPTY);

                    boolean mDateVerify = false;
                    if (mCurrentList.size() > 0 && mCurrentList.size() < m_counter_inner + 1 && mCurrentList.get(m_counter_inner - 1).getDescription() == null && (mCurrentList.size() > m_counter_inner + 1 && mCurrentList.get(m_counter_inner + 1).getDescription() == null || mCurrentList.size() == m_counter_inner + 1)) {
                        mDateVerify = true;
                    }

                    if (mDateVerify) {
                        notifyItemRemoved(m_counter_inner - 1);
                        mCurrentList.remove(m_counter_inner - 1);
                        notifyItemRangeChanged(m_counter_inner - 1, mCurrentList.size());
                    } else {
                        notifyItemRemoved(m_counter_inner);
                        mCurrentList.remove(m_counter_inner);
                        notifyItemRangeChanged(m_counter_inner, mCurrentList.size());
                    }
                    break;
                }
            }
        }
        clearLongSelectedURL();
        // initializeModelWithDate(false);
    }

    private void clearLongSelectedURL() {
        for (int m_counter = 0; m_counter < mCurrentList.size(); m_counter++) {
            for (int m_counter_inner = 0; m_counter_inner < mLongSelectedID.size(); m_counter_inner++) {
                if (mCurrentList.get(m_counter).getID() == mLongSelectedID.get(m_counter_inner)) {
                    mLongSelectedID.remove(m_counter_inner);
                    notifyItemChanged(m_counter);
                    m_counter -= 1;
                    break;
                }
            }
        }

        mLongSelectedDate.clear();
        mLongSelectedIndex.clear();
        mLongSelectedID.clear();
    }

    private String getSelectedURL() {
        String m_joined_url = "\n";
        for (int m_counter = 0; m_counter < mLongSelectedIndex.size(); m_counter++) {
            m_joined_url = m_joined_url.concat("\n" + mLongSelectedIndex.get(m_counter));
        }
        return m_joined_url;
    }

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mListHolderContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookmarkAdapter.listViewHolder holder, int position) {
        holder.bindListView(mCurrentList.get(position), position);
    }

    public int getItem() {
        return mCurrentList.size();
    }

    @Override
    public int getItemCount() {
        return mCurrentList.size();
    }

    /*Listeners*/
    public void onUpdateSearchStatus(boolean p_is_searched) {
        mSearchEnabled = p_is_searched;
    }

    public void invokeSwipeClose(int pPosition) {
        onClose(pPosition);
        invokeFilter(true);

        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public void onSelectView(View pItemView, String pUrl, View pMenuItem, ImageView pLogoImage, boolean pIsForced, int pId, Date pDate) {
        if (!mSearchEnabled) {
            try {
                mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands.M_SELECT_VIEW, Arrays.asList(pItemView, pMenuItem, pLogoImage, pIsForced, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!pIsForced) {
                mLongSelectedDate.add(pDate);
                mLongSelectedIndex.add(pUrl);
                mLongSelectedID.add(pId);
                if (mLongSelectedIndex.size() <= 1) {
                    notifyDataSetChanged();
                }
            }
            onVerifyLongSelectedURL(false);
        }
    }

    public void onVerifyLongSelectedURL(boolean pIsComputing) {
        if (mLongSelectedIndex.size() > 0) {
            mEvent.invokeObserver(Collections.singletonList(false), bookmarkEnums.eBookmarkAdapterCallback.ON_VERIFY_SELECTED_URL_MENU);
        } else {
            if (!pIsComputing) {
                notifyDataSetChanged();
            }
            mEvent.invokeObserver(Collections.singletonList(true), bookmarkEnums.eBookmarkAdapterCallback.ON_VERIFY_SELECTED_URL_MENU);
        }
    }

    public void onClearHighlight(View pItemView, String pUrl, View pMenuItem, ImageView pLogoImage, boolean pIsForced, int pId, Date pDate) {
        try {
            mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands.M_CLEAR_HIGHLIGHT, Arrays.asList(pItemView, pMenuItem, pLogoImage, pIsForced));
            mLongSelectedDate.remove(pDate);
            mLongSelectedIndex.remove(pUrl);
            mLongSelectedID.remove((Integer) pId);
            onVerifyLongSelectedURL(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyFilter() {
        if (mFilter.length() > 0) {
            initializeModelWithDate(true);
            notifyDataSetChanged();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onSwipe(View pItemView, String pUrl, View pMenuItem, ImageView pLogoImage, int pId, Date pDate) {

        Handler handler = new Handler();

        Runnable mLongPressed = () -> {
            if (!mDisableCallable) {
                if (!mLongSelectedIndex.contains(pUrl) || !mLongSelectedID.contains(pId)) {
                    mLongPressedMenuActive = true;
                    onSelectView(pItemView, pUrl, pMenuItem, pLogoImage, false, pId, pDate);
                } else {
                    onClearHighlight(pItemView, pUrl, pMenuItem, pLogoImage, false, pId, pDate);
                    mLongPressedMenuActive = true;
                }
            } else {
                pItemView.setPressed(false);
                pItemView.clearFocus();
            }
            notifyFilter();
        };

        pItemView.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (mLongSelectedIndex.size() > 0) {
                    if (!mLongPressedMenuActive) {
                        if (mLongSelectedIndex.contains(pUrl) && mLongSelectedID.contains(pId)) {
                            handler.removeCallbacks(mLongPressed);
                            bookmarkAdapter.this.onClearHighlight(pItemView, pUrl, pMenuItem, pLogoImage, false, pId, pDate);
                        } else {
                            handler.removeCallbacks(mLongPressed);
                            bookmarkAdapter.this.onSelectView(pItemView, pUrl, pMenuItem, pLogoImage, false, pId, pDate);
                        }
                    }
                    initializeModelWithDate(true);
                    return false;
                }

                v.setPressed(false);
                handler.removeCallbacks(mLongPressed);
                mEvent.invokeObserver(Collections.singletonList(pUrl), bookmarkEnums.eBookmarkAdapterCallback.ON_URL_TRIGGER);
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
        // initializeModelWithDate(false);
    }

    void onOpenMenu(View pView, String pUrl, int pPosition, String pTitle) {
        LayoutInflater layoutInflater = (LayoutInflater) pView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View mPopupView = layoutInflater.inflate(R.layout.history_bookmark__row_menu, null);
        mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands.M_OPEN_MENU, Arrays.asList(mPopupWindow, pView, mPopupView));

        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuCopy), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuShare), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuOpenCurrentTab), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuOpenNewTab), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuDelete), pUrl, pPosition, pTitle);

        if (mFilter.length() > 0) {
            initializeModelWithDate(true);
            notifyDataSetChanged();
        }
    }

    public void setPopupWindowEvents(View pView, String pUrl, int pPosition, String pTitle) {
        pView.setOnClickListener(v -> {
            if (v.getId() == R.id.pMenuCopy) {
                helperMethod.copyURL(pUrl, mListHolderContext);
                mPopupWindow.dismiss();
                helperMethod.showToastMessage("copied to clipboard", mContext);
            } else if (v.getId() == R.id.pMenuShare) {
                helperMethod.shareApp((AppCompatActivity) mListHolderContext, pUrl, pTitle);
                mPopupWindow.dismiss();
            } else if (v.getId() == R.id.pMenuOpenCurrentTab) {
                mEvent.invokeObserver(Collections.singletonList(pUrl), bookmarkEnums.eBookmarkAdapterCallback.ON_URL_TRIGGER);
                mPopupWindow.dismiss();
            } else if (v.getId() == R.id.pMenuOpenNewTab) {
                mEvent.invokeObserver(Collections.singletonList(pUrl), bookmarkEnums.eBookmarkAdapterCallback.ON_URL_TRIGGER_NEW_TAB);
                mPopupWindow.dismiss();
            } else if (v.getId() == R.id.pMenuDelete) {
                onClose(pPosition);
                invokeFilter(true);
                mPopupWindow.dismiss();
            }
        });
    }

    private void setItemViewOnClickListener(View pItemView, View pItemMenu, String pUrl, int pPosition, String pTitle, View pMenuItem, ImageView pLogoImage, int pId, Date pDate) {
        pItemMenu.setOnClickListener((View v) -> onOpenMenu(v, pUrl, pPosition, pTitle));
        onSwipe(pItemView, pUrl, pMenuItem, pLogoImage, pId, pDate);
    }

    private void onClose(int pIndex) {
        //mEvent.invokeObserver(Collections.singletonList(mRealIndex.get(pIndex)),enums.etype.url_clear);
        mEvent.invokeObserver(Collections.singletonList(mRealID.get(pIndex)), bookmarkEnums.eBookmarkAdapterCallback.ON_URL_CLEAR_AT);
        mEvent.invokeObserver(Collections.singletonList(mRealID.get(pIndex)), bookmarkEnums.eBookmarkAdapterCallback.IS_EMPTY);

        if (mPassedList.size() <= 0) {
            mCurrentList.clear();
            return;
        }

        mCurrentList.remove(mRealIndex.get(pIndex));
        notifyItemRemoved(mRealIndex.get(pIndex));
        notifyItemRangeChanged(0, mCurrentList.size());
        clearLongSelectedURL();
    }


    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mHeader;
        TextView mDescription;
        TextView mDate;
        TextView mWebLogo;
        ImageButton mRowMenu;
        ImageView mSelectionImage;
        ImageView mFaviconLogo;
        LinearLayout mRowContainer;
        LinearLayout mDateContainer;
        LinearLayout mLoadingContainer;
        ImageView mHindTypeIconTemp;
        ImageButton mBookmarkEdit;

        listViewHolder(View itemView) {
            super(itemView);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        void bindListView(bookmarkRowModel model, int p_position) {
            mDateContainer = itemView.findViewById(R.id.pDateContainer);
            mHeader = itemView.findViewById(R.id.pOrbotRowHeader);
            mDescription = itemView.findViewById(R.id.pOrbotRowDescription);
            mRowContainer = itemView.findViewById(R.id.pRowContainer);
            mRowMenu = itemView.findViewById(R.id.pRowMenu);
            mDate = itemView.findViewById(R.id.pDate);
            mSelectionImage = itemView.findViewById(R.id.pLogoImage);
            mWebLogo = itemView.findViewById(R.id.pWebLogo);
            mLoadingContainer = itemView.findViewById(R.id.pLoadingContainer);
            mFaviconLogo = itemView.findViewById(R.id.pFaviconLogo);
            mBookmarkEdit = itemView.findViewById(R.id.pBookmarkEdit);
            mHindTypeIconTemp = new ImageView(mContext);

            if (model.getID() == -1) {
                mDate.setText(model.getHeader());
                mDateContainer.setVisibility(View.VISIBLE);
                mRowContainer.setVisibility(View.GONE);
                mRowMenu.setVisibility(View.INVISIBLE);
                mRowMenu.setClickable(false);
                mBookmarkEdit.setVisibility(View.INVISIBLE);
                mBookmarkEdit.setClickable(false);
                mWebLogo.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.GONE);
            } else if (model.getID() == -2) {
                mDate.setText(model.getHeader());
                mDateContainer.setVisibility(View.GONE);
                mRowContainer.setVisibility(View.GONE);
                mRowMenu.setVisibility(View.INVISIBLE);
                mRowMenu.setClickable(false);
                mBookmarkEdit.setVisibility(View.INVISIBLE);
                mBookmarkEdit.setClickable(false);
                mWebLogo.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.VISIBLE);
                return;
            } else {
                mDateContainer.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.GONE);
                mRowContainer.setVisibility(View.VISIBLE);
                if (mLongSelectedID.size() > 0) {
                    mRowMenu.setVisibility(View.INVISIBLE);
                    mRowMenu.setClickable(false);
                    mBookmarkEdit.setVisibility(View.INVISIBLE);
                    mBookmarkEdit.setClickable(false);
                } else {
                    mRowMenu.setVisibility(View.VISIBLE);
                    mRowMenu.setClickable(true);
                    mBookmarkEdit.setVisibility(View.VISIBLE);
                    mBookmarkEdit.setClickable(true);
                }
                mWebLogo.setVisibility(View.VISIBLE);
                mHeader.setText(model.getHeader());
                mWebLogo.setText((helperMethod.getDomainName(model.getHeader()).toUpperCase().charAt(0) + ""));
                String header = model.getHeader();
                mDescription.setText(model.getDescription());
                if (!model.getDescription().startsWith("http://") && !model.getDescription().startsWith("https://")) {
                    mDescription.setText("https://" + mDescription.getText().toString());
                }

                if(status.sLowMemory != enums.MemoryStatus.LOW_MEMORY && status.sLowMemory != enums.MemoryStatus.CRITICAL_MEMORY){
                    if (model.getDescription().contains("167.86.99.31") || model.getDescription().contains("orion.onion")) {
                        mFaviconLogo.setImageDrawable(itemView.getResources().getDrawable(R.drawable.genesis));
                    } else {
                        mEvent.invokeObserver(Arrays.asList(mFaviconLogo, "https://" + helperMethod.getDomainName(model.getDescription())), bookmarkEnums.eBookmarkAdapterCallback.ON_FETCH_FAVICON);
                    }
                }

                mRowMenu.setOnClickListener(this);
                setItemViewOnClickListener(mRowContainer, mRowMenu, mDescription.getText().toString(), p_position, header, mRowMenu, mSelectionImage, model.getID(), model.getDate());
            }

            if (mLongSelectedID.size() > 0) {
                mRowMenu.setVisibility(View.INVISIBLE);
                mRowMenu.setClickable(false);
                mBookmarkEdit.setVisibility(View.INVISIBLE);
                mBookmarkEdit.setClickable(false);
                mBookmarkEdit.setClickable(false);
            } else {
                mRowMenu.setVisibility(View.VISIBLE);
                mRowMenu.setClickable(true);
                mBookmarkEdit.setVisibility(View.VISIBLE);
                mBookmarkEdit.setClickable(true);
            }

            if (mLongSelectedID.contains(model.getID())) {
                mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands.M_SELECT_VIEW, Arrays.asList(mRowContainer, mRowMenu, mSelectionImage, true, false));
            } else if (mSelectionImage.getAlpha() > 0) {
                mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands.M_CLEAR_HIGHLIGHT, Arrays.asList(mRowContainer, mRowMenu, mSelectionImage, true, false));
            }

            mBookmarkEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.pBookmarkEdit) {
                mEvent.invokeObserver(Arrays.asList(mHeader.getText(), mDescription.getText(), mModelList.get(getLayoutPosition()).getID()), bookmarkEnums.eBookmarkAdapterCallback.M_OPEN_BOOKMARK_SETTING);
            }
        }
    }

    void setFilter(String pFilter) {
        this.mFilter = pFilter.toLowerCase();
    }

    void invokeFilter(boolean notify) {
        if (notify) {
            initializeModelWithDate(true);
        }
    }

    private boolean isLongPressMenuActive() {
        return mLongSelectedIndex.size() > 0;
    }

    public Object onTrigger(bookmarkEnums.eBookmarkAdapterCommands pCommands, List<Object> pData) {
        if (pCommands == bookmarkEnums.eBookmarkAdapterCommands.GET_SELECTED_URL) {
            return getSelectedURL();
        } else if (pCommands == bookmarkEnums.eBookmarkAdapterCommands.M_CLEAR_LONG_SELECTED_URL) {
            clearLongSelectedURL();
        } else if (pCommands == bookmarkEnums.eBookmarkAdapterCommands.GET_LONG_SELECTED_URL) {
            return getLongSelectedleURL();
        } else if (pCommands == bookmarkEnums.eBookmarkAdapterCommands.GET_LONG_SELECTED_STATUS) {
            return isLongPressMenuActive();
        } else if (pCommands == bookmarkEnums.eBookmarkAdapterCommands.ON_CLOSE) {
            onClose((int) pData.get(0));
        }
        return null;
    }

}
