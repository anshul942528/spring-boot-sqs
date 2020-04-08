package com.ansh.service;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.ansh.config.ProducerConfig;
import com.ansh.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
@Slf4j
public class ProducerService {

    @Autowired
    private AmazonSQS amazonSQS;

    @Autowired
    private ProducerConfig producerConfig;

    public void sendMessage() {
        String currentDate = getCurrentDate();
        List<Product> productList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try{
            SendMessageRequest messageRequest = new SendMessageRequest()
                    .withQueueUrl(producerConfig.getSqsEndPointUrl())
                    .withMessageBody(mapper.writeValueAsString(productList))
                    .withDelaySeconds(5);
            SendMessageResult sendMessageResult = amazonSQS.sendMessage(messageRequest);
            if(sendMessageResult.getMessageId() == null){
                log.error("Could not submit this batch to queue");
            } else{
                log.info("Batch finished : " + productList.size());
            }
        } catch (AmazonSQSException ex){
            log.error("Exception encountered while putting product-list in queue ", ex);
        } catch(Exception en){
            log.error("Exception encountered while json parsing ", en);
        }
    }

    public String getCurrentDate () {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = format.format(new Date());
        return dateString;
    }
}
