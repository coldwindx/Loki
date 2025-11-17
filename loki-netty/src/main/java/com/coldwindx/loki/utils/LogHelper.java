package com.coldwindx.loki.utils;

import org.slf4j.MDC;
import reactor.util.context.ContextView;

public class LogHelper {
    public static void withContext(ContextView ctx, Runnable runnable) {
        String logid = ctx.get("LOD_ID");
        try(MDC.MDCCloseable ignored = MDC.putCloseable("LOG_ID", logid)) {
            runnable.run();
        }
    }
}
