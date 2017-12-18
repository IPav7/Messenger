package com.igorpavinich.messenger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        return dialogs.get(arg0);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View arg1, ViewGroup viewGroup){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.dialog_item, viewGroup, false);
        TextView receiver = row.findViewById(R.id.dialog_receiver);
        receiver.setText(dialogs.get(position).getName());
        TextView text = row.findViewById(R.id.dialog_lastMessage);
        if(dialogs.get(position).getType().equals("text"))
        text.setText(dialogs.get(position).getLastMessage());
        else text.setText("Вложение");
        TextView date = row.findViewById(R.id.msgTime);
        Date data = new Date(dialogs.get(position).getDate());
        String hours = String.valueOf(data.getHours());
        String minutes = String.valueOf(data.getMinutes());
        if(data.getHours()<10) hours = "0" + hours;
        if(data.getMinutes()<10) minutes = "0" + minutes;
        date.setText(hours + ":" + minutes);
        ImageView imageView = row.findViewById(R.id.item_img);
        Bitmap img = dialogs.get(position).getPicture();
        if(img!=null)
            imageView.setImageBitmap(dialogs.get(position).getPicture());
        else imageView.setImageResource(R.drawable.ic_mood_black_72dp);
        if(dialogs.get(position).isUnread()) row.setBackgroundColor(Color.LTGRAY);
        ImageView onlineImg = row.findViewById(R.id.onlineImg);
        if(dialogs.get(position).isOnline()) onlineImg.setVisibility(View.VISIBLE);
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
                    fullName = dialog.getSecond();
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
