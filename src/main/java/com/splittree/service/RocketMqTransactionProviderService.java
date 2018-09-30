package com.splittree.service;

import com.splittree.rocketmq.RocketMqTransactionProvider;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yangmingquan on 2018/9/30.
 */
@Service
public class RocketMqTransactionProviderService {
    @Autowired
    TransactionMQProducer transactionMQProducer;

    public void sendMessage(Message message) {
        RocketMqTransactionProvider transactionProvider = new RocketMqTransactionProvider();
        try {
            SendResult sendResult = transactionMQProducer.sendMessageInTransaction(message, transactionProvider, null);
            System.out.println(sendResult);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
}
