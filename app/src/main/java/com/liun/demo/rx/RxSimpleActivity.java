package com.liun.demo.rx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.liun.demo.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Description:
 * Author：Liun
 * Date:2019/03/25 14:30
 */
public class RxSimpleActivity extends AppCompatActivity {

    private Observable<String> observable;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rx_simple);

        // 注册观察者
        observable = Observable.create(mObservableOnSubscribe);

        // 设置观察者线程
        observable.subscribeOn(AndroidSchedulers.mainThread());


    }

    public void toast(View view) {
        // 分发消息
        observable.subscribe(mToastSubscriber);
    }

    public void setText(View view) {
        // 分发消息
        observable.subscribe(mTextSubscriber);
    }

    // 观察者事件发生
    private ObservableOnSubscribe<String> mObservableOnSubscribe = new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            emitter.onNext("Hello RxJava!");
            emitter.onComplete();
        }
    };

    // 订阅者
    private Observer<String> mTextSubscriber = new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(String s) {
            ((Button) findViewById(R.id.textButton)).setText(s);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    };

    // 订阅者
    private Observer<String> mToastSubscriber = new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(String s) {
            Toast.makeText(RxSimpleActivity.this, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    };

}
