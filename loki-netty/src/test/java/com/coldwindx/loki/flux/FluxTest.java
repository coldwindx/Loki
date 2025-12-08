package com.coldwindx.loki.flux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class FluxTest {

    @Test
    public void test() throws InterruptedException {
        Flux<Integer> flux = Flux.range(1, 100)
                .concatMap(integer -> {
                    return Mono.fromCallable(() -> {
                        Thread.sleep(1000);
                        System.out.println("val:" + integer + ", thread:" + Thread.currentThread().getId());

                        return integer;
                    }).publishOn(Schedulers.parallel()); // <- Each Mono processing every integer can be processed in a different thread
                })
                .doOnError(e->log.error(e.getMessage()));
        flux.subscribe();
        Thread.sleep(1000000);
    }
    
    @Test
    public void test2() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Sinks.Many<String> sink = Sinks.many().replay().all();
        Flux<String> flux = sink.asFlux().doOnTerminate(latch::countDown);

        for(int i = 0; i < 10; ++i){
            sink.tryEmitNext("val:" + i);
        }
        sink.tryEmitComplete();
        System.out.println("sink.tryEmitComplete");

        flux.subscribe(System.out::println);
        latch.await();

    }
}
