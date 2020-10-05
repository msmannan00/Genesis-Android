package com.darkweb.genesissearchengine.appManager.historyManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class historyAdapter extends RecyclerView.Adapter<historyAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<historyRowModel> mModelList = new ArrayList<>();
    private ArrayList<historyRowModel> tempModelList;
    private ArrayList<historyRowModel> passedModelList;
    private ArrayList<Integer> m_real_id = new ArrayList<>();
    private ArrayList<Integer> m_real_index = new ArrayList<>();
    private ArrayList<String> m_long_selected = new ArrayList<>();
    private ArrayList<Integer> m_long_selected_id = new ArrayList<>();
    private PopupWindow popupWindow = null;
    private eventObserver.eventListener mEvent;
    private AppCompatActivity m_main_context;
    private ArrayList<View> m_long_pressed_list_view_holder = new ArrayList<>();

    /*Local Variables*/

    private float x1,x2;
    private String filter = strings.EMPTY_STR;
    static final int MIN_DISTANCE = 150;
    private boolean isClosing = false;
    boolean m_was_long_pressed = false;

    historyAdapter(ArrayList<historyRowModel> p_model_list, eventObserver.eventListener mEvent, AppCompatActivity p_main_context) {
        this.mEvent = mEvent;
        tempModelList = new ArrayList<>();
        passedModelList = p_model_list;
        m_main_context = p_main_context;
        initializeModelWithDate(false);
    }


    public void onLoading(){
        m_main_context.runOnUiThread(() -> {
            tempModelList.add(new historyRowModel("loading",null,-2));
            notifyItemInserted(tempModelList.size());
        });
    }

    public void onLoadingClear(){

        for(int m_counter=0;m_counter<tempModelList.size();m_counter++){
            if(tempModelList.get(m_counter).getHeader().equals("loading")){
                int finalM_counter = m_counter;
                m_main_context.runOnUiThread(() -> {
                    tempModelList.remove(finalM_counter);
                    notifyItemRemoved(finalM_counter +1);
                });
                break;
            }
        }
    }


    public void initializeModelWithDate(boolean p_filter_enabled){
        int m_real_counter=0;

        m_real_id.clear();
        m_real_index.clear();
        tempModelList.clear();
        this.mModelList.clear();
        onVerifyLongSelectedURL();

        ArrayList<historyRowModel> p_model_list = passedModelList;
        int m_date_state = -1;
        int m_last_day = -1;
        for(int counter = 0; counter< p_model_list.size(); counter++){

            if(p_filter_enabled){
                if(!p_model_list.get(counter).getHeader().toLowerCase().contains(this.filter.toLowerCase()) && !p_model_list.get(counter).getDescription().toLowerCase().contains(this.filter)){
                    continue;
                }
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(p_model_list.get(counter).getDate());

            int m_date_1 = cal.get(Calendar.DAY_OF_YEAR);
            cal.setTime(Calendar.getInstance().getTime());
            int m_date_2 = cal.get(Calendar.DAY_OF_YEAR);

            float diff = m_date_2-m_date_1;
            float days = diff / (24 * 60 * 60 * 1000);

            if(diff==0){
                if(m_date_state!=1){
                    this.mModelList.add(new historyRowModel("Today ",null,-1));
                }
                m_real_id.add(m_real_counter);
                m_real_index.add(m_real_counter);
                m_date_state = 1;
            }else if (diff>=1){

                if(m_date_state!=2 || m_last_day!=(int)(Math.ceil(days/7)*7)){
                    m_last_day = (int)(Math.ceil(days/7)*7);
                    this.mModelList.add(new historyRowModel("Last " + m_last_day + " Days",null,-1));
                }
                m_real_id.add(m_real_counter);
                m_real_index.add(m_real_counter);
                m_date_state = 2;
            }else {
                if(m_date_state!=3){
                    this.mModelList.add(new historyRowModel("Older ",null,-1));
                }
                m_real_id.add(m_real_counter);
                m_real_index.add(m_real_counter);
                m_date_state = 3;
            }

            m_real_id.add(p_model_list.get(counter).getID());
            m_real_index.add(m_real_counter);
            this.mModelList.add(p_model_list.get(counter));
            m_real_counter+=1;
        }
        tempModelList.addAll(this.mModelList);
    }

    /*Initializations*/

    public ArrayList<String> getLongSelectedleURL(){
        return m_long_selected;
    }

    public void onDeleteSelected(){
        for(int m_counter=0;m_counter<m_long_selected.size();m_counter++){
            for(int m_counter_inner=0;m_counter_inner<passedModelList.size();m_counter_inner++){
                if(m_long_selected.get(m_counter).equals("https://"+passedModelList.get(m_counter_inner).getDescription())){
                    mEvent.invokeObserver(Collections.singletonList(m_counter_inner),enums.etype.url_clear);
                    mEvent.invokeObserver(Collections.singletonList(m_counter_inner),enums.etype.url_clear_at);
                    invokeFilter(false);
                    mEvent.invokeObserver(Collections.singletonList(m_counter_inner),enums.etype.is_empty);

                    initializeModelWithDate(false);
                    if(m_counter_inner==0){
                        notifyDataSetChanged();
                    }else {
                        notifyItemRemoved(m_counter_inner+1);
                        notifyItemRangeChanged(m_counter_inner+1, passedModelList.size());
                    }
                    break;
                }
            }
        }

        clearLongSelectedURL();
    }

    public void clearLongSelectedURL(){

        for(int m_counter=0;m_counter<m_long_selected.size();m_counter++){

            View m_item_view = m_long_pressed_list_view_holder.get(m_counter);
            ImageButton m_popup_menu = m_item_view.findViewById(R.id.p_popup_menu);
            ImageView p_logo_image = m_item_view.findViewById(R.id.p_logo_image);

            m_item_view.setPressed(false);
            m_popup_menu.setVisibility(View.VISIBLE);
            m_popup_menu.animate().setDuration(150).alpha(1);
            m_popup_menu.setClickable(true);
            p_logo_image.setAlpha(1f);
            p_logo_image.animate().cancel();
            p_logo_image.animate().setDuration(150).alpha(0).withEndAction(() -> p_logo_image.setVisibility(View.GONE));
        }
        m_long_selected.clear();
        m_long_selected_id.clear();
        m_long_pressed_list_view_holder.clear();
    }

    public String getSelectedURL(){
        String m_joined_url = "\n";
        for(int m_counter=0;m_counter<m_long_selected.size();m_counter++){
            m_joined_url = m_joined_url.concat("\n"+m_long_selected.get(m_counter));
        }
        return m_joined_url;
    }

    Context m_context;
    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        m_context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull historyAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(tempModelList.get(position), position);
    }

    public int getItem(){
        return tempModelList.size();
    }

    @Override
    public int getItemCount() {
        return tempModelList.size();
    }

    /*Listeners*/
    boolean m_is_searched = false;
    public void onUpdateSearchStatus(boolean p_is_searched){
        m_is_searched = !p_is_searched;
    }

    public void onSelectView(View p_item, View itemView, int p_poisition, String p_url, View p_menu_item, TextView p_logo_text, ImageView p_logo_image, boolean p_is_forced, int p_id){
        if(!m_is_searched){
            try {
                itemView.setPressed(false);
                int speed = 150;
                if(p_is_forced){
                    speed=0;
                }
                p_menu_item.animate().setDuration(speed).alpha(0).withEndAction(() -> {
                    p_menu_item.setVisibility(View.INVISIBLE);
                    p_menu_item.setClickable(false);
                });

                p_logo_image.setAlpha(0.5f);
                p_logo_image.setVisibility(View.VISIBLE);
                p_logo_image.animate().cancel();
                p_logo_image.animate().setDuration(speed).alpha(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!p_is_forced){
                m_long_selected.add(p_url);
                m_long_selected_id.add(p_id);
                m_long_pressed_list_view_holder.add(p_item);

            }
            onVerifyLongSelectedURL();
        }
    }

    public void onVerifyLongSelectedURL(){
        if(m_long_selected.size()>0){
            mEvent.invokeObserver(Collections.singletonList(false),enums.etype.on_verify_selected_url_menu);
        }else {
            mEvent.invokeObserver(Collections.singletonList(true),enums.etype.on_verify_selected_url_menu);
        }
    }

    public void onClearHighlight(View p_item, View itemView, int p_poisition, String p_url, View p_menu_item,TextView p_logo_text,ImageView p_logo_image,boolean p_is_forced, int p_id)
    {
        try {
            itemView.setPressed(false);
            p_menu_item.setVisibility(View.VISIBLE);
            int speed = 150;
            if(p_is_forced){
                speed = 0;
            }
            p_menu_item.animate().setDuration(speed).alpha(1);
            p_menu_item.setClickable(true);
            p_logo_image.setAlpha(1f);
            p_logo_image.animate().cancel();
            p_logo_image.animate().setDuration(speed).alpha(0).withEndAction(() -> p_logo_image.setVisibility(View.GONE));


            m_long_selected.remove(p_url);
            m_long_selected_id.remove((Integer) p_id);
            m_long_pressed_list_view_holder.remove(p_item);
            onVerifyLongSelectedURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int m_list_initial_size = 0;

    @SuppressLint("ClickableViewAccessibility")
    public void onSwipe(View p_item, View p_item_view, int p_position, String p_url, View p_menu_item,TextView p_logo_text,ImageView p_logo_image, int p_id){

        Handler handler = new Handler();

        Runnable mLongPressed = () -> {
            if(!m_disable_callable){
                if(!m_long_selected.contains(p_url) || !m_long_selected_id.contains(p_id)) {
                    m_was_long_pressed = true;
                    onSelectView(p_item, p_item_view, p_position, p_url,p_menu_item, p_logo_text, p_logo_image, false, p_id);
                }else {
                    onClearHighlight(p_item, p_item_view, p_position, p_url,p_menu_item, p_logo_text, p_logo_image, false, p_id);
                    m_was_long_pressed = true;
                }
            }
        };

        p_item_view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(m_long_selected.size()>0){
                        if(m_long_selected.contains(p_url) && m_long_selected_id.contains(p_id)){
                            handler.removeCallbacks(mLongPressed);
                            if(!m_was_long_pressed){
                                onClearHighlight(p_item, p_item_view, p_position, p_url,p_menu_item, p_logo_text, p_logo_image,false, p_id);
                            }
                            return false;
                        }else if(!m_was_long_pressed){
                            handler.removeCallbacks(mLongPressed);
                            onSelectView(p_item, p_item_view, p_position, p_url,p_menu_item, p_logo_text, p_logo_image,false, p_id);
                        }
                        return false;
                    }
                    else if(m_list_initial_size>0){
                        return false;
                    }

                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    v.setPressed(false);
                    onClose(p_position);
                }
                else
                {
                    v.setPressed(false);
                    mEvent.invokeObserver(Collections.singletonList(p_url),enums.etype.url_triggered);
                }

                return true;

            }
            else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                m_disable_callable = false;
                m_list_initial_size = m_long_selected.size();
                m_was_long_pressed = false;
                v.setPressed(true);
                x1 = event.getX();
                handler.postDelayed(mLongPressed, ViewConfiguration.getLongPressTimeout());
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_CANCEL)
            {
                m_disable_callable = true;
                if(!m_long_selected.contains(p_url) || !m_long_selected_id.contains(p_id)) {
                    v.setPressed(false);
                }
                return true;
            }
            return false;
        });

    }

    void onOpenMenu(View view, String p_url, int p_position, String p_title){

        if(popupWindow!=null){
            popupWindow.dismiss();
        }

        LayoutInflater layoutInflater
                = (LayoutInflater) view.getContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.history_popup_menu, null);


        this.popupWindow = new PopupWindow(
                popupView,
                ActionMenuView.LayoutParams.WRAP_CONTENT,
                ActionMenuView.LayoutParams.WRAP_CONTENT, true);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xOffset = -(view.getMeasuredWidth() - view.getWidth());

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int y = location[1] + 600;
        int height = helperMethod.getScreenHeight(m_main_context);
        int m_offset_height = 0;
        if(y + 400 >height){
            m_offset_height = y-height;
        }
        else if(y - 1200 < 0){
            m_offset_height = y - 1200;
        }

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        popupWindow.setElevation(7);
        popupWindow.showAsDropDown(view,xOffset - 90,-330 - m_offset_height);

        setPopupWindowEvents(popupView.findViewById(R.id.menu1), p_url, p_position, p_title);
        setPopupWindowEvents(popupView.findViewById(R.id.menu2), p_url, p_position, p_title);
        setPopupWindowEvents(popupView.findViewById(R.id.menu3), p_url, p_position, p_title);
        setPopupWindowEvents(popupView.findViewById(R.id.menu4), p_url, p_position, p_title);
        setPopupWindowEvents(popupView.findViewById(R.id.menu5), p_url, p_position, p_title);
    }

    public void setPopupWindowEvents(View p_view, String p_url, int p_position, String p_title){
        p_view.setOnClickListener(v -> {
            if(v.getId() == R.id.menu1){
                helperMethod.copyURL(p_url, m_context);
                popupWindow.dismiss();
            }
            else if(v.getId() == R.id.menu2){
                helperMethod.shareApp((AppCompatActivity)m_context, p_url, p_title);
                popupWindow.dismiss();
            }
            else if(v.getId() == R.id.menu3){
                mEvent.invokeObserver(Collections.singletonList(p_url),enums.etype.url_triggered);
                popupWindow.dismiss();
            }
            else if(v.getId() == R.id.menu4){
                mEvent.invokeObserver(Collections.singletonList(p_url),enums.etype.url_triggered_new_tab);
                popupWindow.dismiss();
            }
            else if(v.getId() == R.id.menu5){
                onClose(p_position);
                popupWindow.dismiss();
            }
        });
    }

    private void setItemViewOnClickListener(View p_item, View p_item_view, View p_item_menu , String p_url, int p_position, String p_title,View p_menu_item,TextView p_logo_text,ImageView p_logo_image, int p_id)
    {
        p_item_menu.setOnClickListener((View v) -> onOpenMenu(v, p_url, p_position, p_title));
        onSwipe(p_item, p_item_view, p_position, p_url,p_menu_item, p_logo_text, p_logo_image, p_id);
    }

    boolean m_disable_callable = false;

    public void onClose(int index){
        if(!isClosing){
            isClosing = true;
            mEvent.invokeObserver(Collections.singletonList(m_real_index.get(index)),enums.etype.url_clear);
            mEvent.invokeObserver(Collections.singletonList(m_real_id.get(index)),enums.etype.url_clear_at);
            invokeFilter(false);
            mEvent.invokeObserver(Collections.singletonList(m_real_id.get(index)),enums.etype.is_empty);
            if(passedModelList.size()>0){
                initializeModelWithDate(false);
            }else {
                tempModelList.clear();
                index=0;
            }
            int size = tempModelList.size();
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, size);
            if(size>1){
                new Thread(){
                    public void run(){
                        try
                        {
                            sleep(500);
                            isClosing = false;
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }
    }

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder
    {
        TextView m_header;
        TextView m_description;
        TextView m_date;
        TextView m_logo_default;
        ImageButton m_popup_menu;
        ImageView p_logo_image;
        LinearLayout m_item_container;
        LinearLayout m_date_container;
        LinearLayout m_loading;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(historyRowModel model, int p_position) {
            m_date_container = itemView.findViewById(R.id.p_date_container);
            m_header = itemView.findViewById(R.id.p_header);
            m_description = itemView.findViewById(R.id.p_description);
            m_item_container = itemView.findViewById(R.id.p_item_container);
            m_popup_menu = itemView.findViewById(R.id.p_popup_menu);
            m_date = itemView.findViewById(R.id.p_date);
            p_logo_image = itemView.findViewById(R.id.p_logo_image);
            m_logo_default = itemView.findViewById(R.id.p_logo_default);
            m_loading = itemView.findViewById(R.id.p_loading);


            if(model.getID() == -1){
                m_date.setText(model.getHeader());
                m_date_container.setVisibility(View.VISIBLE);
                m_item_container.setVisibility(View.GONE);
                m_popup_menu.setVisibility(View.GONE);
                m_logo_default.setVisibility(View.GONE);
                m_loading.setVisibility(View.GONE);
            }
            else if(model.getID() == -2){
                m_date.setText(model.getHeader());
                m_date_container.setVisibility(View.GONE);
                m_item_container.setVisibility(View.GONE);
                m_popup_menu.setVisibility(View.GONE);
                m_logo_default.setVisibility(View.GONE);
                m_loading.setVisibility(View.VISIBLE);
            }
            else {
                m_date_container.setVisibility(View.GONE);
                m_loading.setVisibility(View.GONE);
                m_item_container.setVisibility(View.VISIBLE);
                m_popup_menu.setVisibility(View.VISIBLE);
                m_logo_default.setVisibility(View.VISIBLE);

                m_logo_default.setText((model.getHeader().toUpperCase().charAt(0)+""));
                String header = model.getHeader();
                m_description.setText(("https://"+model.getDescription()));
                m_header.setText(model.getHeader());

                setItemViewOnClickListener(itemView, m_item_container, m_popup_menu, m_description.getText().toString(), p_position, header, m_popup_menu, m_logo_default, p_logo_image, model.getID());
            }

            if(m_long_selected.contains("https://" + model.getDescription()) && m_long_selected_id.contains(model.getID())){
                onSelectView(itemView, m_item_container, p_position, model.getDescription(), m_popup_menu, m_logo_default, p_logo_image,true, model.getID());
            }else if(m_popup_menu.isClickable()){
                onClearHighlight(itemView, m_item_container, p_position, model.getDescription(), m_popup_menu, m_logo_default, p_logo_image,true, model.getID());
            }
        }
    }

    void setFilter(String filter){
        this.filter = filter.toLowerCase();
    }

    void invokeFilter(boolean notify){
        if(notify){
            if(filter.length()>0){
                initializeModelWithDate(true);
            }else {
                initializeModelWithDate(false);
            }
            notifyDataSetChanged();
        }
   }
}
