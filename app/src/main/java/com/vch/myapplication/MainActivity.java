package com.vch.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //跳转到模块1的Flutter页面，展示原生导航栏，即Flutter页面不要实现自己的导航栏
        findViewById(R.id.first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FlutterFragmentActivity.class);
                intent.putExtra("route", "scheme://flutter_module/mainPage");
                startActivity(intent);
            }
        });

        //跳转到模块1的Flutter页面，隐藏原生导航栏，即Flutter需要自己实现导航栏
        findViewById(R.id.second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FlutterFragmentActivity.class);
                intent.putExtra("route", "scheme://flutter_module/mainPage?hideNav=1");
                startActivity(intent);
            }
        });

        //跳转到模块2的Flutter页面，隐藏原生导航栏，即Flutter需要自己实现导航栏
        findViewById(R.id.third).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FlutterFragmentActivity.class);
                intent.putExtra("route", "scheme://flutter_module_2/mainPage?hideNav=1");
                startActivity(intent);
            }
        });
    }
}
