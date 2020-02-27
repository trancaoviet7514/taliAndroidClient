package com.blue.tali;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainActivity extends Activity {
    private static final String STR_CONNECT_TO_SERVER = "Connect to server";
    private static final String STR_DISCONNECT_TO_SERVER = "Disconnect to server";

    Socket mSocket;
    Button btnConnect, btnGo, btnBack, btnLeft, btnRight, btnRightBackGround, btnLeftBackGround, btnGoBackGround, btnBackBackGround;
    AutoCompleteTextView txtServerAddress, txtServerPort;
    TextView txtStatus, txtServerAddressLabel;
    private String[] mServersAddress = {"https://taliserver.herokuapp.com", "http://192.168.43.72"};
    private String[] mServersPort = {"80", "3484"};
    ImageView imgStatusIcon, imgSpeedNeedle;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btn_connect_to_server);
        btnGo = findViewById(R.id.btn_go);
        btnBack = findViewById(R.id.btn_back);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        imgStatusIcon = findViewById(R.id.img_status_icon);
        imgSpeedNeedle = findViewById(R.id.img_speed_needle);

        btnRightBackGround = findViewById(R.id.btn_right_background);
        btnLeftBackGround = findViewById(R.id.btn_left_background);
        btnGoBackGround = findViewById(R.id.btn_go_background);
        btnBackBackGround = findViewById(R.id.btn_back_background);

        txtServerAddress = findViewById(R.id.txt_server_address);
        txtServerPort = findViewById(R.id.txt_server_port);
        txtStatus = findViewById(R.id.txt_status);
        txtServerAddressLabel = findViewById(R.id.txt_server_address_label);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, mServersAddress);
        txtServerAddress.setThreshold(0); //will start working from first character
        txtServerAddress.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, mServersPort);
        txtServerPort.setThreshold(0); //will start working from first character
        txtServerPort.setAdapter(adapter2);

        addButtonListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addButtonListener(){
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                btnConnect.setEnabled(false);
                if (mSocket == null || !mSocket.connected()) {
                    if (mSocket != null) {
                        mSocket.disconnect();
                        mSocket.close();
                    }
                    try {
                        if (txtServerAddress.getText().toString().toCharArray()[txtServerAddress.getText().toString().length()-1] == 'm') {
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
                        imgStatusIcon.setImageResource(R.drawable.ic_loading);

                        float px = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                12f,
                                getResources().getDisplayMetrics()
                        );
                        RotateAnimation anim = new RotateAnimation(0,360,px,px);
                        anim.setDuration(600);
                        anim.setRepeatCount(Animation.INFINITE);
                        imgStatusIcon.startAnimation(anim);
                        mSocket.connect();
                    }
                } else {
                    if(mSocket != null && mSocket.connected()){
                        mSocket.disconnect();
                    }
                }

            }

        });

        final ObjectAnimator anim = ObjectAnimator.ofFloat(imgSpeedNeedle, "Rotation",90f);
        final ObjectAnimator animButtonGo = ObjectAnimator.ofFloat(btnGoBackGround, "alpha", 1f);
        final int DURATION = 700;
        animButtonGo.setDuration(DURATION);
        btnGo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSocket != null && mSocket.connected()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSocket.emit("move", "go");

                            float px = TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    110,
                                    getResources().getDisplayMetrics()
                            );
                            anim.setFloatValues(90f);
                            imgSpeedNeedle.setPivotX(px);
                            imgSpeedNeedle.setPivotY(px);
                            anim.setDuration(2000);
                            anim.start();
                            animButtonGo.setFloatValues(1f);
                            animButtonGo.start();
                            break;
                        case MotionEvent.ACTION_UP:
                            mSocket.emit("move", "stop");
                            anim.cancel();
                            anim.setDuration((long) (anim.getDuration() * anim.getAnimatedFraction()));
                            anim.setFloatValues(-133f);
                            anim.start();
                            animButtonGo.cancel();
                            animButtonGo.setFloatValues(0f);
                            animButtonGo.start();
                            break;
                    }
                }
                return false;
            }
        });

        final ObjectAnimator animButtonBack = ObjectAnimator.ofFloat(btnBackBackGround, "alpha", 1f);
        animButtonBack.setDuration(DURATION);
        btnBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSocket != null && mSocket.connected()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSocket.emit("move", "back");
                            animButtonBack.setFloatValues(1f);
                            animButtonBack.start();
                            anim.setFloatValues(90f);
                            anim.setDuration((long) (2000 - (anim.getDuration() - anim.getDuration() * anim.getAnimatedFraction())));
                            anim.start();
                            break;
                        case MotionEvent.ACTION_UP:
                            mSocket.emit("move", "stop");
                            animButtonBack.cancel();
                            animButtonBack.setFloatValues(0f);
                            animButtonBack.start();
                            anim.setDuration((long) (anim.getDuration() * anim.getAnimatedFraction()));
                            anim.setFloatValues(-133f);
                            anim.start();
                            break;
                    }
                }
                return false;
            }
        });

        final ObjectAnimator animButtonRight = ObjectAnimator.ofFloat(btnRightBackGround, "alpha", 1f);
        animButtonRight.setDuration(DURATION);
        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSocket != null && mSocket.connected()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSocket.emit("move", "right");
                            animButtonRight.setFloatValues(1f);
                            animButtonRight.start();
                            break;
                        case MotionEvent.ACTION_UP:
                            mSocket.emit("move", "stopturn");
                            animButtonRight.cancel();
                            animButtonRight.setFloatValues(0f);
                            animButtonRight.start();
                            break;
                    }
                }
                return false;
            }
        });

        final ObjectAnimator animButtonLeft = ObjectAnimator.ofFloat(btnLeftBackGround, "alpha", 1f);
        animButtonLeft.setDuration(DURATION);
        btnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSocket != null && mSocket.connected()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSocket.emit("move", "left");
                            animButtonLeft.setFloatValues(1f);
                            animButtonLeft.start();
                            break;
                        case MotionEvent.ACTION_UP:
                            mSocket.emit("move", "stopturn");
                            animButtonLeft.cancel();
                            animButtonLeft.setFloatValues(0f);
                            animButtonLeft.start();
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
                        imgStatusIcon.setImageResource(R.drawable.ic_disconnected);
                        imgStatusIcon.clearAnimation();
                        btnConnect.setEnabled(true);
                        btnConnect.setText(STR_CONNECT_TO_SERVER);
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
                        txtStatus.setText("Status: Connected");
                        btnConnect.setEnabled(true);
                        btnConnect.setText(STR_DISCONNECT_TO_SERVER);
                        imgStatusIcon.setImageResource(R.drawable.ic_connected);
                        imgStatusIcon.clearAnimation();
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
                        mSocket.close();
                        Toast.makeText(MainActivity.this, "timeout", Toast.LENGTH_SHORT).show();
                        imgStatusIcon.setImageResource(R.drawable.ic_disconnected);
                        imgStatusIcon.clearAnimation();
                        txtStatus.setText("Status: Connect error");
                        btnConnect.setEnabled(true);
                        btnConnect.setText(STR_CONNECT_TO_SERVER);
                    }
                });
            }
        });
    }

}
