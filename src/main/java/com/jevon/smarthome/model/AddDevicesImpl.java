package com.jevon.smarthome.model;

import com.google.gson.Gson;
import com.jevon.smarthome.bean.DeviceInfo;
import com.jevon.smarthome.bean.DevicesPaginator;
import com.jevon.smarthome.espush.EspushSdk;
import com.jevon.smarthome.presenter.AddDevicesPresenter;
import com.jevon.smarthome.utils.DButils;
import com.jevon.smarthome.utils.SmartHomeApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class AddDevicesImpl {

    private AddDevicesPresenter mAddPresenter;

    public AddDevicesImpl(AddDevicesPresenter addPresenter) {
        mAddPresenter = addPresenter;
    }

    public void readDevices() {

        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                EspushSdk espushSdk = new EspushSdk();
                emitter.onNext(espushSdk.ListDevices(1, 10));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());


        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String s) {

                if (s.contains("未知错误")) {
                    mAddPresenter.setSelectFaild(s);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String msg = jsonObject.getString("msg");
                    mAddPresenter.setSelectFaild(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Gson gson = new Gson();
                    DevicesPaginator devicesPaginator = gson.fromJson(s, DevicesPaginator.class);
                    if (devicesPaginator == null) {
                        mAddPresenter.setSelectFaild(s);
                        return;
                    }
                    DeviceInfo[] deviceInfos = devicesPaginator.getDevices();
                    List<String> devices = new ArrayList<>();
                    for (int i = 0; i < devicesPaginator.getCount(); i++) {
                        devices.add(deviceInfos[i].getName() + "(" + deviceInfos[i].getId() + ")");
                    }
                    mAddPresenter.setSelectSuccess(devices);
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        observable.subscribe(observer);
    }


    public void saveItem(String name, int device, int io, int id) {
        if (name.equals("")) {
            mAddPresenter.setSaveFaild("请输入名称！");
            return;
        }
        //查询数据库中是否已存在将要添加的端口
        DButils db = new DButils(SmartHomeApp.getContext());
        int d = db.read("Device=?", new String[]{device + ""});
        int o = db.read("Io=?", new String[]{io + ""});
        if (d != 0 & o != 0) {
            mAddPresenter.setAlready();
            return;
        }
        //写入数据库
        long wResult = db.write(name, device, io, id);
        if (wResult != -1) {
            mAddPresenter.setSaveSuccess();
        } else {
            mAddPresenter.setSaveFaild("写入数据库时出错！");
        }
    }

}
