package com.hiddenservices.onionservices.pluginManager.orbotPluginManager;

import com.hiddenservices.onionservices.constants.strings;
import java.util.List;
import static com.hiddenservices.onionservices.pluginManager.orbotPluginManager.orbotPluginEnums.eLogManager.M_GET_CLEANED_LOGS;

public class orbotLogManager {

    private String onGetCleanedLogs(String pLogs) {

        if (pLogs.equals("Starting Orion | Please Wait ...")) {
            return pLogs;
        }

        if (pLogs.equals("No internet connection")) {
            return "Warning | " + pLogs;
        } else if (pLogs.startsWith("Invalid Configuration")) {
            return pLogs;
        }

        if (!pLogs.equals(strings.GENERIC_EMPTY_STR)) {
            String Logs = pLogs;
            Logs = "Installing | " + Logs.replace("FAILED", "Securing");
            return Logs;
        }
        return "Loading Please Wait...";
    }

    /*External Triggers*/

    public Object onTrigger(List<Object> pData, orbotPluginEnums.eLogManager pEventType) {
        if (pEventType.equals(M_GET_CLEANED_LOGS)) {
            return onGetCleanedLogs((String) pData.get(0));
        }
        return null;
    }

}
