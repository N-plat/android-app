package com.nplat;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final ArrayList<Message> messages;

    private static final int VIEW_HOLDER_TYPE_1=1;
    private static final int VIEW_HOLDER_TYPE_2=2;

    public static class ViewHolder_Type1 extends RecyclerView.ViewHolder {
        public TextView forward_message_TextView;//, forward_message_time_TextView;
        public ViewHolder_Type1(View v) {
            super(v);
            this.forward_message_TextView = (TextView) v.findViewById(R.id.forward_message_TextView);
            //this.forward_message_time_TextView = (TextView) v.findViewById(R.id.forward_message_time_TextView);
        }


    }

    public static class ViewHolder_Type2 extends RecyclerView.ViewHolder {
        public TextView backward_message_TextView;//, backward_message_time_TextView;
        public ViewHolder_Type2(View v) {
            super(v);
            this.backward_message_TextView = (TextView) v.findViewById(R.id.backward_message_TextView);
            //this.backward_message_time_TextView = (TextView) v.findViewById(R.id.backward_message_time_TextView);
        }


    }
    public ChatAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {

        View v;

        switch (viewType) {

            case VIEW_HOLDER_TYPE_1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.forward_message, parent, false);
                ViewHolder_Type1 vh1 = new ViewHolder_Type1(v);
                return vh1;

            case VIEW_HOLDER_TYPE_2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.backward_message, parent, false);
                ViewHolder_Type2 vh2 = new ViewHolder_Type2(v);
                return vh2;

            default:
                break;
        }

        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {

            case VIEW_HOLDER_TYPE_1:
                ViewHolder_Type1 viewholder1 = (ViewHolder_Type1) holder;
                //TextView mytimeView = (TextView) viewholder1.forward_message_time_TextView;
                //mytimeView.setText(messages.get(position).time());
                TextView mymsgView = (TextView) viewholder1.forward_message_TextView;
                if (messages.get(position).name().equals("")) {
                    if (messages.get(position).username().length() > 6)
                        mymsgView.setText(messages.get(position).username().substring(0, 3) + "...: " + messages.get(position).message());
                    else
                        mymsgView.setText(messages.get(position).username() + ": " + messages.get(position).message());
                }
                else {
                    if (messages.get(position).name().length() > 6)
                        mymsgView.setText(messages.get(position).name().substring(0, 3) + "...: " + messages.get(position).message());
                    else
                        mymsgView.setText(messages.get(position).name() + ": " + messages.get(position).message());
                }
                break;

            case VIEW_HOLDER_TYPE_2:
                ViewHolder_Type2 viewholder2 = (ViewHolder_Type2) holder;
                //TextView timeView = (TextView) viewholder2.backward_message_time_TextView;
                //timeView.setText(messages.get(position).time());
                TextView msgView = (TextView) viewholder2.backward_message_TextView;
                msgView.setText("You: "+messages.get(position).message());
                break;

            default:
                break;
        }

    }



    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).forward())
            return VIEW_HOLDER_TYPE_1;
        else
            return VIEW_HOLDER_TYPE_2;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}