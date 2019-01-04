package com.jevon.smarthome.presenter;

import com.jevon.smarthome.listener.IAddDevices;
import com.jevon.smarthome.model.AddDevicesImpl;

import java.util.ArrayList;
import java.util.List;

public class AddDevicesPresenter {

    private AddDevicesImpl mModel;
    private IAddDevices mAddView;

    public AddDevicesPresenter(IAddDevices addView) {
        mModel = new AddDevicesImpl(this);
        mAddView = addView;
    }

    public void selectDevices() {
        mModel.readDevices();
    }

    public void save(String name, int device, int io, int id) {
        mModel.saveItem(name, device, io, id);
    }

    public void setAlready() {
        mAddView.onAlready();
    }

    public void setSaveSuccess() {
        mAddView.onSaveSuccess();
    }

    public void setSaveFaild(String msg) {
        mAddView.onSaveFaild(msg);
    }

    public void setSelectSuccess(final List<String> devices) {
        final List<String> gpios = new ArrayList<>();
        gpios.add("GPIO4");
        gpios.add("GPIO5");
        gpios.add("GPIO13");
        mAddView.onSelectSuccess(devices, gpios);
    }

    public void setSelectFaild(String msg) {
        mAddView.onSelectFaild(msg);
    }
}
