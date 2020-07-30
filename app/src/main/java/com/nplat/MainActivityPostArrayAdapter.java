package com.nplat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nplat.ui.main.PageViewModel;

import java.util.List;

public class MainActivityPostArrayAdapter extends ArrayAdapter<PageViewModel.Post> {

    private static final String TAG = "MainActivityPostArrayAdapter";

    private final Context context;
    private final List<PageViewModel.Post> post_list;

    public MainActivityPostArrayAdapter(Context context, List<PageViewModel.Post> post_list) {
        super(context, R.layout.post, post_list);
        this.context = context;
        this.post_list = post_list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View post_view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView text_textview;
        TextView username_textview;

        post_view = inflater.inflate(R.layout.post, parent, false);
        text_textview = (TextView) post_view.findViewById(R.id.text);
        username_textview = (TextView) post_view.findViewById(R.id.username);

        text_textview.setText(post_list.get(position).text);
        username_textview.setText(post_list.get(position).username);

        return post_view;
    }
}
