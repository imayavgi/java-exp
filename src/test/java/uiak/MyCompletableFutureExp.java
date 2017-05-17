package uiak.exper;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MyCompletableFutureExp {

    @Test
    public void simpleCase() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cfs = CompletableFuture.completedFuture(" Who is there ");
        //String s = cfs.get();
        //System.out.println(s);

        CompletableFuture<String> cfs2 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println(System.currentTimeMillis() + " Going to sleep");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Thread.currentThread().getId() + " Hello from another world\n";
        });

        cfs2.thenRun(() -> {
            System.out.println(System.currentTimeMillis() + " Sleeping again");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        CompletableFuture<String> cfs3 = cfs2.thenCombine(cfs, (s1, s2) -> s1 + s2);

        System.out.println(System.currentTimeMillis() + " I am doing other things");
        cfs3.join();

        cfs3.thenAccept(s1 -> System.out.println(System.currentTimeMillis()  + " Finally Got " + s1));
        //System.out.println(cfs2.get());
    }

}
