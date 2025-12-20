package com.coldwindx;

import com.coldwindx.entity.Message;
import com.coldwindx.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@TestPropertySource(properties = {"spring.config.location=classpath:application-unit.yml"})
@SpringBootTest(classes = LokiMqApplication.class)
public class AbstractTest {

    @Autowired
    private TestService testService;

    @Test
    public void test() {
        testService.send(new Message<>("hello world"));
    }
}
