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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by walter on 12.08.14.
 */
public class SocketService extends Service {

    public static final String SERVERIP = /*"192.168.56.1";*/ /*"192.168.1.5"*/ "192.168.43.194"; //your server IP address should be written here
    public static final int SERVERPORT = 8080;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;
    private ConnectSocket clientProccess;
    protected ReceiveData receiveData;
    protected BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    protected BlockingQueue<Float> valueQueue = new LinkedBlockingQueue<Float>();
    private Float X, Y, bitmapAngle, targetX, targetY;
    private Float xFire, yFire;
    private Intent loginIntent;

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

    private Handler toasthandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        clientProccess = new ConnectSocket();
        clientProccess.start();

        MotionData motionData = new MotionData();
        //motionData.start();
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
                //System.out.println("MOTION");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        String string;
        ArrayList<String> list;

        if((string = intent.getStringExtra("lost")) != null){
            queue.offer(string);
        }
        if((string = intent.getStringExtra("start")) != null) {
            queue.offer(string);
            intent.removeExtra("start");
        }
        if((list = intent.getStringArrayListExtra("login")) != null){
            list = intent.getStringArrayListExtra("login");
            for (String str : list) {
                queue.offer(str);
            }
            intent.removeExtra("login");
        }
        if((list = intent.getStringArrayListExtra("fire")) != null){
            //System.out.println("LIST !!! : " + Arrays.deepToString(list.toArray()));
            queue.offer(list.get(0));
            valueQueue.offer(Float.parseFloat(list.get(1)));
            valueQueue.offer(Float.parseFloat(list.get(2)));
            intent.removeExtra("fire");
            list = null;
            intent.putStringArrayListExtra("fire", list);
            //System.out.println("LIST !!! : " + Arrays.deepToString(list.toArray()));
        }
        if((list = intent.getStringArrayListExtra("motion")) != null){
            queue.offer("m");
            valueQueue.offer(Float.parseFloat(list.get(1)));
            valueQueue.offer(Float.parseFloat(list.get(2)));
            valueQueue.offer(Float.parseFloat(list.get(3)));
            valueQueue.offer(Float.parseFloat(list.get(4)));
            valueQueue.offer(Float.parseFloat(list.get(5)));
            //System.out.println("!!!! X = " + Float.parseFloat(list.get(1)) + " ; Y = " + Float.parseFloat(list.get(2)) + " ; angle = " + Float.parseFloat(list.get(3)) + " ; targetX = " + Float.parseFloat(list.get(4)));
            intent.removeExtra("motion");
            list = null;
            intent.putStringArrayListExtra("motion", list);
        }

        if((list = intent.getStringArrayListExtra("registration")) != null){
            for(String str : list){
                queue.offer(str);
            }
            intent.removeExtra("registration");
        }

        return START_NOT_STICKY;
    }

    class ReceiveData extends Thread {
        @Override
        public void run() {
            try {
                while(!Thread.interrupted()) {
                    String str = in.readUTF();
                    if(str.equals("$login_success$")){
                        LoginBridge.login = str;
                    }

                    if(str.equals("$victory$")){
                        //LoginBridge.won = str;
                        //System.out.println("!!! victory accepted !!!");
                        Intent intent = new Intent();
                        intent.setAction("myBroadcast");
                        intent.putExtra("won", str);
                        sendBroadcast(intent);
                    }

                    if(str.equals("$login_failed$")){
                        toasthandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SocketService.this, "WRONG LOGIN OR PASSWORD! PLEASE, TRY AGAIN...", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    if(str.equals("$registration_success$")){
                        LoginBridge.registration = str;
                    }

                    if(str.equals("$registration_exist$")){
                        toasthandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SocketService.this, "SUCH USER ALREADY EXISTS! PLEASE, TRY AGAIN...", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    if (str.equals("m")) {
                        X = in.readFloat();
                        Y = in.readFloat();
                        bitmapAngle = in.readFloat();
                        targetX = in.readFloat();
                        targetY = in.readFloat();

                        //System.out.println(X + " " + Y);


                        //X = Float.parseFloat(in.readUTF());
                        //Y = Float.parseFloat(in.readUTF());
                        //bitmapAngle = Float.parseFloat(in.readUTF());
                        //targetX = Float.parseFloat(in.readUTF());
                        //targetY = Float.parseFloat(in.readUTF());

                        ArrayList<String> list = new ArrayList<String>();
                        list.add(String.valueOf(X));
                        list.add(String.valueOf(Y));
                        list.add(String.valueOf(bitmapAngle));
                        list.add(String.valueOf(targetX));
                        list.add(String.valueOf(targetY));

                        Intent intent = new Intent();
                        intent.setAction("myBroadcast");
                        intent.putStringArrayListExtra("update", list);
                        sendBroadcast(intent);
                        //}


                    }

                    if (str.equals("$fire$")){
                        xFire = Float.parseFloat(in.readUTF());
                        yFire = Float.parseFloat(in.readUTF());

                        ArrayList<String> list = new ArrayList<String>();
                        list.add(String.valueOf(xFire));
                        list.add(String.valueOf(yFire));

                        Intent intent = new Intent();
                        intent.setAction("myBroadcast");
                        intent.putStringArrayListExtra("fireUpdate", list);
                        sendBroadcast(intent);
                    }

                    if(str.equals("$started$")){
                        Intent intent = new Intent();
                        intent.setAction("myBroadcast");
                        intent.putExtra("battleStarted", str);
                        sendBroadcast(intent);
                    }

                    if(str.equals("$first$")){
                        LoginBridge.battlePosition = str;
                        /*Intent intent = new Intent();
                        intent.setAction("myBroadcast");
                        intent.putExtra("currentBattlePosition", str);
                        sendBroadcast(intent);*/
                    }

                    if(str.equals("$second$")){
                        LoginBridge.battlePosition = str;
                        /*Intent intent = new Intent();
                        intent.setAction("myBroadcast");
                        intent.putExtra("currentBattlePosition", str);
                        sendBroadcast(intent);*/
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
                        if(str.equals("m")){
                            sendMessage(valueQueue.take());
                            sendMessage(valueQueue.take());
                            sendMessage(valueQueue.take());
                            sendMessage(valueQueue.take());
                            sendMessage(valueQueue.take());
                        }
                        if(str.equals("$fire$")){
                            //System.out.println("!!!QUEUE : " + Arrays.deepToString(valueQueue.toArray()));
                            sendMessage(valueQueue.take());
                            sendMessage(valueQueue.take());
                        }
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

