package com.igorpavinich.messenger;

import android.content.Context;
import android.graphics.Bitmap;
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
 * Created by Igor Pavinich on 02.12.2017.
 */

public class SearchAdapter extends BaseAdapter implements Filterable {
    ArrayList<User> users;
    Context mContext;
    private NameFilter nameFilter;
    ArrayList<User> bufUsers;

    public SearchAdapter(Context mContext, ArrayList<User> users) {
        this.mContext = mContext;
        this.users = users;
        bufUsers = users;
    }

    public int getCount() {
        return users.size();
    }

    public Object getItem(int arg0) {
        return users.get(arg0);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View arg1, ViewGroup viewGroup){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.user_item, viewGroup, false);
        TextView fullName = row.findViewById(R.id.user_name);
        fullName.setText(users.get(position).getSurname() + " " + users.get(position).getName());
        TextView text = row.findViewById(R.id.user_login);
        text.setText(users.get(position).getLogin());
        ImageView imageView = row.findViewById(R.id.user_img);
        Bitmap img = users.get(position).getPicture();
        if(img!=null)
        imageView.setImageBitmap(users.get(position).getPicture());
        else imageView.setImageResource(R.drawable.ic_mood_black_72dp);
        return row;
    }

    @Override
    public Filter getFilter() {
        if(nameFilter == null)
            nameFilter = new SearchAdapter.NameFilter();
        return nameFilter;
    }

    class NameFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if(charSequence!=null && charSequence.length()>0){
                ArrayList<User> filtered = new ArrayList<>();
                String fullName;
                for (User user :
                        bufUsers) {
                    fullName = user.getName() + " " + user.getSurname() + " " + user.getLogin();
                    if(fullName.toLowerCase().contains(charSequence.toString().toLowerCase()))
                        filtered.add(user);
                }
                filterResults.count = filtered.size();
                filterResults.values = filtered;
            }else{
                filterResults.count = bufUsers.size();
                filterResults.values = bufUsers;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            users = (ArrayList<User>)filterResults.values;
            notifyDataSetChanged();
        }
    }

}
