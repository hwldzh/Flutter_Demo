package com.vch.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import io.flutter.embedding.android.FlutterEngineConfigurator;
import io.flutter.embedding.android.FlutterFragment;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

/**
 * FragmentActivity，装载着具体的FlutterFragment对象
 *
 * @author VC_H
 * @date 2020-03-24
 */
public class FlutterFragmentActivity extends FragmentActivity implements FlutterEngineConfigurator {
    private static final String TAG = "FlutterFragmentActivity";
    private static final String TAG_FLUTTER_FRAGMENT = "flutter_fragment";
    private static final String CHANNEL_NATIVE = "com.example.flutter/native";
    private static final String CHANNEL_FLUTTER = "com.example.flutter/flutter";

    private FlutterFragment flutterFragment;
    private EventChannel.EventSink eventSink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flutter_layout);
        String route = getIntent().getStringExtra("route"); //跳转路由
        if (TextUtils.isEmpty(route)) {
            finish();
            return;
        }

        View titleBarView = findViewById(R.id.title_bar);

        FragmentManager fragmentManager = getSupportFragmentManager();

        flutterFragment = (FlutterFragment) fragmentManager
                .findFragmentByTag(TAG_FLUTTER_FRAGMENT);

        boolean isHideNav = route.contains("?hideNav=1");//简单实现
        if (isHideNav) { //去掉原生导航栏，即由Flutter页面自己实现导航栏
            titleBarView.setVisibility(View.GONE);
            int index = route.indexOf("?hideNav=1");
            route = route.substring(0, index);
            route+="?haveNav=1"; //自己实现导航栏，将该参数传递给Flutter
            if (flutterFragment == null) {
                flutterFragment = FlutterFragment.withNewEngine().initialRoute(route).build();
                fragmentManager.beginTransaction().add(R.id.fragment_container
                        , flutterFragment, TAG_FLUTTER_FRAGMENT).commit();
            }
        } else { //展示原生导航栏
            String page = route; //简单处理
            if (flutterFragment == null) {
                flutterFragment = FlutterFragment.withNewEngine().initialRoute(page).build();
                fragmentManager.beginTransaction().add(R.id.fragment_container
                        , flutterFragment, TAG_FLUTTER_FRAGMENT).commit();
            }
        }
        //标题栏返回按钮的点击事件，交给flutter页面处理
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flutterFragment != null) {
                    flutterFragment.onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        flutterFragment.onNewIntent(intent);
    }

    /**
     * 物理键返回交由Flutter页面处理
     */
    @Override
    public void onBackPressed() {
        flutterFragment.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        flutterFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onUserLeaveHint() {
        flutterFragment.onUserLeaveHint();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        flutterFragment.onTrimMemory(level);
    }

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_NATIVE)
                .setMethodCallHandler(
                        (call, result) -> {
                            methodCall(call, result);
                        });
        EventChannel eventChannel = new EventChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_FLUTTER);
        eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            // 这个onListen是Flutter端开始监听这个channel时的回调，第二个参数 EventSink是用来传数据的载体。
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                eventSink = events;
            }

            @Override
            public void onCancel(Object arguments) {
                Log.d(TAG, "onCancel，arguments=" + arguments);
                // 对面不再接收
                // TODO: 2020-03-26 可以做一下注销操作，例如有广播、通知等
            }
        });
    }

    /**
     * 处理dart层传来的方法调用
     */
    private void methodCall(MethodCall call, MethodChannel.Result result) {
        if (call.method.equals("gotoNativePage")) {
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra("args", (String) call.arguments);
            startActivityForResult(intent, 1);
            result.success("成功调用gotNativePage方法，回调数据返回");
        } else if (call.method.equals("backAction")) {
            finish();
        } else {
            result.notImplemented();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 2) {
            //原生主动发消息给Flutter
            if (data != null) {
                String nativeData = data.getStringExtra("nativeData");
                eventSink.success(nativeData);
            }
        }
    }

    @Override
    public void cleanUpFlutterEngine(FlutterEngine flutterEngine) {

    }
}
