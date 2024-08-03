package com.yang.yangdada;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 测试RxJava
 */
@Slf4j
public class TestRxJava {

    @Test
    void testIntToStr() {
        Flowable<String> flowable = Flowable.range(0, Integer.MAX_VALUE)
                .map(String::valueOf);
    }

    @Test
    void testToList() {
        Single<List<Integer>> list = Flowable.range(0, Integer.MAX_VALUE).toList();
    }

    @Test
    void testFilter() {
        Single<List<Integer>> listSingle = Flowable.range(0, Integer.MAX_VALUE).filter(i -> i > 10).toList();
    }

    @Test
    void testConcat() {
        // 创建两个Flowable对象
        Flowable<String> flowable1 = Flowable.just("A", "B", "C");
        Flowable<String> flowable2 = Flowable.just("A", "B", "C");

        // 使用concat 操作符将两个Flowable合并
        Flowable<String> flowable = Flowable.concat(flowable1, flowable2);
    }

    @Test
    void testSorted() {
        Flowable<String> flowable1 = Flowable.just("A", "B", "C").sorted();
    }

    @Test
    void testDoObserve() {
        Flowable<String> flowable = Flowable.just("A", "B", "C").sorted();
        flowable.observeOn(Schedulers.io())
                .doOnNext(item -> {
                    System.out.println("来数据啦" + item.toString());
                }).doOnError(e -> {
                    System.out.println("出错啦" + e.getMessage());
                }).doOnComplete(() -> {
                    System.out.println("数据处理完啦");
                }).subscribe();
    }

    @Test
    void testRxJava() throws InterruptedException {
        // 创建一个流，每秒发射一个递增的整数（数据流变化）
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS)
                .map(i -> i + 1)
                .subscribeOn(Schedulers.io());

        // 订阅Flowable流，并打印每个接受到的数字
        flowable.observeOn(Schedulers.io())
                .doOnNext(System.out::println)
                .subscribe();

        // 让主线程睡眠，以便观察输出
        Thread.sleep(10000L);
    }
}
