package com.laotunong.blelibrary;

import android.bluetooth.BluetoothDevice;

public interface ConnectionStateChangeListener {


    void onError(BluetoothDevice device);
    void onSuccess(BluetoothDevice device);
}
