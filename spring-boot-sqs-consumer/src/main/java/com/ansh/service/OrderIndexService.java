package com.ansh.service;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.ansh.config.ConsumerConfig;
import com.ansh.constant.ConsumerConstant;
import com.ansh.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

@Service
@Slf4j
public class OrderIndexService implements MessageListener {

    @Autowired
    private ConsumerConfig consumerConfig;

    Queue<Product> queue = new LinkedBlockingDeque<>();

    @PostConstruct
    public void start(){
        for (int i = 0; i < ConsumerConstant.CONSUMER_CONNECTION_COUNT; i++) {
            try {
                SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                        new ProviderConfiguration(),
                        AmazonSQSClientBuilder.standard()
                                .withRegion(Regions.AP_SOUTH_1).build());


                SQSConnection connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageConsumer consumer = session.createConsumer(session.createQueue(consumerConfig.getQueueName()));
                consumer.setMessageListener(this);
                connection.start();
            } catch (Exception ex) {
                log.error("Exception occurred while making SQS connections ", ex);
            }
        }

        for (int i = 0; i < ConsumerConstant.CONSUMER_THREAD_COUNT; i++) {
            new Thread(new Inner(queue)).start();
        }
    }

    public void onMessage(Message msg) {
        try {
            indexOrders(((SQSTextMessage)msg).getText());
        } catch (Exception ex) {
            log.error("Exception occurred while converting SQS message to text ", ex);
        }
    }

    public void indexOrders(String message) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Product>> typRef = new TypeReference<List<Product>>() {};

        try{
            List<Product> productList = mapper.readValue(message, typRef);
            for(Product product : productList)
                queue.add(product);
        } catch(Exception ex){
            log.error("Encountered exception while parsing message received from queue ", ex);
        }
    }

    class Inner implements Runnable {
        private Queue<Product> queue;

        public Inner(Queue<Product> queue) {
            this.queue = queue;
        }

        public void run() {
            while(true) {
                Product product = queue.poll();
                log.info("Product Info : " + product);
            }
        }
    }
}
