package com.blowthem.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by walter on 27.06.14.
 */

public class LogoActivity extends ActionBarActivity {

    private static String SERVER_IP = "192.168.43.194"; // internet ----> 86.57.195.176
    //"192.168.1.2";//localwifi
    private final static Integer SERVER_PORT = 8080;

    private Handler handler = new Handler();
    private Intent intent;
    private Runnable doNextActivity = new Runnable() {
        @Override
        public void run() {
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserRegistration userRegistration = new UserRegistration(this);
        //userRegistration.execute("$login$", "walter", "777");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_logo);
        intent = new Intent(this, MainSettingsActivity.class);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        handler.postDelayed(doNextActivity, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(doNextActivity);
    }

    private class UserRegistration extends AsyncTask<String, Void, Socket> {
        private Socket socket;
        private String answer;
        private Context context;
        private DataOutputStream out;
        private DataInputStream in;

        public UserRegistration(Context context) {
            this.context = context;
            socket = null;
            out = null;
            in = null;
        }

        @Override
        protected Socket doInBackground(String... params) {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new DataOutputStream(socket.getOutputStream());
                //in = new DataInputStream(socket.getInputStream());
                //out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.writeUTF(params[0]);//send number of parameters
                out.writeUTF(params[1]);
                out.writeUTF(params[2]);
                //answer = in.readUTF();
                out.flush();
                return socket;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return socket;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            if (socket != null) {
                Toast.makeText(context, answer, Toast.LENGTH_SHORT).show();
            }
        }
    }
}


