package com.vch.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

/**
 * FlutterActivity的代理类
 *
 * @author VC_H
 * @date 2020-03-21
 */
public class FlutterDelegateActivity extends FlutterActivity {
    private static final String TAG = "FlutterDelegateActivity";
    private static final String CHANNEL_NATIVE = "com.example.flutter/native";
    private static final String CHANNEL_FLUTTER = "com.example.flutter/flutter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_NATIVE)
                .setMethodCallHandler(
                (call, result) -> {
                    methodCall(call, result);
                });
    }

    /**
     * 处理dart层传来的方法调用
     */
    private void methodCall(MethodCall call, MethodChannel.Result result) {
        if (call.method.equals("gotoNativePage")) {
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra("args", (String) call.arguments);
            startActivity(intent);
            result.success("成功调用gotNativePage方法，回调数据返回");
        } else if (call.method.equals("backAction")) {
            finish();
        } else {
            result.notImplemented();
        }
    }

//    @Override
//    public void onBackPressed() {
//        FlutterEngine flutterEngine = getFlutterEngine();
//        if (flutterEngine == null) {
//            super.onBackPressed();
//            return;
//        }
//        MethodChannel flutterChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_FLUTTER);
//        flutterChannel.invokeMethod("backAction", "原生容器发送过去的参数");
//    }
}
