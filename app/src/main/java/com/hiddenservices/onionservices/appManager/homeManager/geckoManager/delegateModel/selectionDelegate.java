package com.hiddenservices.onionservices.appManager.homeManager.geckoManager.delegateModel;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;

import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hiddenservices.onionservices.appManager.activityContextManager;

import org.mozilla.gecko.util.ThreadUtils;
import org.mozilla.geckoview.GeckoSession;

/**
 * Class that implements a basic SelectionActionDelegate. This class is used by GeckoView by
 * default if the consumer does not explicitly set a SelectionActionDelegate.
 * <p>
 * To provide custom actions, extend this class and override the following methods,
 * <p>
 * 1) Override {@link #getAllActions} to include custom action IDs in the returned array. This
 * array must include all actions, available or not, and must not change over the class lifetime.
 * <p>
 * 2) Override {@link #isActionAvailable} to return whether a custom action is currently available.
 * <p>
 * 3) Override {@link #prepareAction} to set custom title and/or icon for a custom action.
 * <p>
 * 4) Override {@link #performAction} to perform a custom action when used.
 */
@UiThread
public class selectionDelegate implements ActionMode.Callback,
        GeckoSession.SelectionActionDelegate {
    private static final String LOGTAG = "BasicSelectionAction";

    protected static final String ACTION_PROCESS_TEXT = "android.intent.action.PROCESS_TEXT";

    private static final String[] FLOATING_TOOLBAR_ACTIONS = new String[]{
            ACTION_CUT, ACTION_COPY, ACTION_PASTE, ACTION_SELECT_ALL, "SEARCH"
    };
    private static final String[] FIXED_TOOLBAR_ACTIONS = new String[]{
            ACTION_SELECT_ALL, ACTION_CUT, ACTION_COPY, ACTION_PASTE, "SEARCH"
    };

    protected final @NonNull
    Activity mActivity;
    protected final boolean mUseFloatingToolbar;
    protected final @NonNull
    Matrix mTempMatrix = new Matrix();
    protected final @NonNull
    RectF mTempRect = new RectF();
    private boolean mFullScreen = false;

    private boolean mExternalActionsEnabled;

    protected @Nullable
    ActionMode mActionMode;
    protected @Nullable
    GeckoSession mSession;
    protected @Nullable
    Selection mSelection;
    protected boolean mRepopulatedMenu;

    public void setFullScreen(boolean pFullScreen) {
        mFullScreen = pFullScreen;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private class Callback2Wrapper extends ActionMode.Callback2 {
        @Override
        public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
            return selectionDelegate.this.onCreateActionMode(actionMode, menu);
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
            return selectionDelegate.this.onPrepareActionMode(actionMode, menu);
        }

        @Override
        public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
            return selectionDelegate.this.onActionItemClicked(actionMode, menuItem);
        }

        @Override
        public void onDestroyActionMode(final ActionMode actionMode) {
            selectionDelegate.this.onDestroyActionMode(actionMode);
        }

        @Override
        public void onGetContentRect(final ActionMode mode, final View view, final Rect outRect) {
            super.onGetContentRect(mode, view, outRect);
            selectionDelegate.this.onGetContentRect(mode, view, outRect);
        }
    }

    @SuppressWarnings("checkstyle:javadocmethod")
    public selectionDelegate(final @NonNull Activity activity,
                             final boolean useFloatingToolbar) {
        mActivity = activity;
        mUseFloatingToolbar = useFloatingToolbar;
        mExternalActionsEnabled = true;
    }

    /**
     * Set whether to include text actions from other apps in the floating toolbar.
     *
     * @param enable True if external actions should be enabled.
     */
    public void enableExternalActions(final boolean enable) {
        ThreadUtils.assertOnUiThread();
        mExternalActionsEnabled = enable;

        if (mActionMode != null) {
            mActionMode.invalidate();
        }
    }

    /**
     * Return list of all actions in proper order, regardless of their availability at present.
     * Override to add to or remove from the default set.
     *
     * @return Array of action IDs in proper order.
     */
    protected @NonNull
    String[] getAllActions() {
        return mUseFloatingToolbar ? FLOATING_TOOLBAR_ACTIONS
                : FIXED_TOOLBAR_ACTIONS;
    }

    /**
     * Return whether an action is presently available. Override to indicate
     * availability for custom actions.
     *
     * @param id Action ID.
     * @return True if the action is presently available.
     */
    protected boolean isActionAvailable(final @NonNull String id) {
        if (mSelection == null || mSelection.text.length() < 1 || mSelection.text.getBytes().length >= 500000) {
            return false;
        }

        if (mExternalActionsEnabled && !mSelection.text.isEmpty() &&
                ACTION_PROCESS_TEXT.equals(id)) {
            final PackageManager pm = mActivity.getPackageManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return pm.resolveActivity(getProcessTextIntent(), PackageManager.MATCH_DEFAULT_ONLY) != null;
            }
        }
        if (id.equals("SEARCH") && !mSelection.text.isEmpty() && mExternalActionsEnabled) {
            return true;
        }

        return mSelection.isActionAvailable(id);
    }

    /**
     * Provides access to whether there are text selection actions available. Override to indicate
     * availability for custom actions.
     *
     * @return True if there are text selection actions available.
     */
    public boolean isActionAvailable() {
        if (mSelection == null) {
            return false;
        }

        return isActionAvailable(ACTION_PROCESS_TEXT) ||
                !mSelection.availableActions.isEmpty();
    }

    /**
     * Prepare a menu item corresponding to a certain action. Override to prepare
     * menu item for custom action.
     *
     * @param id   Action ID.
     * @param item New menu item to prepare.
     */
    protected void prepareAction(final @NonNull String id, final @NonNull MenuItem item) {

        if (mFullScreen) {
            return;
        }
        switch (id) {
            case ACTION_CUT:
                item.setTitle(android.R.string.cut);
                break;
            case ACTION_COPY:
                item.setTitle(android.R.string.copy);
                break;
            case "SEARCH":
                item.setTitle("Search");
                break;
            case ACTION_PASTE:
                item.setTitle(android.R.string.paste);
                break;
            case ACTION_SELECT_ALL:
                item.setTitle(android.R.string.selectAll);
                break;
            case ACTION_PROCESS_TEXT:
                throw new IllegalStateException("Unexpected action");
        }
    }

    /**
     * Perform the specified action. Override to perform custom actions.
     *
     * @param id   Action ID.
     * @param item Nenu item for the action.
     * @return True if the action was performed.
     */
    protected boolean performAction(final @NonNull String id, final @NonNull MenuItem item) {
        if (ACTION_PROCESS_TEXT.equals(id)) {
            try {
                mActivity.startActivity(item.getIntent());
            } catch (final ActivityNotFoundException e) {
                Log.e(LOGTAG, "Cannot perform action", e);
                return false;
            }
            return true;
        }

        if (mSelection == null) {
            return false;
        }
        if (id.equals("SEARCH")) {
            if (activityContextManager.getInstance() != null && activityContextManager.getInstance().getHomeController() != null) {
                activityContextManager.getInstance().getHomeController().onSearchString(mSelection.text);
            }
            clearSelection();
            mActionMode.finish();
            return false;
        }
        mSelection.execute(id);

        if (ACTION_COPY.equals(id)) {
            if (mUseFloatingToolbar) {
                clearSelection();
            } else {
                mActionMode.finish();
            }
        }
        return true;
    }

    /**
     * Get the current selection object. This object should not be stored as it does not update
     * when the selection becomes invalid. Stale actions are ignored.
     *
     * @return The {@link GeckoSession.SelectionActionDelegate.Selection} attached to the current
     * action menu. <code>null</code> if no action menu is active.
     */
    public @Nullable
    Selection getSelection() {
        return mSelection;
    }

    /**
     * Clear the current selection, if possible.
     */
    public void clearSelection() {
        if (mSelection == null) {
            return;
        }

        if (isActionAvailable(ACTION_COLLAPSE_TO_END)) {
            mSelection.collapseToEnd();
        } else if (isActionAvailable(ACTION_UNSELECT)) {
            mSelection.unselect();
        } else {
            mSelection.hide();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Intent getProcessTextIntent() {
        final Intent intent = new Intent(Intent.ACTION_PROCESS_TEXT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, mSelection.text);
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true);
        return intent;
    }

    @Override
    public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
        ThreadUtils.assertOnUiThread();
        final String[] allActions = getAllActions();
        for (final String actionId : allActions) {
            if (isActionAvailable(actionId)) {
                if (!mUseFloatingToolbar && (
                        Build.VERSION.SDK_INT == 22 || Build.VERSION.SDK_INT == 23)) {
                    // Android bug where onPrepareActionMode is not called initially.
                    onPrepareActionMode(actionMode, menu);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
        ThreadUtils.assertOnUiThread();
        final String[] allActions = getAllActions();
        boolean changed = false;

        // Whether we are repopulating an existing menu.
        mRepopulatedMenu = menu.size() != 0;

        // For each action, see if it's available at present, and if necessary,
        // add to or remove from menu.
        for (int i = 0; i < allActions.length; i++) {
            final String actionId = allActions[i];
            final int menuId = i + Menu.FIRST;

            if (ACTION_PROCESS_TEXT.equals(actionId)) {
                if (mExternalActionsEnabled && !mSelection.text.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        menu.addIntentOptions(menuId, menuId, menuId,
                                mActivity.getComponentName(),
                                /* specifiec */ null, getProcessTextIntent(),
                                /* flags */ 0, /* items */ null);
                    }
                    changed = true;
                } else if (menu.findItem(menuId) != null) {
                    menu.removeGroup(menuId);
                    changed = true;
                }
                continue;
            }

            if (isActionAvailable(actionId)) {
                if (menu.findItem(menuId) == null) {
                    prepareAction(actionId, menu.add(/* group */ Menu.NONE, menuId,
                            menuId, /* title */ ""));
                    changed = true;
                }
            } else if (menu.findItem(menuId) != null) {
                menu.removeItem(menuId);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
        ThreadUtils.assertOnUiThread();
        MenuItem realMenuItem = null;
        if (mRepopulatedMenu) {
            // When we repopulate an existing menu, Android can sometimes give us an old,
            // deleted MenuItem. Find the current MenuItem that corresponds to the old one.
            final Menu menu = actionMode.getMenu();
            final int size = menu.size();
            for (int i = 0; i < size; i++) {
                final MenuItem item = menu.getItem(i);
                if (item == menuItem || (item.getItemId() == menuItem.getItemId() &&
                        item.getTitle().equals(menuItem.getTitle()))) {
                    realMenuItem = item;
                    break;
                }
            }
        } else {
            realMenuItem = menuItem;
        }

        if (realMenuItem == null) {
            return false;
        }
        final String[] allActions = getAllActions();
        return performAction(allActions[realMenuItem.getItemId() - Menu.FIRST], realMenuItem);
    }

    @Override
    public void onDestroyActionMode(final ActionMode actionMode) {
        ThreadUtils.assertOnUiThread();
        if (!mUseFloatingToolbar) {
            clearSelection();
        }
        mSession = null;
        mSelection = null;
        mActionMode = null;
    }

    @SuppressWarnings("checkstyle:javadocmethod")
    public void onGetContentRect(final @Nullable ActionMode mode, final @Nullable View view,
                                 final @NonNull Rect outRect) {
        ThreadUtils.assertOnUiThread();
        if (mSelection == null || mSelection.screenRect == null) {
            return;
        }
        mSession.getClientToScreenMatrix(mTempMatrix);
        mTempMatrix.mapRect(mTempRect, mSelection.screenRect);
        mTempRect.roundOut(outRect);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onShowActionRequest(@NonNull final GeckoSession session, @NonNull final Selection selection) {
        ThreadUtils.assertOnUiThread();
        mSession = session;
        mSelection = selection;


        try {
            if (mActionMode != null) {
                if (isActionAvailable()) {
                    mActionMode.invalidate();
                } else {
                    mActionMode.finish();
                }
                return;
            }
            if (mUseFloatingToolbar) {
                String strManufacturer = android.os.Build.MANUFACTURER;
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 || (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1 && (strManufacturer.toLowerCase().contains("samsung") || android.os.Build.MODEL.toLowerCase().contains("samsung") || Build.PRODUCT.toLowerCase().contains("samsung")))) {
                    mActionMode = mActivity.startActionMode(this);
                } else {
                    mActionMode = mActivity.startActionMode(new Callback2Wrapper(), ActionMode.TYPE_FLOATING);
                }
            } else {
                mActionMode = mActivity.startActionMode(this);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onHideAction(@NonNull final GeckoSession session, final int reason) {
        ThreadUtils.assertOnUiThread();
        if (mActionMode == null) {
            return;
        }

        switch (reason) {
            case HIDE_REASON_ACTIVE_SCROLL:
            case HIDE_REASON_ACTIVE_SELECTION:
            case HIDE_REASON_INVISIBLE_SELECTION:
                if (mUseFloatingToolbar) {
                    // Hide the floating toolbar when scrolling/selecting.
                    mActionMode.finish();
                }
                break;

            case HIDE_REASON_NO_SELECTION:
                mActionMode.finish();
                break;
        }
    }
}