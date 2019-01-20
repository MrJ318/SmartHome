package com.jevon.smarthome.espush;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.jevon.smarthome.utils.SmartHomeApp;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

public class EspushSdk {

    private static final MediaType TYPE_JSON = MediaType.parse("text/json; charset=utf-8");
    private String BaseAddr = "https://espush.cn";
    private static String Key;
    private static String Pswd;
    private final String BASE_PATH = "/api/server";

    static {
        SharedPreferences sp = SmartHomeApp.getContext().getSharedPreferences("smarthome", Context.MODE_PRIVATE);
        Key = sp.getString("KEY", "");
        Pswd = sp.getString("PASS", "");
//        Key = "3615541522100107";
//        Pswd = "9f95e8bd86858b01063c42fdc30e6115";
    }

    private String commonRequest(String uri, String method, Map<String, String> args, String body)
            throws IOException, NoSuchAlgorithmException {
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        long timestamp = System.currentTimeMillis() / 1000;
        String fulluri = BaseAddr + BASE_PATH + uri;
        String urlpath = BASE_PATH + uri;
        if (args == null) {
            args = new HashMap<>();
        }

        String signStr = EspushSdk.calcSign(urlpath, method, Key, Pswd, timestamp);
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(fulluri)).newBuilder();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        fulluri = builder.build().toString();
        Request.Builder reqBuilder = new Request.Builder().url(fulluri);

        Request req;
        if (HttpMethod.requiresRequestBody(method)) {
            req = reqBuilder.method(method, RequestBody.create(TYPE_JSON, body)).header("Key", Key)
                    .addHeader("Timestamp", Long.toString(timestamp)).addHeader("Sign", signStr).build();
        } else {
            req = reqBuilder.method(method, null).header("Key", Key)
                    .addHeader("Timestamp", Long.toString(timestamp)).addHeader("Sign", signStr).build();
        }
        Response rsp = client.newCall(req).execute();
        String str = rsp.body().string();
        return str;
    }

    public String ListDevices(int page, int count) {
        String uri = "/devices";
        if (page < 0 || count < 0) {
            throw new ArithmeticException();
        }
        Map<String, String> args = new HashMap<>();
        args.put("page", Integer.toString(page));
        args.put("count", Integer.toString(count));
        args.put("filter", "all");
        String rspbody = null;
        try {
            rspbody = this.commonRequest(uri, "GET", args, "");
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            rspbody = "查询-未知错误!";
        }
        return rspbody;
    }


    private class _setGpioStatusReq {
        int pin;
        boolean edge;
    }

    public String setGpioStatus(int devdbid, int pin, boolean edge) {
        String uri = "/gpio/" + Integer.toString(devdbid);
        Gson gson = new Gson();
        _setGpioStatusReq r = new _setGpioStatusReq();
        r.pin = pin;
        r.edge = edge;
        String reqbody = gson.toJson(r);
        String rspbody = null;
        try {
            rspbody = this.commonRequest(uri, "POST", null, reqbody);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            rspbody = "开关-未知错误!";
        }
        return rspbody;
    }

    public String getGpioStatus(int devdbid) {
        String uri = "/gpio/" + Integer.toString(devdbid);
        String rspbody = null;
        try {
            rspbody = this.commonRequest(uri, "GET", null, "");
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            rspbody = "获取状态-未知错误!";
        }
        return rspbody;
    }

    private static String md5str(String ori) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("md5");
        BigInteger bigInt = new BigInteger(1, md5.digest(ori.getBytes()));
        return bigInt.toString(16);
    }

    private static String calcSign(String urlpath, String method, String key, String password, long timestamp)
            throws NoSuchAlgorithmException {
        String C = "key=" + key + "timestamp=" + timestamp;
        String S = (method + urlpath + C + password).toLowerCase();
        String R = EspushSdk.md5str(S);
        return R.toLowerCase();
    }
}
