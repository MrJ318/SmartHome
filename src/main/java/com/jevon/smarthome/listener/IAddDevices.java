package com.jevon.smarthome.listener;

import java.util.List;

public interface IAddDevices {

    void onAlready();

    void onSaveSuccess();

    void onSaveFaild(String msg);

    void onSelectSuccess(List<String> devices, List<String> ios);

    void onSelectFaild(String msg);
}
