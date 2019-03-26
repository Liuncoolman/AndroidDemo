package com.liun.demo.rx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.liun.demo.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Description:
 * Author：Liun
 * Date:2019/03/25 14:27
 */
public class RxJavaMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rx_main);
    }

    public void simple(View view){
        startActivity(new Intent(this,RxSimpleActivity.class));
    }

    public void methodButton(View view){
        startActivity(new Intent(this,RxMethodActivity.class));
    }
}
