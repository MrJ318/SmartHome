package com.jevon.smarthome.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jevon.smarthome.R;
import com.jevon.smarthome.utils.SmartHomeApp;

import java.util.List;

public class DeviceAdapter extends BaseAdapter {

    private List<String> mNameList;
    private List<Integer> mIDList;
    private boolean[] mStatuList;

    public DeviceAdapter(List<String> listname, List<Integer> listsubname, boolean[] status) {
        mNameList = listname;
        mIDList = listsubname;
        mStatuList = status;
    }

    public void setStatus(boolean[] status) {
        mStatuList = status;
    }

    @Override
    public int getCount() {
        return mNameList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        TextView tName, tSubName;
        ImageView imgSwitch;

        if (view == null) {
            view = LayoutInflater.from(SmartHomeApp.getContext()).inflate(R.layout.layout_devicelist, null);

            tName = view.findViewById(R.id.textview_name);
            tSubName = view.findViewById(R.id.textview_subname);
            imgSwitch = view.findViewById(R.id.img_switch);
            holder = new ViewHolder();
            holder.txtName = tName;
            holder.txtSubName = tSubName;
            holder.img_Switch = imgSwitch;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
            tName = holder.txtName;
            tSubName = holder.txtSubName;
            imgSwitch = holder.img_Switch;
        }

        tName.setText(mNameList.get(i));
        tSubName.setText(mIDList.get(i).toString());
        if (!mStatuList[i]) {
            imgSwitch.setImageResource(R.drawable.switchon);
        } else {
            imgSwitch.setImageResource(R.drawable.switchoff);
        }
        return view;
    }

    class ViewHolder {
        TextView txtName, txtSubName;
        ImageView img_Switch;
    }
}
