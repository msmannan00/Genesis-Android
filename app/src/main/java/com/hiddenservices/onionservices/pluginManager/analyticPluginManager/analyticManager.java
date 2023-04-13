//package com.hiddenservices.onionservices.pluginManager.analyticPluginManager;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.hiddenservices.onionservices.eventObserver;
//import com.hiddenservices.onionservices.pluginManager.pluginEnums;
//import com.flurry.android.FlurryAgent;
//
//import java.lang.ref.WeakReference;
//import java.util.List;
//
//public class analyticManager {
//    /*Private Variables*/
//
//    private WeakReference<AppCompatActivity> mAppContext;
//
//    /*Initializations*/
//
//    public analyticManager(WeakReference<AppCompatActivity> pAppContext, eventObserver.eventListener pEvent) {
//        this.mAppContext = pAppContext;
//
//        initialize();
//    }
//
//    private void initialize() {
//        new FlurryAgent.Builder().withLogEnabled(false).build(mAppContext.get(), "ND6QCR4JSHSJ25VWC8DN");
//    }
//
//    /*External Triggers*/
//
//    private void logEvent(String pValue) {
//        if (FlurryAgent.isSessionActive()) {
//            FlurryAgent.logEvent(pValue);
//        }
//    }
//
//    public void onTrigger(List<Object> pData, pluginEnums.eAnalyticManager pEventType) {
//        if (pEventType.equals(pluginEnums.eAnalyticManager.M_LOG_EVENT)) {
//            logEvent((String) pData.get(0));
//        }
//    }
//}
