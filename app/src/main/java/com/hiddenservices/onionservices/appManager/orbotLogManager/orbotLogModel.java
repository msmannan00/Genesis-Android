package com.hiddenservices.onionservices.appManager.orbotLogManager;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import org.torproject.android.service.wrapper.logRowModel;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCallbackCommands.M_UPDATE_FLOATING_BUTTON;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCallbackCommands.M_UPDATE_LOGS;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCallbackCommands.M_UPDATE_RECYCLE_VIEW;
import static java.lang.Thread.sleep;
import static org.mozilla.gecko.util.ThreadUtils.runOnUiThread;

class orbotLogModel {
    /*Private Variables*/

    private ArrayList<logRowModel> mModelList = new ArrayList<>();
    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;
    private LogHandler mLogHandler;
    private int mLogCounter;

    /*Helper Methods*/

    public orbotLogModel(AppCompatActivity pContext, eventObserver.eventListener pEvent) {
        this.mContext = pContext;
        this.mEvent = pEvent;
    }

    protected void onInit(){
        initLogHandler();
        this.initList(orbotLocalConstants.mTorLogsHistory);
    }

    private void initLogHandler() {
        this.mLogHandler = new LogHandler();
        this.mLogHandler.execute();
    }

    private void initList(ArrayList<logRowModel> pModel) {
        if (pModel.size() > 0) {
            mModelList.clear();
            mModelList.addAll(pModel);
        } else {
            mModelList.add(new logRowModel(constants.CONST_LOGS_DEFAULT_MESSAGE, helperMethod.getCurrentTime()));
        }
    }

    private ArrayList<logRowModel> getList() {
        return mModelList;
    }

    private int getListSize() {
        return mModelList.size();
    }

    /*Triggers*/

    @SuppressLint("StaticFieldLeak")
    class LogHandler extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... arg0) {
            try {
                sleep(1000);
                mLogCounter = mModelList.size();
                while (!mContext.isDestroyed()) {
                    if (status.sLogThemeStyleAdvanced) {
                        sleep(800);
                    } else {
                        sleep(100);
                    }

                    if (mLogCounter > 0) {
                        runOnUiThread(() -> {
                            if (orbotLocalConstants.mTorLogsHistory.size() > mLogCounter) {
                                mModelList.add(orbotLocalConstants.mTorLogsHistory.get(mLogCounter));
                                if (!status.sLogThemeStyleAdvanced) {
                                    mEvent.invokeObserver(Collections.singletonList(mLogCounter), M_UPDATE_LOGS);
                                } else {
                                    mEvent.invokeObserver(Collections.singletonList(mModelList.size() - 1), M_UPDATE_RECYCLE_VIEW);
                                }

                                if (!orbotLogStatus.sUIInteracted) {
                                    helperMethod.onDelayHandler(mContext, 150, () -> {
                                        if (!orbotLogStatus.sUIInteracted) {
                                            mEvent.invokeObserver(null, M_UPDATE_FLOATING_BUTTON);
                                        }
                                        return null;
                                    });
                                }
                                mLogCounter += 1;
                            }
                        });
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void onTrigger(orbotLogEnums.eOrbotLogViewCommands ignoredPCommands, List<Object> ignoredPData) {
    }

    public Object onTrigger(orbotLogEnums.eOrbotLogModelCommands pCommands) {
        if (pCommands.equals(orbotLogEnums.eOrbotLogModelCommands.M_GET_LIST)) {
            return getList();
        } else if (pCommands.equals(orbotLogEnums.eOrbotLogModelCommands.M_GET_LIST_SIZE)) {
            return getListSize();
        }
        return null;
    }

}
