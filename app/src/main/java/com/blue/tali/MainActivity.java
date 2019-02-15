package com.blue.tali;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    Socket mSocket;
    Button btnConnect, btnGo, btnBack, btnLeft, btnRight;
    EditText txtServerAddress, txtServerPort;
    TextView txtStatus, txtServerAddressLabel;
    LinearLayout layout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btn_connect_to_server);
        btnGo = findViewById(R.id.btn_go);
        btnBack = findViewById(R.id.btn_back);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);

        txtServerAddress = findViewById(R.id.txt_server_address);
        txtServerPort = findViewById(R.id.txt_server_port);
        txtStatus = findViewById(R.id.txt_status);
        txtServerAddressLabel = findViewById(R.id.txt_server_address_label);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (btnConnect.getText().equals("Connect to server")){
                    if (mSocket != null) {
                        mSocket.disconnect();
                        mSocket.close();
                    }
                    try {
                        if (txtServerAddress.getText().toString().toCharArray()[0] == 'h') {
                            mSocket = IO.socket(txtServerAddress.getText().toString());
                        } else {
                            mSocket = IO.socket(txtServerAddress.getText().toString() + ":" + txtServerPort.getText().toString());
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "ConnectString is incorrect", Toast.LENGTH_SHORT).show();
                    }
                    if (mSocket != null) {
                        txtStatus.setText("Status: Connecting...");
                        registerSocketEventCallback();
                        mSocket.connect();
                    }
                } else {
                    mSocket.disconnect();
                    btnConnect.setText("Connect to server");
                }

            }

        });

        btnGo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSocket.connected()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSocket.emit("move", "go");
                            break;
                        case MotionEvent.ACTION_UP:
                            mSocket.emit("move", "stop");
                            break;
                    }
                }
                return false;
            }
        });

        btnBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSocket.connected()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSocket.emit("move", "back");
                            break;
                        case MotionEvent.ACTION_UP:
                            mSocket.emit("move", "stop");
                            break;
                    }
                }
                return false;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSocket.connected()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSocket.emit("move", "right");
                            break;
                        case MotionEvent.ACTION_UP:
                            mSocket.emit("move", "stopturn");
                            break;
                    }
                }
                return false;
            }
        });

        btnGo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSocket.connected()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSocket.emit("move", "left");
                            break;
                        case MotionEvent.ACTION_UP:
                            mSocket.emit("move", "stopturn");
                            break;
                    }
                }
                return false;
            }
        });
    }

    private void registerSocketEventCallback() {
        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        txtStatus.setText("Status: Disconnect");
                    }
                });
                mSocket.off();
            }
        });

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "connected!", Toast.LENGTH_SHORT).show();
                        txtStatus.setText("Status: Connected");
                        btnConnect.setText("Disconnect to server");
                    }
                });
            }
        });

        mSocket.on("welcome", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String message = data.optString("data");
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "timeout", Toast.LENGTH_SHORT).show();
                        txtStatus.setText("Status: Connect error");
                    }
                });
            }
        });
    }

}
