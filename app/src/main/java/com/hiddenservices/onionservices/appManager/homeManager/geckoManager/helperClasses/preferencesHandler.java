package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.helperClasses;

import androidx.annotation.NonNull;
import org.mozilla.gecko.EventDispatcher;
import org.mozilla.gecko.util.GeckoBundle;

public class preferencesHandler<T> {
    public final String name;
    public final T defaultValue;
    private T mValue;

    public preferencesHandler(@NonNull final String name, final T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        mValue = defaultValue;

    }
    public void add() {
        final GeckoBundle prefs = new GeckoBundle(1);
        prefs.putInt(this.name, (Integer)this.defaultValue);
        EventDispatcher.getInstance().dispatch("GeckoView:SetDefaultPrefs", prefs);
        addToBundle(prefs);
    }

    private void addToBundle(final GeckoBundle bundle) {
        final T value = mValue;
        if (value instanceof String) {
            bundle.putString(name, (String) value);
        } else if (value instanceof Integer) {
            bundle.putInt(name, (Integer) value);
        } else if (value instanceof Boolean) {
            bundle.putBoolean(name, (Boolean) value);
        } else {
            throw new UnsupportedOperationException("Unhandled pref type for " + name);
        }
    }
}
