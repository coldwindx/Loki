package com.coldwindx.loki;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("unit")
@SpringBootTest(classes = LokiMybatisTestApplication.class)
public class AbstractTest {
}
