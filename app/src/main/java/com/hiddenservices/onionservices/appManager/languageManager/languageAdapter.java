package com.hiddenservices.onionservices.appManager.languageManager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.R;

import java.util.ArrayList;
import java.util.Arrays;

import static com.hiddenservices.onionservices.appManager.languageManager.languageEnums.eLanguageAdapterCallback.*;
import static com.hiddenservices.onionservices.constants.constants.CONST_LANGUAGE_DEFAULT_LANG;
import static com.hiddenservices.onionservices.constants.strings.LANGUAGE_NOT_SUPPORTED;

public class languageAdapter extends RecyclerView.Adapter<languageAdapter.helpViewHolder> {

    /*Private Variables*/

    private ArrayList<languageDataModel> mModelList;
    private Context mContext;

    /*Private Local Variables*/

    private String mCurrentLanguage;
    private eventObserver.eventListener mEvent;
    private int mCurrentIndex = 0;
    private boolean mClickable = false;

    public languageAdapter(ArrayList<languageDataModel> pModelList, Context pContext, String pCurrentLanguage, eventObserver.eventListener pEvent) {
        this.mModelList = pModelList;
        this.mContext = pContext;
        this.mEvent = pEvent;
        this.mCurrentLanguage = pCurrentLanguage;
    }

    @NonNull
    @Override
    public helpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lang_row_view, parent, false);
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
        TextView mHeader;
        TextView mDescription;
        LinearLayout mContainer;
        ImageView mMarker;

        helpViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(languageDataModel model) {
            boolean mIsDefaultSupported = true;
            mHeader = itemView.findViewById(R.id.pOrbotRowHeader);
            mDescription = itemView.findViewById(R.id.pOrbotRowDescription);
            mContainer = itemView.findViewById(R.id.pContainer);
            mMarker = itemView.findViewById(R.id.pMarker);

            mHeader.setText(model.getHeader());
            mContainer.setTag(R.id.LaguageID, model.getTag());
            mContainer.setTag(R.id.LaguageRegion, model.getCountry());

            if (model.getTag().equals(CONST_LANGUAGE_DEFAULT_LANG) && status.sSettingLanguage.equals(CONST_LANGUAGE_DEFAULT_LANG)) {
                mDescription.setText((String) mEvent.invokeObserver(null, M_SYSTEM_LANGUAGE_SUPPORT_INFO));
                if (mDescription.getText().toString().endsWith(LANGUAGE_NOT_SUPPORTED)) {
                    mIsDefaultSupported = false;
                }
            } else {
                mDescription.setText(model.getDescription());
            }

            if (mContainer.getTag(R.id.LaguageID).toString().equals(mCurrentLanguage)) {
                Drawable mDrawable;
                Resources res = mContext.getResources();
                try {
                    mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.gx_border_left));
                    mContainer.setBackground(mDrawable);
                    mHeader.setTextColor(ContextCompat.getColor(mContext, R.color.c_white));
                    mDescription.setTextColor(ContextCompat.getColor(mContext, R.color.white_darker));
                    mMarker.setVisibility(View.VISIBLE);
                    mCurrentIndex = getLayoutPosition();
                    if (!mIsDefaultSupported) {
                        mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_cross));
                        mMarker.setImageDrawable(mDrawable);
                    } else {
                        mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.ic_baseline_done));
                        mMarker.setImageDrawable(mDrawable);
                    }
                } catch (Exception ignored) {
                }
            } else {
                Drawable mDrawable;
                Resources res = mContext.getResources();
                try {
                    mDrawable = Drawable.createFromXml(res, res.getXml(R.xml.gx_ripple_gray));
                    mContainer.setBackground(mDrawable);
                    mHeader.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v1));
                    mDescription.setTextColor(ContextCompat.getColor(mContext, R.color.c_text_v6));
                    mMarker.setVisibility(View.GONE);
                } catch (Exception ignored) {
                }
            }
            mContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.pContainer) {
                boolean mSupportedStatus = (boolean) mEvent.invokeObserver(Arrays.asList(v.getTag(R.id.LaguageID).toString(), v.getTag(R.id.LaguageRegion).toString()), M_UPDATE_LANGUAGE);
                if (mCurrentIndex != getLayoutPosition() && mSupportedStatus) {
                    if (!mClickable) {
                        mClickable = true;
                        mEvent.invokeObserver(null, M_ENABLE_VIEW_CLICK);
                        mCurrentLanguage = v.getTag(R.id.LaguageID).toString();
                        mCurrentIndex = getLayoutPosition();

                        final Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            mClickable = false;
                            mEvent.invokeObserver(null, M_DISABLE_VIEW_CLICK);
                        }, 100);
                    }
                }
            }
        }
    }
}
