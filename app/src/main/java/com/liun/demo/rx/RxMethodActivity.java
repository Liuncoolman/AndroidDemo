package com.liun.demo.rx;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.liun.demo.R;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * Description:
 * Author：Liun
 * Date:2019/03/26 09:17
 */
public class RxMethodActivity extends AppCompatActivity {
    private final String[] mManyWords = {"Hello", "World", "RxJava", "!"};
    private final List<String> mManyWordsList = Arrays.asList(mManyWords);

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rx_method);


    }

    /**
     * 输入待分发的异步接口，调用完直接分发
     *
     * @param view
     */
    public void just(View view) {
        Observable<String> just = Observable.just("Hello");
        just.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(RxMethodActivity.this, s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 异步接口数据变成数组，将数组转变成单个元素，分发多次
     *
     * @param view
     */
    public void from(View view) {
        Observable<String> from = Observable.fromArray(mManyWords);
        from.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(RxMethodActivity.this, s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 映射，调用大小写转换，控制取值范围等特定的方法加工数据流，继续分发
     *
     * @param view
     */
    public void map(View view) {
        Observable<String> just = Observable.just("Hello");
        just.observeOn(AndroidSchedulers.mainThread())
                .map(mUppterLetterFunc) // 转换大写
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(RxMethodActivity.this, s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 增肥映射，与map类似，映射后返回的是观察者对象
     *
     * @param view
     */
    public void flatMap(View view) {
        Observable.fromIterable(mManyWordsList)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(mAppendFunc)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(RxMethodActivity.this, s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 将数组的多个值合成一个数据值，分发一次
     *
     * @param view
     */
    public void reduce(View view) {
        Observable.fromIterable(mManyWordsList)
                .observeOn(AndroidSchedulers.mainThread())
                .reduce(mBiFunc)
                .subscribe(new MaybeObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        Toast.makeText(RxMethodActivity.this, s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 订阅者,大小写转换
    private Function<String, String> mUppterLetterFunc = new Function<String, String>() {
        @Override
        public String apply(String s) throws Exception {
            return s.toUpperCase();
        }
    };

    private Function<String, Observable<String>> mAppendFunc = new Function<String, Observable<String>>() {
        @Override
        public Observable<String> apply(String s) throws Exception {
            return Observable.fromArray(s.toLowerCase());
        }
    };

    private BiFunction<String, String, String> mBiFunc = new BiFunction<String, String, String>() {
        @Override
        public String apply(String s, String s2) throws Exception {
            return String.format("%s %s",s,s2);
        }
    };
}
