package com.blowthem.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by walter on 12.08.14.
 */
public class SocketService extends Service {

    public static final String SERVERIP = /*"192.168.56.1";*/ "192.168.43.194"; //your computer IP address should be written here
    public static final int SERVERPORT = 8080;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;
    private ConnectSocket clientProccess;
    protected ReceiveData receiveData;
    protected BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    protected BlockingQueue<Float> valueQueue = new LinkedBlockingQueue<Float>();
    private Float X, Y;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    private final IBinder myBinder = new LocalBinder();
    //TCPClient mTcpClient = new TCPClient();

    public class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        clientProccess = new ConnectSocket();
        clientProccess.start();

        MotionData motionData = new MotionData();
        motionData.start();
    }

    public synchronized void sendMessage(String message){
        if (out != null ) {
            try {
                out.writeUTF(message);
                out.flush();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public synchronized void sendMessage(Float message){
        if (out != null ) {
            try {
                out.writeFloat(message);
                out.flush();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        //System.out.println("!!!!!!EASCXZ !!!!! " + Arrays.deepToString(queue.toArray()));

        String string;
        ArrayList<String> list;
        Float value;

        if((string = intent.getStringExtra("start")) != null) {
            queue.offer(string);
        } else if((list = intent.getStringArrayListExtra("login")) != null){
            list = intent.getStringArrayListExtra("login");
            for (String str : list) {
                queue.offer(str);
            }
        } else if((list = intent.getStringArrayListExtra("motion")) != null){
            queue.offer(list.get(0));
            valueQueue.offer(Float.parseFloat(list.get(1)));
            valueQueue.offer(Float.parseFloat(list.get(2)));
        } else if((value = intent.getFloatExtra("X", 0.0f)) != null){
            valueQueue.offer(value);
        } else if((value = intent.getFloatExtra("Y", 0.0f)) != null){
            valueQueue.offer(value);
        }

        return START_NOT_STICKY;
    }

    class ReceiveData extends Thread {
        @Override
        public void run() {
            try {
                while(!Thread.interrupted()) {
                    String str = in.readUTF();
                    if(str.equals("$motion$")){
                        X = Float.parseFloat(in.readUTF());
                        Y = Float.parseFloat(in.readUTF());

                        ArrayList<String> list = new ArrayList<String>();
                        list.add(String.valueOf(X));
                        list.add(String.valueOf(Y));

                        Intent intent = new Intent();
                        intent.setAction("myBroadcast");
                        intent.putStringArrayListExtra("update", list);
                        sendBroadcast(intent);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ConnectSocket extends Thread {
        @Override
        public void run() {
            try {
                try {
                    socket = new Socket(SERVERIP, SERVERPORT);
                    out = new DataOutputStream(socket.getOutputStream());
                    in = new DataInputStream(socket.getInputStream());

                    receiveData = new ReceiveData();
                    receiveData.start();

                    while(!Thread.interrupted()){
                        String str = queue.take();
                        sendMessage(str);
                        //if(!valueQueue.isEmpty()){
                        //    Float msg = valueQueue.take();
                        //    System.out.println("!!! ---> msg : " + msg);
                        //    sendMessage(msg);
                        //}
                    }
                } catch (Exception e) {
                    Log.e("TCP", "S: Error", e);
                }
            } catch (Exception e) {
                Log.e("TCP", "C: Error", e);
            }
        }
    }

    class MotionData extends Thread{
        @Override
        public void run() {
            while(!Thread.interrupted()){
                try {
                    Float msg = valueQueue.take();
                    sendMessage(msg);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket = null;
    }

    public Float getX() {
        return X;
    }

    public Float getY() {
        return Y;
    }
}