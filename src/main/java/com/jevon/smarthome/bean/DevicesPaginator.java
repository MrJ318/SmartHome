package com.jevon.smarthome.bean;

public class DevicesPaginator {
    private int page;
    private int count;
    private int total;
    private DeviceInfo[] devices;

    public int getCount() {
        return count;
    }

    public DeviceInfo[] getDevices() {
        return devices;
    }
}
