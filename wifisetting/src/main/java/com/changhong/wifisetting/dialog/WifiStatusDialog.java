package com.changhong.wifisetting.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.wifisetting.R;
import com.changhong.wifisetting.api.OnNetworkChangeListener;
import com.changhong.wifisetting.utils.WifiAdminUtils;


/**
 * Created by fdw on 2017/9/13..
 */
public class WifiStatusDialog extends Dialog {
    // Wifi管理类
    private WifiAdminUtils mWifiAdmin;
    private Context context;
    private ScanResult scanResult;
    private TextView txtWifiName;
    private TextView txtConnStatus;
    private TextView txtSinglStrength;
    private TextView txtSecurityLevel;
    private TextView txtIpAddress;
    private Button txtBtnDisConn;
    private Button txtBtnCancel;
    private String wifiName;
    private String securigyLevel;
    private int level;

    public WifiStatusDialog(Context context, int theme) {
        super(context, theme);
        this.mWifiAdmin = new WifiAdminUtils(context);
    }

    private WifiStatusDialog(Context context, int theme, String wifiName,
                             int singlStren, String securityLevl) {
        super(context, theme);
        this.context = context;
        this.wifiName = wifiName;
        this.level = singlStren;
        this.securigyLevel = securityLevl;
        this.mWifiAdmin = new WifiAdminUtils(context);
    }

    public WifiStatusDialog(Context context, int theme, ScanResult scanResult,
                            OnNetworkChangeListener onNetworkChangeListener) {
        this(context, theme, scanResult.SSID, scanResult.level,
                scanResult.capabilities);
        this.scanResult = scanResult;
        this.mWifiAdmin = new WifiAdminUtils(context);
        this.onNetworkChangeListener = onNetworkChangeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_wifi_status);
        setCanceledOnTouchOutside(false);

        initView();
        setListener();
    }

    private void setListener() {

        txtBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("txtBtnCancel");
                WifiStatusDialog.this.dismiss();
            }
        });

        txtBtnDisConn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 断开连接
                int netId = mWifiAdmin.getConnNetId();
                mWifiAdmin.disConnectionWifi(netId);
                WifiStatusDialog.this.dismiss();
                onNetworkChangeListener.onNetWorkDisConnect();
            }
        });
    }

    private void initView() {
        txtWifiName = (TextView) findViewById(R.id.wifi_name);
        txtConnStatus = (TextView) findViewById(R.id.txt_conn_status);
        txtSinglStrength = (TextView) findViewById(R.id.signal_strength);
        txtSecurityLevel = (TextView) findViewById(R.id.security_level);
        txtIpAddress = (TextView) findViewById(R.id.txt_ip_address);

        txtBtnCancel = (Button) findViewById(R.id.btn_cancel);
        txtBtnDisConn = (Button) findViewById(R.id.btn_disconnect);

        txtWifiName.setText(wifiName);
        txtConnStatus.setText("已连接");
        txtSinglStrength.setText(WifiAdminUtils.singlLevToStr(level));
        txtSecurityLevel.setText(securigyLevel);
        txtIpAddress
                .setText(mWifiAdmin.ipIntToString(mWifiAdmin.getIpAddress()));

    }

    @Override
    public void show() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);

        super.show();
        getWindow().setLayout((int) (size.x * 9 / 10),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void showShortToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private OnNetworkChangeListener onNetworkChangeListener;

}
