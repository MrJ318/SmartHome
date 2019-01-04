package com.jevon.smarthome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jevon.smarthome.adapter.DeviceAdapter;
import com.jevon.smarthome.listener.IMain;
import com.jevon.smarthome.presenter.MainPresenter;
import com.jevon.smarthome.utils.ProgressDialog;
import com.jevon.smarthome.utils.SmartHomeApp;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener,
        IMain, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private MainPresenter mPresenter;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mLinearLayout;

    private DeviceAdapter mAdapter;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @SuppressLint("HandlerLeak")
    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        mListView = findViewById(R.id.listview_device);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLinearLayout = findViewById(R.id.nodevice);

        SharedPreferences sp = this.getSharedPreferences("smarthome", Context.MODE_PRIVATE);
        boolean b = sp.getBoolean("isFirst", true);
        if (b) {
            initKEY();
            return;
        }

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.show();

        mPresenter = new MainPresenter(this);
        mPresenter.loadList();
    }


    //第一次运行时或者设置KEY和PASS的对话框
    public void initKEY() {

        @SuppressLint("InflateParams") View view = LayoutInflater.from(SmartHomeApp.getContext()).inflate(R.layout.layout_initdialog, null);
        final EditText edit_KEY = view.findViewById(R.id.editTextKEY);
        final EditText edit_PASS = view.findViewById(R.id.editTextPASS);

        final AlertDialog builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("请先设置KEY")
                .setView(view)
                .setPositiveButton("保存", null)
                .create();
        builder.show();

        builder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_KEY.getText().toString().equals("") | edit_PASS.getText().toString().equals("")) {
                    Toast.makeText(SmartHomeApp.getContext(), "请填写完整!", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sp = SmartHomeApp.getContext().getSharedPreferences("smarthome", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("KEY", edit_KEY.getText().toString());
                editor.putString("PASS", edit_PASS.getText().toString());
                editor.putBoolean("isFirst", false);
                editor.apply();
                Toast.makeText(SmartHomeApp.getContext(), "保存完成!", Toast.LENGTH_SHORT).show();
                builder.dismiss();
            }
        });
    }

    //添加完成设备后刷新列表
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 & resultCode == RESULT_OK) {
            boolean isLoad = data.getBooleanExtra("ISLOAD", false);
            if (isLoad) {
                mPresenter.loadList();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivityForResult(new Intent(MainActivity.this, AddActivity.class), 1);
        } else if (item.getItemId() == R.id.setting) {
            initKEY();
        }
        return true;
    }

    @Override
    public void onRefresh() {
        Toast.makeText(MainActivity.this, "刷新成功！", Toast.LENGTH_SHORT).show();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadedData(DeviceAdapter adapter) {
        mAdapter = adapter;
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
        mListView.setAdapter(adapter);
        mProgressDialog.dismiss();
    }

    @Override
    public void onError(String msg) {
        Toast.makeText(SmartHomeApp.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceEmpty() {
        mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
        mLinearLayout.setVisibility(View.VISIBLE);
        mProgressDialog.dismiss();
    }


    @Override
    public void onSwitchGpioSuccess() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mPresenter.listViewClick(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showPopWindows(view, i);
        return true;
    }

    @Override
    public void onDeleteSucess() {
        popupWindow.dismiss();
    }


    /*
     *长按item时弹出的popWindows
     * */
    private PopupWindow popupWindow;

    private void showPopWindows(final View v, final int id) {

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(SmartHomeApp.getContext())
                .inflate(R.layout.popup_item, null, false);
        popupWindow = new PopupWindow(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setTouchable(true);
        popupWindow.showAsDropDown(v, v.getWidth() / 2 - 80, -(v.getHeight() / 2) - 40);
        TextView txt1 = view.findViewById(R.id.tv_delete);
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.deleteItem(id);
            }
        });
    }
}
