package com.darkweb.genesissearchengine.helperManager;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
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

            TextView customerNameLabel = v.findViewById(R.id.pHeader);
            TextView myTv = v.findViewById( R.id.pURL);
            ImageButton mMoveURL = v.findViewById( R.id.pMoveURL);

            if (customerNameLabel != null) {
                if(customer.getHeader().equals(strings.GENERIC_EMPTY_STR)){
                    customerNameLabel.setText(customer.getHeader() );
                }else {
                    customerNameLabel.setText(customer.getHeader());
                }
                myTv.setText(customer.getDescription());
                mMoveURL.setTag(customer.getDescription());
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
            if(resultValue==null){
                return strings.GENERIC_EMPTY_STR;
            }
            historyRowModel model = (historyRowModel)(resultValue);
            String str = model.getHeader();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null && !constraint.equals("about:blank")) {
                suggestions.clear();
                if(constraint.toString().startsWith("https://")){
                    constraint = constraint.subSequence(0,8);
                }else if (constraint.toString().startsWith("http://")){
                    constraint = constraint.subSequence(0,7);
                }
                for (historyRowModel customer : itemsAll) {
                    if(suggestions.size()>4){
                        break;
                    }

                    if(!customer.getHeader().equals("$TITLE") && customer.getHeader().length()>2 && customer.getDescription().toLowerCase().length()>2 && (customer.getHeader().toLowerCase().contains(constraint.toString().toLowerCase()) || constraint.toString().toLowerCase().toLowerCase().contains(customer.getHeader()) || constraint.toString().toLowerCase().contains(customer.getDescription().toLowerCase()) || customer.getDescription().toLowerCase().contains(constraint.toString().toLowerCase()))){
                        Log.i("memememe:","memememe:"+constraint.toString().toLowerCase().replace("https://","").replace("http://",""));
                        Log.i("memememe1:","memememe2:"+customer.getDescription().replace("https://","").replace("http://",""));
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
                if(results != null) {
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
