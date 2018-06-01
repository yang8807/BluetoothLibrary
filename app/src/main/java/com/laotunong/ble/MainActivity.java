package com.laotunong.ble;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.laotunong.blelibrary.BLEManager;
import com.laotunong.blelibrary.BLEResponseListener;
import com.laotunong.blelibrary.ConnectionStateChangeListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BLEManager bleManager;
    private CheckBox spo;
    private CheckBox dp;
    private CheckBox hrv;
    private CheckBox heart;
    private RadioButton man;
    private RadioButton woman;
    private RadioGroup radioGroup;
    private EditText age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final StringBuffer sb = new StringBuffer();
        final TextView mBlurtoothState = findViewById(R.id.device_name);
        final TextView responseText = findViewById(R.id.response_text);
        spo = findViewById(R.id.open_spo);
        dp = findViewById(R.id.open_dp);
        hrv = findViewById(R.id.open_hrv);
        heart = findViewById(R.id.open_heart);
        man = findViewById(R.id.man);
        woman = findViewById(R.id.woman);
        age = findViewById(R.id.input_age);
        radioGroup = findViewById(R.id.radio);
        findViewById(R.id.mkdir).setOnClickListener(this);
        dp.setOnClickListener(this);
        hrv.setOnClickListener(this);
        heart.setOnClickListener(this);
        spo.setOnClickListener(this);
        findViewById(R.id.start_ble).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                bleManager = BLEManager.create(MainActivity.this);
                bleManager.startScan();
                bleManager.setConnectionStateChangeListener(new ConnectionStateChangeListener() {
                    @Override
                    public void onError(BluetoothDevice device) {
                        mBlurtoothState.setText(device.getName() + "未连接");
                    }

                    @Override
                    public void onSuccess(BluetoothDevice device) {
                        mBlurtoothState.setText(device.getName() + "已连接");
                    }
                });

                bleManager.setmResponseListener(new BLEResponseListener() {
                    @Override
                    public void ValueCallBack(final byte[] bytes) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (byte aByte : bytes) {
                                    sb.append((char) aByte);
                                }
                                responseText.setText(sb.toString());
                                System.out.println("response message=" + sb.toString());
                                sb.setLength(0);
                            }
                        });

                    }
                });
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.open_spo:
                if (!spo.isChecked()) {
                    spo.setChecked(false);
                    bleManager.write("cSPO2");
                    dp.setText("SPO2关闭");
                } else {
                    spo.setChecked(true);
                    bleManager.write("SPO2");
                    dp.setText("SPO2开启");
                }
                break;
            case R.id.open_dp:
                if (!dp.isChecked()) {
                    dp.setChecked(false);
                    dp.setText("BP关闭");
                    bleManager.write("cBP");
                } else {
                    int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                    if (checkedRadioButtonId == -1) {
                        Toast.makeText(MainActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
                        dp.setChecked(false);
                        return;
                    }
                    CharSequence text = ((RadioButton) findViewById(checkedRadioButtonId)).getText();
                    String sex = null;
                    if (text.toString().equals("男")) {
                        sex = "1";
                    } else if (text.toString().equals("女")) {
                        sex = "2";
                    }
                    String age1 = age.getText().toString();

                    if (sex == null || TextUtils.isEmpty(age1)) {
                        dp.setChecked(false);
                        Toast.makeText(MainActivity.this, "请填写性别或年龄", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dp.setChecked(true);
                    dp.setText("BP开启");
                    bleManager.write("BP " + age1 + " " + sex);
                }
                break;
            case R.id.open_hrv:
                if (!hrv.isChecked()) {
                    hrv.setChecked(false);
                    bleManager.write("cHRV");
                    dp.setText("HRV关闭");
                } else {
                    hrv.setChecked(true);
                    bleManager.write("HRV");
                    dp.setText("HRV开启");
                }
                break;
            case R.id.open_heart:
                if (!heart.isChecked()) {
                    heart.setChecked(false);
                    bleManager.write("cHR");
                    dp.setText("HR关闭");
                } else {
                    heart.setChecked(true);
                    bleManager.write("HR");
                    dp.setText("HR开启");
                }
                break;

            case R.id.mkdir:

                break;
        }

    }
}
