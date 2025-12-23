package com.coldwindx.loki;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("unit")
@SpringBootTest(classes = LokiCoreTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LokiCoreTest {}
