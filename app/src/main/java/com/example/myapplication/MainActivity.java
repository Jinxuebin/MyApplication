package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        System.out.println("dev branch");
    }

    private void initView() {
        
    }

    private void initData() {
        
    }
}