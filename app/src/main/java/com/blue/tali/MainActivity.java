package com.blue.tali;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mSocket = IO.socket("https://taliserver.herokuapp.com");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        btnConnect = findViewById(R.id.btn_connect_to_server);
        btnGo = findViewById(R.id.btn_go);
        btnBack = findViewById(R.id.btn_back);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);

        txtServerAddress = findViewById(R.id.txt_server_address);
        txtServerPort = findViewById(R.id.txt_server_port);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSocket!=null) {
                    mSocket.connect();
                    if (mSocket.connected()) {
                        Toast.makeText(MainActivity.this, "connected!", Toast.LENGTH_SHORT).show();
                    }
                }
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

        btnGo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mSocket.emit("move", "go");
                        break;
                    case MotionEvent.ACTION_UP:
                        mSocket.emit("move", "stop");
                        break;
                }
                return false;
            }
        });

        btnBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mSocket.emit("move", "back");
                        break;
                    case MotionEvent.ACTION_UP:
                        mSocket.emit("move", "stop");
                        break;
                }
                return false;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mSocket.emit("move", "right");
                        break;
                    case MotionEvent.ACTION_UP:
                        mSocket.emit("move", "stop");
                        break;
                }
                return false;
            }
        });

        btnGo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mSocket.emit("move", "left");
                        break;
                    case MotionEvent.ACTION_UP:
                        mSocket.emit("move", "stop");
                        break;
                }
                return false;
            }
        });
    }
}
