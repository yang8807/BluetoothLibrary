package com.laotunong.blelibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * 蓝牙类，单例
 */
public class BLEManager {

    private static BLEManager mBleManager;
    private Context mContext;
    private final BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCharacteristic writeCharateristic;
    private BluetoothGatt mBluetoothGatt;

    private BLEManager(Context context) {
        mContext = context;
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

    }


    public static BLEManager create(Context context) {

        if (mBleManager == null) {
            synchronized (BLEManager.class) {
                if (mBleManager == null)
                    mBleManager = new BLEManager(context);
            }
        }

        return mBleManager;
    }


    public void startScan() {

        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                System.out.println("发现设备===" + deviceName + "..." + deviceAddress);
                if (!TextUtils.isEmpty(deviceName)) {
                    if ("uPI_uS5170".equalsIgnoreCase(deviceName)) {
                        mBluetoothAdapter.stopLeScan(this);
                        connectDevice(device);

                    }
                }

            }
        });

    }

    private void connectDevice(final BluetoothDevice device) {
        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(device.getAddress());
        //: BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        mBluetoothGatt = remoteDevice.connectGatt(mContext, false, new BluetoothGattCallback() {


            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, int status, final int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            if (mConnectChangeListener != null) {
                                mConnectChangeListener.onSuccess(device);
                                gatt.discoverServices();
                                mBluetoothGatt = gatt;
                            }

                        } else {
                            gatt.disconnect();
                            gatt.close();
                            if (mConnectChangeListener != null) {
                                mConnectChangeListener.onError(device);
                            }
                        }
                    }
                });

            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                BluetoothGattService service = gatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"));
                writeCharateristic = service.getCharacteristic(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"));
                BluetoothGattCharacteristic readCharacteristic = service.getCharacteristic(UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"));
                gatt.setCharacteristicNotification(readCharacteristic, true);
                BluetoothGattDescriptor descriptor = readCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                if (descriptor != null) {
                    byte[] val = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;//: BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                    descriptor.setValue(val);
                    gatt.writeDescriptor(descriptor);
                }


            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);

            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                if (mResponseListener != null)
                    mResponseListener.ValueCallBack(characteristic.getValue());
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
            }
        });
    }


    public void write(String value) {
        if (writeCharateristic != null && mBluetoothGatt != null) {
            writeCharateristic.setValue(value);
            System.out.println("send message=" + value);
            mBluetoothGatt.writeCharacteristic(writeCharateristic);
        } else {
            Toast.makeText(mContext, "蓝牙已断开，请重新连接", Toast.LENGTH_SHORT).show();
        }


    }

    private ConnectionStateChangeListener mConnectChangeListener;

    public void setConnectionStateChangeListener(ConnectionStateChangeListener connectionStateChangeListener) {
        if (connectionStateChangeListener == null)
            throw new NullPointerException("ble connection state listener can not be null");
        mConnectChangeListener = connectionStateChangeListener;
    }

    private BLEResponseListener mResponseListener;

    public void setmResponseListener(BLEResponseListener responseListener) {
        if (responseListener == null)
            throw new NullPointerException("ble connection state listener can not be null");
        mResponseListener = responseListener;
    }


}
