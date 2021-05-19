package com.darkweb.genesissearchengine.appManager.homeManager.hintManager;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.dataManager.models.historyRowModel;
import com.darkweb.genesissearchengine.appManager.tabManager.tabEnums;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class hintAdapter extends RecyclerView.Adapter<hintAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<historyRowModel> mHintList;
    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;
    private Map<Integer, Drawable> mPastWebIcon = new HashMap<>();
    private Map<Integer, String> mPastIconFlicker = new HashMap<>();

    public hintAdapter(ArrayList<historyRowModel> pHintList, eventObserver.eventListener pEvent, AppCompatActivity pContext, String pSearch) {
        this.mHintList = new ArrayList();
        int maxCounter=5;
        if(pHintList.size()<maxCounter){
            maxCounter = pHintList.size();
        }

        this.mHintList.addAll(pHintList.subList(0,maxCounter));
        this.mContext = pContext;
        this.mEvent = pEvent;
    }

    public void onUpdateAdapter(ArrayList<historyRowModel> pHintList, String pSearch){
        mHintList = pHintList;
        if(mHintList.size()==1 && mHintList.get(0).getHeader().equals("about:blank")) {
            mHintList.clear();
            mHintList.add( new historyRowModel("Genesis Search", "genesis.onion",-1));
        }

        notifyDataSetChanged();
    }

    public void onClearAdapter(){
        //mPastWebIcon.clear();
        mPastWebIcon.remove(0);
        mPastWebIcon.remove(1);
        mPastWebIcon.remove(2);
    }

    /*Initializations*/

    @NonNull @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hint_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull hintAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(mHintList.get(position));
    }

    @Override
    public int getItemCount() {
        return mHintList.size();
    }

    /*Listeners*/

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener
    {
        TextView mHeader;
        TextView mHeaderSingle;
        TextView mURL;
        ImageButton mMoveURL;
        ImageView mHindTypeIcon;
        ImageView mHintWebIcon = null;
        LinearLayout mpHintListener;
        ImageView mHindTypeIconTemp;

        listViewHolder(View itemView) {
            super(itemView);
        }

        @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
        void bindListView(historyRowModel model) {
            mHeader = itemView.findViewById(R.id.pHeader);
            mHeaderSingle = itemView.findViewById(R.id.pHeaderSingle);
            mURL = itemView.findViewById(R.id.pURL);
            mHindTypeIcon = itemView.findViewById(R.id.pHindTypeIcon);
            mpHintListener = itemView.findViewById(R.id.pHintListener);
            mMoveURL = itemView.findViewById(R.id.pMoveURL);
            mHintWebIcon = itemView.findViewById(R.id.pHintWebIcon);
            mHindTypeIconTemp = new ImageView(mContext);

            mHintWebIcon.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.c_text_v6)));

            if(mPastWebIcon.containsKey(getLayoutPosition())){
                mHintWebIcon.setImageDrawable(mPastWebIcon.get(getLayoutPosition()));
                mHintWebIcon.setImageTintList(null);
            }else {
                mHintWebIcon.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.c_text_v8)));

                Drawable mDrawable;
                Resources res = itemView.getContext().getResources();
                try {
                    mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_browser));
                    mMoveURL.setVisibility(View.VISIBLE);
                    mMoveURL.setOnTouchListener(listViewHolder.this);
                    mHintWebIcon.setImageDrawable(mDrawable);
                } catch (Exception ignored) {
                }

            }

            String mURLLink;
            if(model.getDescription().equals(strings.GENERIC_EMPTY_STR)){
                mURLLink = model.getHeader();
                mHeaderSingle.setText(model.getHeader().replace("+"," ").replace("%", "+"));
                mHeaderSingle.setVisibility(View.VISIBLE);
                mHeader.setVisibility(View.GONE);
                mURL.setVisibility(View.GONE);
                mHintWebIcon.setVisibility(View.GONE);
                mHindTypeIcon.setVisibility(View.VISIBLE);
            }else {
                mURLLink = model.getDescription();
                mHeaderSingle.setVisibility(View.GONE);
                mHeader.setVisibility(View.VISIBLE);
                mURL.setVisibility(View.VISIBLE);
                mHintWebIcon.setVisibility(View.VISIBLE);
                mHindTypeIcon.setVisibility(View.GONE);
            }

            mHeader.setText(model.getHeader());
            if(model.getDescription().equals(strings.GENERIC_EMPTY_STR)){
                mMoveURL.setTag(model.getHeader());
            }else {
                mMoveURL.setTag(model.getDescription());
            }

            mURL.setText(model.getDescription());
            Drawable mDrawable = null;
            Resources res = itemView.getContext().getResources();
            try {
                if(model.getDescription().equals(strings.GENERIC_EMPTY_STR) && !model.getHeader().contains(".")){
                    mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_search));
                    mMoveURL.setVisibility(View.GONE);
                }else {
                    mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_browser));
                    mMoveURL.setVisibility(View.VISIBLE);
                    mMoveURL.setOnTouchListener(listViewHolder.this);
                }
            } catch (Exception ignored) {
            }

            if(model.getDescription().equals(strings.GENERIC_EMPTY_STR)){
                mHindTypeIcon.setImageDrawable(mDrawable);
            }

            mpHintListener.setOnTouchListener(listViewHolder.this);

            if(mURLLink.contains("genesishiddentechnologies.com") || mURLLink.contains("genesis.onion")){
                mHintWebIcon.setImageTintList(null);
                mHintWebIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.genesis));
                mPastWebIcon.put(getLayoutPosition(),mHintWebIcon.getDrawable());
            }else
            {
                new Thread(){

                    public void run(){
                        try {
                            String mURLPast = mURLLink;
                            mPastIconFlicker.put(getLayoutPosition(),mURLPast);

                            mHindTypeIconTemp.setImageDrawable(null);
                            mEvent.invokeObserver(Arrays.asList(mHindTypeIconTemp, "https://" + helperMethod.getDomainName(model.getDescription())), enums.etype.fetch_favicon);
                            sleep(200);

                            mContext.runOnUiThread(() -> new Handler().postDelayed(() ->
                            {
                                if(mHindTypeIconTemp.getDrawable() != null){
                                    if(mURLPast.equals(mPastIconFlicker.get(getLayoutPosition()))){
                                        mHintWebIcon.setImageTintList(null);
                                        mHintWebIcon.setImageDrawable(mHindTypeIconTemp.getDrawable());
                                        mPastWebIcon.put(getLayoutPosition(),mHintWebIcon.getDrawable());
                                    }
                                    if(getLayoutPosition() == 1){
                                        Log.i("FUSSSS1111","FUSSSS4444");
                                    }
                                }
                            }, 200));

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
       }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(v.getId() == mpHintListener.getId() || v.getId() == mMoveURL.getId()){
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    helperMethod.hideKeyboard(mContext);
                }
            }
            return false;
        }
    }

    public Object onTrigger(tabEnums.eTabAdapterCommands pCommands, List<Object> pData){
        return null;
    }

}


