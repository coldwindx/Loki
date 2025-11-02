package com.coldwindx.lokinetty;

import com.coldwindx.lokinetty.server.NettyServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class LokiNettyApplication implements ApplicationRunner {

    private final NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(LokiNettyApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception{
        nettyServer.start();
    }
}
