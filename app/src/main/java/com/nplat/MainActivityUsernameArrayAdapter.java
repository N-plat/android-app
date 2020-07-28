package com.nplat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nplat.ui.main.PageViewModel;

import java.util.List;

public class MainActivityUsernameArrayAdapter extends ArrayAdapter<PageViewModel.Username> {

    private static final String TAG = "ContactArrayAdapter";

    private final Context context;
    private final List<PageViewModel.Username> username_list;

    public MainActivityUsernameArrayAdapter(Context context, List<PageViewModel.Username> username_list) {
        super(context, R.layout.contact, username_list);
        this.context = context;
        this.username_list = username_list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View contact_view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView contact_textview;

        contact_view = inflater.inflate(R.layout.contact, parent, false);
        contact_textview = (TextView) contact_view.findViewById(R.id.contact);

        contact_textview.setText(username_list.get(position).username);


        return contact_view;
    }
}
