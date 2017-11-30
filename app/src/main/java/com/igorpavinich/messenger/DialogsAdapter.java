package com.igorpavinich.messenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Igor Pavinich on 30.11.2017.
 */

public class DialogsAdapter extends BaseAdapter implements Filterable {
    ArrayList<Dialog> dialogs;
    Context mContext;
    private NameFilter nameFilter;
    ArrayList<Dialog> bufDialogs;

    public DialogsAdapter(Context mContext, ArrayList<Dialog> dialogs) {
        this.mContext = mContext;
        this.dialogs = dialogs;
        bufDialogs = dialogs;
    }

    public int getCount() {
        return dialogs.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View arg1, ViewGroup viewGroup){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.dialog_item, viewGroup, false);
        TextView receiver = row.findViewById(R.id.dialog_receiver);
        receiver.setText(dialogs.get(position).getSurname() + " " + dialogs.get(position).getName());
        TextView text = row.findViewById(R.id.dialog_lastMessage);
        text.setText(dialogs.get(position).getLastMessage());
        TextView date = row.findViewById(R.id.msgTime);
        date.setText(dialogs.get(position).getDate());
        ImageView imageView = row.findViewById(R.id.item_img);
        imageView.setImageResource(R.drawable.ic_mood_black_72dp);
        return row;
    }

    @Override
    public Filter getFilter() {
        if(nameFilter == null)
            nameFilter = new NameFilter();
        return nameFilter;
    }

    class NameFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if(charSequence!=null && charSequence.length()>0){
                    ArrayList<Dialog> filtered = new ArrayList<>();
                    String fullName;
                for (Dialog dialog :
                        bufDialogs) {
                    fullName = dialog.getName() + " " + dialog.getSurname();
                    if(fullName.toLowerCase().contains(charSequence.toString().toLowerCase()))
                        filtered.add(dialog);
                }
                filterResults.count = filtered.size();
                filterResults.values = filtered;
            }else{
                filterResults.count = bufDialogs.size();
                filterResults.values = bufDialogs;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            dialogs = (ArrayList<Dialog>)filterResults.values;
            notifyDataSetChanged();
        }
    }

}
