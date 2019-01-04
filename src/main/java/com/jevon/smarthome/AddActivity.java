package com.jevon.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jevon.smarthome.listener.IAddDevices;
import com.jevon.smarthome.presenter.AddDevicesPresenter;
import com.jevon.smarthome.utils.ProgressDialog;

import java.util.List;

public class AddActivity extends AppCompatActivity implements IAddDevices {

    private Spinner spinnerDevice;
    private Spinner spinnerGpio;
    private EditText mEditText;

    private AddDevicesPresenter addPresenter;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        initView();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar_add);
        toolbar.setTitle("添加设备");
        setSupportActionBar(toolbar);

        spinnerDevice = findViewById(R.id.spinner_device);
        spinnerGpio = findViewById(R.id.spinner_gpio);
        mEditText = findViewById(R.id.edit_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.layout_loadingdialog);
        dialog = new ProgressDialog(AddActivity.this);
        dialog.show();

        addPresenter = new AddDevicesPresenter(this);
        addPresenter.selectDevices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String str1 = spinnerDevice.getSelectedItem().toString();
        String str2 = spinnerGpio.getSelectedItem().toString();

        String device = str1.substring(str1.length() - 7, str1.length() - 1);
        String io = str2.substring(4, str2.length());
        String id = device + io;

        addPresenter.save(mEditText.getText().toString(),
                Integer.parseInt(device), Integer.parseInt(io), Integer.parseInt(id));
        return true;
    }

    @Override
    public void onAlready() {
        Toast.makeText(this, "该端口已被占用!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveSuccess() {
        Intent i = new Intent();
        i.putExtra("ISLOAD", true);
        setResult(RESULT_OK, i);

        Toast.makeText(this, "保存成功!", Toast.LENGTH_SHORT).show();
        //收起键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        mEditText.setText("");
    }

    @Override
    public void onSaveFaild(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelectSuccess(List<String> devices, List<String> ios) {
        dialog.dismiss();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, devices);
        spinnerDevice.setAdapter(adapter);
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, ios);
        spinnerGpio.setAdapter(adapter);
    }

    @Override
    public void onSelectFaild(String msg) {
        dialog.dismiss();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}