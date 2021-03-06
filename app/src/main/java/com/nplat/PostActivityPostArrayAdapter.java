package com.nplat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

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
        View post_view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView post_textview;
        TextView post_usernameview;
        TextView post_timestampview;
        VideoView post_videoview;
        ImageView post_imageview;

        if (post_list.get(position).videoid != 0) {

            post_view = inflater.inflate(R.layout.postwithvideo, parent, false);
            post_textview = (TextView) post_view.findViewById(R.id.text);
            post_usernameview = (TextView) post_view.findViewById(R.id.username);
            post_timestampview = (TextView) post_view.findViewById(R.id.timestamp);
            post_videoview = (VideoView) post_view.findViewById(R.id.video);

            post_textview.setText(post_list.get(position).text);
            post_usernameview.setText(post_list.get(position).username);
            post_timestampview.setText(post_list.get(position).timestamp);
            post_videoview.setVideoURI(Uri.parse("https://video.n-plat.com/?filename=video"+post_list.get(position).videoid+".mp4"));
            post_videoview.start();

        } else if (post_list.get(position).imageid != 0) {

            post_view = inflater.inflate(R.layout.postwithimage, parent, false);
            post_textview = (TextView) post_view.findViewById(R.id.text);
            post_usernameview = (TextView) post_view.findViewById(R.id.username);
            post_timestampview = (TextView) post_view.findViewById(R.id.timestamp);
            post_imageview = (ImageView) post_view.findViewById(R.id.image);

            post_textview.setText(post_list.get(position).text);
            post_usernameview.setText(post_list.get(position).username);
            post_timestampview.setText(post_list.get(position).timestamp);
            Glide.with(this.context).load("https://image.n-plat.com/?filename=image"+post_list.get(position).imageid+".jpeg").into(post_imageview);

        } else {

            post_view = inflater.inflate(R.layout.post, parent, false);
            post_textview = (TextView) post_view.findViewById(R.id.text);
            post_usernameview = (TextView) post_view.findViewById(R.id.username);
            post_timestampview = (TextView) post_view.findViewById(R.id.timestamp);

            post_textview.setText(post_list.get(position).text);
            post_usernameview.setText(post_list.get(position).username);
            post_timestampview.setText(post_list.get(position).timestamp);
        }

        return post_view;
    }

}

