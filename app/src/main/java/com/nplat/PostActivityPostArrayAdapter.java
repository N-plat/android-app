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

public class PostActivityPostArrayAdapter extends ArrayAdapter<PostActivity.Post> {

    private static final String TAG = "ContactArrayAdapter";

    private final Context context;
    private final List<PostActivity.Post> post_list;

    public PostActivityPostArrayAdapter(Context context, List<PostActivity.Post> post_list) {
        super(context, R.layout.post, post_list);
        this.context = context;
        this.post_list = post_list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View contact_view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView contact_textview;
        TextView contact_usernameview;

        contact_view = inflater.inflate(R.layout.post, parent, false);
        contact_textview = (TextView) contact_view.findViewById(R.id.text);
        contact_usernameview = (TextView) contact_view.findViewById(R.id.username);

        contact_textview.setText(post_list.get(position).text);
        contact_usernameview.setText(post_list.get(position).username);


        return contact_view;
    }

}

