package com.darkweb.genesissearchengine.helperManager;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.darkweb.genesissearchengine.appManager.historyManager.historyRowModel;
import com.darkweb.genesissearchengine.constants.strings;
import com.example.myapplication.R;

public class autoCompleteAdapter extends ArrayAdapter<historyRowModel> {
    private final String MY_DEBUG_TAG = "CustomerAdapter";
    private ArrayList<historyRowModel> items;
    private ArrayList<historyRowModel> itemsAll;
    private ArrayList<historyRowModel> suggestions;
    private int viewResourceId;

    public autoCompleteAdapter(Context context, int viewResourceId, ArrayList<historyRowModel> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<historyRowModel>) items.clone();
        this.suggestions = new ArrayList<>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        historyRowModel customer = items.get(position);
        if (customer != null) {

            TextView customerNameLabel = v.findViewById(R.id.hintCompletionTitle);
            TextView myTv = v.findViewById( R.id.hintCompletionUrl);

            if (customerNameLabel != null) {
                if(customer.getTitle().equals(strings.EMPTY_STR)){
                    customerNameLabel.setText(customer.getmHeader() );
                }else {
                    customerNameLabel.setText(customer.getTitle());
                }
                myTv.setText(customer.getmDescription());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((historyRowModel)(resultValue)).getmHeader();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null && !constraint.equals("about:blank")) {
                suggestions.clear();
                for (historyRowModel customer : itemsAll) {
                    if(suggestions.size()>10){
                        break;
                    }
                    if(customer.getmHeader().length()>2 && customer.getmDescription().toLowerCase().length()>2 && (customer.getmHeader().toLowerCase().contains(constraint.toString().toLowerCase()) || customer.getmDescription().toLowerCase().contains(constraint.toString().toLowerCase()))){
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            try{
                if(results != null && results.count > 0) {
                    ArrayList<historyRowModel> filteredList = (ArrayList<historyRowModel>)((ArrayList<historyRowModel>)results.values).clone();

                    clear();
                    for (historyRowModel c : filteredList) {
                        add(c);
                    }
                    notifyDataSetChanged();
                }
            }catch (Exception ignored){

            }
        }
    };

}
