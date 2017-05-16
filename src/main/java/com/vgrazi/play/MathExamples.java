package com.vgrazi.play;

import rx.Observable;
import rx.Subscriber;

import java.util.Arrays;
import java.util.List;


/**
 * Created by vgrazi on 7/31/16.
 */
public class MathExamples {
    static Observable<List<Integer>> sequence = Observable.create(new Observable.OnSubscribe<List<Integer>>() {
        @Override
        public void call(Subscriber<? super List<Integer>> subscriber) {
            subscriber.onNext(Arrays.asList(10, 9, 8, null, 1, 2, 3, 4));
            subscriber.onCompleted();
        }
    });

    public static void main(String[] args) throws InterruptedException {
//    ConnectableObservable<Long> observable = Observable.interval(1, TimeUnit.SECONDS).publish();
//    observable.connect();
//    Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
        sequence
                .filter(x -> x != null)
//      .map(aLong -> (double)aLong)
//      .window(100, TimeUnit.MILLISECONDS)
//      .flatMap(MathObservable::averageInteger)
                .subscribe(System.out::println, Throwable::printStackTrace);


        Thread.sleep(10_000);
    }

    public void testMin() {
        Observable<Integer> rObservable = sequence
                .filter(number -> number != null)
                .flatMap(numList -> Observable.from(numList)
//        .min
                );
        rObservable.subscribe(System.out::println);

    }

    public void testReduce() {
        Observable<Integer> rObservable = sequence
                .flatMap(numList -> Observable.from(numList)
                                .filter(number -> number != null)
//        .timeInterval()
//        .reduce(Integer.MAX_VALUE, (currentMin, number) -> number < currentMin ? number : currentMin)
                                .reduce(Integer.MIN_VALUE, (currentMax, number) -> number > currentMax ? number : currentMax)
                                .filter(number -> number != Integer.MIN_VALUE)
                );
        rObservable.subscribe(System.out::println);
    }

}
