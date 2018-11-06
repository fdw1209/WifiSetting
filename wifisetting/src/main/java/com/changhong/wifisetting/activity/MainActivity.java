package com.changhong.wifisetting.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.changhong.wifisetting.R;
import com.changhong.wifisetting.adapter.MyListViewAdapter;
import com.changhong.wifisetting.api.OnNetworkChangeListener;
import com.changhong.wifisetting.component.MyListView;
import com.changhong.wifisetting.dialog.WifiConnDialog;
import com.changhong.wifisetting.dialog.WifiStatusDialog;
import com.changhong.wifisetting.utils.WifiAdminUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by fdw on 2017/9/13.
 */
public class MainActivity extends Activity{

    protected static final String TAG = "MainActivity";

    private static final int REFRESH_CONN = 100;
    // Wifi管理类
    private WifiAdminUtils mWifiAdmin;
    // 扫描结果列表
    private List<ScanResult> list = new ArrayList<ScanResult>();
    // 显示列表
    private MyListView listView;

    private MyListViewAdapter mAdapter;
    //下标
    private int mPosition;

    private WifiReceiver mReceiver;

    private Button mSwitch;

    private WifiManager wifiManager;

    private LinearLayout wifi_list_closed;
    private LinearLayout wifi_enable_wait;
    private LinearLayout list_view1;

    //网络连接状态改变
    private OnNetworkChangeListener mOnNetworkChangeListener = new OnNetworkChangeListener() {

        @Override
        public void onNetWorkDisConnect() {
            getWifiListInfo();
            mAdapter.setDatas(list);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNetWorkConnect() {
            getWifiListInfo();
            mAdapter.setDatas(list);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化数据
        //initData();
        //初始化view
        initView();
        //设置监听
        setListener();
        //定时刷新监听
        refreshWifiStatusOnTime();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //mWifiAdmin = new WifiAdminUtils(MainActivity.this);
          //获得Wifi列表信息
        //getWifiListInfo();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mWifiAdmin = new WifiAdminUtils(MainActivity.this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mSwitch = (Button) findViewById(R.id.switch1);

        listView = (MyListView) findViewById(R.id.freelook_listview);
        list_view1 = (LinearLayout) findViewById(R.id.listview);
        wifi_list_closed = (LinearLayout)findViewById(R.id.wifi_list_content_closed);
        wifi_enable_wait = (LinearLayout)findViewById(R.id.wifi_enable_wait);

        mAdapter = new MyListViewAdapter(this, list);
        listView.setAdapter(mAdapter);
        //检查当前wifi状态

        if (!wifiManager.isWifiEnabled()){
            mSwitch.setBackgroundResource(R.drawable.switch_off);
            list_view1.setVisibility(View.GONE);
            wifi_enable_wait.setVisibility(View.GONE);
            wifi_list_closed.setVisibility(View.VISIBLE);
        }else {
            mSwitch.setBackgroundResource(R.drawable.switch_on);
            wifi_list_closed.setVisibility(View.GONE);
            wifi_enable_wait.setVisibility(View.VISIBLE);
            handler.sendEmptyMessageDelayed(1, 3000);
        }
    }
    /**
     * 得到wifi的列表信息
     */
    private void getWifiListInfo() {
        Log.d(TAG, "getWifiListInfo");
        mWifiAdmin.startScan();
        List<ScanResult> tmpList = mWifiAdmin.getWifiList();
        if (tmpList == null) {
            list.clear();
        } else {
            list = tmpList;
        }
    }
    //前往无线热点设置界面
//        txtBtnHotpost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, WifiApActivity.class);
//                startActivity(intent);
//            }
//        });

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                wifi_enable_wait.setVisibility(View.GONE);
                list_view1.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * 设置监听事件
     */
    private void setListener() {

        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启wifi
                if (!wifiManager.isWifiEnabled()){
                    mSwitch.setBackgroundResource(R.drawable.switch_on);
                    wifiManager.setWifiEnabled(true);
                    wifi_list_closed.setVisibility(View.GONE);
                    wifi_enable_wait.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(1, 5000);
                }else {
                    //关闭wifi
                    mSwitch.setBackgroundResource(R.drawable.switch_off);
                    wifiManager.setWifiEnabled(false);
                    list_view1.setVisibility(View.GONE);
                    wifi_enable_wait.setVisibility(View.GONE);
                    wifi_list_closed.setVisibility(View.VISIBLE);
                }
            }
        });
//        // 设置刷新监听
//        listView.setonRefreshListener(new MyListView.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new AsyncTask<Void, Void, Void>() {
//                    protected Void doInBackground(Void... params) {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        getWifiListInfo();
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void result) {
//                        mAdapter.setDatas(list);
//                        mAdapter.notifyDataSetChanged();
//                        listView.onRefreshComplete();
//                    }
//
//                }.execute();
//            }
//        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                mPosition = pos - 1;
                ScanResult scanResult = list.get(mPosition);
                String desc = "";
                String descOri = scanResult.capabilities;
                if (descOri.toUpperCase().contains("WPA-PSK")) {
                    desc = "通过WPA进行保护";
                }
                if (descOri.toUpperCase().contains("WPA2-PSK")) {
                    desc = "通过WPA2进行保护";
                }
                if (descOri.toUpperCase().contains("WPA-PSK")
                        && descOri.toUpperCase().contains("WPA2-PSK")) {
                    desc = "通过WPA/WPA2进行保护";
                }

                if (desc.equals("")) {
                    isConnectSelf(scanResult);
                    return;
                }
                isConnect(scanResult);
            }

            /**
             * 有密码验证连接
             * @param scanResult
             */
            private void isConnect(ScanResult scanResult) {
                if (mWifiAdmin.isConnect(scanResult)) {
                    // 已连接，显示连接状态对话框
                    WifiStatusDialog mStatusDialog = new WifiStatusDialog(
                            MainActivity.this, R.style.defaultDialogStyle,
                            scanResult, mOnNetworkChangeListener);
                    mStatusDialog.show();
                } else {
                    // 未连接显示连接输入对话框
                    WifiConnDialog mDialog = new WifiConnDialog(
                            MainActivity.this, R.style.defaultDialogStyle, listView, mPosition, mAdapter,
                            scanResult, list, mOnNetworkChangeListener);
                    mDialog.show();
                }
            }

            /**
             * 无密码直连
             * @param scanResult
             */
            private void isConnectSelf(ScanResult scanResult) {
                if (mWifiAdmin.isConnect(scanResult)) {
                    // 已连接，显示连接状态对话框
                    WifiStatusDialog mStatusDialog = new WifiStatusDialog(
                            MainActivity.this, R.style.defaultDialogStyle,
                            scanResult, mOnNetworkChangeListener);
                    mStatusDialog.show();
                } else {
                    boolean iswifi = mWifiAdmin.connectSpecificAP(scanResult);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (iswifi) {
                        Toast.makeText(MainActivity.this, "连接成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private Handler mHandler = new MyHandler(this);

    protected boolean isUpdate = true;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        isUpdate = false;
        unregisterReceiver();
    }

    private static class MyHandler extends Handler {

        private WeakReference<MainActivity> reference;

        public MyHandler(MainActivity activity) {
            this.reference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            MainActivity activity = reference.get();

            switch (msg.what) {
                case REFRESH_CONN:
                    activity.getWifiListInfo();
                    activity.mAdapter.setDatas(activity.list);
                    activity.mAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 定时刷新Wifi列表信息
     *
     * @author fdw
     */
    private void refreshWifiStatusOnTime() {
        new Thread() {
            public void run() {
                while (isUpdate) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(REFRESH_CONN);
                }
            }
        }.start();
    }
    private void registerReceiver() {
        mReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //isUpdate = true;
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isUpdate = false;
        unregisterReceiver();
    }

    /**
     * 取消广播
     */
    private void unregisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private class WifiReceiver extends BroadcastReceiver {
        protected static final String TAG = "MainActivity";
        //记录网络断开的状态
        private boolean isDisConnected = false;
        //记录正在连接的状态
        private boolean isConnecting = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                // wifi连接上与否
                Log.d(TAG, "网络已经改变");
                NetworkInfo info = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTING)){
                    Log.d(TAG, "wifi正在断开");
                } else if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    if (!isDisConnected) {
                        Log.d(TAG, "wifi已经断开");
                        isDisConnected = true;
                    }
                } else if (info.getState().equals(NetworkInfo.State.CONNECTING)) {
                    if (!isConnecting) {
                        Log.d(TAG, "正在连接...");
                        Toast.makeText(getApplicationContext(), "正在连接...", Toast.LENGTH_SHORT).show();
                        isConnecting = true;
                    }
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    Toast.makeText(getApplicationContext(), "连接到网络:"+ wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "连接到网络：" + wifiInfo.getBSSID());
                }

            }else if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
                switch (error) {
                    case WifiManager.ERROR_AUTHENTICATING:
                        Log.d(TAG, "密码认证错误Code为：" + error);
                        Toast.makeText(getApplicationContext(), "wifi密码错误！", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            // 监听wifi的打开与关闭，与wifi的连接无关
            }else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.e("info", "wifiState" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLING:
                        Log.d(TAG, "wifi正在启用");
                        //Toast.makeText(getApplicationContext(), "wifi正在启用！", Toast.LENGTH_SHORT).show();
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.d(TAG, "Wifi已启用");
//                        mSwitch.setBackgroundResource(R.drawable.switch_on);
//                        wifi_list_closed.setVisibility(View.GONE);
//                        wifi_enable_wait.setVisibility(View.VISIBLE);
//                        handler.sendEmptyMessageDelayed(1, 3000);
                        //Toast.makeText(getApplicationContext(), "Wifi已启用！", Toast.LENGTH_SHORT).show();
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        Log.e("TAG", "wifi正在关闭");
                       //Toast.makeText(getApplicationContext(), "wifi正在关闭！", Toast.LENGTH_SHORT).show();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.e("TAG", "wifi已关闭");
//                        mSwitch.setBackgroundResource(R.drawable.switch_off);
//                        list_view1.setVisibility(View.GONE);
//                        wifi_enable_wait.setVisibility(View.GONE);
//                        wifi_list_closed.setVisibility(View.VISIBLE);
                        //Toast.makeText(getApplicationContext(), "wifi已关闭！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }
}

