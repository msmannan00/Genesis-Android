package com.darkweb.genesissearchengine.appManager.list_manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.constants.constants;
import com.example.myapplication.R;

public class list_adapter extends RecyclerView.Adapter<list_adapter.listViewHolder>
{
    list_adapter() {
    }

    /*Initializations*/

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull list_adapter.listViewHolder holder, int position)
    {
        holder.bindListView(list_model.getInstance().getModel().get(position));
        clearMessageItem(holder.messageButton,position);
    }

    @Override
    public int getItemCount() {
        return list_model.getInstance().getModel().size();
    }

    private void setItemViewOnClickListener(View itemView, String url)
    {
        itemView.setOnClickListener(v ->
        {
            list_model.getInstance().getListInstance().onUrlClick(url);
        });
    }

    private void clearMessageItem(ImageButton clearButton, int index)
    {
        list_model.getInstance().getListInstance().onClickListenersInitialize(clearButton,index);
    }

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder
    {
        TextView heaaderText;
        TextView descriptionText;
        ImageButton messageButton;
        ImageView empty_message;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(list_row_model model) {

            heaaderText = itemView.findViewById(R.id.header);
            descriptionText = itemView.findViewById(R.id.description);

            String header = model.getHeader();

            if(model.getHeader().length()<=2)
            {
                header = model.getDescription() + model.getHeader();
            }

            if(list_model.getInstance().getType() == constants.list_bookmark)
            {
                heaaderText.setText(list_model.getInstance().getListInstance().removeHttpRequest(model.getDescription()));
                descriptionText.setText(header);
            }
            else
            {
                heaaderText.setText(list_model.getInstance().getListInstance().removeHttpRequest(header));
                descriptionText.setText(model.getDescription());
            }

            messageButton = itemView.findViewById(R.id.message_button);
            empty_message = itemView.findViewById(R.id.empty_list);

            setItemViewOnClickListener(itemView,header);
        }
    }

}
