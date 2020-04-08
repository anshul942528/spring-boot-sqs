package com.ansh.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ProducerConfig {

    @Value("${cloud.aws.end-point.url}")
    private String sqsEndPointUrl;

    @Bean
    public AmazonSQS getQueue(){
        return AmazonSQSClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();
    }
}
