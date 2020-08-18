package com.nplat;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.nplat.ui.main.PageViewModel;

import java.net.URI;
import java.util.List;

public class MainActivityPostArrayAdapter extends ArrayAdapter<PageViewModel.Post> {

    private static final String TAG = "MainActivity";

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

        TextView post_textview;
        TextView post_usernameview;
        TextView post_timestampview;
        final VideoView post_videoview;
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
            post_videoview.setMediaController(new MediaController(post_videoview.getContext()));
            post_videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.seekTo(0);
                }
            });

//            post_videoview.setOnClickListener(new VideoView.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    post_videoview.start();
//                }
//            });

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
