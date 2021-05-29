package com.darkweb.genesissearchengine.appManager.helpManager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.appManager.historyManager.historyEnums;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class helpAdapter extends RecyclerView.Adapter<helpAdapter.helpViewHolder>{
    private List<helpDataModel> mModelList = new ArrayList<>();
    private List<helpDataModel> mCompleteModelList = new ArrayList<>();
    private LinearLayout mPrevRow;
    private Context mContext;

    private int mCurrentPosition = -1;
    private boolean mIsAnimating = false;

    public helpAdapter(List<helpDataModel> pModelList, Context context) {
        if(pModelList!=null){
            this.mCompleteModelList.addAll(pModelList);
            this.mModelList.addAll(pModelList);
        }
        this.mContext = context;
    }

    private void onSearchFilterInvoked(String pQuery){
        pQuery = pQuery.toLowerCase();
        this.mModelList.clear();
        mCurrentPosition = -1;
        for(int mCounter=0;mCounter<mCompleteModelList.size();mCounter++){
            if(mCompleteModelList.get(mCounter).getHeader().toLowerCase().contains(pQuery) || mCompleteModelList.get(mCounter).getDescription().toLowerCase().contains(pQuery)){
                mModelList.add(mCompleteModelList.get(mCounter));
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public helpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_row_view, parent, false);
        return new helpViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull helpViewHolder holder, int position) {
        holder.bindListView(mModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }

    class helpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mRowHeader;
        TextView mRowDescription;
        ImageView mIcon;
        ImageView mArrowNavigation;

        LinearLayout linearLayout;
        RelativeLayout mHelpRowContainer;

        helpViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(helpDataModel model) {
            mRowHeader = itemView.findViewById(R.id.pRowHeader);
            mIcon = itemView.findViewById(R.id.pIcon);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            mArrowNavigation = itemView.findViewById(R.id.pArrowNavigation);
            mHelpRowContainer = itemView.findViewById(R.id.pHelpRowContainer);
            mRowDescription = itemView.findViewById(R.id.pRowDescription);
            linearLayout.animate().setDuration(150).alpha(0);
            mRowHeader.setText(model.getHeader());
            mRowDescription.setText(Html.fromHtml(model.getDescription()));

            Drawable mDrawable;
            Resources res = itemView.getContext().getResources();
            try {

                int mImageID = helperMethod.getResId(model.getIconID(), R.xml.class);
                mDrawable = Drawable.createFromXml(res, res.getXml(mImageID));
                mIcon.setImageDrawable(mDrawable);

                if (mCurrentPosition == getLayoutPosition()) {
                    mPrevRow = linearLayout;
                    linearLayout.animate().setDuration(300).setStartDelay(100).alpha(1);
                    linearLayout.setVisibility(View.VISIBLE);
                    mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_keyboard_arrow_up));
                    mArrowNavigation.setImageDrawable(mDrawable);
                }else {
                    linearLayout.setVisibility(View.GONE);
                    mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_keyboard_arrow_down));
                    mArrowNavigation.setImageDrawable(mDrawable);
                }
            } catch (Exception ignored) { }

            mHelpRowContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(!mIsAnimating){
                mIsAnimating = true;
                if(mPrevRow !=null && mCurrentPosition !=-1){
                    try {
                        mPrevRow.animate().cancel();
                        mPrevRow.animate().alpha(0).setDuration(300);
                        onCollapse();
                    }catch (Exception ex){
                        mIsAnimating = false;
                        ex.printStackTrace();
                    }
                }else {
                    onCollapse();
                }
            }
        }
        public void onCollapse(){
            mIsAnimating = false;
            int mPreviousItem = mCurrentPosition;
            if(mCurrentPosition ==getLayoutPosition()){
                mCurrentPosition = -1;
            }else {
                mCurrentPosition = getLayoutPosition();
                notifyItemChanged(mCurrentPosition);
            }
            notifyItemChanged(mPreviousItem);
        }
    }

    public Object onTrigger(helpEnums.eHelpAdapter pCommands, List<Object> pData){
        if(pCommands == helpEnums.eHelpAdapter.M_INIT_FILTER){
            onSearchFilterInvoked((String) pData.get(0));
        }

        return null;
    }
}
