package com.nplat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by amlevin on 8/25/2017.
 */

public class PostArrayAdapter extends ArrayAdapter<LoggedIn.Post> {

    private static final String TAG = "ContactArrayAdapter";

    private final Context context;
    private final List<LoggedIn.Post> post_list;

    public PostArrayAdapter(Context context, List<LoggedIn.Post> post_list) {
        super(context, R.layout.contact, post_list);
        this.context = context;
        this.post_list = post_list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View contact_view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView contact_textview;

        contact_view = inflater.inflate(R.layout.contact, parent, false);
        contact_textview = (TextView) contact_view.findViewById(R.id.contact);

        contact_textview.setText(post_list.get(position).text);


        return contact_view;
    }
}
