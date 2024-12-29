package org.qq.keeper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "keeper")
public class KeeperConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler((r, executor1) -> {

                log.warn("任务执行失败，拒绝策略：{}", r);
                r.run();
        });


        return executor;
    }







}
