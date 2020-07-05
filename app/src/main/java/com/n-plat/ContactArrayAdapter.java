package ch.ecommunicate.chat;

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

public class ContactArrayAdapter  extends ArrayAdapter<Contacts.Contact> {

    private static final String TAG = "ContactArrayAdapter";

    private final Context context;
    private final List<Contacts.Contact> contact_list;

    public ContactArrayAdapter(Context context, List<Contacts.Contact> contact_list) {
        super(context, R.layout.contact, contact_list);
        this.context = context;
        this.contact_list = contact_list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View contact_view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        TextView contact_textview;

        if (contact_list.get(position).new_message) {
            contact_view = inflater.inflate(R.layout.contact_new_message, parent, false);
            contact_textview = (TextView) contact_view.findViewById(R.id.contact);
        }
        else {
            contact_view = inflater.inflate(R.layout.contact, parent, false);
            contact_textview = (TextView) contact_view.findViewById(R.id.contact);
        }
        if (contact_list.get(position).name != "")
            contact_textview.setText(contact_list.get(position).name);
        else
            contact_textview.setText(contact_list.get(position).username);

        return contact_view;
    }
}
