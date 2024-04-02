package com.hiddenservices.onionservices.appManager.orbotLogManager;

import android.annotation.SuppressLint;
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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCallbackCommands.M_UPDATE_FLOATING_BUTTON;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCallbackCommands.M_UPDATE_LOGS;
import static com.hiddenservices.onionservices.appManager.orbotLogManager.orbotLogEnums.eOrbotLogModelCallbackCommands.M_UPDATE_RECYCLE_VIEW;
import static org.mozilla.gecko.util.ThreadUtils.runOnUiThread;

class orbotLogModel {
    /*Private Variables*/

    private ArrayList<logRowModel> mModelList = new ArrayList<>();
    private AppCompatActivity mContext;
    private eventObserver.eventListener mEvent;
    private LogHandler mLogHandler;
    private int mLogCounter;
    private static final Executor LOG_HANDLER_EXECUTOR = Executors.newSingleThreadExecutor();
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
        this.mLogHandler.startLogging();
        LOG_HANDLER_EXECUTOR.execute((Runnable) mLogHandler);
    }

    private void initList(ArrayList<logRowModel> pModel) {
        if (!pModel.isEmpty()) {
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
    class LogHandler {
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        public void startLogging() {
            executorService.submit(() -> {
                try {
                    Thread.sleep(1000);
                    mLogCounter = mModelList.size();
                    while (!mContext.isDestroyed()) {
                        if (status.sLogThemeStyleAdvanced) {
                            Thread.sleep(800);
                        } else {
                            Thread.sleep(100);
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
                                        helperMethod.onDelayHandler(150, () -> {
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
                    mLogHandler.stopLogging();
                } catch (InterruptedException ignored) {
                }
            });
        }

        public void stopLogging() {
            executorService.shutdownNow();
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
