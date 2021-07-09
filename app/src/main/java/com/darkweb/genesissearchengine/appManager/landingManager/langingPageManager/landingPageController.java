package com.darkweb.genesissearchengine.appManager.landingManager.langingPageManager;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.darkweb.genesissearchengine.eventObserver;
import com.example.myapplication.R;

import java.util.Collections;
import java.util.List;

import static com.darkweb.genesissearchengine.appManager.landingManager.landingEnums.eLandingPageControllerCallbackCommands.M_ON_LANDING_PAGE_FINISH_TRIGGERED;


public class landingPageController extends Fragment {

    /*UI Variables*/

    private TextView mHeader;
    private TextView mSubHeader;
    private TextView mSubmit;

    /*Local Variables*/

    private eventObserver.eventListener mEvent;
    private landingPageDataModel mDataModel;
    private landingPageViewController mLandingPageViewController;

    /*Initializations*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.landing_view, container, false);

        initializeModels(view);
        initializeLocalEventHandlers();
        return view;

    }

    private void initializeModels(View pView){
        mHeader = pView.findViewById(R.id.pHeader);
        mSubHeader = pView.findViewById(R.id.pSubHeader);
        mSubmit = pView.findViewById(R.id.pNextButton);

        mLandingPageViewController = new landingPageViewController(this,new landingPageViewCallback(), mHeader, mSubHeader, mSubmit, mDataModel);
    }

    public landingPageController onInitialize(eventObserver.eventListener pEvent, landingPageDataModel pDataModel) {
        landingPageController mSlidePage = new landingPageController();
        Bundle args = new Bundle();

        mDataModel = pDataModel;
        mEvent = pEvent;

        mSlidePage.setArguments(args);
        return mSlidePage;
    }


    private void initializeLocalEventHandlers(){
        mSubmit.setOnClickListener(v -> mEvent.invokeObserver(Collections.singletonList(mDataModel.getPageType()), M_ON_LANDING_PAGE_FINISH_TRIGGERED));
    }

    private class landingPageViewCallback implements eventObserver.eventListener{

        @Override
        public Object invokeObserver(List<Object> pData, Object pType)
        {
            return null;
        }
    }

}
