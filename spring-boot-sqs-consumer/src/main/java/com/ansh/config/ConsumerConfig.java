package com.ansh.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ConsumerConfig {

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Value("${aws.queue.name}")
    private String queueName;
}
