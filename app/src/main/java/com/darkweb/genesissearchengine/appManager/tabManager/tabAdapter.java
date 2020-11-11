package com.darkweb.genesissearchengine.appManager.tabManager;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;

import org.mozilla.geckoview.GeckoView;

import java.util.ArrayList;
import java.util.Collections;

public class tabAdapter extends RecyclerView.Adapter<tabAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<tabRowModel> mModelList;
    private eventObserver.eventListener mEvent;

    private GeckoView mCurrentGeckoView = null;
    private int mCurrentSession;

    tabAdapter(ArrayList<tabRowModel> model_list, eventObserver.eventListener event, GeckoView pGeckoView, int pCurrentSession) {
        this.mModelList = model_list;
        this.mEvent = event;

        pGeckoView.releaseSession();
        this.mCurrentSession = pCurrentSession;
    }

    /*Initializations*/

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tabview_row, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull tabAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(mModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mModelList.size();
    }

    /*Listeners*/

    public void onClose(){
        if(mCurrentGeckoView!=null){
            mCurrentGeckoView.releaseSession();
        }
    }

    public void setOnClickListener(ImageView pLoadSession, tabRowModel model, GeckoView pGeckoView, ImageButton mRemoveRow){
        pLoadSession.setOnClickListener(v -> {
            pGeckoView.releaseSession();
            activityContextManager.getInstance().getHomeController().onLoadTab(model.getSession(), false);
            activityContextManager.getInstance().getTabController().onBackPressed(null);
            activityContextManager.getInstance().getTabController().initTabCount();
        });

        mRemoveRow.setOnClickListener(v -> {
            model.getSession().closeSession();
            activityContextManager.getInstance().getTabController().onRemoveView(model.getSession().getSessionID());
            notifyDataSetChanged();
            activityContextManager.getInstance().getTabController().initTabCount();
        });
    }

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder
    {
        GeckoView mGeckoView;
        TextView mHeader;
        TextView mDescription;
        ImageView mLoadSession;
        ImageButton mRemoveRow;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(tabRowModel model) {
            mGeckoView = itemView.findViewById(R.id.pGeckoView);
            mHeader = itemView.findViewById(R.id.pHeader);
            mDescription = itemView.findViewById(R.id.pDescription);
            mLoadSession = itemView.findViewById(R.id.pLoadSession);
            mRemoveRow = itemView.findViewById(R.id.pRemoveRow);

            mGeckoView.releaseSession();
            mGeckoView.setSession(model.getSession());
            if(model.getmId() == mCurrentSession){
                mCurrentGeckoView = mGeckoView;
            }
            model.setGeckoView(mGeckoView);
            mHeader.setText(model.getSession().getTitle());
            mDescription.setText(model.getSession().getCurrentURL());

            setOnClickListener(mLoadSession, model, mGeckoView, mRemoveRow);
        }
    }

}
