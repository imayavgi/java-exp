package uiak.exper;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.ConnectableObservable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoConnectTest {

    public static void main(String[] args) {

        final List<Integer> emitted = new ArrayList<>();

        // The source is expensive, so I only want it subscribed to once.
        Observable<Integer> expensiveSource = Observable.range(1, 5);
        // Testing this by ensuring each value is only emitted once.
        Observable<Integer> expensiveSourceWireTap = expensiveSource.doOnNext(integer -> emitted.add(integer));

        // This method creates a ConnectableObservable with publish(), but performs no subscriptions
        Observable<List<Integer>[]> doubledAndTripled = teeAndRecombineObservables(expensiveSourceWireTap);

        Observable<List<Integer>> results = doubledAndTripled.map(lists -> {
            List<Integer> result = new ArrayList<Integer>();
            for (int i = 0; i < lists[0].size(); i++) {
                result.add(lists[0].get(i) * 10 + lists[1].get(i) * 100);
            }
            return result;
        });

        // Subscription starts here, but there is no reference to the ConnectableObservable at this point
        results.subscribe(integers -> System.out.println("results = " + integers));

        System.out.println("emitted = " + emitted);
    }

    private static Observable<List<Integer>[]> teeAndRecombineObservables(Observable<Integer> integers) {

        Observable<Integer> observableToUse = integers;

        boolean usePublish = true; // If changed to false, you will see the source emits the original integers twice

        if (usePublish) {
            // I need to 'tee' the output into two Observables.
            // I can't use cache() because there is LOTS of data and I don't want it all cached.
            // publish() is correct, but troublesome because the point where I subscribe doesn't have access to the ConnectableObservable
            // to call connect() after the subscription
            final ConnectableObservable<Integer> publishedIntegers = integers.publish();

            // However, I know at this point exactly how many subscribers I am expecting (2).
            // So the following method creates a decorator over 'publishedIntegers' that calls connect() after the 2nd subscriber subscribes
            Observable<Integer> autoConnectingPublishedIntegers = connectWithSubscribers(publishedIntegers, 2);

            observableToUse = autoConnectingPublishedIntegers;
        }

        Observable<Integer> doubled = observableToUse.map(integer -> integer * 2);

        Observable<Integer> tripled = observableToUse.map(integer -> integer * 3);

        return Observable.zip(doubled.toList(), tripled.toList(), (Func2<List<Integer>, List<Integer>, List<Integer>[]>) (integers1, integers2) -> new List[]{integers1, integers2});
    }

    private static Observable<Integer> connectWithSubscribers(final ConnectableObservable<Integer> publishedObservable, final int expectedSubscriberCount) {
        return Observable.create(new OnSubscribe<Integer>() {
            private final AtomicInteger subscriberCount = new AtomicInteger(0);

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int subscribers = subscriberCount.incrementAndGet();
                if (subscribers <= expectedSubscriberCount) {
                    publishedObservable.subscribe(subscriber);
                    if (subscribers == expectedSubscriberCount) {
                        publishedObservable.connect();
                    }
                } else {
                    throw new IllegalStateException("Only " + expectedSubscriberCount + " subscribers expected");
                }
            }
        });
    }
}
