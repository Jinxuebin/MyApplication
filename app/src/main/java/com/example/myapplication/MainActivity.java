package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etdMessage;
    private Button btnSendMsg;
    private Button btnDismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initData();
        initView();
        initEvent();

    }

    private String msg = "";

    private void initEvent() {
        System.out.println("dev branch");
        System.out.println("dev branch");
        System.out.println("dev branch");
        etdMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                msg = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyMqttService.publish(msg);
            }
        });
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyMqttService.dismiss();
            }
        });
    }

    private void initView() {
        etdMessage = (EditText) findViewById(R.id.edt_msg);
        btnSendMsg = (Button) findViewById(R.id.btn_send_msg);
        btnDismiss = (Button) findViewById(R.id.btn_dismiss);
    }

    private void initData() {
        MyMqttService.startService(MainActivity.this);
    }
}