package com.blue.tali;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    Socket mSocket;
    Button btnConnect, btnGo, btnBack, btnLeft, btnRight;
    EditText txtServerAddress, txtServerPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mSocket = IO.socket("http://tali.herokuapp.com:8080");
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
                }
            }
        });
    }
}
