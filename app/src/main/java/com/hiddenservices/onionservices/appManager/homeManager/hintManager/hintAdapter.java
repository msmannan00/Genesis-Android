package com.hiddenservices.onionservices.appManager.homeManager.hintManager;

import static com.hiddenservices.onionservices.constants.constants.CONST_PRIVACY_POLICY_URL_NON_TOR;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeEnums;
import com.hiddenservices.onionservices.appManager.tabManager.tabEnums;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.dataManager.models.historyRowModel;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class hintAdapter extends RecyclerView.Adapter<hintAdapter.listViewHolder> {

    /*Private Variables*/

    private ArrayList<historyRowModel> mHintList;
    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;

    public hintAdapter(ArrayList<historyRowModel> pHintList, eventObserver.eventListener pEvent, AppCompatActivity pContext, String pSearch) {
        this.mHintList = new ArrayList();
        int maxCounter = 5;
        if (pHintList.size() < maxCounter) {
            maxCounter = pHintList.size();
        }

        this.mHintList.addAll(pHintList.subList(0, maxCounter));
        this.mContext = pContext;
        this.mEvent = pEvent;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onUpdateAdapter(ArrayList<historyRowModel> pHintList, String pSearch) {
        mHintList = pHintList;
        if (mHintList.size() == 1 && mHintList.get(0).getHeader().equals("about:blank")) {
            mHintList.clear();
            mHintList.add(new historyRowModel("Orion Search", "orion.onion", -1));
        }
        notifyDataSetChanged();
    }

    /*Initializations*/

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hint_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull hintAdapter.listViewHolder holder, int position) {
        holder.bindListView(mHintList.get(position));
    }

    @Override
    public int getItemCount() {
        return mHintList.size();
    }

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        TextView mHeader;
        TextView mHeaderSingle;
        TextView mURL;
        ImageButton mMoveURL;
        ImageView mHindTypeIcon;
        CardView mHintWebIcon = null;
        ImageView pHintWebIconImage = null;
        LinearLayout mpHintListener;
        ImageView mHindTypeIconTemp;

        listViewHolder(View itemView) {
            super(itemView);
        }

        @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
        void bindListView(historyRowModel model) {
            mHeader = itemView.findViewById(R.id.pOrbotRowHeader);
            mHeaderSingle = itemView.findViewById(R.id.pHeaderSingle);
            mURL = itemView.findViewById(R.id.pURL);
            mHindTypeIcon = itemView.findViewById(R.id.pHindTypeIcon);
            mpHintListener = itemView.findViewById(R.id.pHintListener);
            mMoveURL = itemView.findViewById(R.id.pMoveURL);
            mHintWebIcon = itemView.findViewById(R.id.pHintWebIcon);
            pHintWebIconImage = itemView.findViewById(R.id.pHintWebIconImage);
            mHindTypeIconTemp = new ImageView(mContext);

            pHintWebIconImage.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.c_text_v6)));

            pHintWebIconImage.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.c_text_v8)));

            Drawable mDrawable;
            Resources res = itemView.getContext().getResources();
            try {
                mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_browser));
                mMoveURL.setVisibility(View.VISIBLE);
                mMoveURL.setOnTouchListener(listViewHolder.this);
                pHintWebIconImage.setImageDrawable(mDrawable);
            } catch (Exception ignored) {
            }

            String mURLLink;
            if (model.getDescription().equals(strings.GENERIC_EMPTY_STR)) {
                mURLLink = model.getHeader();
                mHeaderSingle.setText(model.getHeader().replace("+", " ").replace("%", "+"));
                mHeaderSingle.setVisibility(View.VISIBLE);
                mHeader.setVisibility(View.GONE);
                mURL.setVisibility(View.GONE);
                mHintWebIcon.setVisibility(View.GONE);
                mHindTypeIcon.setVisibility(View.VISIBLE);
            } else {
                mURLLink = model.getDescription();
                mHeaderSingle.setVisibility(View.GONE);
                mHeader.setVisibility(View.VISIBLE);
                mURL.setVisibility(View.VISIBLE);
                mHintWebIcon.setVisibility(View.VISIBLE);
                mHindTypeIcon.setVisibility(View.GONE);
            }

            mHeader.setText(model.getHeader());
            if (model.getDescription().equals(strings.GENERIC_EMPTY_STR)) {
                mMoveURL.setTag(model.getHeader());
            } else {
                mMoveURL.setTag(model.getDescription());
            }

            String m_url = model.getDescription();
            if (!status.sTorBrowsing && (m_url.equals("https://167.86.99.31/privacy") || m_url.equals(CONST_PRIVACY_POLICY_URL_NON_TOR))) {
                m_url = "https://orion.onion/privacy";
            }

            mURL.setText(m_url);
            try {
                if (model.getDescription().equals(strings.GENERIC_EMPTY_STR) && !model.getHeader().contains(".")) {
                    mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_search));
                    mMoveURL.setVisibility(View.GONE);
                } else {
                    mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_browser));
                    mMoveURL.setVisibility(View.VISIBLE);
                    mMoveURL.setOnTouchListener(listViewHolder.this);
                }

                mHindTypeIcon.setImageDrawable(mDrawable);
            } catch (Exception ignored) {
            }

            mpHintListener.setOnTouchListener(listViewHolder.this);

            if (mURLLink.contains("167.86.99.31") || mURLLink.contains("orion.onion")) {
                pHintWebIconImage.setImageTintList(null);
                pHintWebIconImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.genesis));
            } else {
                mHindTypeIconTemp.setImageDrawable(null);
                mEvent.invokeObserver(Arrays.asList(mHindTypeIconTemp, "https://" + helperMethod.getDomainName(model.getDescription())), homeEnums.eHintCallback.ON_FETCH_FAVICON);

                if(status.sLowMemory != enums.MemoryStatus.CRITICAL_MEMORY && status.sLowMemory != enums.MemoryStatus.LOW_MEMORY){
                    if (mHindTypeIconTemp.getDrawable() != null) {
                        pHintWebIconImage.setImageTintList(null);
                        pHintWebIconImage.setImageDrawable(mHindTypeIconTemp.getDrawable());
                    }
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getId() == mpHintListener.getId()) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    helperMethod.hideKeyboard(mContext);
                }
            }
            return false;
        }
    }

    public Object onTrigger(tabEnums.eTabAdapterCommands pCommands, List<Object> pData) {
        return null;
    }

}


