package com.jevon.smarthome.model;

import android.database.Cursor;

import com.jevon.smarthome.espush.EspushSdk;
import com.jevon.smarthome.presenter.MainPresenter;
import com.jevon.smarthome.utils.DButils;
import com.jevon.smarthome.utils.Jlog;
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

public class MainImpl {

    private MainPresenter mPresenter;
    private DButils db = new DButils(SmartHomeApp.getContext());
    private boolean[] ioStatus;

    public MainImpl(MainPresenter mainPresenter) {
        mPresenter = mainPresenter;
    }

    public void getDataFromDB() {
        List<String> nameList = new ArrayList<>();
        List<Integer> deviceList = new ArrayList<>();
        List<Integer> ioList = new ArrayList<>();
        List<Integer> idList = new ArrayList<>();

        List<Integer> devices = new ArrayList<>();

        Cursor cursor = db.read();
        if (cursor.moveToFirst()) {

            do {
                nameList.add(cursor.getString(cursor.getColumnIndex("Name")));
                int deviceid = cursor.getInt(cursor.getColumnIndex("Device"));
                deviceList.add(deviceid);
                ioList.add(cursor.getInt(cursor.getColumnIndex("Io")));
                idList.add(cursor.getInt(cursor.getColumnIndex("Id")));

                if (!devices.contains(deviceid)) {
                    devices.add(deviceid);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();

        if (devices.size() == 0) {
            mPresenter.setDeviceEmpty();
            return;
        }

        mPresenter.setDBData(nameList, deviceList, ioList, idList);
        ioStatus = new boolean[nameList.size()];
        getStatusFromInternet(devices, idList);
    }

    private void getStatusFromInternet(final List<Integer> device, final List<Integer> id) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                EspushSdk espushSdk = new EspushSdk();
                for (int d : device) {
                    emitter.onNext(d + espushSdk.getGpioStatus(d));
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        Observer<String> observer = new Observer<String>() {

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String str) {
                if (str.contains("msg")) {
                    try {
                        JSONObject json = new JSONObject(str);
                        String msg = json.getString("msg");
                        mPresenter.setError(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mPresenter.setError(str);
                    }
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(str.substring(6, str.length()));
                    String status = jsonObject.getString("status");

                    for (int i = 0; i < id.size(); i++) {
                        String id1 = id.get(i) + "";
                        String strd = id1.substring(0, 6);
                        if (strd.equals(str.substring(0, 6))) {
                            int io = Integer.parseInt(id1.substring(6, id1.length()));
                            if (Integer.parseInt(status.substring(io, io + 1)) == 1) {
                                ioStatus[i] = true;
                            } else {
                                ioStatus[i] = false;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mPresenter.setError("解析时错误!");
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                mPresenter.completeData(ioStatus);
            }
        };
        observable.subscribe(observer);
    }

    public void onoff(final int device, final int pin, final boolean oc) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                EspushSdk espushSdk = new EspushSdk();
                emitter.onNext(espushSdk.setGpioStatus(device, pin, oc));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String msg = jsonObject.getString("msg");
                    if (msg.equals("OK")) {
                        mPresenter.setSwitchGpioSuccess();
                    } else {
                        mPresenter.setError(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mPresenter.setError(s);
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

    public void deleteFromDB(int id) {
        db.delete("Id=?", new String[]{id + ""});
        getDataFromDB();
        mPresenter.setDeleteSuccess();
    }

}
