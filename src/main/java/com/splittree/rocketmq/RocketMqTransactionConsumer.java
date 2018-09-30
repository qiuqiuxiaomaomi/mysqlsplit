package com.splittree.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yangmingquan on 2018/9/29.
 */
@Service
public class RocketMqTransactionConsumer {
    @Autowired
    DefaultMQPushConsumer defaultMQPushConsumer;

    public void rockemqConsumerHandler(Message message){

    }
}
