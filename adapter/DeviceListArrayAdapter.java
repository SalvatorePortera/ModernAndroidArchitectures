package com.nereus.craftbeer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nereus.craftbeer.R;
import com.nereus.craftbeer.util.printer.PrinterUtil;

import java.util.List;


/**
 * ArrayAdapter of the device list.
 */
public class DeviceListArrayAdapter extends ArrayAdapter<DeviceListItem> {

    /**
     * Create view from xml
     */
    private LayoutInflater mLayoutInflater;


    public DeviceListArrayAdapter(Context context, List<DeviceListItem> objects) {
        super(context, 0, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null && mLayoutInflater != null) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null);
        }

        final DeviceListItem item = this.getItem(position);
        if (item != null) {
            final TextView deviceNameTxt = (TextView) convertView.findViewById(android.R.id.text1);
            deviceNameTxt.setText(item.getName());

            final TextView deviceAddressTxt = (TextView) convertView.findViewById(android.R.id.text2);
            if (PrinterUtil.isEmpty(item.getIpAddress())) {
                deviceAddressTxt.setText(item.getMacAddress());
            } else {
                deviceAddressTxt.setText(getContext().getResources().getString(R.string.device_address_format, item.getMacAddress(), item.getIpAddress()));
            }
        }
        return convertView;
    }

}
