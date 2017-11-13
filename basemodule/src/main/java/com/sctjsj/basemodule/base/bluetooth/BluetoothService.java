package com.sctjsj.basemodule.base.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import static com.sctjsj.basemodule.base.bluetooth.BTConfig.DEVICE_NAME;
import static com.sctjsj.basemodule.base.bluetooth.BTConfig.MESSAGE_DEVICE_NAME;
import static com.sctjsj.basemodule.base.bluetooth.BTConfig.MESSAGE_READ;
import static com.sctjsj.basemodule.base.bluetooth.BTConfig.MESSAGE_STATE_CHANGE;
import static com.sctjsj.basemodule.base.bluetooth.BTConfig.MESSAGE_TOAST;
import static com.sctjsj.basemodule.base.bluetooth.BTConfig.MESSAGE_WRITE;
import static com.sctjsj.basemodule.base.bluetooth.BTConfig.TOAST;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothService {
    // Debugging
    private static final String TAG = "BluetoothService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME = "BTPrinter";

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");	//change by chongqing jinou

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    //当前状态
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    //    public static final String[] items = { "复位打印机", "标准ASCII字体", "压缩ASCII字体", "字体大小:00","字体大小:02","字体大小:06",
//            "字体大小:11", "取消加粗模式", "选择加粗模式", "取消倒置打印", "选择倒置打印", "取消黑白反显", "选择黑白反显",
//            "取消顺时针旋转90°", "选择顺时针旋转90°","左对齐","居中","右对齐" };
    public static final byte[][] byteCommands = {
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x4d, 0x00 },// 标准ASCII字体
            { 0x1b, 0x4d, 0x01 },// 压缩ASCII字体
            { 0x1d, 0x21, 0x00 },// 字体不放大
            { 0x1d, 0x21, 0x02 },// 宽高加倍
            { 0x1d, 0x21, 0x11 },// 宽高加倍
//            { 0x1d, 0x21, 0x11 },// 宽高加倍
            { 0x1b, 0x45, 0x00 },// 取消加粗模式
            { 0x1b, 0x45, 0x01 },// 选择加粗模式
            { 0x1b, 0x7b, 0x00 },// 取消倒置打印
            { 0x1b, 0x7b, 0x01 },// 选择倒置打印
            { 0x1d, 0x42, 0x00 },// 取消黑白反显
            { 0x1d, 0x42, 0x01 },// 选择黑白反显
            { 0x1b, 0x56, 0x00 },// 取消顺时针旋转90°
            { 0x1b, 0x56, 0x01 },// 选择顺时针旋转90°

            { 0x1b, 0x61, 0x30 },// 左对齐
            { 0x1b, 0x61, 0x31 },// 居中对齐
            { 0x1b, 0x61, 0x32 },// 右对齐
//            { 0x1b, 0x69 },// 切纸
    };


    public void print(int i){
        write(byteCommands[i]);
    }

    public void printReset(){
        if (getState() != BluetoothService.STATE_CONNECTED) {
            return;
        }
        write(byteCommands[0]);
    }

//    public void printDensity(int density){
//    	if(density<1)
//    		density = 4;
//        if(density>8)
//        	density = 8;
//        byte[] send = new byte[3];//ESC m n
//        send[0] = 0x1B;
//        send[1] = 0x6D;
//    	send[2] = (byte) density;
//    	write(send);
//    }

    public void printSize(int size){
        if (getState() != BluetoothService.STATE_CONNECTED) {
            return;
        }
        switch (size) {
            case 1:
                write(byteCommands[4]);
                break;
            case 2:
                write(byteCommands[5]);
                break;
            default:
                write(byteCommands[3]);
                break;
        }
    }

    /**
     * 左对齐
     */
    public void printLeft(){
        if (getState() != BluetoothService.STATE_CONNECTED) {
            return;
        }
        write(byteCommands[14]);
    }

    /**
     * 右对齐
     */
    public void printRight(){
        if (getState() != BluetoothService.STATE_CONNECTED) {
            return;
        }
        write(byteCommands[16]);
    }

    /**
     * 居中
     */
    public void printCenter(){
        if (getState() != BluetoothService.STATE_CONNECTED) {
            return;
        }
        write(byteCommands[15]);
    }

    public void printCut(){
        if (getState() != BluetoothService.STATE_CONNECTED) {
            return;
        }
        write(byteCommands[18]);
    }

    private Context context;

    /**
     * 构造方法
     */
    public BluetoothService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //初始化时 是 STATE_NONE
        mState = STATE_NONE;
        this.context=context;
        mHandler = handler;
    }

    /**
     * 修改状态
     */
    private synchronized void setState(int state) {
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    /**
     * （作为 server）
     * 启动 AcceptThread 线程来监听别的连接请求
     * Start the service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    /**
     * （作为 client）
     * 启动 ConnectThread 线程来初始化连接到远程设备
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  要连接的蓝牙设备
     */
    public synchronized void connect(BluetoothDevice device) {
        //取消所有正在尝试的连接
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        //中断已经建立的连接
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // 获取 socket 对象
        mConnectThread = new ConnectThread(device);
        //连接 socket
        mConnectThread.start();
        //手动修改状态
        setState(STATE_CONNECTING);

    }

    /**
     * 启动 ConnectedThread 线程来管理蓝牙连接
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  已建立连接的 socket
     * @param device  已经连接的蓝牙设备
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");
        //socket 连接已经建立之后，关闭请求创建连接的线程
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        //socket 连接已经建立之后，关闭正在运行连接的线程
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        //关闭接收外部连接的线程
        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}


        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        /**
         * 真正的 socket 连接成功
         */
        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     * 终止所有线程，置空
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        setState(STATE_NONE);
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
    }

    /**
     * 发送打印信息给打印机
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * 尝试连接失败后调用
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "蓝牙连接失败");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * 连接中断、丢失
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        //setState(STATE_LISTEN);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "连接中断");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * （Server）用于监听连接请求的线程
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    /**
     * （Client）用于连接外部设备的线程
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            //通过 Socket 连接设备
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "创建 ConnectThread 线程失败", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "开启 ConnectThread 线程");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();

            } catch (IOException e) {
                //连接失败
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "ConnectThread 线程创建的socket连接失败后，socket 无法关闭", e2);
                }
                // Start the service over to restart listening mode
                BluetoothService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        /**
         * 关闭 socket 连接
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * 用于管理已经连接的线程
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    byte[] buffer = new byte[256];
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    if(bytes>0)
                    {
                        //将要打印的消息通知给前台 UI
                        // Send the obtained bytes to the UI Activity
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget();
                    } else {
                        Log.e(TAG, "disconnected");
                        connectionLost();

                        //add by chongqing jinou
                        if(mState != STATE_NONE)
                        {
                            Log.e(TAG, "disconnected");
                            // Start the service over to restart listening mode
                            BluetoothService.this.start();
                        }
                        break;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();

                    //add by chongqing jinou
                    if(mState != STATE_NONE)
                    {
                        // Start the service over to restart listening mode
                        BluetoothService.this.start();
                    }
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "打印过程中发生异常", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

}
