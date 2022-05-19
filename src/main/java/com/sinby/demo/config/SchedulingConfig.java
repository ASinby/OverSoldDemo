package com.sinby.demo.config;

import com.sinby.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author ：sinby
 * @Date :2022/05/18 16:17
 * @Version 1.0
 */
@EnableScheduling
@Configuration
@ConditionalOnProperty(name = "enabled.scheduing.redis", havingValue = "true")
public class SchedulingConfig {

    @Autowired
    TaskService taskService;

    @Scheduled(fixedRate = 1000*60*2)
    public void runTaskOfRedis(){
        taskService.purchaseTask();
    }
}
