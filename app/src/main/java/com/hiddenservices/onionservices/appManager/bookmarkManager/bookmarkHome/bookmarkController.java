package com.hiddenservices.onionservices.appManager.bookmarkManager.bookmarkHome;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.bookmarkManager.bookmarkSettings.bookmarkSettingController;
import com.hiddenservices.onionservices.dataManager.models.bookmarkRowModel;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.editTextManager;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeController;
import com.hiddenservices.onionservices.constants.enums;
import com.hiddenservices.onionservices.constants.keys;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.dataManager.dataController;
import com.hiddenservices.onionservices.dataManager.dataEnums;
import com.hiddenservices.onionservices.eventObserver;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.hiddenservices.onionservices.appManager.activityThemeManager;
import com.hiddenservices.onionservices.pluginManager.pluginController;
import com.hiddenservices.onionservices.pluginManager.pluginEnums;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.hiddenservices.onionservices.appManager.bookmarkManager.bookmarkHome.bookmarkEnums.eBookmarkViewCommands.M_VERTIFY_SELECTION_MENU;
import static com.hiddenservices.onionservices.constants.keys.M_ACTIVITY_RESPONSE;
import static com.hiddenservices.onionservices.constants.responses.BOOKMARK_SETTING_CONTROLLER_SHOW_DELETE_ALERT;
import static com.hiddenservices.onionservices.constants.responses.BOOKMARK_SETTING_CONTROLLER_SHOW_SUCCESS_ALERT;
import static com.hiddenservices.onionservices.constants.sql.SQL_CLEAR_BOOKMARK;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_CLEAR_BOOKMARK;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_DELETE_BOOKMARK;
import static com.hiddenservices.onionservices.pluginManager.pluginEnums.eMessageManager.M_UPDATE_BOOKMARK;


public class bookmarkController extends AppCompatActivity {
    /*Private Variables*/

    private bookmarkModel mbookmarkModel;
    private homeController mHomeController;
    private bookmarkAdapter mbookmarkAdapter;
    private LinearLayout mHeaderContainer;
    private TextView mTitle;
    private bookmarkViewController mbookmarkViewController;

    /*Private Views*/

    private TextView mEmptyListNotification;
    private editTextManager mSearchInput;
    private RecyclerView mRecycleView;
    private Button mClearButton;
    private ImageButton mMenuButton;
    private ImageButton mSearchButton;

    /*Local Variables*/

    private int mResponseRequestCode = 10113;

    /*Initializations*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_view);
        initializeListModel();
        initializeViews();
        initializeList();
        initCustomListeners();
        initSwipe();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_ACTIVITY_CREATED);
        super.onConfigurationChanged(newConfig);

        if (newConfig.uiMode != getResources().getConfiguration().uiMode) {
            activityContextManager.getInstance().onResetTheme();
            activityThemeManager.getInstance().onConfigurationChanged(this);
        }

    }

    public void initializeListModel() {
        mbookmarkModel = new bookmarkModel();
        activityContextManager.getInstance().setBookmarkController(this);
        mHomeController = activityContextManager.getInstance().getHomeController();
    }

    public void initializeViews() {
        mEmptyListNotification = findViewById(R.id.pEmptyListNotification);
        mSearchInput = findViewById(R.id.pSearchInput);
        mRecycleView = findViewById(R.id.pRecycleView);
        mClearButton = findViewById(R.id.pClearButton);
        mMenuButton = findViewById(R.id.pMenuButton);
        mSearchButton = findViewById(R.id.pSearchButton);
        mTitle = findViewById(R.id.pTitle);
        mHeaderContainer = findViewById(R.id.pHeaderContainer);

        mbookmarkViewController = new bookmarkViewController(mEmptyListNotification, mSearchInput, mRecycleView, mClearButton, this, mMenuButton, mSearchButton, mHeaderContainer, mTitle);
    }

    public void initializeList() {
        ArrayList<bookmarkRowModel> model = (ArrayList<bookmarkRowModel>) dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_GET_BOOKMARK, null);
        mbookmarkModel.setList(model);
        bookmarkAdapter adapter = new bookmarkAdapter(mbookmarkModel.getList(), new adapterCallback(), this);
        mbookmarkAdapter = adapter;
        adapter.invokeFilter(false);
        mRecycleView.setAdapter(adapter);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_UPDATE_LIST_IF_EMPTY, Arrays.asList(mbookmarkModel.getList().size(), 0));
    }

    public void initCustomListeners() {
        mClearButton.requestFocusFromTouch();

        mSearchInput.setEventHandler(new edittextManagerCallback());

        mClearButton.setOnClickListener(v -> {
            pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(strings.GENERIC_EMPTY_STR, this), M_CLEAR_BOOKMARK);
        });

        mSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                helperMethod.hideKeyboard(this);
                return true;
            }
            return false;
        });

        mSearchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // mSearchInput.clearFocus();
            } else {
                mbookmarkAdapter.setFilter(mSearchInput.getText().toString());
                mbookmarkAdapter.invokeFilter(true);
                mbookmarkAdapter.notifyDataSetChanged();
            }
        });

        mSearchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mbookmarkAdapter.setFilter(mSearchInput.getText().toString());
                mbookmarkAdapter.invokeFilter(true);
                mbookmarkAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onDestroy() {
        activityContextManager.getInstance().onRemoveStack(this);
        activityContextManager.getInstance().setHistoryController(null);
        super.onDestroy();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mbookmarkAdapter.invokeSwipeClose(position);
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                final int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(swipeFlags, dragFlags);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Canvas mCanvas = (Canvas) mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.ON_GENERATE_SWIPABLE_BACKGROUND, Arrays.asList(c, viewHolder, dX, actionState));
                super.onChildDraw(mCanvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecycleView);
    }

    /*View Custom Overrides*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (status.sSettingIsAppPaused && (level == 80 || level == 15)) {
            dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.HOME_LOW_MEMORY, true));
            finish();
        }
    }

    @Override
    public void onResume() {
        activityContextManager.getInstance().onCheckPurgeStack();
        pluginController.getInstance().onLanguageInvoke(Collections.singletonList(this), pluginEnums.eLangManager.M_RESUME);
        activityContextManager.getInstance().setCurrentActivity(this);
        status.sSettingIsAppPaused = false;
        activityContextManager.getInstance().onStack(this);
        if (mbookmarkAdapter != null) {
            mbookmarkAdapter.notifyDataSetChanged();
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        status.sSettingIsAppPaused = true;

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mSearchInput.getVisibility() == View.VISIBLE) {
            onHideSearch(null);
        } else if ((Boolean) mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.GET_LONG_SELECTED_STATUS, null)) {
            onClearMultipleSelection(null);
        } else {
            activityContextManager.getInstance().onRemoveStack(this);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mResponseRequestCode) {
            if (data != null) {
                if (data.getExtras().getString(M_ACTIVITY_RESPONSE).equals(BOOKMARK_SETTING_CONTROLLER_SHOW_SUCCESS_ALERT)) {
                    pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_UPDATE_BOOKMARK);
                } else if (data.getExtras().getString(M_ACTIVITY_RESPONSE).equals(BOOKMARK_SETTING_CONTROLLER_SHOW_DELETE_ALERT)) {
                    initializeList();
                    pluginController.getInstance().onMessageManagerInvoke(Collections.singletonList(this), M_DELETE_BOOKMARK);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /* UI Redirection */

    public void onClose(View view) {
        onBackPressed();
    }

    public void onHideSearch(View view) {
        ((bookmarkAdapter) mRecycleView.getAdapter()).onUpdateSearchStatus((boolean) mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_HIDE_SEARCH, null));
    }

    public void onLongPressMenu(View view) {
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_LONG_PRESS_MENU, Collections.singletonList(view));
    }

    public void onOpenMultipleTabs(View view) {
        ArrayList<String> m_long_selected_urk = (ArrayList<String>) mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.GET_LONG_SELECTED_URL, null);
        for (int m_counter = 0; m_counter < m_long_selected_urk.size(); m_counter++) {
            mHomeController.postNewLinkTabAnimation(m_long_selected_urk.get(m_counter), false);
        }
        onClose(null);
        mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.M_CLEAR_LONG_SELECTED_URL, null);
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_CLOSE_MENU, null);
    }

    public void onShareSelectedURL(View view) {
        String m_joined_url = (String) mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.GET_SELECTED_URL, null);
        helperMethod.shareURL(this, m_joined_url);
        mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.M_CLEAR_LONG_SELECTED_URL, null);
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_CLOSE_MENU, null);
    }

    public void onClearMultipleSelection(View view) {
        mbookmarkAdapter.onTrigger(bookmarkEnums.eBookmarkAdapterCommands.M_CLEAR_LONG_SELECTED_URL, null);
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_CLOSE_MENU, null);
        mbookmarkViewController.onTrigger(M_VERTIFY_SELECTION_MENU, Collections.singletonList(true));
    }

    public void onDeleteSelected(View view) {
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_CLOSE_MENU, null);
        mbookmarkAdapter.onDeleteSelected();
    }

    /*Helper Methods*/

    public void onclearData() {
        mbookmarkModel.clearList();
        ((bookmarkAdapter) Objects.requireNonNull(mRecycleView.getAdapter())).invokeFilter(true);
        mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_CLEAR_LIST, null);
        dataController.getInstance().invokeSQLCipher(dataEnums.eSqlCipherCommands.M_EXEC_SQL, Arrays.asList(SQL_CLEAR_BOOKMARK, null));
        mRecycleView.setAlpha(0);
    }

    /*Event Observer*/

    public class edittextManagerCallback implements eventObserver.eventListener {

        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {

            if (e_type.equals(enums.etype.ON_KEYBOARD_CLOSE)) {
                onBackPressed();
            }
            return null;
        }
    }

    public class adapterCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            if (e_type.equals(enums.etype.url_triggered)) {
                String url_temp = helperMethod.completeURL(data.get(0).toString());
                mHomeController.onLoadURL(url_temp);
                finish();
            } else if (e_type.equals(enums.etype.url_triggered_new_tab)) {
                String url_temp = helperMethod.completeURL(data.get(0).toString());
                mHomeController.onOpenLinkNewTab(url_temp);
                finish();
            } else if (e_type.equals(enums.etype.fetch_favicon)) {
                mHomeController.onGetFavIcon((ImageView) data.get(0), (String) data.get(1));
            } else if (e_type.equals(enums.etype.url_clear)) {
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_DELETE_BOOKMARK, data);
            } else if (e_type.equals(enums.etype.url_clear_at)) {
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_DELETE_BOOKMARK, data);
            } else if (e_type.equals(enums.etype.is_empty)) {
                mbookmarkViewController.onTrigger(bookmarkEnums.eBookmarkViewCommands.M_UPDATE_LIST_IF_EMPTY, Arrays.asList(mbookmarkModel.getList().size(), 300));
            } else if (e_type.equals(enums.etype.remove_from_database)) {
                dataController.getInstance().invokeSQLCipher(dataEnums.eSqlCipherCommands.M_DELETE_FROM_HISTORY, Arrays.asList(data.get(0), strings.HISTORY_TITLE));
            } else if (e_type.equals(enums.etype.on_verify_selected_url_menu)) {
                mbookmarkViewController.onTrigger(M_VERTIFY_SELECTION_MENU, data);
            } else if (e_type.equals(enums.etype.M_OPEN_BOOKMARK_SETTING)) {
                Intent intent = new Intent(bookmarkController.this, bookmarkSettingController.class);
                intent.putExtra(keys.BOOKMARK_SETTING_NAME, (String) data.get(0));
                intent.putExtra(keys.BOOKMARK_SETTING_URL, (String) data.get(1));
                intent.putExtra(keys.BOOKMARK_SETTING_ID, (int) data.get(2));
                startActivityForResult(intent, mResponseRequestCode);
            }
            return null;
        }
    }
}
