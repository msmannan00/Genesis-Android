package com.darkweb.genesissearchengine.appManager.historyManager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.constants.strings;
import com.example.myapplication.R;
import java.util.List;
import java.util.Objects;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class historyViewController
{
    /*Private Variables*/
    private AppCompatActivity m_context;

    private ImageView m_empty_list;
    private EditText m_search;
    private RecyclerView m_listview;
    private Button m_clearButton;
    private ImageButton m_menu_button;
    private ImageButton m_search_button;

    /*Initializations*/

    historyViewController(ImageView p_empty_list, EditText p_search, RecyclerView p_listview, Button p_clearButton,AppCompatActivity p_context,ImageButton p_menu_button,ImageButton p_search_button)
    {
        this.m_empty_list = p_empty_list;
        this.m_search = p_search;
        this.m_listview = p_listview;
        this.m_clearButton = p_clearButton;
        this.m_context = p_context;
        this.m_menu_button = p_menu_button;
        this.m_search_button = p_search_button;

        initPostUI();
    }

    private void initPostUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = m_context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                window.setStatusBarColor(m_context.getResources().getColor(R.color.blue_dark));
            }
            else {
                m_context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                m_context.getWindow().setStatusBarColor(ContextCompat.getColor(m_context, R.color.white));
            }
        }
    }

    private void updateIfListEmpty(int size,int duration){
        if(size>0){
            m_empty_list.animate().setDuration(duration).alpha(0f);
            // m_clearButton.animate().setDuration(duration).alpha(1f);
            m_clearButton.setText("CLEAR HISTORY");
            m_clearButton.setClickable(true);
        }
        else {
            m_empty_list.animate().setDuration(duration).alpha(1f);
            // m_clearButton.animate().setDuration(duration).alpha(0f);
            m_clearButton.setText("NO HISTORY FOUND");
            m_clearButton.setClickable(false);
        }
    }

    public void onCloseMenu(){
        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    private void onSelectionMenu(boolean p_status){
        if(!p_status){
            m_clearButton.setClickable(false);
            m_clearButton.setTextColor(m_context.getApplication().getResources().getColor(R.color.float_white));
            m_menu_button.setVisibility(View.VISIBLE);
            m_search_button.setVisibility(View.GONE);
            if (m_search.getVisibility() == View.VISIBLE){
                m_search.animate().cancel();
                m_search.animate().alpha(0).setDuration(150).withEndAction(() -> {
                    m_search.getText().clear();
                    m_search.setVisibility(View.GONE);
                });
                m_search.setClickable(false);
            }
        }else {
            if (m_search.getVisibility() != View.VISIBLE) {
                m_clearButton.setClickable(true);
                m_clearButton.setTextColor(m_context.getApplication().getResources().getColor(R.color.blue));
            }
            m_menu_button.setVisibility(View.GONE);
            m_search_button.setVisibility(View.VISIBLE);

        }

    }

    private void updateList(){
        int index = Objects.requireNonNull(m_listview.getAdapter()).getItemCount()-1;
        m_listview.getAdapter().notifyDataSetChanged();
        m_listview.scrollToPosition(index);
    }

    private void removeFromList(int index)
    {
        Objects.requireNonNull(m_listview.getAdapter()).notifyItemRemoved(index);
        m_listview.getAdapter().notifyItemRangeChanged(index, m_listview.getAdapter().getItemCount());
    }

    private void clearList(){
        Objects.requireNonNull(m_listview.getAdapter()).notifyDataSetChanged();
        updateIfListEmpty(m_listview.getAdapter().getItemCount(),300);
        m_search.clearFocus();
        m_search.setText(strings.EMPTY_STR);
    }

    public void onTrigger(historyEnums.eHistoryViewCommands p_commands, List<Object> p_data){
        if(p_commands == historyEnums.eHistoryViewCommands.M_UPDATE_LIST_IF_EMPTY){
            updateIfListEmpty((int)p_data.get(0), (int)p_data.get(1));
        }
        else if(p_commands == historyEnums.eHistoryViewCommands.M_UPDATE_LIST){
            updateList();
        }
        else if(p_commands == historyEnums.eHistoryViewCommands.M_REMOVE_FROM_LIST){
            removeFromList((int)p_data.get(0));
        }
        else if(p_commands == historyEnums.eHistoryViewCommands.M_CLEAR_LIST){
            clearList();
        }
        else if(p_commands == historyEnums.eHistoryViewCommands.M_VERTIFY_SELECTION_MENU){
            onSelectionMenu((boolean)p_data.get(0));
        }
    }

    public boolean onHideSearch() {
        if(m_search.getVisibility() == View.VISIBLE){
            m_search.animate().cancel();
            m_search.animate().alpha(0).setDuration(150).withEndAction(() -> {
                m_search.setVisibility(View.GONE);
                m_search.setText(strings.EMPTY_STR);
            });
            m_search.setText(strings.EMPTY_STR);
            m_search.setClickable(false);
            m_clearButton.setClickable(true);
            m_clearButton.setTextColor(m_context.getApplication().getResources().getColor(R.color.blue));
            return false;
        }else {
            m_search.animate().cancel();
            m_search.setAlpha(0f);
            m_search.animate().setDuration(150).alpha(1);
            m_search.setVisibility(View.VISIBLE);
            m_search.setClickable(true);
            m_clearButton.setClickable(false);
            m_clearButton.setTextColor(m_context.getApplication().getResources().getColor(R.color.float_white));
            m_search.requestFocus();
            InputMethodManager imm = (InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return true;
        }
    }

    private PopupWindow popupWindow = null;
    public void onLongPressMenu(View view) {

        if(popupWindow!=null){
            popupWindow.dismiss();
        }

        LayoutInflater layoutInflater
                = (LayoutInflater) view.getContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.history_choose_popup_menu, null);


        popupWindow = new PopupWindow(
                popupView,
                ActionMenuView.LayoutParams.WRAP_CONTENT,
                ActionMenuView.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        popupWindow.showAtLocation(view, Gravity.TOP|Gravity.END,0,0);
        popupWindow.setElevation(7);
    }
}
