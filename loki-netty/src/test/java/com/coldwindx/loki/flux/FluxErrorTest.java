package com.coldwindx.loki.flux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class FluxErrorTest {
    @Test
    public void test3() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Object> flux = sink.asFlux()
                .handle((val, sink1) -> {
                    log.info(val);

                })
                .doOnError(e->log.error(e.getMessage()))
                .doOnTerminate(() -> log.info("terminate"));
//                .doFinally(signal->latch.countDown());

        flux.subscribe();

        for(int i = 0; i < 10; i++) {
            sink.tryEmitNext("val:" + i);
            if(i == 5) {
                sink.tryEmitError(new RuntimeException("error"));
            }
        }

//        sink.tryEmitComplete();
        latch.await();
    }
}
