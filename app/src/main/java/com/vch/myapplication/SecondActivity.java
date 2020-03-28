package com.vch.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        String args = getIntent().getStringExtra("args");
        TextView secondPageTextView = findViewById(R.id.second_page_text);
        secondPageTextView.setText("接收Flutter页面参数：" + args);

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataBack();
                finish();
            }
        });
    }

    private void sendDataBack() {
        setResult(2, new Intent().putExtra("nativeData"
                , "原生主动发消息给Flutter" + hashCode())); //加上hashCode为了测试，Flutter端是否能接收到数据的变化
    }

    @Override
    public void onBackPressed() {
        sendDataBack();
        super.onBackPressed();
    }
}
