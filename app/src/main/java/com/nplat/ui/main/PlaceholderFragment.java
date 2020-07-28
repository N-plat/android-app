package com.nplat.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nplat.FeedPostArrayAdapter;
import com.nplat.R;
import com.nplat.UsernameArrayAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    ListView feed_listview;

    FeedPostArrayAdapter feed_array_adapter;

    UsernameArrayAdapter username_array_adapter;

    List<PageViewModel.Post> post_list;

    List<PageViewModel.Username> username_list;

    PageViewModel.Post mypost;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                if (pageViewModel.mIndex == 1 || pageViewModel.mIndex == 2) {

                    post_list = Arrays.asList(gson.fromJson(s, PageViewModel.Post[].class));

                    feed_array_adapter = new FeedPostArrayAdapter(getContext(), post_list);

                    feed_listview.setAdapter((ListAdapter) feed_array_adapter);

                } else {

                    username_list = Arrays.asList(gson.fromJson(s, PageViewModel.Username[].class));

                    username_array_adapter = new UsernameArrayAdapter(getContext(), username_list);

                    feed_listview.setAdapter((ListAdapter) username_array_adapter);

                }
            }
        });

        feed_listview = (ListView) root.findViewById(R.id.feed2ListView);

//        feed_listview = (ListView) findViewById(R.id.feedListView);

        return root;
    }
}