package com.example.amlevin.chat;

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
public class ContactArrayAdapter  extends ArrayAdapter<Contacts.ContactInfo> {

    private static final String TAG = "ContactArrayAdapter";

    private final Context context;
    private final List<Contacts.ContactInfo> contactInfoArrayList;

    public ContactArrayAdapter(Context context, List<Contacts.ContactInfo> contactInfoArrayList) {
        super(context, R.layout.contact, contactInfoArrayList);
        this.context = context;
        this.contactInfoArrayList = contactInfoArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View contactInfoView;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        contactInfoView = inflater.inflate(R.layout.contact, parent, false);
        TextView contact = (TextView) contactInfoView.findViewById(R.id.contact);
        contact.setText(contactInfoArrayList.get(position).name);
        TextView statusMsg = (TextView) contactInfoView.findViewById(R.id.statusMsg);
        statusMsg.setText(contactInfoArrayList.get(position).statusMsg);

        return contactInfoView;
    }
}
