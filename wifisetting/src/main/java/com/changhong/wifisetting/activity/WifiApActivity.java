package com.changhong.wifisetting.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.wifisetting.R;
import com.changhong.wifisetting.utils.WifiAPUtils;

/**
 * wifi热点实现
 */
public class WifiApActivity extends Activity {

    private String TAG = "WifiApActivity";
    public final static boolean DEBUG = true;
    private TextView mBtStartWifiAp,mBtStopWifiAp;
    private EditText mWifiSsid,mWifiPassword;
    private RadioGroup mRgWifiSerurity;
    private RadioButton mRdNo,mRdWpa,mRdWpa2;
    private CheckBox cbx_show_pass;
    private WifiAPUtils.WifiSecurityType mWifiType = WifiAPUtils.WifiSecurityType.WIFICIPHER_NOPASS;
    //Handler传递wifi热点开启、关闭消息
    private Handler mHandler = new Handler(){

        public void handleMessage(Message msg) {
            if(DEBUG) Log.i(TAG, "WifiApActivity message.what="+msg.what);
            switch (msg.what) {
                case WifiAPUtils.MESSAGE_AP_STATE_ENABLED:
                    String ssid = WifiAPUtils.getInstance(WifiApActivity.this).getValidApSsid();
                    String pw = WifiAPUtils.getInstance(WifiApActivity.this).getValidPassword();
                    //int security = WifiAPUtils.getInstance(WifiApActivity.this).getValidSecurity();
                    Toast.makeText(WifiApActivity.this,"wifi热点开启成功"+"\n"
                            +"SSID = "+ssid+"\n"
                            +"Password = "+pw +"\n"
                            +"Security = "+mWifiType,Toast.LENGTH_SHORT).show();
                    break;
                case WifiAPUtils.MESSAGE_AP_STATE_FAILED:
                    Toast.makeText(WifiApActivity.this,"wifi热点关闭",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_hotspot);
        //初始化
        init();
    }

    private void init() {
        WifiAPUtils.getInstance(getApplicationContext());//初始化WifiAPUtils类
        WifiAPUtils.getInstance(this).regitsterHandler(mHandler);
        //定位控件
        mBtStartWifiAp = (TextView) findViewById(R.id.bt_start_wifiap);
        mWifiSsid = (EditText) findViewById(R.id.et_ssid);
        mWifiPassword = (EditText) findViewById(R.id.et_password);
        cbx_show_pass = (CheckBox) findViewById(R.id.cbx_show_pass);
        mRgWifiSerurity = (RadioGroup) findViewById(R.id.rg_security);
        mRdNo = (RadioButton) findViewById(R.id.rd_no);
        mRdWpa = (RadioButton) findViewById(R.id.rd_wpa);
        mRdWpa2 = (RadioButton) findViewById(R.id.rd_wpa2);
        mBtStopWifiAp = (TextView) findViewById(R.id.bt_stop_wifiap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //安全加密类型
        mRgWifiSerurity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {

                if(arg1 == mRdNo.getId()){
                    mWifiType = WifiAPUtils.WifiSecurityType.WIFICIPHER_NOPASS;
                    mWifiPassword.setEnabled(false);
                } else if (arg1 == mRdWpa.getId()){
                    mWifiType = WifiAPUtils.WifiSecurityType.WIFICIPHER_WPA;
                }else if (arg1 == mRdWpa2.getId()){
                    mWifiType = WifiAPUtils.WifiSecurityType.WIFICIPHER_WPA2;
                }
                if(DEBUG)Log.i(TAG, "radio check mWifiType = "+mWifiType);
            }
        });
        //显示密码单选框
        cbx_show_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 文本正常显示
                    mWifiPassword
                            .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Editable etable = mWifiPassword.getText();
                    Selection.setSelection(etable, etable.length());

                } else {
                    // 文本以密码形式显示
                    mWifiPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    // 下面两行代码实现: 输入框光标一直在输入文本后面
                    Editable etable = mWifiPassword.getText();
                    Selection.setSelection(etable, etable.length());
                }
            }
        });
        //开启wifi热点按钮点击事件
        mBtStartWifiAp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String ssid = mWifiSsid.getText().toString();
                String password = mWifiPassword.getText().toString();
                if(DEBUG)Log.d(TAG, "ssid = "+ssid +"password = "+password);
                if(null == ssid || "".equals(ssid)){
                    Toast.makeText(WifiApActivity.this, "请输入ssid", Toast.LENGTH_SHORT).show();
                    return;
                }
                WifiAPUtils.getInstance(WifiApActivity.this)
                        .turnOnWifiAp(ssid, password, mWifiType);

            }
        });
        //关闭wifi热点
        mBtStopWifiAp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiAPUtils.getInstance(WifiApActivity.this).closeWifiAp();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(DEBUG) Log.i(TAG, "WifiApActivity onBackPressed");
        finish();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(DEBUG) Log.i(TAG, "WifiApActivity onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(DEBUG) Log.i(TAG, "WifiApActivity onDestroy");
        WifiAPUtils.getInstance(this).unregitsterHandler();
    }
}
