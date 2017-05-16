package uiak.exper;

import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

/**
 * Created by imaya on 9/30/16.
 */
public class RxTest {
    public static void main(String args[]) {
        RxTest t = new RxTest();
        //t.intervalExp();
        t.testFuncs();
        try {
            Thread.sleep(60*2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void intervalExp() {
        Observable<Long> tickGen = Observable.interval(1, TimeUnit.SECONDS);
        tickGen.map(tick -> new StockTick(tick, "TICK"+tick, tick*100, tick*4.5*100))
                .subscribe(e -> System.out.println(e.toString()));
    }

    private void testFuncs() {
        Observable.just(1,2,1,1,1,2,3,3,4).distinct().delay(1, TimeUnit.MINUTES)
                .subscribe(System.out::println);
    }
}

class StockTick {
    private final long id;
    private final String name;
    private final long qty;
    private final double price;

    public StockTick(long id, String name, long qty, double price) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.price = price;
    }

    public String toString() {
        return id + " " + name + " "  + qty + " " + price;
    }
}
