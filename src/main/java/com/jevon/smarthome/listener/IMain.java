package com.jevon.smarthome.listener;

import com.jevon.smarthome.adapter.DeviceAdapter;

public interface IMain {
    void onLoadedData(DeviceAdapter adapter);

    void onError(String msg);

    void onDeviceEmpty();

    void onSwitchGpioSuccess();

    void onDeleteSucess();
}
