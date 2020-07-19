package com.nplat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedPostArrayAdapter extends ArrayAdapter<Feed.Post> {

    private static final String TAG = "ContactArrayAdapter";

    private final Context context;
    private final List<Feed.Post> post_list;

    public FeedPostArrayAdapter(Context context, List<Feed.Post> post_list) {
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
