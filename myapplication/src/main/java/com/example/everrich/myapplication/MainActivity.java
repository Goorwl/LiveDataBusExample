package com.example.everrich.myapplication;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Observer<String> mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });

        //       测试非活跃状态获取消息的行为
        LiveDataBus.get()
                .with("key_word", String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(MainActivity.this, "A-A : Normal get the msg is :" + s, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "A-A : Normal get the msg is :" + s);
                    }
                });

        mObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(MainActivity.this, "A-A : Forever get the msg is :" + s, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "A-A : Forever get the msg is :" + s);
            }
        };

        //        测试跨界面消息发送
        LiveDataBus.get()
                .with("key_forever", String.class)
                .observeForever(mObserver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveDataBus.get().with("key_forever", String.class)
                .removeObserver(mObserver);
    }
}
