package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;


import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.autofill.AutofillManager;

import androidx.annotation.NonNull;

import org.mozilla.geckoview.Autofill;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class autofillDelegate implements Autofill.Delegate {

    private GeckoView mGeckoView;

    public autofillDelegate(GeckoView pGeckoView){
        mGeckoView = pGeckoView;
    }

    private Rect displayRectForId(@NonNull final GeckoSession session, @NonNull final Autofill.Node node) {
        final Matrix matrix = new Matrix();
        final RectF rectF = new RectF(node.getScreenRect());
        session.getPageToScreenMatrix(matrix);
        matrix.mapRect(rectF);

        final Rect screenRect = new Rect();
        rectF.roundOut(screenRect);
        return screenRect;
    }

    @Override
    public void onNodeUpdate(@NonNull final GeckoSession session, @NonNull final Autofill.Node node, @NonNull final Autofill.NodeData data) {
        final AutofillManager manager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            manager = mGeckoView.getContext().getSystemService(AutofillManager.class);
            if (manager == null) {
                return;
            }
            manager.notifyViewEntered(mGeckoView, data.getId(),displayRectForId(session, node));
        }
    }

    @Override
    public void onNodeFocus(@NonNull final GeckoSession session, @NonNull final Autofill.Node node, @NonNull final Autofill.NodeData data) {
        final AutofillManager manager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            manager = mGeckoView.getContext().getSystemService(AutofillManager.class);
            if (manager == null) {
                return;
            }
            manager.notifyViewEntered(mGeckoView, data.getId(),displayRectForId(session, node));
        }
    }
}