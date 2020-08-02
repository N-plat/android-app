package com.nplat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nplat.ui.main.PageViewModel;

import java.net.URI;
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
        TextView timestamp_textview;
        ImageView image_imageview;

        post_view = inflater.inflate(R.layout.postwithimage, parent, false);
        text_textview = (TextView) post_view.findViewById(R.id.text);
        username_textview = (TextView) post_view.findViewById(R.id.username);
        timestamp_textview = (TextView) post_view.findViewById(R.id.timestamp);
        image_imageview = (ImageView) post_view.findViewById(R.id.image);

        text_textview.setText(post_list.get(position).text);
        username_textview.setText(post_list.get(position).username);
        timestamp_textview.setText(post_list.get(position).timestamp);
        Glide.with(this.context).load("https://image.n-plat.com/image1.jpeg").into(image_imageview);
//        image_imageview.setImageURI(Uri.parse("https://n-plat.com/image1.jpeg"));

        return post_view;
    }
}
