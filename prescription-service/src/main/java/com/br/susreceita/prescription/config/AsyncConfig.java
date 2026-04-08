package com.br.susreceita.prescription.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // TODO: Define custom Executor if needed (e.g. ThreadPoolTaskExecutor)
}
