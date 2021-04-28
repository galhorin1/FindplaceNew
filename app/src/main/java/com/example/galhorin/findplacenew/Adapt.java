package com.example.galhorin.findplacenew;

/**
 * Created by galhorin on 12/1/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class Adapt extends ArrayAdapter<Messages> {

    public Adapt(Context context, Messages[] values) {
        super(context, R.layout.message_item,values);

    }

    @Override
    public View getView ( int position , View convertView , ViewGroup parent)
    {
        LayoutInflater theInflater = LayoutInflater.from(getContext());
        View theView = theInflater.inflate(R.layout.message_item, parent, false);

        Messages ms = getItem(position);

        TextView Description=(TextView)theView.findViewById(R.id.Description);
        TextView SenderName=(TextView)theView.findViewById(R.id.SenderName);

        Description.setText(ms.getDescription());
        SenderName.setText(ms.getNickname());

        return theView;

    }


}
