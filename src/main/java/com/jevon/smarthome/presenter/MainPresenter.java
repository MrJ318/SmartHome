package com.jevon.smarthome.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jevon.smarthome.MainActivity;
import com.jevon.smarthome.R;
import com.jevon.smarthome.adapter.DeviceAdapter;
import com.jevon.smarthome.listener.IMain;
import com.jevon.smarthome.model.MainImpl;
import com.jevon.smarthome.utils.SmartHomeApp;

import java.util.List;

public class MainPresenter {

    private IMain mMainView;
    private MainImpl mMainModel;

    private List<String> mNameList;
    private List<Integer> mDeviceList;
    private List<Integer> mIOList;
    private List<Integer> mIDList;
    private boolean[] mStatusList;

    private DeviceAdapter mAdapter;
    private int id;

    public MainPresenter(IMain mainView) {
        mMainView = mainView;
        mMainModel = new MainImpl(this);
    }

    public void loadList() {
        mMainModel.getDataFromDB();
    }

    public void listViewClick(int id) {
        this.id = id;
        mMainModel.onoff(mDeviceList.get(id), mIOList.get(id), !mStatusList[id]);
    }

    public void deleteItem(int i) {
        mMainModel.deleteFromDB(mIDList.get(i));
    }

    public void setDBData(List<String> list1, List<Integer> list2, List<Integer> list3, List<Integer> list4) {
        mNameList = list1;
        mDeviceList = list2;
        mIOList = list3;
        mIDList = list4;
    }

    public void completeData(boolean[] iostatus) {
        mStatusList = iostatus;
        mAdapter = new DeviceAdapter(mNameList, mIDList, mStatusList);
        mMainView.onLoadedData(mAdapter);
    }

    public void setError(String msg) {
        mMainView.onError(msg);
    }

    public void setDeviceEmpty() {
        mMainView.onDeviceEmpty();
    }

    public void setSwitchGpioSuccess() {
        mStatusList[id] = !mStatusList[id];
        mAdapter.setStatus(mStatusList);
        mMainView.onSwitchGpioSuccess();
    }

    public void setDeleteSuccess() {
        mMainView.onDeleteSucess();
    }


}
