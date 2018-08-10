package com.example.everrich.myapplication;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "Main2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        LiveDataBus.get()
                .with("key_word", String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(Main2Activity.this, "A-B : Normal get the msg is :" + s, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "A-B : Normal get the msg is :" + s);
                    }
                });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveDataBus
                        .get()
                        .with("key_word", String.class)
                        .setValue("send the msg from MainThread!");
            }
        });


        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveDataBus
                        .get()
                        .with("key_forever", String.class)
                        .setValue("send the msg from A-B!");
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LiveDataBus.get()
                                .with("key_word", String.class)
                                .postValue("send the msg from SubThread!");
                    }
                }).start();
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveDataBus.get()
                        .with("key_word", String.class)
                        .observeSticky(Main2Activity.this, new Observer<String>() {
                            @Override
                            public void onChanged(@Nullable String s) {
                                Toast.makeText(Main2Activity.this, "A-B : Sticky get the msg is :" + s, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "A-B : Sticky get the msg is :" + s);
                            }
                        });
            }
        });
    }
}
