package com.splittree.config;

import com.splittree.constant.RocketmqProps;
import com.splittree.rocketmq.RocketMqTransactionCheckListener;
import com.splittree.rocketmq.RocketMqTransactionConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by yangmingquan on 2018/9/29.
 */
@Configuration
public class RocketMqConfig {
    private final static Log log = LogFactory.getLog(RocketMqConfig.class);
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private RocketmqProps rocketmqProps;
    @Autowired
    private RocketMqTransactionCheckListener rocketMqTransactionCheckListener;
    @Autowired
    private RocketMqTransactionConsumer rocketMqTransactionConsumer;
    private static boolean isFirstSub = true;
    private static long startTime = System.currentTimeMillis();

    @Bean
    public TransactionMQProducer transactionMQProducer() throws MQClientException{
        TransactionMQProducer producer = new TransactionMQProducer(rocketmqProps.getTransactionProducerGroupName());
        producer.setNamesrvAddr(rocketmqProps.getNamesrvAddr());
        producer.setInstanceName(rocketmqProps.getTransactionProducerInstanceName());
        producer.setRetryTimesWhenSendAsyncFailed(10);
        producer.setCheckThreadPoolMinSize(2);
        producer.setCheckThreadPoolMaxSize(2);
        producer.setCheckRequestHoldMax(2000);
        producer.setTransactionCheckListener(rocketMqTransactionCheckListener);
        producer.start();
        return producer;
    }

    @Bean
    public DefaultMQPushConsumer pushConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketmqProps.getConsumerGroupName());
        consumer.setNamesrvAddr(rocketmqProps.getNamesrvAddr());
        consumer.setInstanceName(rocketmqProps.getConsumerInstanceName());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("bonaparte", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                try {
                    for (MessageExt messageExt : list) {
                        rocketMqTransactionConsumer.rockemqConsumerHandler(messageExt);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }
}
