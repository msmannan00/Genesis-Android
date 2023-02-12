package com.hiddenservices.onionservices.constants;

public class enums {

    /*General Enums*/
    public static class StoreType {
        public static final int GOOGLE_PLAY = 0;
        public static final int AMAZON = 1;
        public static final int SAMSUNG = 2;
        public static final int HUAWEI = 0;
    }

    public static class MemoryStatus {
        public static final int STABLE = 0;
        public static final int MODERATE_MEMORY = 1;
        public static final int LOW_MEMORY = 2;
        public static final int CRITICAL_MEMORY = 3;
    }

    public enum MediaController {
        DESTROY, PLAY, PAUSE, SKIP_FORWARD, SKIP_BACKWARD, STOP
    }

    public static class AddTabCallback {
        public static final int TAB_ADDED = 0;
        public static final int TAB_FULL = 1;
    }

    public static class Theme {
        public static final int THEME_LIGHT = 0;
        public static final int THEME_DARK = 1;
        public static final int THEME_DEFAULT = 2;
    }

    public static class WidgetResponse {
        public static final int NONE = 0;
        public static final int VOICE = 1;
        public static final int SEARCHBAR = 2;
    }

    public static class ScrollDirection {
        public static final int VERTICAL = 1;
    }

    public static class WidgetCommands {
        public static final String OPEN_APPLICATION = "mOpenApplication";
        public static final String OPEN_VOICE = "mOpenVoice";
    }

    public static class MediaNotificationReciever {
        public static final int PLAY = 0;
        public static final int PAUSE = 1;
        public static final int SKIP_FOWWARD = 2;
        public static final int SKIP_BACKWARD = 3;
    }

    public static class DownloadNotificationReciever {
        public static final int DOWNLOAD_CANCEL = 0;
        public static final int DOWNLOAD_OPEN = 1;
    }
}